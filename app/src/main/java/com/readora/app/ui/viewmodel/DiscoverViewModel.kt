package com.readora.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.readora.app.data.repository.SourceRepository
import com.readora.app.core.LogLevel
import com.readora.app.core.ReadoraError
import com.readora.app.core.ReadoraLogger
import com.readora.app.core.toReadoraError
import com.readora.app.source.MangaDexSource
import com.readora.app.source.OnlineComicSummary
import com.readora.app.source.OnlineSourceRegistry
import com.readora.app.source.api.SourceFilters
import com.readora.app.storage.SettingsSerializer
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SourceSearchSection(
    val sourceId: String,
    val sourceName: String,
    val loading: Boolean = false,
    val resultCount: Int = 0,
    val error: String? = null,
)

enum class DiscoverFeedTab { Popular, Latest }
enum class DiscoverSortOrder { Default, TitleAZ, TitleZA, MostTags, YearNewest, YearOldest }

data class DiscoverUiState(
    val draftQuery: String = "",
    val submittedQuery: String = "",
    val popularComics: List<OnlineComicSummary> = emptyList(),
    val popularPage: Int = 1,
    val popularLoadingMore: Boolean = false,
    val latestComics: List<OnlineComicSummary> = emptyList(),
    val latestPage: Int = 1,
    val latestLoading: Boolean = false,
    val latestLoadingMore: Boolean = false,
    val latestError: ReadoraError? = null,
    val searchResults: List<OnlineComicSummary> = emptyList(),
    val loading: Boolean = false,
    val error: ReadoraError? = null,
    val sourceSections: List<SourceSearchSection> = emptyList(),
    val selectedTags: Set<String> = emptySet(),
    val feedTab: DiscoverFeedTab = DiscoverFeedTab.Popular,
    val sortOrder: DiscoverSortOrder = DiscoverSortOrder.Default,
) {
    val visibleComics: List<OnlineComicSummary>
        get() {
            val base = when {
                submittedQuery.isNotBlank() -> searchResults
                feedTab == DiscoverFeedTab.Latest -> latestComics
                else -> popularComics
            }
            val filtered = if (selectedTags.isEmpty()) base
            else base.filter { comic -> selectedTags.all { sel -> comic.tags.any { it.equals(sel, ignoreCase = true) } } }
            return when (sortOrder) {
                DiscoverSortOrder.TitleAZ   -> filtered.sortedBy { it.title.lowercase() }
                DiscoverSortOrder.TitleZA   -> filtered.sortedByDescending { it.title.lowercase() }
                DiscoverSortOrder.MostTags  -> filtered.sortedByDescending { it.tags.size }
                DiscoverSortOrder.YearNewest -> filtered.sortedByDescending { it.year ?: 0 }
                DiscoverSortOrder.YearOldest -> filtered.sortedBy { it.year ?: Int.MAX_VALUE }
                DiscoverSortOrder.Default   -> filtered
            }
        }

    val availableTags: List<String>
        get() {
            val base = when {
                submittedQuery.isNotBlank() -> searchResults
                feedTab == DiscoverFeedTab.Latest -> latestComics
                else -> popularComics
            }
            return base.flatMap { it.tags }
                .groupingBy { it.lowercase() }
                .eachCount()
                .entries
                .sortedByDescending { it.value }
                .take(12)
                .map { it.key.replaceFirstChar { c -> c.uppercaseChar() } }
        }
}

class DiscoverViewModel(
    private val sourceRepository: SourceRepository,
    private val settingsSerializer: SettingsSerializer,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        DiscoverUiState(
            loading = true,
            selectedTags = settingsSerializer.load().discoverTagFilter
                .let { if (it.isBlank()) emptySet() else setOf(it) },
        )
    )
    val uiState: StateFlow<DiscoverUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun updateDraftQuery(query: String) {
        _uiState.update { it.copy(draftQuery = query) }
    }

    fun submitSearch() {
        val query = _uiState.value.draftQuery.trim()
        _uiState.update { it.copy(submittedQuery = query) }
        refresh()
    }

    fun quickSearch(query: String) {
        _uiState.update { it.copy(draftQuery = query, submittedQuery = query) }
        refresh()
    }

    fun retry() = refresh()

    fun selectTag(tag: String) {
        val current = _uiState.value.selectedTags
        val newTags = if (current.any { it.equals(tag, ignoreCase = true) })
            current.filter { !it.equals(tag, ignoreCase = true) }.toSet()
        else current + tag
        _uiState.update { it.copy(selectedTags = newTags) }
        persistTagFilter(newTags.firstOrNull() ?: "")
    }

    fun clearTagFilter() {
        _uiState.update { it.copy(selectedTags = emptySet()) }
        persistTagFilter("")
    }

    private fun persistTagFilter(tag: String) {
        viewModelScope.launch {
            val current = settingsSerializer.load()
            settingsSerializer.save(current.copy(discoverTagFilter = tag))
        }
    }

    fun selectSortOrder(order: DiscoverSortOrder) {
        _uiState.update { it.copy(sortOrder = order) }
    }

    fun selectFeedTab(tab: DiscoverFeedTab) {
        _uiState.update { it.copy(feedTab = tab) }
        if (tab == DiscoverFeedTab.Latest && _uiState.value.latestComics.isEmpty()) {
            refreshLatest()
        }
    }

    fun refreshLatest() {
        viewModelScope.launch {
            val langs = settingsSerializer.load().preferredLanguages.ifEmpty { listOf("en") }
            _uiState.update { it.copy(latestLoading = true, latestError = null, latestPage = 1, latestComics = emptyList()) }
            runCatching {
                MangaDexSource.getLatest(page = 1, langs = langs)
            }.onSuccess { comics ->
                _uiState.update { it.copy(latestComics = comics, latestLoading = false) }
            }.onFailure { e ->
                _uiState.update { it.copy(latestLoading = false, latestError = e.toReadoraError()) }
            }
        }
    }

    fun loadMorePopular() {
        val state = _uiState.value
        if (state.popularLoadingMore || state.loading) return
        viewModelScope.launch {
            val langs = settingsSerializer.load().preferredLanguages.ifEmpty { listOf("en") }
            val nextPage = state.popularPage + 1
            _uiState.update { it.copy(popularLoadingMore = true) }
            runCatching {
                MangaDexSource.getPopular(page = nextPage, langs = langs)
            }.onSuccess { comics ->
                _uiState.update { it ->
                    // Guard against a concurrent refresh() having reset the page counter
                    if (it.popularPage != nextPage - 1) return@update it.copy(popularLoadingMore = false)
                    it.copy(
                        popularComics = (it.popularComics + comics).distinctBy { c -> c.id },
                        popularPage = nextPage,
                        popularLoadingMore = false,
                    )
                }
            }.onFailure { e ->
                ReadoraLogger.log("DiscoverViewModel", "loadMorePopular failed: ${e.message}", LogLevel.WARN)
                _uiState.update { it.copy(popularLoadingMore = false, error = e.toReadoraError()) }
            }
        }
    }

    fun loadMoreLatest() {
        val state = _uiState.value
        if (state.latestLoadingMore || state.latestLoading) return
        viewModelScope.launch {
            val langs = settingsSerializer.load().preferredLanguages.ifEmpty { listOf("en") }
            val nextPage = state.latestPage + 1
            _uiState.update { it.copy(latestLoadingMore = true) }
            runCatching {
                MangaDexSource.getLatest(page = nextPage, langs = langs)
            }.onSuccess { comics ->
                _uiState.update { it ->
                    // Guard against a concurrent refreshLatest() having reset the page counter
                    if (it.latestPage != nextPage - 1) return@update it.copy(latestLoadingMore = false)
                    it.copy(
                        latestComics = (it.latestComics + comics).distinctBy { c -> c.id },
                        latestPage = nextPage,
                        latestLoadingMore = false,
                    )
                }
            }.onFailure { e ->
                ReadoraLogger.log("DiscoverViewModel", "loadMoreLatest failed: ${e.message}", LogLevel.WARN)
                _uiState.update { it.copy(latestLoadingMore = false, latestError = e.toReadoraError()) }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            val query = _uiState.value.submittedQuery
            val savedSettings = settingsSerializer.load()
            val allowNsfw = savedSettings.contentRating == "adult"
            val preferredLangs = savedSettings.preferredLanguages.ifEmpty { listOf("en") }
            val filters = SourceFilters(isNsfw = allowNsfw)
            val enabledSources = runCatching {
                sourceRepository.getEnabledSources()
            }.getOrDefault(emptyList())
            val sources = OnlineSourceRegistry.enabledRuntimeSources(enabledSources)
            val initialSections = sources.map { source ->
                SourceSearchSection(
                    sourceId = source.id,
                    sourceName = source.name,
                    loading = true,
                )
            }
            _uiState.update { it.copy(loading = true, error = null, sourceSections = initialSections) }

            val sourceResults = mutableMapOf<String, List<OnlineComicSummary>>()
            var firstError: Throwable? = null

            supervisorScope {
                val jobs = sources.map { source ->
                    launch {
                        runCatching {
                            if (source is com.readora.app.source.MangaDexSource) {
                                if (query.isBlank()) {
                                    source.getPopular(page = 1, langs = preferredLangs).take(18)
                                } else {
                                    source.search(query, filters, page = 1, langs = preferredLangs).take(18)
                                }
                            } else {
                                if (query.isBlank()) {
                                    source.getPopular(page = 1).take(18)
                                } else {
                                    source.search(query, filters, page = 1).take(18)
                                }
                            }
                        }.onSuccess { comics ->
                            sourceResults[source.id] = comics
                            _uiState.update { state ->
                                state.copy(
                                    sourceSections = state.sourceSections.map { section ->
                                        if (section.sourceId == source.id) {
                                            section.copy(loading = false, resultCount = comics.size, error = null)
                                        } else {
                                            section
                                        }
                                    },
                                )
                            }
                        }.onFailure { throwable ->
                            if (firstError == null) firstError = throwable
                            ReadoraLogger.log(
                                tag = "DiscoverViewModel",
                                message = "${source.name} request failed: ${throwable.message ?: "Unknown error"}",
                                level = LogLevel.WARN,
                            )
                            _uiState.update { state ->
                                state.copy(
                                    sourceSections = state.sourceSections.map { section ->
                                        if (section.sourceId == source.id) {
                                            section.copy(
                                                loading = false,
                                                resultCount = 0,
                                                error = throwable.message ?: "Request failed",
                                            )
                                        } else {
                                            section
                                        }
                                    },
                                )
                            }
                        }
                    }
                }
                jobs.joinAll()
            }

            val merged = sourceResults.values
                .flatten()
                .distinctBy { normalizeTitle(it.title) }
                .sortedBy { it.title.lowercase() }

            ReadoraLogger.log(
                "DiscoverViewModel",
                "Loaded ${merged.size} merged titles across ${sources.size} sources for query '$query'",
            )

            _uiState.update {
                val nextState = if (query.isBlank()) {
                    it.copy(popularComics = merged, popularPage = 1)
                } else {
                    it.copy(searchResults = merged)
                }
                nextState.copy(
                    loading = false,
                    error = firstError?.toReadoraError(),
                )
            }
        }
    }

    private fun normalizeTitle(value: String): String {
        return value
            .lowercase()
            .replace(Regex("[^a-z0-9]+"), "")
            .trim()
    }
}

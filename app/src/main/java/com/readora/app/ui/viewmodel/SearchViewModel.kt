package com.readora.app.ui.viewmodel

import com.readora.app.core.ReadoraLogger
import com.readora.app.core.LogLevel
import com.readora.app.core.toReadoraError
import com.readora.app.data.repository.SourceRepository
import com.readora.app.source.MangaDexSource
import com.readora.app.source.OnlineComicSummary
import com.readora.app.source.OnlineSourceRegistry
import com.readora.app.source.api.SourceFilters
import com.readora.app.storage.ReadoraPreferences
import com.readora.app.storage.SavedOnlineComic
import com.readora.app.storage.SettingsSerializer
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.delay
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

data class SearchUiState(
    val query: String = "",
    val loading: Boolean = false,
    val libraryResults: List<SavedOnlineComic> = emptyList(),
    val sourceSections: List<SourceSearchSection> = emptyList(),
    val searchResults: List<OnlineComicSummary> = emptyList(),
    val error: String? = null,
    val recentSearches: List<String> = emptyList(),
)

class SearchViewModel(
    private val sourceRepository: SourceRepository,
    private val settingsSerializer: SettingsSerializer,
    private val preferences: ReadoraPreferences,
) : ViewModel() {
    private var searchJob: Job? = null

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        loadRecentSearches()
    }

    fun updateQuery(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    fun search(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(325)
            executeSearch(query.trim())
        }
    }

    fun quickSearch(query: String) {
        updateQuery(query)
        search(query)
    }

    fun clearHistory() {
        settingsSerializer.save(settingsSerializer.load().copy(savedSearches = emptyList()))
        _uiState.update { it.copy(recentSearches = emptyList()) }
    }

    fun removeRecentSearch(query: String) {
        val updated = settingsSerializer.load().savedSearches.filterNot { it.equals(query, ignoreCase = true) }
        settingsSerializer.save(settingsSerializer.load().copy(savedSearches = updated))
        _uiState.update { it.copy(recentSearches = updated) }
    }

    private fun loadRecentSearches() {
        _uiState.update { it.copy(recentSearches = settingsSerializer.load().savedSearches) }
    }

    private suspend fun executeSearch(query: String) {
        if (query.isBlank()) {
            _uiState.update {
                it.copy(
                    loading = false,
                    libraryResults = emptyList(),
                    sourceSections = emptyList(),
                    searchResults = emptyList(),
                    error = null,
                )
            }
            return
        }

        val settings = settingsSerializer.load()
        val allowNsfw = settings.contentRating == "adult"
        val preferredLangs = settings.preferredLanguages.ifEmpty { listOf("en") }
        val filters = SourceFilters(isNsfw = allowNsfw)
        val enabledSources = runCatching { sourceRepository.getEnabledSources() }.getOrDefault(emptyList())
        val sources = OnlineSourceRegistry.enabledRuntimeSources(enabledSources)

        val initialSections = sources.map { source ->
            SourceSearchSection(sourceId = source.id, sourceName = source.name, loading = true)
        }

        val libraryResults = preferences.loadOnlineLibrary()
            .filter { it.title.contains(query, ignoreCase = true) }

        _uiState.update {
            it.copy(
                loading = true,
                error = null,
                libraryResults = libraryResults,
                sourceSections = initialSections,
                searchResults = emptyList(),
            )
        }

        val sourceResults = mutableMapOf<String, List<OnlineComicSummary>>()
        var firstError: Throwable? = null

        supervisorScope {
            val jobs = sources.map { source ->
                launch {
                    runCatching {
                        if (source is MangaDexSource) {
                            source.search(query, filters, page = 1, langs = preferredLangs).take(20)
                        } else {
                            source.search(query, filters, page = 1).take(20)
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
                            tag = "SearchViewModel",
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

        val mergedResults = sourceResults.values
            .flatten()
            .distinctBy { normalizeTitle(it.title) }
            .sortedBy { it.title.lowercase() }

        if (mergedResults.isNotEmpty()) {
            saveSearchHistory(query)
        }

        _uiState.update {
            it.copy(
                loading = false,
                searchResults = mergedResults,
                error = firstError?.toReadoraError()?.displayMessage,
            )
        }
    }

    private fun saveSearchHistory(query: String) {
        val cleaned = query.trim()
        if (cleaned.isBlank()) return
        val settings = settingsSerializer.load()
        val updated = (listOf(cleaned) + settings.savedSearches.filterNot { it.equals(cleaned, ignoreCase = true) }).take(12)
        settingsSerializer.save(settings.copy(savedSearches = updated))
        _uiState.update { it.copy(recentSearches = updated) }
    }

    private fun normalizeTitle(value: String): String {
        return value
            .lowercase()
            .replace(Regex("[^a-z0-9]+"), "")
            .trim()
    }
}

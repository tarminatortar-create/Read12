package com.readora.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.readora.app.data.repository.ProgressRepository
import com.readora.app.source.MangaDexSource
import com.readora.app.source.OnlinePage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReaderUiState(
    val pages: List<OnlinePage> = emptyList(),
    val currentPage: Int = 0,
    val loading: Boolean = false,
    val error: String? = null,
)

class ReaderViewModel(
    val progressRepository: ProgressRepository,
) : ViewModel() {
    val _uiState = MutableStateFlow(ReaderUiState())
    val uiState: StateFlow<ReaderUiState> = _uiState.asStateFlow()

    fun loadPages(chapterId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            runCatching { MangaDexSource.pages(chapterId) }
                .onSuccess { pages -> _uiState.update { it.copy(pages = pages, loading = false) } }
                .onFailure { error -> _uiState.update { it.copy(error = error.message ?: "Could not load pages", loading = false) } }
        }
    }

    fun setCurrentPage(page: Int) {
        _uiState.update { it.copy(currentPage = page.coerceAtLeast(0)) }
    }

    fun saveProgress(comicId: String, chapterId: String, page: Int, total: Int) {
        viewModelScope.launch {
            progressRepository.saveProgress(comicId, chapterId, page, total)
        }
    }
}

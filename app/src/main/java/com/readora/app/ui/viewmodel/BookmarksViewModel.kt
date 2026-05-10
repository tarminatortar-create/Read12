package com.readora.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.readora.app.data.db.BookmarkEntity
import com.readora.app.data.db.ChapterNoteEntity
import com.readora.app.data.repository.BookmarkRepository
import com.readora.app.data.repository.ChapterNoteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class BookmarksUiState(
    val bookmarks: List<BookmarkEntity> = emptyList(),
    val notes: List<ChapterNoteEntity> = emptyList(),
)

class BookmarksViewModel(
    private val bookmarkRepository: BookmarkRepository,
    private val noteRepository: ChapterNoteRepository,
) : ViewModel() {

    val uiState: StateFlow<BookmarksUiState> =
        combine(bookmarkRepository.getAll(), noteRepository.getAll()) { bookmarks, notes ->
            BookmarksUiState(bookmarks = bookmarks, notes = notes)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BookmarksUiState())

    fun deleteBookmark(id: Long) {
        viewModelScope.launch { bookmarkRepository.delete(id) }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch { noteRepository.delete(id) }
    }

    fun clearAllBookmarks() {
        viewModelScope.launch { bookmarkRepository.deleteAll() }
    }

    fun clearAllNotes() {
        viewModelScope.launch { noteRepository.deleteAll() }
    }
}

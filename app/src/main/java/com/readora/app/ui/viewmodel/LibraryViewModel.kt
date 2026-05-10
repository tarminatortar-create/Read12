package com.readora.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.readora.app.data.db.ComicEntity
import com.readora.app.data.repository.LibraryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class LibraryViewModel(private val libraryRepository: LibraryRepository) : ViewModel() {
    val searchQuery = MutableStateFlow("")

    val comics: StateFlow<List<ComicEntity>> = libraryRepository.getAllComics()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val filteredComics: StateFlow<List<ComicEntity>> = searchQuery
        .flatMapLatest { query -> libraryRepository.searchComics(query.trim()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

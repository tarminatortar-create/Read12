package com.readora.app.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.readora.app.data.db.UpdateEntity
import com.readora.app.data.repository.UpdatesRepository
import com.readora.app.worker.LibraryUpdatesWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class UpdatesUiState(
    val updates: List<UpdateEntity> = emptyList(),
    val unreadCount: Int = 0,
    val isChecking: Boolean = false,
)

class UpdatesViewModel(
    private val updatesRepository: UpdatesRepository,
) : ViewModel() {

    private val _isChecking = MutableStateFlow(false)

    val uiState: StateFlow<UpdatesUiState> =
        combine(updatesRepository.getAll(), _isChecking) { list, checking ->
            UpdatesUiState(
                updates = list,
                unreadCount = list.count { !it.isRead },
                isChecking = checking,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UpdatesUiState())

    fun checkNow(context: Context) {
        _isChecking.value = true
        LibraryUpdatesWorker.runOnce(context)
        viewModelScope.launch {
            kotlinx.coroutines.delay(3_000)
            _isChecking.value = false
        }
    }

    fun markAllRead() {
        viewModelScope.launch { updatesRepository.markAllRead() }
    }

    fun markAllReadForComic(comicId: String) {
        viewModelScope.launch { updatesRepository.markAllReadForComic(comicId) }
    }

    fun clearAll() {
        viewModelScope.launch { updatesRepository.clearAll() }
    }

    fun markRead(id: Long) {
        viewModelScope.launch { updatesRepository.markRead(id) }
    }

    fun delete(id: Long) {
        viewModelScope.launch { updatesRepository.delete(id) }
    }

    fun deleteAllRead() {
        viewModelScope.launch { updatesRepository.deleteAllRead() }
    }
}


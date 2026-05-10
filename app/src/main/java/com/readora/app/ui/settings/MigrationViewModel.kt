package com.readora.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.readora.app.data.db.ComicEntity
import com.readora.app.data.db.ReadoraDatabase
import com.readora.app.data.db.SourceEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class MigrationState(
    val fromSources: List<SourceEntity> = emptyList(),
    val toSources: List<SourceEntity> = emptyList(),
    val affectedComics: List<ComicEntity> = emptyList(),
    val selectedFromId: String? = null,
    val selectedToId: String? = null,
    val isMigrating: Boolean = false,
    val migratedCount: Int = 0,
    val error: String? = null,
    val isDone: Boolean = false
)

/**
 * Phase 53 — Source Migration ViewModel
 *
 * Manages the state and logic for migrating library comics from one source to another.
 * This is critical when a source dies, changes domains, or is replaced by a better version.
 *
 * Migration approach:
 *   1. User selects "from" source (e.g. dead or old source)
 *   2. User selects "to" source (e.g. replacement source)
 *   3. All ComicEntity rows with sourceId == fromId get sourceId updated to toId
 *   4. ChapterEntity rows are also migrated (marked as needing re-fetch)
 *   5. Library comics are preserved — only sourceId changes, no deletions
 */
class MigrationViewModel(private val database: ReadoraDatabase) : ViewModel() {

    private val _state = MutableStateFlow(MigrationState())
    val state: StateFlow<MigrationState> = _state.asStateFlow()

    init {
        loadSources()
    }

    private fun loadSources() {
        viewModelScope.launch {
            val sources = database.sourceDao().getAll().first()
            _state.value = _state.value.copy(
                fromSources = sources,
                toSources = sources
            )
        }
    }

    fun selectFromSource(sourceId: String) {
        viewModelScope.launch {
            val comics = withContext(Dispatchers.IO) {
                database.comicDao().getBySourceOnce(sourceId)
            }
            _state.value = _state.value.copy(
                selectedFromId = sourceId,
                affectedComics = comics,
                isDone = false,
                migratedCount = 0,
                error = null
            )
        }
    }

    fun selectToSource(sourceId: String) {
        _state.value = _state.value.copy(
            selectedToId = sourceId,
            isDone = false,
            error = null
        )
    }

    fun startMigration() {
        val fromId = _state.value.selectedFromId ?: return
        val toId = _state.value.selectedToId ?: return
        if (fromId == toId) {
            _state.value = _state.value.copy(error = "Source and destination cannot be the same.")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isMigrating = true, error = null)
            try {
                val count = withContext(Dispatchers.IO) {
                    database.comicDao().migrateSource(fromId, toId)
                }
                _state.value = _state.value.copy(
                    isMigrating = false,
                    isDone = true,
                    migratedCount = count
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isMigrating = false,
                    error = e.message ?: "Migration failed"
                )
            }
        }
    }

    fun reset() {
        _state.value = MigrationState(
            fromSources = _state.value.fromSources,
            toSources = _state.value.toSources
        )
    }
}

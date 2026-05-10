package com.readora.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.readora.app.data.db.ComicEntity
import com.readora.app.data.repository.LibraryRepository
import com.readora.app.data.repository.ReadingSessionRepository
import com.readora.app.data.repository.UpdatesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import java.util.concurrent.TimeUnit

class HomeViewModel(
    libraryRepository: LibraryRepository,
    readingSessionRepository: ReadingSessionRepository,
    updatesRepository: UpdatesRepository,
) : ViewModel() {
    val lastRead: StateFlow<List<ComicEntity>> = libraryRepository.getAllComics()
        .map { comics -> comics.filter { it.lastReadAt != null }.sortedByDescending { it.lastReadAt } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val continueReading: StateFlow<List<ComicEntity>> = libraryRepository.getAllComics()
        .map { comics -> comics.filter { it.status.equals("Reading", ignoreCase = true) || it.lastReadAt != null } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Total reading sessions count. */
    val totalSessionCount: StateFlow<Int> = readingSessionRepository.getAll()
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    /** Total reading hours (all time). */
    val totalReadingMinutes: StateFlow<Long> = readingSessionRepository.getAll()
        .map { sessions -> sessions.sumOf { it.durationMs } / 60_000L }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0L)

    /** Comic IDs that have at least one unread update, ordered by most recent first. */
    val recentlyUpdatedComicIds: StateFlow<List<String>> = updatesRepository.getAll()
        .map { updates ->
            updates.filter { !it.isRead }
                .groupBy { it.comicId }
                .entries
                .sortedByDescending { (_, v) -> v.maxOf { it.id } }
                .map { it.key }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Reading minutes completed today. */
    val todayMinutes: StateFlow<Long> = readingSessionRepository.getAll()
        .map { sessions ->
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            val todayStart = cal.timeInMillis
            sessions.filter { it.startedAt >= todayStart }.sumOf { it.durationMs } / 60_000L
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0L)

    /** Number of consecutive days (ending today) with at least one reading session. */
    val readingStreak: StateFlow<Int> = readingSessionRepository.getAll()
        .map { sessions ->
            if (sessions.isEmpty()) return@map 0
            // Collect distinct calendar days (as day-start epoch ms) from session startedAt
            val cal = Calendar.getInstance()
            val activeDays = sessions.map { session ->
                cal.timeInMillis = session.startedAt
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                cal.timeInMillis
            }.toSortedSet(reverseOrder())

            // Walk back from today counting consecutive days
            cal.timeInMillis = System.currentTimeMillis()
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            var streak = 0
            var cursor = cal.timeInMillis
            while (activeDays.contains(cursor)) {
                streak++
                cursor -= TimeUnit.DAYS.toMillis(1)
            }
            streak
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)
}

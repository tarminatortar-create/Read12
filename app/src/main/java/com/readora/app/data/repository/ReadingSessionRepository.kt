package com.readora.app.data.repository

import com.readora.app.data.db.ReadoraDatabase
import com.readora.app.data.db.ReadingSessionEntity
import kotlinx.coroutines.flow.Flow

class ReadingSessionRepository(private val database: ReadoraDatabase) {
    fun getAll(): Flow<List<ReadingSessionEntity>> = database.readingSessionDao().getAll()

    suspend fun add(session: ReadingSessionEntity): Long = database.readingSessionDao().insert(session)

    suspend fun totalDurationSince(since: Long): Long =
        database.readingSessionDao().totalDurationSince(since) ?: 0L

    suspend fun clearAll() = database.readingSessionDao().deleteAll()
}


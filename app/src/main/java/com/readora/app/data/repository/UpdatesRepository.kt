package com.readora.app.data.repository

import com.readora.app.data.db.ReadoraDatabase
import com.readora.app.data.db.UpdateEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UpdatesRepository(private val database: ReadoraDatabase) {
    fun getAll(): Flow<List<UpdateEntity>> = database.updateDao().getAll()

    fun getUnreadCount(): Flow<Int> = database.updateDao().getUnread().map { it.size }

    fun getUnreadCountForComic(comicId: String): Flow<Int> = database.updateDao().getUnreadCountForComic(comicId)

    suspend fun add(update: UpdateEntity): Long = database.updateDao().insert(update)

    suspend fun markRead(id: Long) = database.updateDao().markRead(id)

    suspend fun markAllRead() = database.updateDao().markAllRead()

    suspend fun markAllReadForComic(comicId: String) = database.updateDao().markAllReadForComic(comicId)

    suspend fun clearAll() = database.updateDao().clearAll()

    suspend fun delete(id: Long) = database.updateDao().deleteById(id)
    suspend fun deleteAllRead() = database.updateDao().deleteAllRead()
}


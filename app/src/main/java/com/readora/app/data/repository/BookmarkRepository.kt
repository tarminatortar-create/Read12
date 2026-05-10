package com.readora.app.data.repository

import com.readora.app.data.db.BookmarkEntity
import com.readora.app.data.db.ReadoraDatabase
import kotlinx.coroutines.flow.Flow

class BookmarkRepository(private val database: ReadoraDatabase) {
    fun getAll(): Flow<List<BookmarkEntity>> = database.bookmarkDao().getAll()

    fun getByComic(sourceId: String, comicId: String): Flow<List<BookmarkEntity>> =
        database.bookmarkDao().getByComic(sourceId, comicId)

    suspend fun add(bookmark: BookmarkEntity): Long = database.bookmarkDao().insert(bookmark)

    suspend fun delete(id: Long) = database.bookmarkDao().deleteById(id)

    suspend fun deleteAll() = database.bookmarkDao().deleteAll()
}


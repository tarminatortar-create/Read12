package com.readora.app.data.repository

import com.readora.app.data.db.ChapterNoteEntity
import com.readora.app.data.db.ReadoraDatabase
import kotlinx.coroutines.flow.Flow

class ChapterNoteRepository(private val database: ReadoraDatabase) {
    fun getAll(): Flow<List<ChapterNoteEntity>> = database.chapterNoteDao().getAll()

    fun getByComic(sourceId: String, comicId: String): Flow<List<ChapterNoteEntity>> =
        database.chapterNoteDao().getByComic(sourceId, comicId)

    suspend fun add(note: ChapterNoteEntity): Long = database.chapterNoteDao().insert(note)

    suspend fun delete(id: Long) = database.chapterNoteDao().deleteById(id)

    suspend fun deleteAll() = database.chapterNoteDao().deleteAll()
}


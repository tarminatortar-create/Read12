package com.readora.app.data.repository

import com.readora.app.data.db.ComicEntity
import com.readora.app.data.db.ReadoraDatabase
import kotlinx.coroutines.flow.Flow

class LibraryRepository(private val database: ReadoraDatabase) {
    fun getAllComics(): Flow<List<ComicEntity>> = database.comicDao().getAll()

    suspend fun getComicById(id: String): ComicEntity? = database.comicDao().getById(id)

    suspend fun addComic(comic: ComicEntity): Long = database.comicDao().insert(comic)

    suspend fun removeComic(id: String) {
        database.comicDao().deleteById(id)
        database.progressDao().deleteByComic(id)
    }

    fun searchComics(query: String): Flow<List<ComicEntity>> =
        if (query.isBlank()) getAllComics() else database.comicDao().search(query)
}

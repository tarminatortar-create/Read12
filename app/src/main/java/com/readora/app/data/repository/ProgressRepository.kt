package com.readora.app.data.repository

import com.readora.app.data.db.ProgressEntity
import com.readora.app.data.db.ReadoraDatabase

class ProgressRepository(private val database: ReadoraDatabase) {
    suspend fun saveProgress(comicId: String, chapterId: String, page: Int, total: Int) {
        database.progressDao().upsert(
            ProgressEntity(
                comicId = comicId,
                chapterId = chapterId,
                pageIndex = page.coerceAtLeast(0),
                totalPages = total.coerceAtLeast(1),
                updatedAt = System.currentTimeMillis(),
            ),
        )
    }

    suspend fun getProgress(comicId: String, chapterId: String): ProgressEntity? =
        database.progressDao().getByComicAndChapter(comicId, chapterId)

    suspend fun getLastRead(comicId: String): ProgressEntity? =
        database.progressDao().getLastRead(comicId)
}

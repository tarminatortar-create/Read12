package com.readora.app.data.repository

import com.readora.app.data.db.DownloadJobEntity
import com.readora.app.data.db.ReadoraDatabase
import kotlinx.coroutines.flow.Flow

class DownloadRepository(private val database: ReadoraDatabase) {
    suspend fun enqueue(job: DownloadJobEntity): Long = database.downloadJobDao().insert(job)

    suspend fun updateJob(job: DownloadJobEntity) = database.downloadJobDao().update(job)

    fun getAllJobs(): Flow<List<DownloadJobEntity>> = database.downloadJobDao().getAll()

    suspend fun getJobsByStatus(status: String): List<DownloadJobEntity> =
        database.downloadJobDao().getByStatus(status)
}

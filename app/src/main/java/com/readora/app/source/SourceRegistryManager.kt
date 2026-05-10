package com.readora.app.source

import com.readora.app.data.db.SourceDao
import com.readora.app.data.db.SourceEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class SourceRegistryManager(private val sourceDao: SourceDao) {

    fun getAll(): Flow<List<SourceEntity>> {
        return sourceDao.getAll()
    }

    suspend fun getEnabled(): List<SourceEntity> {
        return sourceDao.getEnabled()
    }

    suspend fun enable(sourceId: String) = withContext(Dispatchers.IO) {
        sourceDao.setEnabled(sourceId, true)
    }

    suspend fun disable(sourceId: String) = withContext(Dispatchers.IO) {
        sourceDao.setEnabled(sourceId, false)
    }

    suspend fun install(sourceDefinition: SourceEntity) = withContext(Dispatchers.IO) {
        sourceDao.insert(sourceDefinition)
    }

    suspend fun remove(sourceId: String) = withContext(Dispatchers.IO) {
        val entity = sourceDao.getById(sourceId)
        if (entity != null) {
            sourceDao.delete(entity)
        }
    }
}

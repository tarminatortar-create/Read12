package com.readora.app.data.repository

import com.readora.app.data.db.ReadoraDatabase
import com.readora.app.data.db.SourceEntity
import kotlinx.coroutines.flow.Flow

class SourceRepository(private val database: ReadoraDatabase) {
    fun getAllSources(): Flow<List<SourceEntity>> = database.sourceDao().getAll()

    suspend fun getEnabledSources(): List<SourceEntity> = database.sourceDao().getEnabled()

    suspend fun saveSource(source: SourceEntity): Long = database.sourceDao().insert(source)

    suspend fun toggleSource(id: String, enabled: Boolean) = database.sourceDao().setEnabled(id, enabled)
}

package com.readora.app.data.repository

import com.readora.app.data.db.MergeGroupEntity
import com.readora.app.data.db.ReadoraDatabase
import kotlinx.coroutines.flow.Flow

class MergeRepository(private val database: ReadoraDatabase) {
    fun getAllGroups(): Flow<List<MergeGroupEntity>> = database.mergeGroupDao().getAll()

    suspend fun saveGroup(group: MergeGroupEntity): Long = database.mergeGroupDao().insert(group)

    suspend fun removeGroup(groupId: String) = database.mergeGroupDao().deleteById(groupId)
}

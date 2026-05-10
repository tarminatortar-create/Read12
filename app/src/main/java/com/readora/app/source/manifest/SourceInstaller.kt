package com.readora.app.source.manifest

import com.readora.app.data.db.SourceEntity
import com.readora.app.source.SourceRegistryManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.readora.app.BuildConfig

class SourceInstaller(
    private val sourceRegistryManager: SourceRegistryManager
) {
    suspend fun install(entry: SourceManifestEntry): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (entry.minAppVersion > BuildConfig.VERSION_CODE) {
                return@withContext Result.failure(IllegalStateException("App version ${BuildConfig.VERSION_CODE} is too old for this source. Minimum required: ${entry.minAppVersion}"))
            }
            val entity = SourceEntity(
                sourceId = entry.id,
                name = entry.name,
                baseUrl = entry.baseUrl,
                enabled = true,
                version = entry.version,
                language = entry.language,
                category = entry.categories.firstOrNull() ?: "General",
                iconUrl = entry.iconUrl,
                lastCheckedAt = System.currentTimeMillis(),
                minAppVersion = entry.minAppVersion,
                parserType = entry.parserType,
                parserDefinitionJson = entry.definitionJson,
                repositoryId = entry.repositoryId,
                trustLevel = entry.trustLevel,
            )
            sourceRegistryManager.install(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun update(sourceId: String, newEntry: SourceManifestEntry): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (newEntry.minAppVersion > BuildConfig.VERSION_CODE) {
                return@withContext Result.failure(IllegalStateException("App version ${BuildConfig.VERSION_CODE} is too old for this source update. Minimum required: ${newEntry.minAppVersion}"))
            }
            // Updating a source generally updates fields but preserves the ID
            val entity = SourceEntity(
                sourceId = newEntry.id,
                name = newEntry.name,
                baseUrl = newEntry.baseUrl,
                enabled = true,
                version = newEntry.version,
                language = newEntry.language,
                category = newEntry.categories.firstOrNull() ?: "General",
                iconUrl = newEntry.iconUrl,
                lastCheckedAt = System.currentTimeMillis(),
                minAppVersion = newEntry.minAppVersion,
                parserType = newEntry.parserType,
                parserDefinitionJson = newEntry.definitionJson,
                repositoryId = newEntry.repositoryId,
                trustLevel = newEntry.trustLevel,
            )
            sourceRegistryManager.install(entity) // Uses ON CONFLICT REPLACE
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uninstall(sourceId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            sourceRegistryManager.remove(sourceId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

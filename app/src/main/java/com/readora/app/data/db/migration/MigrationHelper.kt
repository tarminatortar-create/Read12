package com.readora.app.data.db.migration

import android.content.Context
import com.readora.app.data.db.ChapterEntity
import com.readora.app.data.db.ComicEntity
import com.readora.app.data.db.DownloadJobEntity
import com.readora.app.data.db.MergeGroupEntity
import com.readora.app.data.db.ProgressEntity
import com.readora.app.data.db.ReadoraDatabase
import com.readora.app.data.db.SourceEntity
import com.readora.app.source.SourceKind
import com.readora.app.source.SourceRegistry
import com.readora.app.storage.DownloadQueueManager
import com.readora.app.storage.QueueStatus
import com.readora.app.storage.ReadoraPreferences
import org.json.JSONObject

class MigrationHelper(
    context: Context,
    val database: ReadoraDatabase,
) {
    val appContext = context.applicationContext
    val prefs = appContext.getSharedPreferences(STATE_PREFS, Context.MODE_PRIVATE)
    val preferences = ReadoraPreferences(appContext)
    val downloadQueue = DownloadQueueManager(appContext)

    suspend fun migrateIfNeeded() {
        if (prefs.getBoolean(KEY_MIGRATION_DONE, false)) return

        migrateSources()
        migrateOnlineLibrary()
        migrateLocalLibrary()
        migrateMergeGroups()
        migrateProgress()
        migrateDownloadJobs()

        prefs.edit().putBoolean(KEY_MIGRATION_DONE, true).apply()
    }

    private suspend fun migrateSources() {
        SourceRegistry.builtInSources.forEach { source ->
            database.sourceDao().insert(
                SourceEntity(
                    sourceId = source.id,
                    name = source.name,
                    baseUrl = source.website,
                    enabled = source.installed || source.kind == SourceKind.BuiltIn,
                    version = source.version,
                    language = source.language,
                    category = source.kind.label,
                    iconUrl = null,
                    lastCheckedAt = System.currentTimeMillis(),
                    minAppVersion = 1,
                ),
            )
        }
        preferences.loadRepositories().forEach { repository ->
            database.sourceDao().insert(
                SourceEntity(
                    sourceId = repository.id,
                    name = repository.name,
                    baseUrl = repository.url,
                    enabled = repository.trusted,
                    version = "repository",
                    language = "Multi",
                    category = "Repository",
                    iconUrl = null,
                    lastCheckedAt = System.currentTimeMillis(),
                    minAppVersion = 1,
                ),
            )
        }
    }

    private suspend fun migrateOnlineLibrary() {
        val lastRead = preferences.loadLastOnlineRead()
        preferences.loadOnlineLibrary().forEach { saved ->
            val comicId = onlineComicId(saved.sourceId, saved.id)
            database.comicDao().insert(
                ComicEntity(
                    id = comicId,
                    title = saved.title,
                    coverUrl = saved.coverUrl,
                    sourceId = saved.sourceId,
                    addedAt = saved.addedAt,
                    lastReadAt = lastRead
                        ?.takeIf { it.sourceId == saved.sourceId && it.comicId == saved.id }
                        ?.updatedAt,
                    status = saved.status.ifBlank { "Reading" },
                    tags = saved.tags,
                    isLocal = false,
                ),
            )
            if (saved.lastChapterNumber.isNotBlank() || saved.lastChapterTitle.isNotBlank()) {
                database.chapterDao().insert(
                    ChapterEntity(
                        id = chapterId(saved.sourceId, saved.id, saved.lastChapterNumber.ifBlank { saved.lastChapterTitle }),
                        comicId = comicId,
                        sourceId = saved.sourceId,
                        number = saved.lastChapterNumber,
                        title = saved.lastChapterTitle.ifBlank { "Latest chapter" },
                        url = "",
                        readAt = null,
                        isDownloaded = false,
                        downloadPath = null,
                    ),
                )
            }
        }
        if (lastRead != null) {
            database.comicDao().insert(
                ComicEntity(
                    id = onlineComicId(lastRead.sourceId, lastRead.comicId),
                    title = lastRead.comicTitle,
                    coverUrl = lastRead.coverUrl,
                    sourceId = lastRead.sourceId,
                    addedAt = lastRead.updatedAt,
                    lastReadAt = lastRead.updatedAt,
                    status = "Reading",
                    tags = emptyList(),
                    isLocal = false,
                ),
            )
        }
    }

    private suspend fun migrateLocalLibrary() {
        preferences.loadLocalLibrary().forEach { local ->
            database.comicDao().insert(
                ComicEntity(
                    id = localComicId(local.uri),
                    title = local.title,
                    coverUrl = null,
                    sourceId = LOCAL_SOURCE_ID,
                    addedAt = local.importedAt,
                    lastReadAt = null,
                    status = "Local",
                    tags = listOf(local.type),
                    isLocal = true,
                ),
            )
        }
    }

    private suspend fun migrateMergeGroups() {
        preferences.loadMergeGroups().forEachIndexed { index, group ->
            database.mergeGroupDao().insert(
                MergeGroupEntity(
                    groupId = group.id,
                    primaryComicId = group.itemKeys.firstOrNull().orEmpty(),
                    title = group.title,
                    sourceIds = group.itemKeys,
                    priority = 1_000 - index,
                ),
            )
        }
    }

    private suspend fun migrateProgress() {
        prefs.all.forEach { (key, value) ->
            if (!key.startsWith(PROGRESS_PREFIX) || value !is String) return@forEach
            runCatching {
                val item = JSONObject(value)
                database.progressDao().upsert(
                    ProgressEntity(
                        comicId = onlineComicId(item.optString("sourceId"), item.optString("comicId")),
                        chapterId = chapterId(
                            item.optString("sourceId"),
                            item.optString("comicId"),
                            item.optString("chapterId"),
                        ),
                        pageIndex = item.optInt("currentPage", 1).coerceAtLeast(1) - 1,
                        totalPages = item.optInt("totalPages", 1).coerceAtLeast(1),
                        updatedAt = item.optLong("updatedAt", System.currentTimeMillis()),
                    ),
                )
            }
        }
    }

    private suspend fun migrateDownloadJobs() {
        downloadQueue.load().forEach { job ->
            database.downloadJobDao().insert(
                DownloadJobEntity(
                    jobId = job.id,
                    comicId = onlineComicId(job.sourceId, job.comicId),
                    chapterId = chapterId(job.sourceId, job.comicId, job.chapterId),
                    sourceId = job.sourceId,
                    status = job.status,
                    queuedAt = job.enqueuedAt,
                    completedAt = job.updatedAt.takeIf { job.status == QueueStatus.Done.value },
                    pagesFailed = if (job.status == QueueStatus.Failed.value) 1 else 0,
                ),
            )
        }
    }

    fun onlineComicId(sourceId: String, comicId: String): String = "$sourceId:$comicId"

    fun localComicId(uri: String): String = "local:${uri.hashCode()}"

    fun chapterId(sourceId: String, comicId: String, chapterId: String): String =
        "$sourceId:$comicId:$chapterId"

    private companion object {
        const val STATE_PREFS = "readora_state"
        const val KEY_MIGRATION_DONE = "room_migration_done_v1"
        const val PROGRESS_PREFIX = "progress_"
        const val LOCAL_SOURCE_ID = "local-cbz"
    }
}

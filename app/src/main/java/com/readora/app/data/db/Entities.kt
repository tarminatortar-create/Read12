package com.readora.app.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "comics",
    indices = [Index(value = ["sourceId", "id"], unique = true), Index(value = ["title"])],
)
data class ComicEntity(
    @PrimaryKey val id: String,
    val title: String,
    val coverUrl: String?,
    val sourceId: String,
    val addedAt: Long,
    val lastReadAt: Long?,
    val status: String,
    val tags: List<String>,
    val isLocal: Boolean,
)

@Entity(
    tableName = "chapters",
    indices = [Index(value = ["comicId"]), Index(value = ["sourceId", "comicId", "id"], unique = true)],
)
data class ChapterEntity(
    @PrimaryKey val id: String,
    val comicId: String,
    val sourceId: String,
    val number: String,
    val title: String,
    val url: String,
    val readAt: Long?,
    val isDownloaded: Boolean,
    val downloadPath: String?,
)

@Entity(
    tableName = "progress",
    primaryKeys = ["comicId", "chapterId"],
    indices = [Index(value = ["comicId"]), Index(value = ["chapterId"])],
)
data class ProgressEntity(
    val comicId: String,
    val chapterId: String,
    val pageIndex: Int,
    val totalPages: Int,
    val updatedAt: Long,
)

@Entity(
    tableName = "download_jobs",
    indices = [Index(value = ["comicId"]), Index(value = ["status"])],
)
data class DownloadJobEntity(
    @PrimaryKey val jobId: String,
    val comicId: String,
    val chapterId: String,
    val sourceId: String,
    val status: String,
    val queuedAt: Long,
    val completedAt: Long?,
    val pagesFailed: Int,
)

@Entity(tableName = "merge_groups")
data class MergeGroupEntity(
    @PrimaryKey val groupId: String,
    val primaryComicId: String,
    val title: String,
    val sourceIds: List<String>,
    val priority: Int,
)

@Entity(tableName = "sources")
data class SourceEntity(
    @PrimaryKey val sourceId: String,
    val name: String,
    val baseUrl: String,
    val enabled: Boolean,
    val version: String,
    val language: String,
    val category: String,
    val iconUrl: String?,
    val lastCheckedAt: Long?,
    val minAppVersion: Int,
    val parserType: String? = null,
    val parserDefinitionJson: String? = null,
    val repositoryId: String? = null,
    val trustLevel: String? = null,
)

@Entity(
    tableName = "updates",
    indices = [
        Index(value = ["sourceId", "comicId", "chapterId"], unique = true),
        Index(value = ["foundAt"]),
        Index(value = ["isRead"]),
    ],
)
data class UpdateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sourceId: String,
    val sourceName: String,
    val comicId: String,
    val comicTitle: String,
    val coverUrl: String?,
    val chapterId: String,
    val chapterNumber: String,
    val chapterTitle: String,
    val foundAt: Long,
    val isRead: Boolean,
)

@Entity(
    tableName = "bookmarks",
    indices = [
        Index(value = ["sourceId", "comicId", "chapterId", "pageIndex"], unique = true),
        Index(value = ["sourceId", "comicId"]),
        Index(value = ["createdAt"]),
    ],
)
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sourceId: String,
    val comicId: String,
    val comicTitle: String,
    val chapterId: String,
    val chapterNumber: String,
    val chapterTitle: String,
    val pageIndex: Int,
    val note: String?,
    val createdAt: Long,
)

@Entity(
    tableName = "chapter_notes",
    indices = [
        Index(value = ["sourceId", "comicId", "chapterId"]),
        Index(value = ["createdAt"]),
    ],
)
data class ChapterNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sourceId: String,
    val comicId: String,
    val chapterId: String,
    val chapterNumber: String,
    val chapterTitle: String,
    val pageIndex: Int?,
    val content: String,
    val createdAt: Long,
)

@Entity(
    tableName = "reading_sessions",
    indices = [
        Index(value = ["sourceId", "comicId"]),
        Index(value = ["startedAt"]),
    ],
)
data class ReadingSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sourceId: String,
    val comicId: String,
    val comicTitle: String,
    val chapterId: String?,
    val chapterNumber: String?,
    val startedAt: Long,
    val endedAt: Long,
    val durationMs: Long,
)

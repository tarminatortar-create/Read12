package com.readora.app.storage

import android.content.Context
import com.readora.app.source.OnlineChapter
import com.readora.app.source.OnlineComicSummary
import org.json.JSONArray
import org.json.JSONObject

class DownloadQueueManager(context: Context) {
    val prefs = context.applicationContext.getSharedPreferences("readora_download_queue", Context.MODE_PRIVATE)

    fun load(): List<QueuedDownload> {
        val raw = prefs.getString(KEY_QUEUE, null) ?: return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (index in 0 until array.length()) {
                    val item = array.optJSONObject(index) ?: continue
                    add(
                        QueuedDownload(
                            id = item.optString("id"),
                            sourceId = item.optString("sourceId"),
                            comicId = item.optString("comicId"),
                            sourceName = item.optString("sourceName"),
                            comicTitle = item.optString("comicTitle"),
                            coverUrl = item.optString("coverUrl").ifBlank { null },
                            description = item.optString("description"),
                            status = item.optString("status", QueueStatus.Queued.value),
                            tags = item.optJSONArray("tags")?.let { tags ->
                                buildList {
                                    for (tagIndex in 0 until tags.length()) add(tags.optString(tagIndex))
                                }
                            }.orEmpty(),
                            chapterId = item.optString("chapterId"),
                            chapterNumber = item.optString("chapterNumber"),
                            chapterTitle = item.optString("chapterTitle"),
                            progress = item.optInt("progress"),
                            totalPages = item.optInt("totalPages"),
                            error = item.optString("error").ifBlank { null },
                            enqueuedAt = item.optLong("enqueuedAt"),
                            updatedAt = item.optLong("updatedAt"),
                        ),
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    fun enqueue(comic: OnlineComicSummary, chapter: OnlineChapter): List<QueuedDownload> {
        val id = idFor(comic.sourceId, comic.id, chapter.id)
        val existing = load().filterNot { it.id == id }
        val job = QueuedDownload(
            id = id,
            sourceId = comic.sourceId,
            comicId = comic.id,
            sourceName = comic.sourceName,
            comicTitle = comic.title,
            coverUrl = comic.coverUrl,
            description = comic.description,
            status = QueueStatus.Queued.value,
            tags = comic.tags,
            chapterId = chapter.id,
            chapterNumber = chapter.number,
            chapterTitle = chapter.title,
            progress = 0,
            totalPages = chapter.pages,
            error = null,
            enqueuedAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
        )
        val updated = (listOf(job) + existing).take(200)
        save(updated)
        return updated
    }

    fun update(job: QueuedDownload): List<QueuedDownload> {
        val updated = load().map {
            if (it.id == job.id) job.copy(updatedAt = System.currentTimeMillis()) else it
        }
        save(updated)
        return updated
    }

    fun remove(jobId: String): List<QueuedDownload> {
        val updated = load().filterNot { it.id == jobId }
        save(updated)
        return updated
    }

    fun clearFinished(): List<QueuedDownload> {
        val updated = load().filterNot { it.status == QueueStatus.Done.value }
        save(updated)
        return updated
    }

    fun clearAll(): List<QueuedDownload> {
        save(emptyList())
        return emptyList()
    }

    fun save(queue: List<QueuedDownload>) {
        val array = JSONArray()
        queue.forEach { job ->
            val tags = JSONArray()
            job.tags.forEach { tags.put(it) }
            array.put(
                JSONObject()
                    .put("id", job.id)
                    .put("sourceId", job.sourceId)
                    .put("comicId", job.comicId)
                    .put("sourceName", job.sourceName)
                    .put("comicTitle", job.comicTitle)
                    .put("coverUrl", job.coverUrl)
                    .put("description", job.description)
                    .put("status", job.status)
                    .put("tags", tags)
                    .put("chapterId", job.chapterId)
                    .put("chapterNumber", job.chapterNumber)
                    .put("chapterTitle", job.chapterTitle)
                    .put("progress", job.progress)
                    .put("totalPages", job.totalPages)
                    .put("error", job.error)
                    .put("enqueuedAt", job.enqueuedAt)
                    .put("updatedAt", job.updatedAt),
            )
        }
        prefs.edit().putString(KEY_QUEUE, array.toString()).apply()
    }

    fun idFor(sourceId: String, comicId: String, chapterId: String): String = "$sourceId:$comicId:$chapterId"

    private companion object {
        const val KEY_QUEUE = "queue"
    }
}

data class QueuedDownload(
    val id: String,
    val sourceId: String,
    val comicId: String,
    val sourceName: String,
    val comicTitle: String,
    val coverUrl: String?,
    val description: String,
    val status: String,
    val tags: List<String>,
    val chapterId: String,
    val chapterNumber: String,
    val chapterTitle: String,
    val progress: Int,
    val totalPages: Int,
    val error: String?,
    val enqueuedAt: Long,
    val updatedAt: Long,
) {
    val fraction: Float get() = if (totalPages <= 0) 0f else progress.toFloat() / totalPages.toFloat()

    fun toOnlineSummary(): OnlineComicSummary = OnlineComicSummary(
        id = comicId,
        sourceId = sourceId,
        sourceName = sourceName,
        title = comicTitle,
        description = description,
        coverUrl = coverUrl,
        tags = tags,
        status = "",
    )

    fun toOnlineChapter(): OnlineChapter = OnlineChapter(
        id = chapterId,
        number = chapterNumber,
        title = chapterTitle,
        pages = totalPages,
        readableAt = "",
    )
}

enum class QueueStatus(val value: String) {
    Queued("Queued"),
    Running("Running"),
    Done("Done"),
    Failed("Failed"),
    Paused("Paused"),
}

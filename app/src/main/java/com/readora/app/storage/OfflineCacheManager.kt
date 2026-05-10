package com.readora.app.storage

import android.content.Context
import com.readora.app.source.OnlineChapter
import com.readora.app.source.OnlineComicSummary
import com.readora.app.source.OnlinePage
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

class OfflineCacheManager(context: Context) {
    val root = File(context.applicationContext.filesDir, "offline_chapters")

    fun isChapterCached(sourceId: String, comicId: String, chapterId: String): Boolean {
        val dir = chapterDir(sourceId, comicId, chapterId)
        return File(dir, "meta.json").exists() && dir.listFiles { file -> file.name.startsWith("page_") }?.isNotEmpty() == true
    }

    fun cachedPages(sourceId: String, comicId: String, chapterId: String): List<OnlinePage> {
        val dir = chapterDir(sourceId, comicId, chapterId)
        return dir.listFiles { file -> file.name.startsWith("page_") }
            ?.sortedBy { file -> file.name.substringAfter("page_").substringBefore('.').toIntOrNull() ?: 0 }
            ?.mapIndexed { index, file -> OnlinePage(index + 1, file.toURI().toString()) }
            .orEmpty()
    }

    fun cacheChapter(
        comic: OnlineComicSummary,
        chapter: OnlineChapter,
        pages: List<OnlinePage>,
        onProgress: (current: Int, total: Int) -> Unit = { _, _ -> },
    ): Int {
        val dir = chapterDir(comic.sourceId, comic.id, chapter.id).apply { mkdirs() }
        pages.forEachIndexed { index, page ->
            val target = File(dir, "page_${page.index}.${extension(page.url)}")
            if (!target.exists() || target.length() == 0L) {
                download(page.url, target)
            }
            onProgress(index + 1, pages.size)
        }
        File(dir, "meta.json").writeText(
            JSONObject()
                .put("sourceId", comic.sourceId)
                .put("comicId", comic.id)
                .put("comicTitle", comic.title)
                .put("chapterId", chapter.id)
                .put("chapterNumber", chapter.number)
                .put("chapterTitle", chapter.title)
                .put("pageCount", pages.size)
                .put("cachedAt", System.currentTimeMillis())
                .toString(),
        )
        return pages.size
    }

    fun deleteChapter(sourceId: String, comicId: String, chapterId: String) {
        chapterDir(sourceId, comicId, chapterId).deleteRecursively()
    }

    fun cacheSizeBytes(): Long = root.walkTopDown().filter { it.isFile }.sumOf { it.length() }

    fun clearAll() {
        root.deleteRecursively()
        root.mkdirs()
    }

    fun chapterDir(sourceId: String, comicId: String, chapterId: String): File =
        File(root, "${safe(sourceId)}/${safe(comicId)}/${safe(chapterId)}")

    fun safe(value: String): String = value.replace(Regex("[^A-Za-z0-9._-]"), "_")

    fun extension(url: String): String = when (url.substringBefore('?').substringAfterLast('.', "jpg").lowercase()) {
        "png" -> "png"
        "webp" -> "webp"
        "gif" -> "gif"
        else -> "jpg"
    }

    fun download(url: String, target: File) {
        val temp = File(target.parentFile, target.name + ".tmp")
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            connectTimeout = 15_000
            readTimeout = 30_000
            requestMethod = "GET"
            setRequestProperty("User-Agent", "Readora/0.1 offline cache")
        }
        val stream = if (connection.responseCode in 200..299) connection.inputStream else connection.errorStream
        if (connection.responseCode !in 200..299) {
            val message = stream.bufferedReader().use { it.readText() }
            throw IllegalStateException("${connection.responseCode}: $message")
        }
        stream.use { input -> temp.outputStream().use { output -> input.copyTo(output) } }
        if (target.exists()) target.delete()
        temp.renameTo(target)
    }
}

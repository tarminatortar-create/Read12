package com.readora.app.storage

import android.content.Context
import android.net.Uri
import com.readora.app.source.OnlinePage
import java.io.File
import java.util.zip.ZipFile

class LocalArchiveReader(context: Context) {
    val appContext = context.applicationContext
    val root = File(appContext.cacheDir, "local_reader_pages")

    fun pagesFor(local: SavedLocalComic): List<OnlinePage> {
        val uri = Uri.parse(local.uri)
        return when {
            local.type.contains("CBZ", ignoreCase = true) || local.title.endsWith(".zip", true) || local.title.endsWith(".cbz", true) -> extractZip(local, uri)
            else -> listOf(OnlinePage(1, local.uri))
        }
    }

    fun extractZip(local: SavedLocalComic, uri: Uri): List<OnlinePage> {
        val dir = File(root, safe(local.uri)).apply { mkdirs() }
        val existing = dir.listFiles { file -> file.isFile && isImage(file.name) }
            ?.sortedWith(naturalFileOrder())
            ?.mapIndexed { index, file -> OnlinePage(index + 1, file.toURI().toString()) }
            .orEmpty()
        if (existing.isNotEmpty()) return existing

        val tempZip = File(root, "${safe(local.uri)}.zip.tmp")
        appContext.contentResolver.openInputStream(uri)?.use { input ->
            tempZip.outputStream().use { output -> input.copyTo(output) }
        }

        ZipFile(tempZip).use { zip ->
            zip.entries().asSequence()
                .filter { entry -> !entry.isDirectory && isImage(entry.name.substringAfterLast('/')) }
                .sortedWith(compareBy { entry -> naturalSortKey(entry.name) })
                .forEachIndexed { index, entry ->
                    val name = entry.name.substringAfterLast('/').ifBlank { "page_${index + 1}.jpg" }
                    val target = File(dir, "page_${(index + 1).toString().padStart(5, '0')}_${safe(name)}")
                    zip.getInputStream(entry).use { input -> target.outputStream().use { output -> input.copyTo(output) } }
                }
        }
        tempZip.delete()

        return dir.listFiles { file -> file.isFile && isImage(file.name) }
            ?.sortedWith(naturalFileOrder())
            ?.mapIndexed { index, file -> OnlinePage(index + 1, file.toURI().toString()) }
            .orEmpty()
    }

    fun cachedPageCount(local: SavedLocalComic): Int {
        val dir = File(root, safe(local.uri))
        return dir.listFiles { file -> file.isFile && isImage(file.name) }?.size ?: 0
    }

    fun naturalFileOrder(): Comparator<File> = compareBy { file -> naturalSortKey(file.name) }

    fun naturalSortKey(value: String): String {
        val parts = Regex("\\d+|\\D+").findAll(value.lowercase()).map { it.value }.toList()
        return parts.joinToString("") { part ->
            if (part.all { it.isDigit() }) part.padStart(10, '0') else part
        }
    }

    fun isImage(name: String): Boolean {
        val lower = name.lowercase()
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") || lower.endsWith(".webp")
    }

    fun safe(value: String): String = value.replace(Regex("[^A-Za-z0-9._-]"), "_")
}

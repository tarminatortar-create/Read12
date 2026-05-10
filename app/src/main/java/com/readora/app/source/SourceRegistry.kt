package com.readora.app.source

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

enum class SourceKind(val label: String) {
    BuiltIn("Built-in"),
    Repository("Repository"),
    Local("Local import"),
}

data class SourceDescriptor(
    val id: String,
    val name: String,
    val language: String,
    val kind: SourceKind,
    val version: String,
    val website: String,
    val installed: Boolean,
    val trusted: Boolean,
    val description: String,
)

data class SourceRepository(
    val id: String,
    val name: String,
    val url: String,
    val trusted: Boolean,
    val sourceCount: Int,
    val description: String,
)

object SourceRegistry {
    val builtInSources = listOf(
        SourceDescriptor(
            id = MangaDexSource.id,
            name = MangaDexSource.name,
            language = MangaDexSource.lang,
            kind = SourceKind.BuiltIn,
            version = "0.1.0",
            website = "https://mangadex.org",
            installed = true,
            trusted = true,
            description = "Official API connector for search, details, chapters, covers, and image pages.",
        ),
        SourceDescriptor(
            id = "local-cbz",
            name = "Local CBZ / ZIP",
            language = "Any",
            kind = SourceKind.Local,
            version = "planned",
            website = "device storage",
            installed = false,
            trusted = true,
            description = "Planned local import source for CBZ, ZIP, image folders, and offline-first collections.",
        ),
    )

    val recommendedRepositories = listOf(
        SourceRepository(
            id = "readora-official",
            name = "Readora Official Sources",
            url = "https://example.com/readora/sources.json",
            trusted = true,
            sourceCount = 1,
            description = "Manifest slot for official/API-based connectors. Replace with your own repo URL when hosting source manifests.",
        ),
        SourceRepository(
            id = "user-installed",
            name = "User-installed repositories",
            url = "Custom HTTPS manifest",
            trusted = false,
            sourceCount = 0,
            description = "External repos are listed separately so the app can show trust, version, and update warnings before enabling sources.",
        ),
    )
}

object SourceRepositoryClient {
    suspend fun fetchRepository(url: String): SourceRepository = withContext(Dispatchers.IO) {
        val json = requestJson(url)
        val sources = json.optJSONArray("sources") ?: JSONArray()
        SourceRepository(
            id = json.optString("id").ifBlank { url.hashCode().toString() },
            name = json.optString("name").ifBlank { "Custom Source Repository" },
            url = url,
            trusted = json.optBoolean("trusted", false),
            sourceCount = sources.length(),
            description = json.optString("description").ifBlank { "External source manifest" },
        )
    }

    fun requestJson(url: String): JSONObject {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            connectTimeout = 15_000
            readTimeout = 20_000
            requestMethod = "GET"
            setRequestProperty("Accept", "application/json")
            setRequestProperty("User-Agent", "Readora/0.1 Android source repository client")
        }
        val stream = if (connection.responseCode in 200..299) connection.inputStream else connection.errorStream
        val body = BufferedReader(InputStreamReader(stream)).use { reader -> reader.readText() }
        if (connection.responseCode !in 200..299) {
            throw IllegalStateException("${connection.responseCode}: $body")
        }
        return JSONObject(body)
    }
}

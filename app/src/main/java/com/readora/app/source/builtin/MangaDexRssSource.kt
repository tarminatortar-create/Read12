package com.readora.app.source.builtin

import com.readora.app.source.OnlineChapter
import com.readora.app.source.OnlineComicDetails
import com.readora.app.source.OnlineComicSummary
import com.readora.app.source.api.OnlineSource
import com.readora.app.source.api.SourceCapability
import com.readora.app.source.api.SourceFilters
import com.readora.app.source.api.SourceHealth
import com.readora.app.source.api.SourcePage
import com.readora.app.source.network.SourceHttpClient
import com.readora.app.source.parser.ParserEngineImpl
import com.readora.app.source.parser.ParserType
import com.readora.app.source.parser.ParseResult
import com.readora.app.source.parser.RssDefinition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request

/**
 * Phase 50 — Additional Built-In Source 3 (RSS)
 *
 * Demonstrates the RSS parser against a public, openly accessible RSS feed.
 * Uses MangaDex's own RSS feed for new chapters.
 * Shows that zero-code new sources work once the RssSourceAdapter is ready.
 */
object MangaDexRssStubSource : OnlineSource {

    private val definition = RssDefinition(
        feedUrl = "https://mangadex.org/rss/follows"
    )

    override val id: String = "mangadex_rss_stub"
    override val name: String = "MangaDex RSS"
    override val lang: String = "en"
    override val baseUrl: String = "https://mangadex.org"
    override val version: Int = 1
    override val capabilities: Set<SourceCapability> = setOf(SourceCapability.Chapters)

    private suspend fun fetchFeed(url: String): String = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "Readora/0.1 Android source engine")
            .build()
        val response = SourceHttpClient.client.newCall(request).execute()
        if (!response.isSuccessful) throw Exception("HTTP ${response.code}")
        response.body?.string() ?: throw Exception("Empty body")
    }

    override suspend fun getPopular(page: Int): List<OnlineComicSummary> {
        // RSS sources surface latest, not popular
        return getLatest(page)
    }

    override suspend fun getLatest(page: Int): List<OnlineComicSummary> = withContext(Dispatchers.IO) {
        val xml = fetchFeed(definition.feedUrl)
        val result = ParserEngineImpl.parse(ParserType.RSS, definition, xml)
        when (result) {
            is ParseResult.Success -> result.items.map { item ->
                OnlineComicSummary(
                    id = item["guid"] ?: item["link"] ?: "",
                    title = item["title"] ?: "Untitled",
                    coverUrl = null,
                    sourceId = id,
                    sourceName = name,
                    description = item["description"] ?: "",
                    tags = emptyList(),
                    status = "Ongoing"
                )
            }
            is ParseResult.Error -> throw Exception(result.message, result.cause)
        }
    }

    override suspend fun search(query: String, filters: SourceFilters, page: Int): List<OnlineComicSummary> {
        // RSS doesn't support server-side search; do client-side filtering
        return getLatest(page).filter { it.title.contains(query, ignoreCase = true) }
    }

    override suspend fun getDetails(comicId: String): OnlineComicDetails {
        val summary = OnlineComicSummary(
            id = comicId,
            title = comicId,
            coverUrl = null,
            sourceId = id,
            sourceName = name,
            description = "Open in browser for full details.",
            tags = emptyList(),
            status = "Unknown"
        )
        return OnlineComicDetails(summary = summary, chapters = emptyList())
    }

    override suspend fun getChapterList(comicId: String): List<OnlineChapter> = emptyList()

    override suspend fun getPageList(chapterId: String): List<SourcePage> = emptyList()

    override suspend fun checkHealth(): SourceHealth {
        return try {
            val start = System.currentTimeMillis()
            val request = Request.Builder()
                .url("https://mangadex.org")
                .header("User-Agent", "Readora/0.1")
                .build()
            val response = SourceHttpClient.client.newCall(request).execute()
            SourceHealth(
                isOnline = response.isSuccessful,
                latencyMs = System.currentTimeMillis() - start,
                message = if (response.isSuccessful) null else "HTTP ${response.code}"
            )
        } catch (e: Exception) {
            SourceHealth(false, 0, e.message)
        }
    }
}

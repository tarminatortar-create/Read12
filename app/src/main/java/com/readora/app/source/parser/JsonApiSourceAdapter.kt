package com.readora.app.source.parser

import com.readora.app.source.OnlineChapter
import com.readora.app.source.OnlineComicDetails
import com.readora.app.source.OnlineComicSummary
import com.readora.app.source.api.OnlineSource
import com.readora.app.source.api.SourceCapability
import com.readora.app.source.api.SourceFilters
import com.readora.app.source.api.SourceHealth
import com.readora.app.source.api.SourcePage
import com.readora.app.source.network.SourceHttpClient
import okhttp3.Request

class JsonApiSourceAdapter(
    private val definition: JsonApiDefinition,
    private val httpClient: SourceHttpClient,
    private val parserEngine: ParserEngine,
    override val id: String,
    override val name: String,
    override val baseUrl: String,
    override val lang: String,
    override val version: Int
) : OnlineSource {

    override val capabilities: Set<SourceCapability> = setOf(
        SourceCapability.Search, 
        SourceCapability.Chapters, 
        SourceCapability.PageList
    )

    private fun buildRequest(url: String): Request {
        val builder = Request.Builder().url(url)
        definition.headers.forEach { (key, value) ->
            builder.addHeader(key, value)
        }
        return builder.build()
    }

    private fun fetchAndParse(url: String, listPath: String): List<Map<String, String>> {
        val request = buildRequest(url)
        val response = httpClient.client.newCall(request).execute()
        if (!response.isSuccessful) throw Exception("HTTP ${response.code}")
        val bodyString = response.body?.string() ?: throw Exception("Empty body")
        
        val result = parserEngine.parse(ParserType.JSON_API, definition, bodyString, listPath)
        return when (result) {
            is ParseResult.Success -> result.items
            is ParseResult.Error -> throw Exception(result.message, result.cause)
        }
    }

    override suspend fun getPopular(page: Int): List<OnlineComicSummary> {
        val url = baseUrl + definition.popularEndpoint.replace("{page}", page.toString())
        val items = fetchAndParse(url, definition.popularListPath)
        
        return items.map { item ->
            OnlineComicSummary(
                id = item[definition.popularIdPath] ?: "",
                title = item[definition.popularTitlePath] ?: "Unknown",
                coverUrl = item[definition.popularCoverPath] ?: "",
                sourceId = this.id,
                sourceName = this.name,
                description = "",
                tags = emptyList(),
                status = "Ongoing"
            )
        }
    }

    override suspend fun getLatest(page: Int): List<OnlineComicSummary> {
        return getPopular(page)
    }

    override suspend fun search(query: String, filters: SourceFilters, page: Int): List<OnlineComicSummary> {
        val url = baseUrl + definition.searchEndpoint
            .replace("{query}", query)
            .replace("{page}", page.toString())
            
        val items = fetchAndParse(url, definition.searchListPath)
        return items.map { item ->
            OnlineComicSummary(
                id = item[definition.searchIdPath] ?: "",
                title = item[definition.searchTitlePath] ?: "Unknown",
                coverUrl = item[definition.searchCoverPath] ?: "",
                sourceId = this.id,
                sourceName = this.name,
                description = "",
                tags = emptyList(),
                status = "Ongoing"
            )
        }
    }

    override suspend fun getDetails(comicId: String): OnlineComicDetails {
        val url = baseUrl + definition.detailEndpoint.replace("{id}", comicId)
        val items = fetchAndParse(url, "$")
        
        val item = items.firstOrNull() ?: throw Exception("Details not found")
        
        val summary = OnlineComicSummary(
            id = comicId,
            title = item[definition.detailTitlePath] ?: "",
            sourceId = this.id,
            sourceName = this.name,
            description = item[definition.detailDescriptionPath] ?: "",
            status = item[definition.detailStatusPath] ?: "",
            coverUrl = item[definition.detailCoverPath] ?: "",
            tags = item[definition.detailTagsPath]?.split(",")?.map { it.trim() } ?: emptyList()
        )
        
        val chapters = getChapterList(comicId)
        return OnlineComicDetails(summary = summary, chapters = chapters)
    }

    override suspend fun getChapterList(comicId: String): List<OnlineChapter> {
        val url = baseUrl + definition.chaptersEndpoint.replace("{id}", comicId)
        val items = fetchAndParse(url, definition.chaptersListPath)
        
        return items.map { item ->
            OnlineChapter(
                id = item[definition.chaptersIdPath] ?: "",
                number = item[definition.chaptersNumberPath] ?: "-1",
                title = item[definition.chaptersTitlePath] ?: "",
                pages = 0,
                readableAt = item[definition.chaptersDatePath] ?: ""
            )
        }
    }

    override suspend fun getPageList(chapterId: String): List<SourcePage> {
        val url = baseUrl + definition.pagesEndpoint.replace("{id}", chapterId)
        val items = fetchAndParse(url, definition.pagesListPath)
        
        return items.mapIndexedNotNull { index, item -> 
            val imgUrl = item[definition.pagesUrlPath] ?: return@mapIndexedNotNull null
            SourcePage(index = index, url = imgUrl, imageUrl = imgUrl)
        }
    }

    override suspend fun checkHealth(): SourceHealth {
        return try {
            val start = System.currentTimeMillis()
            val request = buildRequest(baseUrl)
            val response = httpClient.client.newCall(request).execute()
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

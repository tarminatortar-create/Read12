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
import com.readora.app.source.parser.JsonApiDefinition
import com.readora.app.source.parser.JsonApiSourceAdapter
import com.readora.app.source.parser.ParserEngineImpl

/**
 * Phase 48 — Additional Built-In Source 1 (JSON API)
 *
 * Demonstrates the declarative JsonApiSourceAdapter with a public,
 * openly documented test API (dummyjson.com). The definition mirrors
 * what a real JSON comic source would look like — when a real API is
 * swapped in, only the JsonApiDefinition fields change.
 */
object ComicVineStubSource : OnlineSource {

    override val id: String = "comicvine_stub"
    override val name: String = "ComicVine Stub (JSON)"
    override val lang: String = "en"
    override val baseUrl: String = "https://dummyjson.com"
    override val version: Int = 1
    override val capabilities: Set<SourceCapability> = setOf(SourceCapability.Search)

    private val definition by lazy {
        JsonApiDefinition(
            popularEndpoint = "/products?limit=20&skip={page}",
            popularListPath = "$.products",
            popularIdPath = "id",
            popularTitlePath = "title",
            popularCoverPath = "thumbnail",
            popularUrlPath = "id",

            searchEndpoint = "/products/search?q={query}&limit=20&skip={page}",
            searchListPath = "$.products",
            searchIdPath = "id",
            searchTitlePath = "title",
            searchCoverPath = "thumbnail",
            searchUrlPath = "id",

            detailEndpoint = "/products/{id}",
            detailTitlePath = "title",
            detailCoverPath = "thumbnail",
            detailAuthorPath = "brand",
            detailStatusPath = "availabilityStatus",
            detailDescriptionPath = "description",
            detailTagsPath = "tags",

            chaptersEndpoint = "/products/{id}",
            chaptersListPath = "$.images",
            chaptersIdPath = "id",
            chaptersNumberPath = "id",
            chaptersTitlePath = "id",
            chaptersUrlPath = "id",
            chaptersDatePath = "id",

            pagesEndpoint = "/products/{id}",
            pagesListPath = "$.images",
            pagesUrlPath = "",

            headers = mapOf("User-Agent" to "Readora/0.1")
        )
    }

    private val adapter by lazy {
        JsonApiSourceAdapter(
            definition = definition,
            httpClient = SourceHttpClient,
            parserEngine = ParserEngineImpl,
            id = id,
            name = name,
            baseUrl = baseUrl,
            lang = lang,
            version = version
        )
    }

    override suspend fun getPopular(page: Int): List<OnlineComicSummary> = adapter.getPopular(page)
    override suspend fun getLatest(page: Int): List<OnlineComicSummary> = adapter.getPopular(page)
    override suspend fun search(query: String, filters: SourceFilters, page: Int): List<OnlineComicSummary> = adapter.search(query, filters, page)
    override suspend fun getDetails(comicId: String): OnlineComicDetails = adapter.getDetails(comicId)
    override suspend fun getChapterList(comicId: String): List<OnlineChapter> = adapter.getChapterList(comicId)
    override suspend fun getPageList(chapterId: String): List<SourcePage> = adapter.getPageList(chapterId)
    override suspend fun checkHealth(): SourceHealth = adapter.checkHealth()
}

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
import com.readora.app.source.parser.HtmlDefinition
import com.readora.app.source.parser.HtmlSourceAdapter

/**
 * Phase 49 — Additional Built-In Source 2 (HTML/CSS)
 *
 * Demonstrates the declarative HtmlSourceAdapter against
 * books.toscrape.com, a freely scrape-able test website.
 */
object BooksToScrapeStubSource : OnlineSource {

    override val id: String = "books_to_scrape_stub"
    override val name: String = "BooksToScrape Stub (HTML)"
    override val lang: String = "en"
    override val baseUrl: String = "https://books.toscrape.com"
    override val version: Int = 1
    override val capabilities: Set<SourceCapability> = setOf(SourceCapability.Search)

    private val definition by lazy {
        HtmlDefinition(
            popularUrl = "/catalogue/page-{page}.html",
            searchUrlTemplate = "/catalogue/search.html?query={query}&page={page}",

            popularListSelector = "article.product_pod",
            popularIdSelector = "h3 a",
            popularTitleSelector = "h3 a",
            popularCoverSelector = "div.thumbnail img",
            popularUrlSelector = "h3 a",

            searchListSelector = "article.product_pod",
            searchIdSelector = "h3 a",
            searchTitleSelector = "h3 a",
            searchCoverSelector = "div.thumbnail img",
            searchUrlSelector = "h3 a",

            detailUrlTemplate = "/catalogue/{id}/index.html",
            detailTitleSelector = "h1",
            detailCoverSelector = "div.item.active img",
            detailAuthorSelector = "table.table td:first-child",
            detailStatusSelector = "table.table tr:nth-child(6) td",
            detailDescriptionSelector = "#product_description + p",
            detailTagsSelector = "ul.breadcrumb li:not(:last-child):not(:first-child) a",

            chapterListSelector = "table.table tbody tr",
            chapterIdSelector = "td:first-child",
            chapterNumberSelector = "td:first-child",
            chapterTitleSelector = "td:first-child",
            chapterUrlSelector = "td:first-child a",
            chapterDateSelector = "td:last-child",

            pageListSelector = "div.item img",
            pageUrlSelector = "src",

            headers = mapOf("User-Agent" to "Readora/0.1")
        )
    }

    private val adapter by lazy {
        HtmlSourceAdapter(
            definition = definition,
            httpClient = SourceHttpClient,
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

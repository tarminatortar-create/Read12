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
import org.jsoup.Jsoup

class HtmlSourceAdapter(
    private val definition: HtmlDefinition,
    private val httpClient: SourceHttpClient,
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

    private fun fetchHtml(url: String): String {
        val request = buildRequest(url)
        val response = httpClient.client.newCall(request).execute()
        if (!response.isSuccessful) throw Exception("HTTP ${response.code}")
        return response.body?.string() ?: throw Exception("Empty body")
    }

    override suspend fun getPopular(page: Int): List<OnlineComicSummary> {
        val url = baseUrl + definition.popularUrl.replace("{page}", page.toString())
        val html = fetchHtml(url)
        val doc = Jsoup.parse(html, baseUrl)
        
        val elements = doc.select(definition.popularListSelector)
        return elements.map { el ->
            val urlElement = el.selectFirst(definition.popularUrlSelector)
            val fullUrl = urlElement?.absUrl("href") ?: ""
            val computedId = el.selectFirst(definition.popularIdSelector)?.text() ?: fullUrl.substringAfterLast("/")
            
            OnlineComicSummary(
                id = computedId,
                title = el.selectFirst(definition.popularTitleSelector)?.text() ?: "Unknown",
                coverUrl = el.selectFirst(definition.popularCoverSelector)?.absUrl("src") ?: "",
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
        val url = baseUrl + definition.searchUrlTemplate
            .replace("{query}", query)
            .replace("{page}", page.toString())
            
        val html = fetchHtml(url)
        val doc = Jsoup.parse(html, baseUrl)
        
        val elements = doc.select(definition.searchListSelector)
        return elements.map { el ->
            val urlElement = el.selectFirst(definition.searchUrlSelector)
            val fullUrl = urlElement?.absUrl("href") ?: ""
            val computedId = el.selectFirst(definition.searchIdSelector)?.text() ?: fullUrl.substringAfterLast("/")
            
            OnlineComicSummary(
                id = computedId,
                title = el.selectFirst(definition.searchTitleSelector)?.text() ?: "Unknown",
                coverUrl = el.selectFirst(definition.searchCoverSelector)?.absUrl("src") ?: "",
                sourceId = this.id,
                sourceName = this.name,
                description = "",
                tags = emptyList(),
                status = "Ongoing"
            )
        }
    }

    override suspend fun getDetails(comicId: String): OnlineComicDetails {
        val url = baseUrl + definition.detailUrlTemplate.replace("{id}", comicId)
        val html = fetchHtml(url)
        val doc = Jsoup.parse(html, baseUrl)
        
        val summary = OnlineComicSummary(
            id = comicId,
            title = doc.selectFirst(definition.detailTitleSelector)?.text() ?: "",
            sourceId = this.id,
            sourceName = this.name,
            description = doc.selectFirst(definition.detailDescriptionSelector)?.text() ?: "",
            status = doc.selectFirst(definition.detailStatusSelector)?.text() ?: "",
            coverUrl = doc.selectFirst(definition.detailCoverSelector)?.absUrl("src") ?: "",
            tags = doc.select(definition.detailTagsSelector).map { it.text() }
        )
        
        val chapters = getChapterList(comicId)
        return OnlineComicDetails(summary = summary, chapters = chapters)
    }

    override suspend fun getChapterList(comicId: String): List<OnlineChapter> {
        val url = baseUrl + definition.detailUrlTemplate.replace("{id}", comicId)
        val html = fetchHtml(url)
        val doc = Jsoup.parse(html, baseUrl)
        
        val elements = doc.select(definition.chapterListSelector)
        return elements.map { el ->
            val urlElement = el.selectFirst(definition.chapterUrlSelector)
            val fullUrl = urlElement?.absUrl("href") ?: ""
            val computedId = el.selectFirst(definition.chapterIdSelector)?.text() ?: fullUrl.substringAfterLast("/")
            
            OnlineChapter(
                id = computedId,
                number = el.selectFirst(definition.chapterNumberSelector)?.text() ?: "-1",
                title = el.selectFirst(definition.chapterTitleSelector)?.text() ?: "",
                pages = 0,
                readableAt = "" 
            )
        }
    }

    override suspend fun getPageList(chapterId: String): List<SourcePage> {
        val url = baseUrl + chapterId 
        val html = fetchHtml(url)
        val doc = Jsoup.parse(html, baseUrl)
        
        val elements = doc.select(definition.pageListSelector)
        return elements.mapIndexedNotNull { index, el -> 
            val imgUrl = if (el.tagName() == "img") el.absUrl(definition.pageUrlSelector) 
                         else el.selectFirst(definition.pageUrlSelector)?.absUrl("src")
                         
            if (!imgUrl.isNullOrEmpty()) {
                SourcePage(index = index, url = imgUrl, imageUrl = imgUrl)
            } else {
                null
            }
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

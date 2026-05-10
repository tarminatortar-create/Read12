package com.readora.app.source.api

import com.readora.app.source.OnlineChapter
import com.readora.app.source.OnlineComicDetails
import com.readora.app.source.OnlineComicSummary
import com.readora.app.source.OnlinePage

enum class SourceCapability {
    Search, Filters, Login, Chapters, PageList
}

data class SourceFilters(
    val genres: List<String> = emptyList(),
    val status: String? = null,
    val sort: String? = null,
    val isNsfw: Boolean = false
)

data class SourcePage(
    val index: Int,
    val url: String,
    val imageUrl: String,
    val headers: Map<String, String> = emptyMap()
)

data class SourceHealth(
    val isOnline: Boolean,
    val latencyMs: Long,
    val message: String? = null
)

interface OnlineSource {
    val id: String
    val name: String
    val lang: String
    val baseUrl: String
    val version: Int
    val capabilities: Set<SourceCapability>

    suspend fun getPopular(page: Int = 1): List<OnlineComicSummary>
    suspend fun getLatest(page: Int = 1): List<OnlineComicSummary>
    suspend fun search(query: String, filters: SourceFilters = SourceFilters(), page: Int = 1): List<OnlineComicSummary>
    suspend fun getDetails(comicId: String): OnlineComicDetails
    suspend fun getChapterList(comicId: String): List<OnlineChapter>
    suspend fun getPageList(chapterId: String): List<SourcePage>
    suspend fun checkHealth(): SourceHealth
}

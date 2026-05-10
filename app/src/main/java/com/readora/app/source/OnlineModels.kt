package com.readora.app.source

data class OnlineComicSummary(
    val id: String,
    val sourceId: String,
    val sourceName: String,
    val title: String,
    val description: String,
    val coverUrl: String?,
    val tags: List<String>,
    val status: String,
    val year: Int? = null,
)

data class OnlineChapter(
    val id: String,
    val number: String,
    val title: String,
    val pages: Int,
    val readableAt: String,
    val scanlator: String? = null,
    val language: String? = null,
)

data class OnlineComicDetails(
    val summary: OnlineComicSummary,
    val chapters: List<OnlineChapter>,
)

data class OnlinePage(
    val index: Int,
    val url: String,
)

interface OnlineComicSource {
    val id: String
    val name: String
    val language: String

    suspend fun popular(limit: Int = 20): List<OnlineComicSummary>
    suspend fun search(query: String, limit: Int = 20): List<OnlineComicSummary>
    suspend fun details(summary: OnlineComicSummary): OnlineComicDetails
    suspend fun pages(chapterId: String): List<OnlinePage>
}

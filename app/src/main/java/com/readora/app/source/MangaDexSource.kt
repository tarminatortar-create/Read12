package com.readora.app.source

import com.readora.app.source.api.OnlineSource
import com.readora.app.source.api.SourceCapability
import com.readora.app.source.api.SourceFilters
import com.readora.app.source.api.SourceHealth
import com.readora.app.source.api.SourcePage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import okhttp3.Request
import com.readora.app.source.network.SourceHttpClient
import java.net.URLEncoder

object MangaDexSource : OnlineSource {
    override val id: String = "mangadex"
    override val name: String = "MangaDex"
    override val lang: String = "en"
    override val baseUrl: String = "https://mangadex.org"
    override val version: Int = 1
    override val capabilities: Set<SourceCapability> = setOf(
        SourceCapability.Search,
        SourceCapability.Chapters,
        SourceCapability.PageList
    )

    private const val api = "https://api.mangadex.org"
    private const val uploads = "https://uploads.mangadex.org"

    /** Build `&availableTranslatedLanguage[]=xx` params from a list of codes. Falls back to "en". */
    private fun availableLangParams(langs: List<String>): String {
        val list = langs.ifEmpty { listOf("en") }
        return list.joinToString("") { "&availableTranslatedLanguage%5B%5D=$it" }
    }

    /** Build `&translatedLanguage[]=xx` params for chapter feed. Falls back to "en". */
    private fun translatedLangParams(langs: List<String>): String {
        val list = langs.ifEmpty { listOf("en") }
        return list.joinToString("") { "&translatedLanguage%5B%5D=$it" }
    }

    private fun contentRatingParams(filters: SourceFilters): String {
        return if (filters.isNsfw) {
            "&contentRating%5B%5D=safe&contentRating%5B%5D=suggestive&contentRating%5B%5D=erotica&contentRating%5B%5D=pornographic"
        } else {
            "&contentRating%5B%5D=safe&contentRating%5B%5D=suggestive"
        }
    }

    override suspend fun getPopular(page: Int): List<OnlineComicSummary> =
        getPopular(page, listOf("en"))

    suspend fun getPopular(page: Int, langs: List<String>): List<OnlineComicSummary> = withContext(Dispatchers.IO) {
        val limit = 30
        val offset = (page - 1) * limit
        val url = "$api/manga?limit=$limit&offset=$offset&includes%5B%5D=cover_art" +
            availableLangParams(langs) +
            contentRatingParams(SourceFilters()) +
            "&order%5BfollowedCount%5D=desc"
        parseMangaList(requestJson(url))
    }

    override suspend fun getLatest(page: Int): List<OnlineComicSummary> =
        getLatest(page, listOf("en"))

    suspend fun getLatest(page: Int, langs: List<String>): List<OnlineComicSummary> = withContext(Dispatchers.IO) {
        val limit = 30
        val offset = (page - 1) * limit
        val url = "$api/manga?limit=$limit&offset=$offset&includes%5B%5D=cover_art" +
            availableLangParams(langs) +
            contentRatingParams(SourceFilters()) +
            "&order%5BupdatedAt%5D=desc"
        parseMangaList(requestJson(url))
    }

    override suspend fun search(query: String, filters: SourceFilters, page: Int): List<OnlineComicSummary> =
        search(query, filters, page, listOf("en"))

    suspend fun search(query: String, filters: SourceFilters, page: Int, langs: List<String>): List<OnlineComicSummary> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext getPopular(page, langs)
        val limit = 30
        val offset = (page - 1) * limit
        val encoded = URLEncoder.encode(query.trim(), "UTF-8")
        val url = "$api/manga?limit=$limit&offset=$offset&title=$encoded&includes%5B%5D=cover_art" +
            availableLangParams(langs) +
            contentRatingParams(filters) +
            "&order%5Brelevance%5D=desc"
        parseMangaList(requestJson(url))
    }

    override suspend fun getDetails(comicId: String): OnlineComicDetails = withContext(Dispatchers.IO) {
        val mangaUrl = "$api/manga/$comicId?includes%5B%5D=cover_art"
        val mangaJson = requestJson(mangaUrl).optJSONObject("data") ?: JSONObject()
        val summary = parseMangaSummary(mangaJson) ?: throw IllegalStateException("Could not parse manga")

        val chapters = getChapterList(comicId)
        OnlineComicDetails(summary, chapters)
    }

    override suspend fun getChapterList(comicId: String): List<OnlineChapter> =
        getChapterList(comicId, listOf("en"))

    suspend fun getChapterList(comicId: String, langs: List<String>): List<OnlineChapter> = withContext(Dispatchers.IO) {
        val url = "$api/manga/$comicId/feed?limit=200" +
            translatedLangParams(langs) +
            "&order%5Bchapter%5D=desc" +
            "&includes%5B%5D=scanlation_group"
        val data = requestJson(url).optJSONArray("data") ?: JSONArray()
        buildList {
            for (i in 0 until data.length()) {
                val item = data.optJSONObject(i) ?: continue
                val attr = item.optJSONObject("attributes") ?: JSONObject()
                val chapter = attr.optString("chapter").ifBlank { "?" }
                val scanlator = runCatching { scanlationGroupName(item.optJSONArray("relationships")) }.getOrNull()
                val lang = attr.optString("translatedLanguage").ifBlank { null }
                add(
                    OnlineChapter(
                        id = item.optString("id"),
                        number = chapter,
                        title = attr.optString("title").ifBlank { "Chapter $chapter" },
                        pages = attr.optInt("pages", 0),
                        readableAt = attr.optString("readableAt").take(10),
                        scanlator = scanlator,
                        language = lang,
                    ),
                )
            }
        }
    }

    private fun scanlationGroupName(relationships: JSONArray?): String? {
        if (relationships == null) return null
        for (i in 0 until relationships.length()) {
            val relation = relationships.optJSONObject(i) ?: continue
            if (relation.optString("type") == "scanlation_group") {
                val name = relation.optJSONObject("attributes")?.optString("name").orEmpty()
                if (name.isNotBlank()) return name
            }
        }
        return null
    }

    override suspend fun getPageList(chapterId: String): List<SourcePage> = withContext(Dispatchers.IO) {
        val json = requestJson("$api/at-home/server/$chapterId")
        val baseUrlObj = json.optString("baseUrl")
        val chapter = json.optJSONObject("chapter") ?: JSONObject()
        val hash = chapter.optString("hash")
        val dataSaver = chapter.optJSONArray("dataSaver") ?: chapter.optJSONArray("data") ?: JSONArray()
        buildList {
            for (i in 0 until dataSaver.length()) {
                val fileName = dataSaver.optString(i)
                if (fileName.isNotBlank()) {
                    val url = "$baseUrlObj/data-saver/$hash/$fileName"
                    add(SourcePage(i + 1, url, url))
                }
            }
        }
    }

    override suspend fun checkHealth(): SourceHealth = withContext(Dispatchers.IO) {
        val start = System.currentTimeMillis()
        try {
            getPopular(1).take(1)
            SourceHealth(true, System.currentTimeMillis() - start, "OK")
        } catch (e: Exception) {
            SourceHealth(false, System.currentTimeMillis() - start, e.message ?: "Unknown error")
        }
    }

    // Keep compatibility for other code depending on popular and search without SourceFilters
    suspend fun popular(limit: Int): List<OnlineComicSummary> = getPopular(1).take(limit)
    suspend fun search(query: String, limit: Int): List<OnlineComicSummary> = search(query, SourceFilters(), 1).take(limit)
    suspend fun details(summary: OnlineComicSummary): OnlineComicDetails = getDetails(summary.id)
    suspend fun pages(chapterId: String): List<OnlinePage> = withContext(Dispatchers.IO) {
        getPageList(chapterId).map { OnlinePage(it.index, it.imageUrl) }
    }

    private fun parseMangaSummary(item: JSONObject): OnlineComicSummary? {
        val id = item.optString("id")
        if (id.isEmpty()) return null
        val attr = item.optJSONObject("attributes") ?: JSONObject()
        val fileName = coverFileName(item.optJSONArray("relationships"))
        val coverUrl = fileName?.let { "$uploads/covers/$id/$it.256.jpg" }
        val yearRaw = attr.optInt("year", 0).takeIf { it > 0 }
        return OnlineComicSummary(
            id = id,
            sourceId = this.id,
            sourceName = name,
            title = localized(attr.optJSONObject("title")),
            description = localized(attr.optJSONObject("description")).take(260),
            coverUrl = coverUrl,
            tags = parseTags(attr.optJSONArray("tags")),
            status = attr.optString("status").replaceFirstChar { it.uppercase() },
            year = yearRaw,
        )
    }

    fun parseMangaList(json: JSONObject): List<OnlineComicSummary> {
        val data = json.optJSONArray("data") ?: JSONArray()
        return buildList {
            for (i in 0 until data.length()) {
                val item = data.optJSONObject(i) ?: continue
                parseMangaSummary(item)?.let { add(it) }
            }
        }
    }

    fun coverFileName(relationships: JSONArray?): String? {
        if (relationships == null) return null
        for (i in 0 until relationships.length()) {
            val relation = relationships.optJSONObject(i) ?: continue
            if (relation.optString("type") == "cover_art") {
                return relation.optJSONObject("attributes")?.optString("fileName")?.ifBlank { null }
            }
        }
        return null
    }

    fun parseTags(tags: JSONArray?): List<String> {
        if (tags == null) return emptyList()
        val output = mutableListOf<String>()
        for (i in 0 until tags.length()) {
            val tag = tags.optJSONObject(i) ?: continue
            val name = localized(tag.optJSONObject("attributes")?.optJSONObject("name"))
            if (name.isNotBlank()) output += name
            if (output.size == 4) break
        }
        return output
    }

    fun localized(values: JSONObject?): String {
        if (values == null) return "Untitled"
        values.optString("en").ifBlank { null }?.let { return it }
        val keys = values.keys()
        while (keys.hasNext()) {
            val value = values.optString(keys.next())
            if (value.isNotBlank()) return value
        }
        return "Untitled"
    }

    fun requestJson(url: String): JSONObject {
        val request = Request.Builder()
            .url(url)
            .header("Accept", "application/json")
            .header("User-Agent", "Readora/0.1 Android source engine")
            .build()
        val response = SourceHttpClient.client.newCall(request).execute()
        val body = response.body?.string() ?: ""
        if (!response.isSuccessful) {
            response.close()
            throw IllegalStateException("${response.code}: $body")
        }
        return JSONObject(body.ifBlank { "{}" })
    }
}

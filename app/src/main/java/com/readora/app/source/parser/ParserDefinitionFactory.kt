package com.readora.app.source.parser

import org.json.JSONObject

object ParserDefinitionFactory {
    fun from(type: String?, rawJson: String?): ParserDefinition? {
        if (rawJson.isNullOrBlank()) return null
        val json = JSONObject(rawJson)
        return when (type?.uppercase()) {
            "JSON_API", "JSON" -> jsonApi(json)
            "HTML_CSS", "HTML" -> html(json)
            "RSS" -> RssDefinition(feedUrl = json.optString("feedUrl"))
            else -> null
        }
    }

    private fun jsonApi(json: JSONObject): JsonApiDefinition = JsonApiDefinition(
        popularEndpoint = json.optString("popularEndpoint"),
        popularListPath = json.optString("popularListPath"),
        popularIdPath = json.optString("popularIdPath", "id"),
        popularTitlePath = json.optString("popularTitlePath", "title"),
        popularCoverPath = json.optString("popularCoverPath", "coverUrl"),
        popularUrlPath = json.optString("popularUrlPath", "url"),
        searchEndpoint = json.optString("searchEndpoint"),
        searchListPath = json.optString("searchListPath"),
        searchIdPath = json.optString("searchIdPath", "id"),
        searchTitlePath = json.optString("searchTitlePath", "title"),
        searchCoverPath = json.optString("searchCoverPath", "coverUrl"),
        searchUrlPath = json.optString("searchUrlPath", "url"),
        detailEndpoint = json.optString("detailEndpoint"),
        detailTitlePath = json.optString("detailTitlePath", "title"),
        detailCoverPath = json.optString("detailCoverPath", "coverUrl"),
        detailAuthorPath = json.optString("detailAuthorPath", "author"),
        detailStatusPath = json.optString("detailStatusPath", "status"),
        detailDescriptionPath = json.optString("detailDescriptionPath", "description"),
        detailTagsPath = json.optString("detailTagsPath", "tags"),
        chaptersEndpoint = json.optString("chaptersEndpoint"),
        chaptersListPath = json.optString("chaptersListPath"),
        chaptersIdPath = json.optString("chaptersIdPath", "id"),
        chaptersNumberPath = json.optString("chaptersNumberPath", "number"),
        chaptersTitlePath = json.optString("chaptersTitlePath", "title"),
        chaptersUrlPath = json.optString("chaptersUrlPath", "url"),
        chaptersDatePath = json.optString("chaptersDatePath", "readableAt"),
        pagesEndpoint = json.optString("pagesEndpoint"),
        pagesListPath = json.optString("pagesListPath"),
        pagesUrlPath = json.optString("pagesUrlPath", "url"),
        headers = headers(json),
    )

    private fun html(json: JSONObject): HtmlDefinition = HtmlDefinition(
        popularUrl = json.optString("popularUrl"),
        searchUrlTemplate = json.optString("searchUrlTemplate"),
        popularListSelector = json.optString("popularListSelector"),
        popularIdSelector = json.optString("popularIdSelector"),
        popularTitleSelector = json.optString("popularTitleSelector"),
        popularCoverSelector = json.optString("popularCoverSelector"),
        popularUrlSelector = json.optString("popularUrlSelector"),
        searchListSelector = json.optString("searchListSelector"),
        searchIdSelector = json.optString("searchIdSelector"),
        searchTitleSelector = json.optString("searchTitleSelector"),
        searchCoverSelector = json.optString("searchCoverSelector"),
        searchUrlSelector = json.optString("searchUrlSelector"),
        detailUrlTemplate = json.optString("detailUrlTemplate"),
        detailTitleSelector = json.optString("detailTitleSelector"),
        detailCoverSelector = json.optString("detailCoverSelector"),
        detailAuthorSelector = json.optString("detailAuthorSelector"),
        detailStatusSelector = json.optString("detailStatusSelector"),
        detailDescriptionSelector = json.optString("detailDescriptionSelector"),
        detailTagsSelector = json.optString("detailTagsSelector"),
        chapterListSelector = json.optString("chapterListSelector"),
        chapterIdSelector = json.optString("chapterIdSelector"),
        chapterNumberSelector = json.optString("chapterNumberSelector"),
        chapterTitleSelector = json.optString("chapterTitleSelector"),
        chapterUrlSelector = json.optString("chapterUrlSelector"),
        chapterDateSelector = json.optString("chapterDateSelector"),
        pageListSelector = json.optString("pageListSelector"),
        pageUrlSelector = json.optString("pageUrlSelector"),
        headers = headers(json),
    )

    private fun headers(json: JSONObject): Map<String, String> {
        val headers = json.optJSONObject("headers") ?: return emptyMap()
        return buildMap {
            headers.keys().forEach { key -> put(key, headers.optString(key)) }
        }
    }
}

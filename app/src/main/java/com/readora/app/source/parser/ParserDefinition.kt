package com.readora.app.source.parser

enum class ParserType {
    JSON_API, HTML_CSS, RSS
}

sealed class ParserDefinition

data class JsonApiDefinition(
    val popularEndpoint: String,
    val popularListPath: String,
    val popularIdPath: String,
    val popularTitlePath: String,
    val popularCoverPath: String,
    val popularUrlPath: String,

    val searchEndpoint: String,
    val searchListPath: String,
    val searchIdPath: String,
    val searchTitlePath: String,
    val searchCoverPath: String,
    val searchUrlPath: String,

    val detailEndpoint: String,
    val detailTitlePath: String,
    val detailCoverPath: String,
    val detailAuthorPath: String,
    val detailStatusPath: String,
    val detailDescriptionPath: String,
    val detailTagsPath: String,

    val chaptersEndpoint: String,
    val chaptersListPath: String,
    val chaptersIdPath: String,
    val chaptersNumberPath: String,
    val chaptersTitlePath: String,
    val chaptersUrlPath: String,
    val chaptersDatePath: String,

    val pagesEndpoint: String,
    val pagesListPath: String,
    val pagesUrlPath: String,
    
    val headers: Map<String, String> = emptyMap()
) : ParserDefinition()

data class HtmlDefinition(
    val popularUrl: String,
    val searchUrlTemplate: String,
    
    val popularListSelector: String,
    val popularIdSelector: String,
    val popularTitleSelector: String,
    val popularCoverSelector: String,
    val popularUrlSelector: String,

    val searchListSelector: String,
    val searchIdSelector: String,
    val searchTitleSelector: String,
    val searchCoverSelector: String,
    val searchUrlSelector: String,

    val detailUrlTemplate: String,
    val detailTitleSelector: String,
    val detailCoverSelector: String,
    val detailAuthorSelector: String,
    val detailStatusSelector: String,
    val detailDescriptionSelector: String,
    val detailTagsSelector: String,

    val chapterListSelector: String,
    val chapterIdSelector: String,
    val chapterNumberSelector: String,
    val chapterTitleSelector: String,
    val chapterUrlSelector: String,
    val chapterDateSelector: String,

    val pageListSelector: String,
    val pageUrlSelector: String,

    val headers: Map<String, String> = emptyMap()
) : ParserDefinition()

data class RssDefinition(
    val feedUrl: String
) : ParserDefinition()

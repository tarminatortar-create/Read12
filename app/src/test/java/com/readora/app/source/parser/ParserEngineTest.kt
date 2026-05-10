package com.readora.app.source.parser

import org.junit.Assert.*
import org.junit.Test

/**
 * Phase 51 — Source Test Harness
 * Tests the declarative parser engine using local sample data.
 * These tests run on the JVM (no Android device/emulator needed).
 */
class ParserEngineTest {

    private val engine = ParserEngineImpl

    // ── JSON Parser Tests ─────────────────────────────────────────────────────

    private val sampleJson = """
        {
          "data": [
            { "id": "1", "title": "Attack on Titan", "cover": "https://cdn.example.com/1.jpg", "url": "/manga/1" },
            { "id": "2", "title": "One Piece",        "cover": "https://cdn.example.com/2.jpg", "url": "/manga/2" }
          ],
          "total": 2
        }
    """.trimIndent()

    private val sampleJsonDef = JsonApiDefinition(
        popularEndpoint = "/manga?page={page}",
        popularListPath = "$.data",
        popularIdPath = "id",
        popularTitlePath = "title",
        popularCoverPath = "cover",
        popularUrlPath = "url",
        searchEndpoint = "/manga/search?q={query}&page={page}",
        searchListPath = "$.data",
        searchIdPath = "id",
        searchTitlePath = "title",
        searchCoverPath = "cover",
        searchUrlPath = "url",
        detailEndpoint = "/manga/{id}",
        detailTitlePath = "title",
        detailCoverPath = "cover",
        detailAuthorPath = "author",
        detailStatusPath = "status",
        detailDescriptionPath = "description",
        detailTagsPath = "tags",
        chaptersEndpoint = "/manga/{id}/chapters",
        chaptersListPath = "$.chapters",
        chaptersIdPath = "id",
        chaptersNumberPath = "number",
        chaptersTitlePath = "title",
        chaptersUrlPath = "url",
        chaptersDatePath = "date",
        pagesEndpoint = "/chapter/{id}/pages",
        pagesListPath = "$.pages",
        pagesUrlPath = "url",
    )

    @Test
    fun `json parser extracts correct number of items`() {
        val result = engine.parse(ParserType.JSON_API, sampleJsonDef, sampleJson, "$.data")
        assertTrue("Expected Success", result is ParseResult.Success)
        val items = (result as ParseResult.Success).items
        assertEquals(2, items.size)
    }

    @Test
    fun `json parser extracts correct title field`() {
        val result = engine.parse(ParserType.JSON_API, sampleJsonDef, sampleJson, "$.data")
        val items = (result as ParseResult.Success).items
        assertEquals("Attack on Titan", items[0]["title"])
        assertEquals("One Piece", items[1]["title"])
    }

    @Test
    fun `json parser returns error on invalid path`() {
        val result = engine.parse(ParserType.JSON_API, sampleJsonDef, sampleJson, "$.nonexistent")
        assertTrue("Expected Error for bad path", result is ParseResult.Error)
    }

    @Test
    fun `json parser handles empty array`() {
        val emptyJson = """{"data": []}"""
        val result = engine.parse(ParserType.JSON_API, sampleJsonDef, emptyJson, "$.data")
        assertTrue(result is ParseResult.Success)
        assertEquals(0, (result as ParseResult.Success).items.size)
    }

    // ── RSS Parser Tests ──────────────────────────────────────────────────────

    private val sampleRss = """
        <?xml version="1.0" encoding="UTF-8"?>
        <rss version="2.0">
          <channel>
            <title>Test Manga Feed</title>
            <item>
              <title>Attack on Titan - Chapter 140</title>
              <link>https://example.com/manga/aot/140</link>
              <guid>https://example.com/manga/aot/140</guid>
              <pubDate>Mon, 09 May 2026 00:00:00 +0000</pubDate>
              <description>Final chapter of Attack on Titan.</description>
            </item>
            <item>
              <title>One Piece - Chapter 1050</title>
              <link>https://example.com/manga/onepiece/1050</link>
              <guid>https://example.com/manga/onepiece/1050</guid>
              <pubDate>Sun, 08 May 2026 00:00:00 +0000</pubDate>
              <description>One Piece continues.</description>
            </item>
          </channel>
        </rss>
    """.trimIndent()

    @Test
    fun `rss parser extracts correct number of items`() {
        val def = RssDefinition(feedUrl = "https://example.com/rss")
        val result = engine.parse(ParserType.RSS, def, sampleRss)
        assertTrue("Expected Success", result is ParseResult.Success)
        val items = (result as ParseResult.Success).items
        assertEquals(2, items.size)
    }

    @Test
    fun `rss parser extracts correct title`() {
        val def = RssDefinition(feedUrl = "https://example.com/rss")
        val result = engine.parse(ParserType.RSS, def, sampleRss)
        val items = (result as ParseResult.Success).items
        assertEquals("Attack on Titan - Chapter 140", items[0]["title"])
    }

    @Test
    fun `rss parser extracts link and description`() {
        val def = RssDefinition(feedUrl = "https://example.com/rss")
        val result = engine.parse(ParserType.RSS, def, sampleRss)
        val items = (result as ParseResult.Success).items
        assertTrue(items[0].containsKey("link"))
        assertTrue(items[0].containsKey("description"))
    }

    @Test
    fun `rss parser returns error on invalid xml`() {
        val def = RssDefinition(feedUrl = "https://example.com/rss")
        val result = engine.parse(ParserType.RSS, def, "NOT VALID XML {{{")
        assertTrue("Expected Error for bad XML", result is ParseResult.Error)
    }

    // ── HTML Parser Tests ─────────────────────────────────────────────────────

    private val sampleHtml = """
        <html>
          <body>
            <div class="manga-list">
              <article class="manga-item">
                <h2 class="title"><a href="/manga/1">Attack on Titan</a></h2>
                <img src="https://cdn.example.com/1.jpg" class="cover"/>
              </article>
              <article class="manga-item">
                <h2 class="title"><a href="/manga/2">One Piece</a></h2>
                <img src="https://cdn.example.com/2.jpg" class="cover"/>
              </article>
            </div>
          </body>
        </html>
    """.trimIndent()

    private val sampleHtmlDef = HtmlDefinition(
        popularUrl = "/popular?page={page}",
        searchUrlTemplate = "/search?q={query}&page={page}",
        popularListSelector = "article.manga-item",
        popularIdSelector = "h2.title a",
        popularTitleSelector = "h2.title a",
        popularCoverSelector = "img.cover",
        popularUrlSelector = "h2.title a",
        searchListSelector = "article.manga-item",
        searchIdSelector = "h2.title a",
        searchTitleSelector = "h2.title a",
        searchCoverSelector = "img.cover",
        searchUrlSelector = "h2.title a",
        detailUrlTemplate = "/manga/{id}",
        detailTitleSelector = "h1.title",
        detailCoverSelector = "img.cover",
        detailAuthorSelector = "span.author",
        detailStatusSelector = "span.status",
        detailDescriptionSelector = "div.description",
        detailTagsSelector = "span.tag",
        chapterListSelector = "li.chapter",
        chapterIdSelector = "a",
        chapterNumberSelector = "span.num",
        chapterTitleSelector = "span.title",
        chapterUrlSelector = "a",
        chapterDateSelector = "span.date",
        pageListSelector = "img.page",
        pageUrlSelector = "src",
    )

    @Test
    fun `html parser extracts correct number of items`() {
        val result = engine.parse(ParserType.HTML_CSS, sampleHtmlDef, sampleHtml, "article.manga-item")
        assertTrue("Expected Success", result is ParseResult.Success)
        val items = (result as ParseResult.Success).items
        assertEquals(2, items.size)
    }

    @Test
    fun `html parser returns error on empty selector`() {
        val result = engine.parse(ParserType.HTML_CSS, sampleHtmlDef, sampleHtml, null)
        assertTrue("Expected Error for null selector", result is ParseResult.Error)
    }
}

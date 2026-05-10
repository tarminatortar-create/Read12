package com.readora.app.source.parser

import com.jayway.jsonpath.JsonPath
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.w3c.dom.Element as XmlElement
import javax.xml.parsers.DocumentBuilderFactory
import java.io.ByteArrayInputStream

/**
 * Phase 50 — RSS Parser implementation.
 * Parses standard RSS 2.0 and Atom feeds declaratively.
 */
object ParserEngineImpl : ParserEngine {
    override fun parse(type: ParserType, definition: ParserDefinition, input: String, rootPath: String?): ParseResult {
        return try {
            when (type) {
                ParserType.JSON_API -> parseJson(definition as JsonApiDefinition, input, rootPath)
                ParserType.HTML_CSS -> parseHtml(definition as HtmlDefinition, input, rootPath)
                ParserType.RSS -> parseRss(definition as RssDefinition, input)
            }
        } catch (e: Exception) {
            ParseResult.Error("Parsing failed: ${e.message}", e)
        }
    }

    private fun parseJson(def: JsonApiDefinition, input: String, rootPath: String?): ParseResult {
        val documentContext = JsonPath.parse(input)
        val listPath = rootPath ?: return ParseResult.Error("No list path provided for JSON parse")

        return try {
            val raw = documentContext.read<Any>(listPath)
            val list = when (raw) {
                is List<*> -> raw
                else -> listOf(raw)
            }
            val paths = listOf(
                def.popularIdPath,
                def.popularTitlePath,
                def.popularCoverPath,
                def.popularUrlPath,
                def.searchIdPath,
                def.searchTitlePath,
                def.searchCoverPath,
                def.searchUrlPath,
                def.detailTitlePath,
                def.detailCoverPath,
                def.detailAuthorPath,
                def.detailStatusPath,
                def.detailDescriptionPath,
                def.detailTagsPath,
                def.chaptersIdPath,
                def.chaptersNumberPath,
                def.chaptersTitlePath,
                def.chaptersUrlPath,
                def.chaptersDatePath,
                def.pagesUrlPath,
            ).filter { it.isNotBlank() }.distinct()
            val mappedItems = list.map { item ->
                val itemContext = JsonPath.parse(item)
                val map = mutableMapOf<String, String>()
                if (item is Map<*, *>) {
                    item.forEach { (key, value) -> if (key != null && value != null) map[key.toString()] = value.toString() }
                } else if (item != null) {
                    map[""] = item.toString()
                }
                paths.forEach { path ->
                    val jsonPath = if (path.startsWith("$")) path else "$.$path"
                    runCatching { itemContext.read<Any>(jsonPath) }
                        .getOrNull()
                        ?.let { value -> map[path] = stringifyJsonValue(value) }
                }
                map
            }
            ParseResult.Success(mappedItems)
        } catch (e: Exception) {
            ParseResult.Error("JSON Path evaluation failed: ${e.message}", e)
        }
    }

    private fun stringifyJsonValue(value: Any): String = when (value) {
        is List<*> -> value.filterNotNull().joinToString(",") { it.toString() }
        else -> value.toString()
    }

    private fun parseHtml(def: HtmlDefinition, input: String, rootPath: String?): ParseResult {
        val doc: Document = Jsoup.parse(input)
        val listSelector = rootPath ?: return ParseResult.Error("No list selector provided for HTML parse")

        return try {
            val elements = doc.select(listSelector)
            val mappedItems = elements.map { element ->
                mapOf("html" to element.outerHtml())
            }
            ParseResult.Success(mappedItems)
        } catch (e: Exception) {
            ParseResult.Error("HTML evaluation failed: ${e.message}", e)
        }
    }

    /** 
     * Parses a standard RSS 2.0 or Atom feed, extracting:
     *   title, link, pubDate (or published for Atom), description / summary.
     * Returns each <item> or <entry> as a Map<String, String>.
     */
    private fun parseRss(def: RssDefinition, input: String): ParseResult {
        return try {
            val factory = DocumentBuilderFactory.newInstance()
            factory.isNamespaceAware = true
            val builder = factory.newDocumentBuilder()
            val document = builder.parse(ByteArrayInputStream(input.toByteArray(Charsets.UTF_8)))
            document.documentElement.normalize()

            // Support both RSS 2.0 <item> and Atom <entry>
            val rssItems = document.getElementsByTagName("item")
            val atomEntries = document.getElementsByTagName("entry")
            val nodeList = if (rssItems.length > 0) rssItems else atomEntries

            val items = mutableListOf<Map<String, String>>()
            for (i in 0 until nodeList.length) {
                val node = nodeList.item(i) as? XmlElement ?: continue
                val map = mutableMapOf<String, String>()
                // Extract common RSS/Atom fields
                listOf("title", "link", "pubDate", "published", "description", "summary", "id", "guid").forEach { tag ->
                    val elements = node.getElementsByTagName(tag)
                    if (elements.length > 0) {
                        val text = elements.item(0)?.textContent?.trim() ?: ""
                        if (text.isNotEmpty()) map[tag] = text
                    }
                }
                // Normalize: prefer pubDate but fall back to published
                if (!map.containsKey("pubDate") && map.containsKey("published")) {
                    map["pubDate"] = map["published"]!!
                }
                // Normalize: prefer description but fall back to summary
                if (!map.containsKey("description") && map.containsKey("summary")) {
                    map["description"] = map["summary"]!!
                }
                if (map.isNotEmpty()) items.add(map)
            }
            ParseResult.Success(items)
        } catch (e: Exception) {
            ParseResult.Error("RSS parsing failed: ${e.message}", e)
        }
    }
}

package com.readora.app.source

import com.readora.app.data.db.SourceEntity
import com.readora.app.source.api.OnlineSource
import com.readora.app.source.builtin.BooksToScrapeStubSource
import com.readora.app.source.builtin.ComicVineStubSource
import com.readora.app.source.builtin.MangaDexRssStubSource
import com.readora.app.source.network.SourceHttpClient
import com.readora.app.source.parser.HtmlDefinition
import com.readora.app.source.parser.HtmlSourceAdapter
import com.readora.app.source.parser.JsonApiDefinition
import com.readora.app.source.parser.JsonApiSourceAdapter
import com.readora.app.source.parser.ParserDefinitionFactory
import com.readora.app.source.parser.ParserEngineImpl

object OnlineSourceRegistry {
    private val builtIn: List<OnlineSource> = listOf(
        MangaDexSource,
        ComicVineStubSource,
        BooksToScrapeStubSource,
        MangaDexRssStubSource,
    )

    fun enabledSources(enabledIds: List<String>): List<OnlineSource> {
        if (enabledIds.isEmpty()) return builtIn
        val idSet = enabledIds.toSet()
        val filtered = builtIn.filter { it.id in idSet }
        return if (filtered.isEmpty()) builtIn else filtered
    }

    fun enabledRuntimeSources(enabled: List<SourceEntity>): List<OnlineSource> {
        if (enabled.isEmpty()) return builtIn
        val enabledIds = enabled.map { it.sourceId }.toSet()
        val builtInEnabled = builtIn.filter { it.id in enabledIds }
        val dynamic = enabled
            .filterNot { entity -> builtIn.any { it.id == entity.sourceId } }
            .mapNotNull { entity -> entity.toRuntimeSource() }
        return (builtInEnabled + dynamic).ifEmpty { builtIn }
    }

    private fun SourceEntity.toRuntimeSource(): OnlineSource? {
        val definition = ParserDefinitionFactory.from(parserType, parserDefinitionJson) ?: return null
        return when (definition) {
            is JsonApiDefinition -> JsonApiSourceAdapter(
                definition = definition,
                httpClient = SourceHttpClient,
                parserEngine = ParserEngineImpl,
                id = sourceId,
                name = name,
                baseUrl = baseUrl,
                lang = language,
                version = version.toIntOrNull() ?: 1,
            )
            is HtmlDefinition -> HtmlSourceAdapter(
                definition = definition,
                httpClient = SourceHttpClient,
                id = sourceId,
                name = name,
                baseUrl = baseUrl,
                lang = language,
                version = version.toIntOrNull() ?: 1,
            )
            else -> null
        }
    }
}

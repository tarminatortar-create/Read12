package com.readora.app.source.parser

sealed class ParseResult {
    data class Success(val items: List<Map<String, String>>) : ParseResult()
    data class Error(val message: String, val cause: Throwable? = null) : ParseResult()
}

interface ParserEngine {
    fun parse(type: ParserType, definition: ParserDefinition, input: String, rootPath: String? = null): ParseResult
}

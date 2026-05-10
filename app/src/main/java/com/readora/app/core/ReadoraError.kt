package com.readora.app.core

sealed class ReadoraError(open val displayMessage: String) {
    data class NetworkError(
        override val displayMessage: String,
        val cause: Throwable? = null,
    ) : ReadoraError(displayMessage)

    data class SourceError(
        val sourceId: String,
        override val displayMessage: String,
    ) : ReadoraError(displayMessage)

    data class ParseError(
        val sourceId: String,
        override val displayMessage: String,
    ) : ReadoraError(displayMessage)

    data class StorageError(
        override val displayMessage: String,
    ) : ReadoraError(displayMessage)

    data class NotFoundError(
        override val displayMessage: String,
    ) : ReadoraError(displayMessage)

    data class UnknownError(
        val cause: Throwable,
    ) : ReadoraError(cause.message ?: "Something went wrong")
}

fun Throwable.toReadoraError(sourceId: String? = null): ReadoraError {
    val message = message ?: "Unexpected failure"
    return when (this) {
        is java.net.SocketTimeoutException,
        is java.net.UnknownHostException,
        is java.io.IOException -> ReadoraError.NetworkError(message, this)
        is org.json.JSONException -> ReadoraError.ParseError(sourceId.orEmpty(), message)
        is NoSuchElementException -> ReadoraError.NotFoundError(message)
        else -> if (sourceId != null) ReadoraError.SourceError(sourceId, message) else ReadoraError.UnknownError(this)
    }
}

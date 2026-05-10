package com.readora.app.ui.navigation

object ReadoraRoutes {
    const val Home = "home"
    const val Library = "library"
    const val Updates = "updates"
    const val Discover = "discover"
    const val Merge = "merge"
    const val Settings = "settings"

    const val SourceManager = "source_manager"
    const val RepositoryManager = "repository_manager"
    const val SourceMigration = "source_migration"

    const val DetailsPattern = "details/{sourceId}/{comicId}"
    const val ReaderPattern = "reader/{sourceId}/{comicId}/{chapterId}"
    const val ReaderSettings = "settings/reader"
    const val DownloadSettings = "settings/downloads"
    const val SourceSettings = "settings/sources"
    const val LibrarySettings = "settings/library"
    const val About = "settings/about"
    const val Bookmarks = "bookmarks"
    const val Search = "search"
    const val DownloadManager = "download_manager"
    const val ReadingHistory = "reading_history"
    const val Stats = "stats"

    fun details(sourceId: String, comicId: String): String =
        "details/${sourceId.encodeRouteArg()}/${comicId.encodeRouteArg()}"

    fun reader(sourceId: String, comicId: String, chapterId: String): String =
        "reader/${sourceId.encodeRouteArg()}/${comicId.encodeRouteArg()}/${chapterId.encodeRouteArg()}"
}

fun String.encodeRouteArg(): String =
    java.net.URLEncoder.encode(this, Charsets.UTF_8.name())

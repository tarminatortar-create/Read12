package com.readora.app.model

enum class ReadingMode { Webtoon, Paged }

enum class Direction { LeftToRight, RightToLeft }

enum class LibraryStatus(val label: String) {
    Reading("Reading"),
    Planned("Plan to read"),
    Completed("Completed"),
    Paused("Paused"),
}

data class SourceMirror(
    val name: String,
    val quality: Int,
    val chapterCount: Int,
    val language: String,
    val speed: String,
    val isPrimary: Boolean = false,
)

data class Chapter(
    val number: String,
    val title: String,
    val pages: Int,
    val readProgress: Float,
    val downloaded: Boolean,
)

data class Comic(
    val id: String,
    val title: String,
    val subtitle: String,
    val author: String,
    val genres: List<String>,
    val status: LibraryStatus,
    val rating: Float,
    val coverA: Long,
    val coverB: Long,
    val accent: Long,
    val progress: Float,
    val latestChapter: String,
    val nextChapter: String,
    val description: String,
    val sources: List<SourceMirror>,
    val chapters: List<Chapter>,
)

data class SmartShelf(
    val title: String,
    val description: String,
    val count: Int,
    val accent: Long,
)

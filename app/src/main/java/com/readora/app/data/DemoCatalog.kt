package com.readora.app.data

import com.readora.app.model.Chapter
import com.readora.app.model.Comic
import com.readora.app.model.LibraryStatus
import com.readora.app.model.SmartShelf
import com.readora.app.model.SourceMirror

object DemoCatalog {
    val shelves = listOf(
        SmartShelf("Continue", "Unread chapters waiting", 18, 0xFFF9B17AL),
        SmartShelf("Merged", "One title, many mirrors", 9, 0xFF7AD7F9L),
        SmartShelf("Offline vault", "Cached for no-signal reading", 42, 0xFFA7F97AL),
        SmartShelf("Hidden gems", "High rating, low noise", 27, 0xFFFF7AAFL),
    )

    val comics = listOf(
        Comic(
            id = "orbital-saint",
            title = "Orbital Saint",
            subtitle = "A cathedral-city spins above a ruined Earth.",
            author = "Mira Kwon",
            genres = listOf("Sci-fi", "Drama", "Action"),
            status = LibraryStatus.Reading,
            rating = 9.8f,
            coverA = 0xFF172033L,
            coverB = 0xFFB76E79L,
            accent = 0xFFF9B17AL,
            progress = 0.68f,
            latestChapter = "86",
            nextChapter = "72",
            description = "A hand-painted vertical epic with quiet character moments, explosive sky battles, and mirror-synced chapters across multiple sources.",
            sources = listOf(
                SourceMirror("Primary Mirror", 98, 86, "EN", "Fast", true),
                SourceMirror("Archive Mirror", 94, 86, "EN", "Stable"),
                SourceMirror("Fan Mirror", 88, 84, "EN", "Fast"),
            ),
            chapters = chapters(86, downloadedEvery = 4),
        ),
        Comic(
            id = "salt-moon",
            title = "Salt Moon Atelier",
            subtitle = "Cooking magic, family debts, and coastal spirits.",
            author = "Ishaan Vale",
            genres = listOf("Fantasy", "Slice of life", "Food"),
            status = LibraryStatus.Planned,
            rating = 9.5f,
            coverA = 0xFF12332FL,
            coverB = 0xFFE6C27AL,
            accent = 0xFF7AD7F9L,
            progress = 0.12f,
            latestChapter = "41",
            nextChapter = "6",
            description = "A cozy panel-by-panel reader showcase with soft colors, recipes, and chapter notes designed for relaxed evening reading.",
            sources = listOf(
                SourceMirror("Official Sample", 100, 41, "EN", "Stable", true),
                SourceMirror("Community Index", 89, 40, "EN", "Medium"),
            ),
            chapters = chapters(41, downloadedEvery = 5),
        ),
        Comic(
            id = "iron-lotus",
            title = "Iron Lotus Protocol",
            subtitle = "A regression thriller built for page-turning mode.",
            author = "Ren Sato",
            genres = listOf("Regression", "Martial arts", "Thriller"),
            status = LibraryStatus.Reading,
            rating = 9.7f,
            coverA = 0xFF231826L,
            coverB = 0xFFE74D3CL,
            accent = 0xFFFF7A59L,
            progress = 0.44f,
            latestChapter = "132",
            nextChapter = "59",
            description = "Fast combat pages, source priority controls, duplicate chapter detection, and reading-direction switching in one title.",
            sources = listOf(
                SourceMirror("High Quality", 97, 132, "EN", "Medium", true),
                SourceMirror("Fast Updates", 90, 135, "EN", "Fast"),
                SourceMirror("JP Raw Tracker", 85, 138, "JP", "Fast"),
            ),
            chapters = chapters(132, downloadedEvery = 7),
        ),
        Comic(
            id = "paper-kingdom",
            title = "Paper Kingdom Afterlight",
            subtitle = "A librarian fights wars by editing memories.",
            author = "Noa Prism",
            genres = listOf("Mystery", "Supernatural", "Romance"),
            status = LibraryStatus.Completed,
            rating = 9.9f,
            coverA = 0xFF1C2541L,
            coverB = 0xFFB8F2E6L,
            accent = 0xFFB8F2E6L,
            progress = 1f,
            latestChapter = "64",
            nextChapter = "Complete",
            description = "A complete series used to demonstrate backups, finished shelves, intelligent recommendations, and rich metadata.",
            sources = listOf(
                SourceMirror("Complete Mirror", 99, 64, "EN", "Stable", true),
                SourceMirror("Collector Scan", 92, 64, "EN", "Slow"),
            ),
            chapters = chapters(64, downloadedEvery = 3),
        ),
    )

    fun chapters(total: Int, downloadedEvery: Int): List<Chapter> =
        (1..total).map { index ->
            Chapter(
                number = index.toString(),
                title = when {
                    index == total -> "Latest signal"
                    index % 12 == 0 -> "Season gate"
                    index % 7 == 0 -> "Mirror drift"
                    else -> "Chapter $index"
                },
                pages = 28 + (index % 9),
                readProgress = when {
                    index < total / 2 -> 1f
                    index == total / 2 -> 0.45f
                    else -> 0f
                },
                downloaded = index % downloadedEvery == 0,
            )
        }.reversed()
}

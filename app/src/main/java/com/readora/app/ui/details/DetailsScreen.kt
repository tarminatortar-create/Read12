package com.readora.app

import android.os.Bundle
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.graphics.BitmapFactory
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.CallMerge
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.IosShare
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material.icons.rounded.CollectionsBookmark
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.readora.app.data.DemoCatalog
import com.readora.app.core.ReadoraLogger
import com.readora.app.model.Comic
import com.readora.app.model.Direction
import com.readora.app.model.ReadingMode
import com.readora.app.model.SmartShelf
import com.readora.app.source.MangaDexSource
import com.readora.app.source.OnlineChapter
import com.readora.app.source.OnlineComicDetails
import com.readora.app.source.OnlineComicSummary
import com.readora.app.source.OnlinePage
import com.readora.app.source.SourceDescriptor
import com.readora.app.source.SourceKind
import com.readora.app.source.SourceRegistry
import com.readora.app.source.SourceRepository
import com.readora.app.source.SourceRepositoryClient
import com.readora.app.storage.ReadoraPreferences
import com.readora.app.storage.OfflineCacheManager
import com.readora.app.storage.LocalArchiveReader
import com.readora.app.storage.DownloadQueueManager
import com.readora.app.storage.LastOnlineRead
import com.readora.app.storage.QueueStatus
import com.readora.app.storage.QueuedDownload
import com.readora.app.storage.SavedOnlineComic
import com.readora.app.storage.SavedLocalComic
import com.readora.app.storage.SavedMergeGroup
import com.readora.app.storage.SavedReadingProgress
import com.readora.app.ui.theme.Coral
import com.readora.app.ui.theme.Ember
import com.readora.app.ui.theme.Ink
import com.readora.app.ui.theme.InkRaised
import com.readora.app.ui.theme.Mint
import com.readora.app.ui.theme.Paper
import com.readora.app.ui.theme.ReadoraTheme
import com.readora.app.ui.theme.Sky
import com.readora.app.ui.navigation.ReadoraRoutes
import com.readora.app.ui.viewmodel.DiscoverViewModel
import com.readora.app.ui.viewmodel.ReadoraViewModelFactory
import com.readora.app.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

@Composable
fun DetailsScreen(comic: Comic, onBack: () -> Unit, onRead: () -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(start = 18.dp, top = 8.dp, end = 18.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = Paper)
                }
                Text("Series", color = Color(0xFFB7AA99), fontWeight = FontWeight.SemiBold)
            }
        }
        item {
            DetailsHero(comic = comic, onRead = onRead)
        }
        item {
            PremiumPanel {
                SectionTitle("Synopsis", "Metadata stays with the merged title")
                Text(
                    comic.description,
                    color = Color(0xFFE6D9C8),
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
        item {
            SectionTitle("Source mirrors", "Pick priority without splitting your progress")
        }
        items(comic.sources) { source ->
            SourceRow(source.name, source.quality, source.chapterCount, source.language, source.speed, source.isPrimary)
        }
        item {
            SectionTitle("Chapters", "Downloaded chapters and progress are visible")
        }
        items(comic.chapters.take(12)) { chapter ->
            ChapterRow(
                number = chapter.number,
                title = chapter.title,
                pages = chapter.pages,
                readProgress = chapter.readProgress,
                downloaded = chapter.downloaded,
            )
        }
    }
}

@Composable
fun OnlineDetailsScreen(
    initialComic: OnlineComicSummary,
    initialDetails: OnlineComicDetails? = null,
    onBack: () -> Unit,
    onRead: (OnlineChapter, Int?, List<OnlineChapter>) -> Unit,
    onTagSearch: ((String) -> Unit)? = null,
) {
    var comic by remember(initialComic.id) { mutableStateOf(initialComic) }
    val context = LocalContext.current
    val app = context.applicationContext as ReadoraApplication
    val preferences = remember { ReadoraPreferences(context) }
    val bookmarkRepository = remember { app.appContainer.bookmarkRepository }
    val chapterNoteRepository = remember { app.appContainer.chapterNoteRepository }
    val cacheManager = remember { OfflineCacheManager(context) }
    val queueManager = remember { DownloadQueueManager(context) }
    val scope = rememberCoroutineScope()
    val bookmarks by bookmarkRepository.getByComic(comic.sourceId, comic.id).collectAsState(emptyList())
    val notes by chapterNoteRepository.getByComic(comic.sourceId, comic.id).collectAsState(emptyList())
    var inLibrary by remember(comic.id) { mutableStateOf(preferences.isInOnlineLibrary(comic)) }
    var downloadingChapterId by remember(comic.id) { mutableStateOf<String?>(null) }
    var downloadProgressMap by remember(comic.id) { mutableStateOf<Map<String, Float>>(emptyMap()) }
    var queuedIds by remember(comic.id) { mutableStateOf(queueManager.load().map { it.chapterId }.toSet()) }
    var details by remember(comic.id) { mutableStateOf(initialDetails) }
    var loading by remember(comic.id) { mutableStateOf(initialDetails == null) }
    var error by remember(comic.id) { mutableStateOf<String?>(null) }
    var refreshKey by rememberSaveable(comic.id) { mutableStateOf(0) }
    var preferredScanlator by remember(comic.id) { mutableStateOf(preferences.loadPreferredScanlator(comic.sourceId, comic.id)) }
    var chapterLimit by rememberSaveable(comic.id) { mutableStateOf(60) }
    var chapterSearchQuery by rememberSaveable(comic.id) { mutableStateOf("") }
    var similarComics by remember(comic.id) { mutableStateOf<List<OnlineComicSummary>>(emptyList()) }
    var similarLoading by remember(comic.id) { mutableStateOf(false) }
    // Locally-toggled read IDs (persisted to preferences immediately)
    var localReadIds by remember(comic.id) { mutableStateOf<Set<String>>(emptySet()) }
    // Reading-list status: "" | "plan" | "reading" | "completed"
    var readingListStatus by remember(comic.id) { mutableStateOf(preferences.loadReadingListStatus(comic.id)) }
    // User star rating: 0 = unrated, 1-5
    var userRating by remember(comic.id) { mutableStateOf(preferences.loadUserRating(comic.id)) }

    LaunchedEffect(comic.id, refreshKey) {
        if (details != null) return@LaunchedEffect
        loading = true
        error = null
        runCatching { MangaDexSource.details(comic) }
            .onSuccess {
                details = it
                comic = it.summary
            }
            .onFailure { error = it.message ?: "Could not load chapters" }
        loading = false
    }

    // Load similar titles from first tag after details are available
    LaunchedEffect(comic.id, comic.tags) {
        val tag = comic.tags.firstOrNull { it.isNotBlank() } ?: return@LaunchedEffect
        similarLoading = true
        runCatching {
            withContext(Dispatchers.IO) { MangaDexSource.search(tag, 8) }
        }.onSuccess { results ->
            similarComics = results.filter { it.id != comic.id }.take(6)
        }
        similarLoading = false
    }

    LazyColumn(
        contentPadding = PaddingValues(start = 18.dp, top = 8.dp, end = 18.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = Paper)
                }
                Text("${comic.sourceName} source", color = Color(0xFFB7AA99), fontWeight = FontWeight.SemiBold)
            }
        }
        item {
            PremiumPanel {
                Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                    OnlineCover(comic = comic, modifier = Modifier.size(128.dp, 184.dp))
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                        Text(comic.title, color = Paper, fontSize = 27.sp, lineHeight = 29.sp, fontWeight = FontWeight.Black, maxLines = 4, overflow = TextOverflow.Ellipsis)
                        Text("${comic.sourceName} - ${comic.status.ifBlank { "Unknown" }}", color = Color(0xFFCDBFAD))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                            comic.tags.take(5).forEach { tag ->
                                Box(
                                    modifier = if (onTagSearch != null)
                                        Modifier.clickable { onTagSearch(tag) }
                                    else Modifier,
                                ) {
                                    Pill(
                                        tag,
                                        if (onTagSearch != null) Ember.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.08f),
                                    )
                                }
                            }
                        }
                        Button(
                            onClick = { details?.chapters?.firstOrNull()?.let { onRead(it, null, details?.chapters ?: emptyList()) } },
                            enabled = details?.chapters?.isNotEmpty() == true,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Icon(Icons.Rounded.MenuBook, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Read latest")
                        }
                        TextButton(
                            onClick = {
                                preferences.addOnlineLibrary(comic, details?.chapters?.firstOrNull())
                                inLibrary = true
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Icon(if (inLibrary) Icons.Rounded.CheckCircle else Icons.Rounded.Bookmark, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(if (inLibrary) "Saved to library" else "Add to library")
                        }
                        TextButton(
                            onClick = {
                                val chapter = details?.chapters?.firstOrNull()
                                if (chapter != null && downloadingChapterId == null) {
                                    scope.launch {
                                        downloadingChapterId = chapter.id
                                        runCatching {
                                            val pages = MangaDexSource.pages(chapter.id)
                                            withContext(Dispatchers.IO) { cacheManager.cacheChapter(comic, chapter, pages) }
                                        }.onSuccess {
                                            preferences.addOnlineLibrary(comic, chapter)
                                            Toast.makeText(context, "Latest chapter saved offline", Toast.LENGTH_SHORT).show()
                                        }.onFailure {
                                            Toast.makeText(context, it.message ?: "Download failed", Toast.LENGTH_LONG).show()
                                        }
                                        downloadingChapterId = null
                                    }
                                }
                            },
                            enabled = details?.chapters?.isNotEmpty() == true && downloadingChapterId == null,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Icon(Icons.Rounded.CloudDownload, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(if (downloadingChapterId != null) "Saving offline..." else "Save latest offline")
                        }
                        TextButton(
                            onClick = {
                                details?.chapters?.firstOrNull()?.let { chapter ->
                                    queueManager.enqueue(comic, chapter)
                                    queuedIds = queueManager.load().map { it.chapterId }.toSet()
                                    preferences.addOnlineLibrary(comic, chapter)
                                    Toast.makeText(context, "Added latest chapter to download queue", Toast.LENGTH_SHORT).show()
                                }
                            },
                            enabled = details?.chapters?.isNotEmpty() == true,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Icon(Icons.Rounded.Refresh, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Queue latest offline")
                        }
                        TextButton(
                            onClick = {
                                val url = "https://mangadex.org/title/${comic.id}"
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, comic.title)
                                    putExtra(Intent.EXTRA_TEXT, "${comic.title}\n$url")
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share manga"))
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Icon(Icons.Rounded.IosShare, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Share")
                        }
                        // Reading-list status chips
                        val statusOptions = listOf(
                            "plan" to "Plan to Read",
                            "reading" to "Reading",
                            "completed" to "Completed",
                        )
                        Text("Reading list", color = Color(0xFFB7AA99), fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                            statusOptions.forEach { (key, label) ->
                                FilterChip(
                                    selected = readingListStatus == key,
                                    onClick = {
                                        val next = if (readingListStatus == key) "" else key
                                        readingListStatus = next
                                        preferences.saveReadingListStatus(comic.id, next)
                                    },
                                    label = { Text(label, fontSize = 12.sp) },
                                )
                            }
                        }
                        // Star rating
                        Text("My rating", color = Color(0xFFB7AA99), fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            (1..5).forEach { star ->
                                IconButton(
                                    onClick = {
                                        val next = if (userRating == star) 0 else star
                                        userRating = next
                                        preferences.saveUserRating(comic.id, next)
                                    },
                                    modifier = Modifier.size(36.dp),
                                ) {
                                    Icon(
                                        if (star <= userRating) Icons.Rounded.Star else Icons.Rounded.StarOutline,
                                        contentDescription = "$star stars",
                                        tint = if (star <= userRating) Color(0xFFFFD166) else Color(0xFF6B5E4E),
                                        modifier = Modifier.size(24.dp),
                                    )
                                }
                            }
                            if (userRating > 0) {
                                Text(
                                    "$userRating/5",
                                    color = Color(0xFFFFD166),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    modifier = Modifier.align(Alignment.CenterVertically),
                                )
                            }
                        }
                    }
                }
            }
        }
        item {
            PremiumPanel {
                SectionTitle("Source synopsis", "Fetched live from ${comic.sourceName}")
                Text(
                    comic.description.ifBlank { "No description supplied by source." },
                    color = Color(0xFFE6D9C8),
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
        // Resume banner — shown when there is a partially-read or next-unread chapter
        if (details != null) {
            item {
                val resumeChapter = remember(details, localReadIds) {
                    val chapters = details?.chapters ?: return@remember null
                    val fullyReadIds = localReadIds
                    // Prefer the first partially-read chapter (progress > 0 and < 0.97)
                    val partialChapter = chapters.firstOrNull { chapter ->
                        !fullyReadIds.contains(chapter.id) && preferences.loadChapterProgress(comic.sourceId, comic.id, chapter.id).let { p -> p != null && p.fraction > 0.01f && p.fraction < 0.97f }
                    }
                    partialChapter ?: chapters.lastOrNull { !fullyReadIds.contains(it.id) }
                }
                if (resumeChapter != null) {
                val progress = preferences.loadChapterProgress(comic.sourceId, comic.id, resumeChapter.id)
                val isPartial = progress != null && progress.fraction > 0.01f && progress.fraction < 0.97f
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (isPartial) Sky.copy(alpha = 0.14f) else Ember.copy(alpha = 0.12f)
                        )
                        .clickable { onRead(resumeChapter, if (isPartial) progress?.currentPage else null, details!!.chapters) }
                        .padding(14.dp),
                ) {
                    Icon(
                        Icons.Rounded.PlayArrow,
                        contentDescription = null,
                        tint = if (isPartial) Sky else Ember,
                        modifier = Modifier.size(28.dp),
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            if (isPartial) "Continue reading" else "Start reading",
                            color = if (isPartial) Sky else Ember,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                        )
                        Text(
                            "Ch. ${resumeChapter.number} — ${resumeChapter.title}",
                            color = Paper,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        if (isPartial && progress != null) {
                            Spacer(Modifier.height(5.dp))
                            LinearProgressIndicator(
                                progress = { progress.fraction.coerceIn(0f, 1f) },
                                color = Sky,
                                trackColor = Color.White.copy(alpha = 0.08f),
                                modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                            )
                            Text(
                                "Page ${progress.currentPage} / ${progress.totalPages}",
                                color = Color(0xFFB0A090),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 3.dp),
                            )
                        }
                    }
                }
                } // end if (resumeChapter != null)
            }
        } // end if (details != null) — resume banner
        if (bookmarks.isNotEmpty()) {
            item {
                PremiumPanel {
                    SectionTitle("Page bookmarks", "Jump back to saved pages")
                    Spacer(Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        bookmarks.take(8).forEach { bookmark ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                TextButton(
                                    onClick = {
                                        val chapter = details?.chapters?.firstOrNull { it.id == bookmark.chapterId }
                                        if (chapter != null) onRead(chapter, bookmark.pageIndex, details?.chapters ?: emptyList())
                                    },
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text(
                                        "Ch. ${bookmark.chapterNumber} • Page ${bookmark.pageIndex} • ${bookmark.chapterTitle}",
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                                IconButton(
                                    onClick = { scope.launch { bookmarkRepository.delete(bookmark.id) } },
                                ) {
                                    Icon(Icons.Rounded.Delete, contentDescription = "Delete bookmark", tint = Color(0xFFB8AA98))
                                }
                            }
                        }
                    }
                }
            }
        }
        if (notes.isNotEmpty()) {
            item {
                PremiumPanel {
                    SectionTitle("Chapter notes", "Your quick notes")
                    Spacer(Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        notes.take(8).forEach { note ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "Ch. ${note.chapterNumber}${note.pageIndex?.let { " • p.$it" } ?: ""} — ${note.content}",
                                    color = Color(0xFFDCCDBB),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f),
                                )
                                IconButton(onClick = { scope.launch { chapterNoteRepository.delete(note.id) } }) {
                                    Icon(Icons.Rounded.Delete, contentDescription = "Delete note", tint = Color(0xFFB8AA98))
                                }
                            }
                        }
                    }
                }
            }
        }
        // Similar titles section
        if (similarLoading || similarComics.isNotEmpty()) {
            item {
                PremiumPanel {
                    SectionTitle(
                        "You might also like",
                        if (comic.tags.isNotEmpty()) "Based on: ${comic.tags.first()}" else "Similar titles",
                    )
                    Spacer(Modifier.height(10.dp))
                    if (similarLoading) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            repeat(3) {
                                Box(
                                    modifier = Modifier
                                        .size(width = 80.dp, height = 116.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color.White.copy(alpha = 0.06f)),
                                )
                            }
                        }
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(horizontal = 2.dp),
                        ) {
                            items(similarComics) { similar ->
                                Column(
                                    modifier = Modifier
                                        .width(88.dp)
                                        .clickable {
                                            // Navigate within the same screen by switching the comic state
                                            comic = similar
                                        },
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    OnlineCover(
                                        comic = similar,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(0.68f),
                                    )
                                    Text(
                                        similar.title,
                                        color = Paper,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        lineHeight = 14.sp,
                                    )
                                    Text(
                                        similar.sourceName,
                                        color = Sky,
                                        fontSize = 9.sp,
                                        maxLines = 1,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        item {
            val totalChapters = details?.chapters?.size ?: 0
            val readCount = if (totalChapters > 0) localReadIds.intersect(details!!.chapters.map { it.id }.toSet()).size else 0
            val unreadCount = (totalChapters - readCount).coerceAtLeast(0)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Live chapters", color = Paper, fontWeight = FontWeight.Black, fontSize = 18.sp)
                    if (totalChapters > 0) {
                        val estMinutes = totalChapters * 7
                        val estLabel = if (estMinutes >= 60) {
                            val h = estMinutes / 60; val m = estMinutes % 60
                            if (m == 0) "~${h}h to read" else "~${h}h ${m}m to read"
                        } else "~${estMinutes}m to read"
                        Text(
                            buildString {
                                append("$totalChapters chapters")
                                if (readCount > 0) append(" · $readCount read")
                                if (unreadCount > 0) append(" · $unreadCount unread")
                                append(" · $estLabel")
                            },
                            color = Color(0xFFB8AA98),
                            fontSize = 12.sp,
                        )
                    } else {
                        Text("Chapter list from the source feed", color = Color(0xFF9E927F), fontSize = 12.sp)
                    }
                }
                if (unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .background(Ember.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                    ) {
                        Text("$unreadCount new", color = Ember, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
        // Chapter search bar
        if (!details?.chapters.isNullOrEmpty()) {
            item {
                OutlinedTextField(
                    value = chapterSearchQuery,
                    onValueChange = { chapterSearchQuery = it; chapterLimit = 60 },
                    placeholder = { Text("Search chapters…", fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = Color(0xFFB8AA98)) },
                    trailingIcon = {
                        if (chapterSearchQuery.isNotEmpty()) {
                            IconButton(onClick = { chapterSearchQuery = ""; chapterLimit = 60 }) {
                                Icon(Icons.Rounded.Close, contentDescription = "Clear", tint = Color(0xFFB8AA98))
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        when {
            loading -> item { LoadingPanel("Loading chapter feed...") }
            error != null -> item { ErrorPanel(error ?: "Unknown error", onRetry = { refreshKey++ }) }
            details?.chapters.isNullOrEmpty() -> item { ErrorPanel("No readable English chapters returned.", onRetry = onBack) }
            else -> {
                val groups = details!!.chapters.mapNotNull { it.scanlator }.distinct().sorted()
                if (groups.isNotEmpty()) {
                    item {
                        PremiumPanel {
                            SectionTitle("Scanlator filter", "Choose which group to show for this title")
                            Spacer(Modifier.height(10.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                            ) {
                                FilterChip(
                                    selected = preferredScanlator == null,
                                    onClick = {
                                        preferredScanlator = null
                                        preferences.savePreferredScanlator(comic.sourceId, comic.id, null)
                                    },
                                    label = { Text("All") },
                                )
                                groups.forEach { name ->
                                    FilterChip(
                                        selected = preferredScanlator == name,
                                        onClick = {
                                            preferredScanlator = name
                                            preferences.savePreferredScanlator(comic.sourceId, comic.id, name)
                                        },
                                        label = { Text(name) },
                                    )
                                }
                            }
                        }
                    }
                }
                val scanlatorFiltered = if (preferredScanlator.isNullOrBlank()) {
                    details!!.chapters
                } else {
                    details!!.chapters.filter { it.scanlator == preferredScanlator }
                }
                val visibleChapters = if (chapterSearchQuery.isBlank()) {
                    scanlatorFiltered
                } else {
                    val q = chapterSearchQuery.trim()
                    scanlatorFiltered.filter {
                        it.number.contains(q, ignoreCase = true) ||
                        it.title.contains(q, ignoreCase = true)
                    }
                }
                items(visibleChapters.take(chapterLimit)) { chapter ->
                val effectiveProgress = if (localReadIds.contains(chapter.id)) {
                    // Treat manually-marked chapters as fully read
                    preferences.loadChapterProgress(comic.sourceId, comic.id, chapter.id)
                        ?: com.readora.app.storage.SavedReadingProgress(
                            sourceId = comic.sourceId, comicId = comic.id,
                            comicTitle = comic.title, chapterId = chapter.id,
                            chapterNumber = chapter.number, chapterTitle = chapter.title,
                            currentPage = chapter.pages.coerceAtLeast(1),
                            totalPages = chapter.pages.coerceAtLeast(1),
                            updatedAt = System.currentTimeMillis(),
                        )
                } else {
                    preferences.loadChapterProgress(comic.sourceId, comic.id, chapter.id)
                }
                OnlineChapterRow(
                    chapter = chapter,
                    progress = effectiveProgress,
                    cached = cacheManager.isChapterCached(comic.sourceId, comic.id, chapter.id),
                    downloading = downloadingChapterId == chapter.id,
                    queued = queuedIds.contains(chapter.id),
                    downloadProgress = downloadProgressMap[chapter.id],
                    noteSnippet = notes.firstOrNull { it.chapterId == chapter.id }?.content,
                    onClick = { onRead(chapter, null, details?.chapters ?: emptyList()) },
                    onToggleRead = {
                        val pages = chapter.pages.coerceAtLeast(1)
                        if (localReadIds.contains(chapter.id)) {
                            // Unmark: clear saved progress
                            preferences.clearChapterProgress(comic.sourceId, comic.id, chapter.id)
                            localReadIds = localReadIds - chapter.id
                        } else {
                            // Mark read: save 100% progress
                            preferences.saveChapterProgress(
                                comic, chapter, pages, pages,
                            )
                            localReadIds = localReadIds + chapter.id
                        }
                    },
                    onDownload = {
                        if (downloadingChapterId == null) {
                            scope.launch {
                                downloadingChapterId = chapter.id
                                downloadProgressMap = downloadProgressMap + (chapter.id to 0f)
                                runCatching {
                                    val pages = MangaDexSource.pages(chapter.id)
                                    withContext(Dispatchers.IO) {
                                        cacheManager.cacheChapter(comic, chapter, pages) { current, total ->
                                            val fraction = if (total > 0) current.toFloat() / total else 0f
                                            downloadProgressMap = downloadProgressMap + (chapter.id to fraction)
                                        }
                                    }
                                }.onSuccess {
                                    preferences.addOnlineLibrary(comic, chapter)
                                    Toast.makeText(context, "Chapter saved offline", Toast.LENGTH_SHORT).show()
                                }.onFailure {
                                    Toast.makeText(context, it.message ?: "Download failed", Toast.LENGTH_LONG).show()
                                }
                                downloadingChapterId = null
                                downloadProgressMap = downloadProgressMap - chapter.id
                            }
                        }
                    },
                    onQueue = {
                        queueManager.enqueue(comic, chapter)
                        queuedIds = queueManager.load().map { it.chapterId }.toSet()
                        preferences.addOnlineLibrary(comic, chapter)
                        Toast.makeText(context, "Chapter queued for offline download", Toast.LENGTH_SHORT).show()
                    },
                    onShare = {
                        val chapterUrl = "https://mangadex.org/chapter/${chapter.id}"
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "${comic.title} — Ch. ${chapter.number}")
                            putExtra(Intent.EXTRA_TEXT, "${comic.title}\nChapter ${chapter.number}: ${chapter.title}\n$chapterUrl")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share chapter"))
                    },
                    onOpenInBrowser = {
                        val chapterUrl = "https://mangadex.org/chapter/${chapter.id}"
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(chapterUrl))
                        context.startActivity(browserIntent)
                    },
                    onCopyUrl = {
                        val chapterUrl = "https://mangadex.org/chapter/${chapter.id}"
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Chapter URL", chapterUrl))
                        Toast.makeText(context, "URL copied", Toast.LENGTH_SHORT).show()
                    },
                )
            }
                // Load more button when there are chapters beyond the current limit
                if (visibleChapters.size > chapterLimit) {
                    item {
                        TextButton(
                            onClick = { chapterLimit += 60 },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("Load more chapters (${visibleChapters.size - chapterLimit} remaining)")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailsHero(comic: Comic, onRead: () -> Unit) {
    PremiumPanel {
        Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
            MiniCover(comic, Modifier.size(128.dp, 184.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Text(comic.title, color = Paper, fontSize = 28.sp, lineHeight = 30.sp, fontWeight = FontWeight.Black)
                Text("by ${comic.author}", color = Color(0xFFCDBFAD))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    comic.genres.forEach { Pill(it, Color.White.copy(alpha = 0.08f)) }
                }
                RatingPill(comic.rating)
                Button(onClick = onRead, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Rounded.MenuBook, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Open reader")
                }
            }
        }
    }
}

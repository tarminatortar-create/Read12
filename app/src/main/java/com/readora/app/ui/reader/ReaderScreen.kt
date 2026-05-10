package com.readora.app

import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.graphics.BitmapFactory
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Brightness4
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.CallMerge
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.IosShare
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material.icons.rounded.CollectionsBookmark
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.FormatListBulleted
import androidx.compose.material.icons.rounded.Close
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Slider
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
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
import com.readora.app.storage.SettingsSerializer
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
import com.readora.app.data.db.BookmarkEntity
import com.readora.app.data.db.ChapterNoteEntity
import com.readora.app.data.db.ReadingSessionEntity
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
import android.app.Activity
import android.view.WindowManager
import androidx.compose.runtime.DisposableEffect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

@Composable
fun ReaderScreen(
    comic: Comic,
    mode: ReadingMode,
    direction: Direction,
    onModeChange: (ReadingMode) -> Unit,
    onDirectionChange: (Direction) -> Unit,
    onBack: () -> Unit,
) {
    var page by rememberSaveable(comic.id) { mutableStateOf(1) }
    val pageCount = 12
    val context = LocalContext.current
    val settings = remember { SettingsSerializer(context).load() }
    fun nextPage() {
        page = (page + 1).coerceAtMost(pageCount)
    }
    fun previousPage() {
        page = (page - 1).coerceAtLeast(1)
    }
    fun leftTap() {
        if (direction == Direction.RightToLeft) nextPage() else previousPage()
    }
    fun rightTap() {
        if (direction == Direction.RightToLeft) previousPage() else nextPage()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF07080D))
            .onPreviewKeyEvent { event ->
                if (!settings.volumeButtonNavigation) return@onPreviewKeyEvent false
                val native = event.nativeKeyEvent
                if (native.action != android.view.KeyEvent.ACTION_DOWN) return@onPreviewKeyEvent false
                when (native.keyCode) {
                    android.view.KeyEvent.KEYCODE_VOLUME_UP -> {
                        if (settings.volumeButtonInverted) rightTap() else leftTap()
                        true
                    }
                    android.view.KeyEvent.KEYCODE_VOLUME_DOWN -> {
                        if (settings.volumeButtonInverted) leftTap() else rightTap()
                        true
                    }
                    else -> false
                }
            }
            .statusBarsPadding(),
    ) {
        ReaderToolbar(
            title = comic.title,
            mode = mode,
            direction = direction,
            onBack = onBack,
            onModeChange = onModeChange,
            onDirectionChange = onDirectionChange,
        )

        if (mode == ReadingMode.Webtoon) {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f),
            ) {
                itemsIndexed((1..pageCount).toList()) { index, _ ->
                    ReaderPage(comic = comic, index = index + 1, tall = index % 3 != 1)
                }
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f)
                    .padding(14.dp),
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    ReaderPage(comic = comic, index = page, tall = false)
                    Row(modifier = Modifier.matchParentSize()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .clickable(onClick = { leftTap() }),
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .clickable(onClick = { rightTap() }),
                        )
                    }
                }
                Spacer(Modifier.height(14.dp))
                Text("Tap left/right edge to turn pages", color = Color(0xFF9E927F), fontSize = 12.sp)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TextButton(onClick = { previousPage() }) {
                        Text("Prev")
                    }
                    Text("$page / $pageCount", color = Paper, modifier = Modifier.padding(top = 12.dp))
                    TextButton(onClick = { nextPage() }) {
                        Text("Next")
                    }
                }
            }
        }

        ReaderFooter(progress = if (mode == ReadingMode.Webtoon) 0.42f else page / pageCount.toFloat())
    }
}

@Composable
fun LocalReaderScreen(comic: SavedLocalComic?, onBack: () -> Unit) {
    val context = LocalContext.current
    val app = context.applicationContext as ReadoraApplication
    val readingSessionRepository = remember { app.appContainer.readingSessionRepository }
    val archiveReader = remember { LocalArchiveReader(context) }
    var pages by remember(comic?.uri) { mutableStateOf<List<OnlinePage>>(emptyList()) }
    var loading by remember(comic?.uri) { mutableStateOf(true) }
    var error by remember(comic?.uri) { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()
    val startedAt = remember(comic?.uri) { System.currentTimeMillis() }

    LaunchedEffect(comic?.uri) {
        if (comic == null) return@LaunchedEffect
        loading = true
        error = null
        runCatching { withContext(Dispatchers.IO) { archiveReader.pagesFor(comic) } }
            .onSuccess { pages = it }
            .onFailure { error = it.message ?: "Could not open local comic" }
        loading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF07080D))
            .statusBarsPadding(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xF207080D))
                .padding(horizontal = 8.dp, vertical = 8.dp),
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = Paper)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(comic?.title ?: "Local comic", color = Paper, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${comic?.type ?: "Local"} - ${pages.size} natural-sorted pages", color = Color(0xFFCDBFAD), fontSize = 12.sp)
            }
            Pill("Local", Ember.copy(alpha = 0.18f))
        }

        when {
            loading -> Box(Modifier.weight(1f), contentAlignment = Alignment.Center) { LoadingPanel("Opening local archive...") }
            error != null -> Box(Modifier.weight(1f).padding(18.dp), contentAlignment = Alignment.Center) { ErrorPanel(error ?: "Unknown error", onRetry = onBack) }
            pages.isEmpty() -> Box(Modifier.weight(1f).padding(18.dp), contentAlignment = Alignment.Center) { ErrorPanel("No image pages found in this file.", onRetry = onBack) }
            else -> LazyColumn(
                state = listState,
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f),
            ) {
                items(pages) { page ->
                    RemoteImage(url = page.url, contentDescription = "Local page ${page.index}")
                }
            }
        }

        val currentPage = if (pages.isEmpty()) 0 else (listState.firstVisibleItemIndex + 1).coerceIn(1, pages.size)
        ReaderFooter(
            progress = if (pages.isEmpty()) 0f else currentPage / pages.size.toFloat(),
            currentPage = currentPage,
            totalPages = pages.size,
            chapterLabel = comic?.title ?: "Local",
        )
    }

    androidx.compose.runtime.DisposableEffect(comic?.uri) {
        onDispose {
            if (comic != null) {
                val endedAt = System.currentTimeMillis()
                val duration = (endedAt - startedAt).coerceAtLeast(0L)
                if (duration >= 3_000L) {
                    kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                        readingSessionRepository.add(
                            ReadingSessionEntity(
                                sourceId = "local",
                                comicId = comic.uri,
                                comicTitle = comic.title,
                                chapterId = null,
                                chapterNumber = null,
                                startedAt = startedAt,
                                endedAt = endedAt,
                                durationMs = duration,
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OnlineReaderScreen(
    comic: OnlineComicSummary,
    chapter: OnlineChapter,
    onBack: () -> Unit,
    initialPageIndex: Int? = null,
    allChapters: List<OnlineChapter> = emptyList(),
    onNavigateChapter: ((OnlineChapter) -> Unit)? = null,
) {
    val context = LocalContext.current
    val preferences = remember { ReadoraPreferences(context) }
    val app = context.applicationContext as ReadoraApplication
    val bookmarkRepository = remember { app.appContainer.bookmarkRepository }
    val chapterNoteRepository = remember { app.appContainer.chapterNoteRepository }
    val readingSessionRepository = remember { app.appContainer.readingSessionRepository }
    val settingsSerializer = remember { SettingsSerializer(context) }
    val settings = remember { settingsSerializer.load() }
    val cacheManager = remember { OfflineCacheManager(context) }
    val scope = rememberCoroutineScope()
    val savedProgress = remember(chapter.id) { preferences.loadChapterProgress(comic.sourceId, comic.id, chapter.id) }
    val initialIndex = (((initialPageIndex ?: savedProgress?.currentPage ?: 1) - 1).coerceAtLeast(0))
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    var pages by remember(chapter.id) { mutableStateOf<List<OnlinePage>>(emptyList()) }
    var loading by remember(chapter.id) { mutableStateOf(true) }
    var error by remember(chapter.id) { mutableStateOf<String?>(null) }
    var cached by remember(chapter.id) { mutableStateOf(cacheManager.isChapterCached(comic.sourceId, comic.id, chapter.id)) }
    var downloading by remember(chapter.id) { mutableStateOf(false) }
    var refreshKey by rememberSaveable(chapter.id) { mutableStateOf(0) }
    var autoScroll by rememberSaveable(chapter.id) { mutableStateOf(settings.autoScrollEnabled) }
    var noteDraft by rememberSaveable(chapter.id) { mutableStateOf("") }
    var noteBoxOpen by rememberSaveable(chapter.id) { mutableStateOf(false) }
    var bookmarkPanelOpen by rememberSaveable { mutableStateOf(false) }
    val comicBookmarks by bookmarkRepository.getByComic(comic.sourceId, comic.id).collectAsState(emptyList())
    val startedAt = remember(chapter.id) { System.currentTimeMillis() }

    // Reader mode & direction — mutable so user can toggle mid-session
    val modeOverride = remember(comic.sourceId, comic.id) { preferences.loadReaderModeOverride(comic.sourceId, comic.id) }
    var readerMode by rememberSaveable { mutableStateOf(modeOverride ?: settings.readerMode) }   // "webtoon" | "paged" | "paged_double"
    var readingDir by rememberSaveable { mutableStateOf(settings.readingDirection) } // "ltr" | "rtl"
    var spreadPageIndex by rememberSaveable(chapter.id) {
        val saved = savedProgress?.currentPage ?: 1
        mutableStateOf(if (saved % 2 == 0) saved - 1 else saved)
    }
    // Left-edge swipe brightness
    var swipeBrightness by rememberSaveable {
        mutableStateOf(if (settings.defaultBrightness >= 0f) settings.defaultBrightness else 0.5f)
    }
    var brightnessIndicatorVisible by remember { mutableStateOf(false) }
    var brightnessHideJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }
    fun showBrightnessIndicator() {
        brightnessIndicatorVisible = true
        brightnessHideJob?.cancel()
        brightnessHideJob = scope.launch {
            kotlinx.coroutines.delay(1_500)
            brightnessIndicatorVisible = false
        }
    }

    suspend fun isTallPage(url: String): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            java.net.URL(url).openStream().use { stream -> BitmapFactory.decodeStream(stream, null, options) }
            options.outHeight > options.outWidth * 1.7f
        }.getOrDefault(false)
    }

    suspend fun shouldAutoSwitchToWebtoon(): Boolean {
        if (!settings.autoDetectWebtoon) return false
        if (modeOverride != null) return false
        val signalKeywords = listOf("webtoon", "manhwa", "manhua", "scroll", "vertical", "episode")
        if (comic.tags.any { tag -> signalKeywords.any { keyword -> tag.contains(keyword, ignoreCase = true) } }) return true
        if (signalKeywords.any { comic.title.contains(it, ignoreCase = true) }) return true
        return pages.firstOrNull()?.let { page -> isTallPage(page.url) } ?: false
    }

    fun currentPageIndex(): Int =
        if (pages.isEmpty()) 1 else (listState.firstVisibleItemIndex + 1).coerceIn(1, pages.size)

    fun currentSpreadLeft(): Int = if (pages.isEmpty()) 1 else spreadPageIndex.coerceIn(1, pages.size)
    fun currentSpreadRight(): Int = if (pages.isEmpty()) 1 else (currentSpreadLeft() + 1).coerceAtMost(pages.size)
    var zoomLevel by rememberSaveable { mutableStateOf(1f) }
    val animatedZoom by animateFloatAsState(targetValue = zoomLevel, label = "zoom")
    // Webtoon pinch-to-zoom state (independent from paged zoom)
    var webtoonZoom by rememberSaveable { mutableStateOf(1f) }
    val animatedWebtoonZoom by animateFloatAsState(targetValue = webtoonZoom, label = "webtoon_zoom")
    fun goToPage(target: Int) {
        if (pages.isEmpty()) return
        val idx = (target - 1).coerceIn(0, pages.lastIndex)
        scope.launch { listState.scrollToItem(idx) }
    }
    fun goToSpreadPage(target: Int) {
        if (pages.isEmpty()) return
        val safeTarget = target.coerceIn(1, pages.size)
        spreadPageIndex = if (safeTarget % 2 == 0) (safeTarget - 1).coerceAtLeast(1) else safeTarget
    }
    fun nextPage() {
        zoomLevel = 1f
        if (readerMode == "paged_double") goToSpreadPage(currentSpreadLeft() + 2)
        else goToPage(currentPageIndex() + 1)
    }
    fun prevPage() {
        zoomLevel = 1f
        if (readerMode == "paged_double") goToSpreadPage(currentSpreadLeft() - 2)
        else goToPage(currentPageIndex() - 1)
    }
    fun leftTap() = if (readingDir == "rtl") nextPage() else prevPage()
    fun rightTap() = if (readingDir == "rtl") prevPage() else nextPage()

    fun bookmarkCurrentPage() {
        val pageIdx = if (readerMode == "paged_double") currentSpreadLeft() else currentPageIndex()
        scope.launch {
            bookmarkRepository.add(
                BookmarkEntity(
                    sourceId = comic.sourceId,
                    comicId = comic.id,
                    comicTitle = comic.title,
                    chapterId = chapter.id,
                    chapterNumber = chapter.number,
                    chapterTitle = chapter.title,
                    pageIndex = pageIdx,
                    note = null,
                    createdAt = System.currentTimeMillis(),
                )
            )
            android.widget.Toast.makeText(context, "Page ${pageIdx + 1} bookmarked ★", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    // Apply swipeBrightness to window whenever it changes
    val activity = context as? Activity
    LaunchedEffect(swipeBrightness) {
        val window = activity?.window
        val lp = window?.attributes
        if (lp != null) {
            lp.screenBrightness = swipeBrightness.coerceIn(0.01f, 1f)
            window.attributes = lp
        }
    }

    // Apply keepScreenOn and reader brightness from settings
    DisposableEffect(settings.keepScreenOn, settings.defaultBrightness) {
        val window = activity?.window
        if (settings.keepScreenOn) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        if (settings.defaultBrightness >= 0f) {
            val lp = window?.attributes
            if (lp != null) {
                lp.screenBrightness = settings.defaultBrightness
                window.attributes = lp
            }
        }
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            val lp = window?.attributes
            if (lp != null) {
                lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
                window.attributes = lp
            }
        }
    }

    val readerBgColor = remember(settings.readerBackground) {
        when (settings.readerBackground) {
            "black" -> Color(0xFF000000)
            "sepia" -> Color(0xFF2B1F0E)
            "white" -> Color(0xFFF5F0E8)
            else    -> Color(0xFF07080D) // "dark" default
        }
    }

    var hudVisible by remember { mutableStateOf(false) }
    var hudHideJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }
    var pageJumpVisible by remember { mutableStateOf(false) }
    var brightnessSliderVisible by remember { mutableStateOf(false) }
    var chapterDrawerOpen by remember { mutableStateOf(false) }
    // Reading tint: 0 = off, 1 = sepia, 2 = night blue
    var readingTintMode by remember { mutableStateOf(0) }
    val tintOverlayColor = remember(readingTintMode) {
        when (readingTintMode) {
            1    -> Color(0xFFAA7744).copy(alpha = 0.18f) // warm sepia
            2    -> Color(0xFF001144).copy(alpha = 0.28f) // deep night blue
            else -> Color.Transparent
        }
    }
    // Live session timer: ticks every second
    var elapsedSeconds by remember { mutableStateOf(0L) }
    LaunchedEffect(chapter.id) {
        while (true) {
            kotlinx.coroutines.delay(1_000)
            elapsedSeconds = (System.currentTimeMillis() - startedAt) / 1_000L
        }
    }
    // Long-press page info popup state: Pair(url, 0-based index) or null when hidden
    var pageInfoPopup by remember { mutableStateOf<Pair<String, Int>?>(null) }
    fun showHudBriefly() {
        hudVisible = true
        pageJumpVisible = false
        hudHideJob?.cancel()
        hudHideJob = scope.launch {
            kotlinx.coroutines.delay(3_000)
            hudVisible = false
        }
    }

    fun savePageToGallery(url: String, pageIndex: Int) {
        scope.launch(kotlinx.coroutines.Dispatchers.IO) {
            runCatching {
                val stream = java.net.URL(url).openStream()
                val bitmap = android.graphics.BitmapFactory.decodeStream(stream) ?: return@runCatching
                val filename = "readora_${comic.id}_ch${chapter.number}_p${pageIndex + 1}.jpg"
                val values = android.content.ContentValues().apply {
                    put(android.provider.MediaStore.Images.Media.DISPLAY_NAME, filename)
                    put(android.provider.MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(android.provider.MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Readora")
                }
                val resolver = context.contentResolver
                val uri = resolver.insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                if (uri != null) {
                    resolver.openOutputStream(uri)?.use { out ->
                        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 92, out)
                    }
                }
            }.onSuccess {
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    android.widget.Toast.makeText(context, "Page saved to gallery", android.widget.Toast.LENGTH_SHORT).show()
                }
            }.onFailure {
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    android.widget.Toast.makeText(context, "Failed to save page", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    LaunchedEffect(chapter.id, refreshKey) {
        webtoonZoom = 1f
        if (!settings.incognitoMode) {
            preferences.saveLastOnlineRead(comic, chapter)
            preferences.addOnlineLibrary(comic, chapter)
        }
        loading = true
        error = null
        runCatching {
            val cachedPages = cacheManager.cachedPages(comic.sourceId, comic.id, chapter.id)
            if (cachedPages.isNotEmpty()) cachedPages else MangaDexSource.pages(chapter.id)
        }
            .onSuccess { pages = it }
            .onFailure { error = it.message ?: "Could not load pages" }
        loading = false

        if (pages.isNotEmpty() && settings.autoDetectWebtoon && modeOverride == null && readerMode == settings.readerMode) {
            runCatching {
                if (shouldAutoSwitchToWebtoon()) {
                    readerMode = "webtoon"
                    preferences.saveReaderModeOverride(comic.sourceId, comic.id, "webtoon")
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(readerBgColor)
            .onPreviewKeyEvent { event ->
                if (!settings.volumeButtonNavigation) return@onPreviewKeyEvent false
                val native = event.nativeKeyEvent
                if (native.action != android.view.KeyEvent.ACTION_DOWN) return@onPreviewKeyEvent false
                when (native.keyCode) {
                    android.view.KeyEvent.KEYCODE_VOLUME_UP -> {
                        if (readerMode == "paged") {
                            if (settings.volumeButtonInverted) rightTap() else leftTap()
                        } else {
                            val step = if (settings.volumeButtonInverted) 3 else -3
                            scope.launch { listState.animateScrollBy(step * 600f) }
                        }
                        true
                    }
                    android.view.KeyEvent.KEYCODE_VOLUME_DOWN -> {
                        if (readerMode == "paged") {
                            if (settings.volumeButtonInverted) leftTap() else rightTap()
                        } else {
                            val step = if (settings.volumeButtonInverted) -3 else 3
                            scope.launch { listState.animateScrollBy(step * 600f) }
                        }
                        true
                    }
                    else -> false
                }
            }
            .statusBarsPadding(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xF207080D))
                .padding(horizontal = 8.dp, vertical = 8.dp),
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = Paper)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(comic.title, color = Paper, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = (18 * settings.readerFontScale).sp)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text("${comic.sourceName} - Chapter ${chapter.number}", color = Color(0xFFCDBFAD), fontSize = (12 * settings.readerFontScale).sp)
                    val h = elapsedSeconds / 3600L
                    val m = (elapsedSeconds % 3600L) / 60L
                    val s = elapsedSeconds % 60L
                    val timerLabel = if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%d:%02d".format(m, s)
                    Text(
                        "⏱ $timerLabel",
                        color = Ember.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            AssistChip(
                onClick = {
                    if (!downloading && pages.isNotEmpty()) {
                        scope.launch {
                            downloading = true
                            runCatching { withContext(Dispatchers.IO) { cacheManager.cacheChapter(comic, chapter, pages) } }
                                .onSuccess { cached = true }
                                .onFailure { error = it.message ?: "Download failed" }
                            downloading = false
                        }
                    }
                },
                label = { Text(if (cached) "Offline" else if (downloading) "Saving" else "Download") },
                leadingIcon = { Icon(Icons.Rounded.CloudDownload, contentDescription = null, modifier = Modifier.size(18.dp)) },
            )
            AssistChip(
                onClick = { autoScroll = !autoScroll },
                label = { Text(if (autoScroll) "Auto-scroll: ON" else "Auto-scroll: OFF") },
                leadingIcon = { Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.padding(start = 8.dp),
            )
            // Bookmark: tap opens panel, + button adds bookmark at current page
            AssistChip(
                onClick = { bookmarkPanelOpen = !bookmarkPanelOpen },
                label = {
                    Text(
                        if (comicBookmarks.isEmpty()) "Bookmarks"
                        else "Bookmarks (${comicBookmarks.size})"
                    )
                },
                leadingIcon = { Icon(Icons.Rounded.Bookmark, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.padding(start = 8.dp),
            )
            IconButton(
                onClick = {
                    scope.launch {
                        bookmarkRepository.add(
                            BookmarkEntity(
                                sourceId = comic.sourceId,
                                comicId = comic.id,
                                comicTitle = comic.title,
                                chapterId = chapter.id,
                                chapterNumber = chapter.number,
                                chapterTitle = chapter.title,
                                pageIndex = if (readerMode == "paged_double") currentSpreadLeft() else currentPageIndex(),
                                note = null,
                                createdAt = System.currentTimeMillis(),
                            ),
                        )
                        android.widget.Toast.makeText(context, "Bookmarked p.${currentPageIndex()}", android.widget.Toast.LENGTH_SHORT).show()
                    }
                },
            ) {
                Icon(Icons.Rounded.Bookmark, contentDescription = "Add bookmark", tint = Paper, modifier = Modifier.size(20.dp))
            }
            AssistChip(
                onClick = { noteBoxOpen = !noteBoxOpen },
                label = { Text("Note") },
                leadingIcon = { Icon(Icons.Rounded.Edit, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.padding(start = 8.dp),
            )
            if (allChapters.isNotEmpty() && onNavigateChapter != null) {
                IconButton(
                    onClick = { chapterDrawerOpen = true },
                    modifier = Modifier.padding(start = 4.dp),
                ) {
                    Icon(Icons.Rounded.FormatListBulleted, contentDescription = "Chapter list", tint = Paper)
                }
            }
        }

        // Mode / direction quick-toggle strip
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xDD07080D))
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 10.dp, vertical = 4.dp),
        ) {
            FilterChip(
                selected = readerMode == "webtoon",
                onClick = {
                    readerMode = "webtoon"
                    preferences.saveReaderModeOverride(comic.sourceId, comic.id, "webtoon")
                },
                label = { Text("Webtoon", fontSize = 11.sp) },
            )
            FilterChip(
                selected = readerMode == "paged",
                onClick = {
                    readerMode = "paged"
                    preferences.saveReaderModeOverride(comic.sourceId, comic.id, "paged")
                },
                label = { Text("Paged", fontSize = 11.sp) },
            )
            FilterChip(
                selected = readerMode == "paged_double",
                onClick = {
                    readerMode = "paged_double"
                    preferences.saveReaderModeOverride(comic.sourceId, comic.id, "paged_double")
                },
                label = { Text("Spread", fontSize = 11.sp) },
            )
            Spacer(Modifier.width(8.dp))
            FilterChip(
                selected = readingDir == "ltr",
                onClick = { readingDir = "ltr" },
                label = { Text("LTR", fontSize = 11.sp) },
            )
            FilterChip(
                selected = readingDir == "rtl",
                onClick = { readingDir = "rtl" },
                label = { Text("RTL", fontSize = 11.sp) },
            )
            Spacer(Modifier.width(8.dp))
            // Reading tint cycle button
            FilterChip(
                selected = readingTintMode != 0,
                onClick = { readingTintMode = (readingTintMode + 1) % 3 },
                label = {
                    Text(
                        when (readingTintMode) {
                            1    -> "Sepia"
                            2    -> "Night"
                            else -> "Tint"
                        },
                        fontSize = 11.sp,
                    )
                },
            )
        }

        // Chapter navigation strip — only shown when a chapter list was provided
        if (allChapters.isNotEmpty() && onNavigateChapter != null) {
            val currentIndex = allChapters.indexOfFirst { it.id == chapter.id }
            val prevChapter = if (currentIndex < allChapters.lastIndex) allChapters[currentIndex + 1] else null
            val nextChapter = if (currentIndex > 0) allChapters[currentIndex - 1] else null
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xEE07080D))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
            ) {
                IconButton(
                    onClick = { prevChapter?.let(onNavigateChapter) },
                    enabled = prevChapter != null,
                ) {
                    Icon(
                        Icons.Rounded.SkipPrevious,
                        contentDescription = "Previous chapter",
                        tint = if (prevChapter != null) Paper else Paper.copy(alpha = 0.25f),
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        "Ch. ${chapter.number}" + if (chapter.title.isNotBlank()) " — ${chapter.title}" else "",
                        color = Paper,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        buildString {
                            if (prevChapter != null) append("← Ch. ${prevChapter.number}  ")
                            if (nextChapter != null) append("Ch. ${nextChapter.number} →")
                        }.trim(),
                        color = Color(0xFF9E927F),
                        fontSize = 11.sp,
                        maxLines = 1,
                    )
                }
                IconButton(
                    onClick = { nextChapter?.let(onNavigateChapter) },
                    enabled = nextChapter != null,
                ) {
                    Icon(
                        Icons.Rounded.SkipNext,
                        contentDescription = "Next chapter",
                        tint = if (nextChapter != null) Paper else Paper.copy(alpha = 0.25f),
                    )
                }
            }
        }

        val density = LocalDensity.current
        // Auto-scroll: only active in webtoon mode; uses autoScrollSpeed (dp/s) from settings
        LaunchedEffect(autoScroll, readerMode) {
            if (!autoScroll || readerMode != "webtoon") return@LaunchedEffect
            val pixelsPerSecond = with(density) { settings.autoScrollSpeed.dp.toPx() }
            val frameMs = 16L
            val pixelsPerFrame = pixelsPerSecond * frameMs / 1000f
            while (autoScroll && readerMode == "webtoon") {
                listState.animateScrollBy(pixelsPerFrame)
                kotlinx.coroutines.delay(frameMs)
            }
        }

        if (noteBoxOpen) {
            PremiumPanel(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                OutlinedTextField(
                    value = noteDraft,
                    onValueChange = { noteDraft = it },
                    label = { Text("Chapter note") },
                    placeholder = { Text("Write a short note for this chapter/page") },
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 8.dp),
                ) {
                    Button(
                        onClick = {
                            val content = noteDraft.trim()
                            if (content.isNotBlank()) {
                                scope.launch {
                                    chapterNoteRepository.add(
                                        ChapterNoteEntity(
                                            sourceId = comic.sourceId,
                                            comicId = comic.id,
                                            chapterId = chapter.id,
                                            chapterNumber = chapter.number,
                                            chapterTitle = chapter.title,
                                            pageIndex = currentPageIndex(),
                                            content = content,
                                            createdAt = System.currentTimeMillis(),
                                        ),
                                    )
                                }
                                noteDraft = ""
                                noteBoxOpen = false
                            }
                        },
                        enabled = noteDraft.trim().isNotBlank(),
                    ) {
                        Text("Save note")
                    }
                    TextButton(onClick = { noteBoxOpen = false }) {
                        Text("Cancel")
                    }
                }
            }
        }

        // Bookmark panel
        if (bookmarkPanelOpen) {
            PremiumPanel(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        "Bookmarks for ${comic.title}",
                        color = Paper,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    IconButton(onClick = { bookmarkPanelOpen = false }) {
                        Icon(Icons.Rounded.CheckCircle, contentDescription = "Close", tint = Color(0xFF9E927F), modifier = Modifier.size(18.dp))
                    }
                }
                if (comicBookmarks.isEmpty()) {
                    Text("No bookmarks yet. Tap + to add one at the current page.", color = Color(0xFF9E927F), fontSize = 12.sp)
                } else {
                    comicBookmarks.forEach { bm ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val target = bm.pageIndex.coerceIn(1, pages.size)
                                    scope.launch { listState.scrollToItem(target - 1) }
                                }
                                .padding(vertical = 4.dp),
                        ) {
                            Icon(Icons.Rounded.Bookmark, contentDescription = null, tint = Ember, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Ch. ${bm.chapterNumber}" + (if (bm.chapterTitle.isNotBlank()) " — ${bm.chapterTitle}" else "") + "  •  p.${bm.pageIndex}",
                                    color = Paper,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                val ago = run {
                                    val diff = System.currentTimeMillis() - bm.createdAt
                                    when {
                                        diff < 60_000 -> "just now"
                                        diff < 3_600_000 -> "${diff / 60_000}m ago"
                                        diff < 86_400_000 -> "${diff / 3_600_000}h ago"
                                        else -> "${diff / 86_400_000}d ago"
                                    }
                                }
                                Text(ago, color = Color(0xFF9E927F), fontSize = 11.sp)
                            }
                            IconButton(
                                onClick = { scope.launch { bookmarkRepository.delete(bm.id) } },
                                modifier = Modifier.size(32.dp),
                            ) {
                                Icon(Icons.Rounded.Delete, contentDescription = "Delete bookmark", tint = Color(0xFF9E927F), modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }

        Box(modifier = Modifier.weight(1f)) {
        when {
            loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { LoadingPanel("Fetching page URLs...") }
            error != null -> Box(Modifier.fillMaxSize().padding(18.dp), contentAlignment = Alignment.Center) { ErrorPanel(error ?: "Unknown error", onRetry = { refreshKey++ }) }
            pages.isEmpty() -> Box(Modifier.fillMaxSize().padding(18.dp), contentAlignment = Alignment.Center) { ErrorPanel("No image pages returned.", onRetry = { refreshKey++ }) }
            readerMode == "paged_double" -> {
                val leftPageIndex = currentSpreadLeft()
                val rightPageIndex = currentSpreadRight()
                Box(modifier = Modifier.fillMaxSize().background(readerBgColor)) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .pointerInput(leftPageIndex) {
                                    detectTapGestures(
                                        onTap = { leftTap() },
                                        onDoubleTap = { zoomLevel = if (zoomLevel > 1f) 1f else 2.5f },
                                        onLongPress = { pageInfoPopup = pages[leftPageIndex - 1].url to leftPageIndex - 1 },
                                    )
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            RemoteImage(
                                url = pages[leftPageIndex - 1].url,
                                contentDescription = "Page $leftPageIndex",
                            )
                        }
                        if (rightPageIndex <= pages.size) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .pointerInput(rightPageIndex) {
                                        detectTapGestures(
                                            onTap = { rightTap() },
                                            onDoubleTap = { zoomLevel = if (zoomLevel > 1f) 1f else 2.5f },
                                            onLongPress = { pageInfoPopup = pages[rightPageIndex - 1].url to rightPageIndex - 1 },
                                        )
                                    },
                                contentAlignment = Alignment.Center,
                            ) {
                                RemoteImage(
                                    url = pages[rightPageIndex - 1].url,
                                    contentDescription = "Page $rightPageIndex",
                                )
                            }
                        }
                    }
                    Row(modifier = Modifier.matchParentSize()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .pointerInput(readingDir) {
                                    detectTapGestures(
                                        onTap = { leftTap() },
                                        onDoubleTap = { zoomLevel = if (zoomLevel > 1f) 1f else 2.5f },
                                    )
                                },
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = { showHudBriefly() },
                                        onDoubleTap = { zoomLevel = if (zoomLevel > 1f) 1f else 2.5f },
                                        onLongPress = { bookmarkCurrentPage() },
                                    )
                                },
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .pointerInput(readingDir) {
                                    detectTapGestures(
                                        onTap = { rightTap() },
                                        onDoubleTap = { zoomLevel = if (zoomLevel > 1f) 1f else 2.5f },
                                    )
                                },
                        )
                    }
                    if (hudVisible) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 10.dp)
                                .padding(horizontal = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            if (pageJumpVisible && pages.size > 1) {
                                Slider(
                                    value = leftPageIndex.toFloat(),
                                    onValueChange = { goToPage(it.toInt()) },
                                    valueRange = 1f..pages.size.toFloat(),
                                    steps = (pages.size - 2).coerceAtLeast(0),
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                            if (brightnessSliderVisible) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Icon(Icons.Rounded.Brightness4, contentDescription = "Brightness", tint = Paper.copy(alpha = 0.85f), modifier = Modifier.size(18.dp))
                                    Slider(
                                        value = swipeBrightness,
                                        onValueChange = { swipeBrightness = it },
                                        valueRange = 0.01f..1f,
                                        modifier = Modifier.weight(1f),
                                    )
                                    Text("${(swipeBrightness * 100).toInt()}%", color = Paper.copy(alpha = 0.8f), fontSize = 11.sp)
                                }
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    "${leftPageIndex}${if (rightPageIndex <= pages.size) "–$rightPageIndex" else ""} / ${pages.size}",
                                    color = Paper.copy(alpha = 0.9f),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier
                                        .background(Color(0xCC000000), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 12.dp, vertical = 5.dp)
                                        .clickable { pageJumpVisible = !pageJumpVisible },
                                )
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xCC000000), RoundedCornerShape(8.dp))
                                        .clickable { brightnessSliderVisible = !brightnessSliderVisible }
                                        .padding(horizontal = 10.dp, vertical = 5.dp),
                                ) {
                                    Icon(Icons.Rounded.Brightness4, contentDescription = "Toggle brightness", tint = if (brightnessSliderVisible) Ember else Paper.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                                }
                            }
                            if (zoomLevel > 1f) {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Double-tap to reset zoom",
                                    color = Paper.copy(alpha = 0.7f),
                                    fontSize = 11.sp,
                                    modifier = Modifier
                                        .background(Color(0x99000000), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp),
                                )
                            }
                        }
                    }
                }
            }
            readerMode == "paged" -> {
                // Paged mode: show one page at a time, tap zones for navigation + double-tap zoom
                val curPage = currentPageIndex()
                Box(modifier = Modifier.fillMaxSize().background(readerBgColor)) {
                    // Keep the LazyColumn to preserve scroll state; only the current page is visible
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        userScrollEnabled = false,
                    ) {
                        items(pages) { page ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillParentMaxHeight(),
                                contentAlignment = Alignment.Center,
                            ) {
                                // Page transition animation wrapper
                                @OptIn(ExperimentalAnimationApi::class)
                                AnimatedContent(
                                    targetState = page.url,
                                    transitionSpec = {
                                        when (settings.pageTransition) {
                                            "fade" -> (fadeIn(tween(220)) togetherWith fadeOut(tween(220)))
                                            "slide" -> (
                                                slideInHorizontally(tween(220)) { it / 4 } + fadeIn(tween(220)) togetherWith
                                                slideOutHorizontally(tween(220)) { -it / 4 } + fadeOut(tween(220))
                                            )
                                            else -> (fadeIn(tween(0)) togetherWith fadeOut(tween(0)))
                                        }
                                    },
                                    label = "page_transition",
                                ) { url ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .graphicsLayer {
                                                scaleX = animatedZoom
                                                scaleY = animatedZoom
                                            }
                                            .pointerInput(url) {
                                                detectTapGestures(
                                                    onLongPress = { pageInfoPopup = url to page.index },
                                                )
                                            },
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        RemoteImage(
                                            url = url,
                                            contentDescription = "Page ${page.index}",
                                        )
                                    }
                                }
                            }
                        }
                    }
                    // Gesture overlay: left/right thirds navigate, center third handles double-tap zoom
                    Row(modifier = Modifier.matchParentSize()) {
                        // Left tap zone
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .pointerInput(readingDir) {
                                    detectTapGestures(
                                        onTap = { leftTap() },
                                        onDoubleTap = { zoomLevel = if (zoomLevel > 1f) 1f else 2.5f },
                                    )
                                },
                        )
                        // Centre zone — single-tap toggles HUD, double-tap zooms, long-press bookmarks
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = { showHudBriefly() },
                                        onDoubleTap = { zoomLevel = if (zoomLevel > 1f) 1f else 2.5f },
                                        onLongPress = { bookmarkCurrentPage() },
                                    )
                                },
                        )
                        // Right tap zone
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .pointerInput(readingDir) {
                                    detectTapGestures(
                                        onTap = { rightTap() },
                                        onDoubleTap = { zoomLevel = if (zoomLevel > 1f) 1f else 2.5f },
                                    )
                                },
                        )
                    }
                    // Page HUD overlay — shown briefly on centre tap
                    if (hudVisible) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 10.dp)
                                .padding(horizontal = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            if (pageJumpVisible && pages.size > 1) {
                                Slider(
                                    value = curPage.toFloat(),
                                    onValueChange = { goToPage(it.toInt()) },
                                    valueRange = 1f..pages.size.toFloat(),
                                    steps = (pages.size - 2).coerceAtLeast(0),
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                            if (brightnessSliderVisible) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Icon(Icons.Rounded.Brightness4, contentDescription = "Brightness", tint = Paper.copy(alpha = 0.85f), modifier = Modifier.size(18.dp))
                                    Slider(
                                        value = swipeBrightness,
                                        onValueChange = { swipeBrightness = it },
                                        valueRange = 0.01f..1f,
                                        modifier = Modifier.weight(1f),
                                    )
                                    Text("${(swipeBrightness * 100).toInt()}%", color = Paper.copy(alpha = 0.8f), fontSize = 11.sp)
                                }
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    "$curPage / ${pages.size}",
                                    color = Paper.copy(alpha = 0.9f),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier
                                        .background(Color(0xCC000000), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 12.dp, vertical = 5.dp)
                                        .clickable { pageJumpVisible = !pageJumpVisible },
                                )
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xCC000000), RoundedCornerShape(8.dp))
                                        .clickable { brightnessSliderVisible = !brightnessSliderVisible }
                                        .padding(horizontal = 10.dp, vertical = 5.dp),
                                ) {
                                    Icon(Icons.Rounded.Brightness4, contentDescription = "Toggle brightness", tint = if (brightnessSliderVisible) Ember else Paper.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                                }
                            }
                            if (zoomLevel > 1f) {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Double-tap to reset zoom",
                                    color = Paper.copy(alpha = 0.7f),
                                    fontSize = 11.sp,
                                    modifier = Modifier
                                        .background(Color(0x99000000), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp),
                                )
                            }
                        }
                    }
                }
            }
            else -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, _, zoom, _ ->
                            webtoonZoom = (webtoonZoom * zoom).coerceIn(0.75f, 3f)
                        }
                    },
            ) {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = animatedWebtoonZoom
                            scaleY = animatedWebtoonZoom
                        },
                ) {
                    items(pages) { page ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .pointerInput(page.url) {
                                    detectTapGestures(
                                        onTap = { showHudBriefly() },
                                        onLongPress = { pageInfoPopup = page.url to page.index },
                                    )
                                },
                        ) {
                            RemoteImage(url = page.url, contentDescription = "Page ${page.index}")
                        }
                    }
                }
                // Continuous right-edge scroll progress bar (webtoon/long-strip mode)
                if (pages.isNotEmpty()) {
                    val scrollFraction = (listState.firstVisibleItemIndex.toFloat() / (pages.size - 1).coerceAtLeast(1)).coerceIn(0f, 1f)
                    val animatedFraction by animateFloatAsState(targetValue = scrollFraction, label = "webtoon_progress")
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxHeight()
                            .width(3.dp)
                            .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(2.dp)),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(animatedFraction.coerceAtLeast(0.03f))
                                .background(Ember.copy(alpha = 0.7f), RoundedCornerShape(2.dp)),
                        )
                    }
                }
                // Webtoon HUD — page position shown briefly on tap
                // Scroll-to-top FAB — shown in webtoon mode when past first 3 pages
                if (pages.isNotEmpty() && listState.firstVisibleItemIndex >= 3) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 12.dp, end = 12.dp)
                            .size(40.dp)
                            .background(Color(0xCC000000), RoundedCornerShape(12.dp))
                            .clickable { scope.launch { listState.animateScrollToItem(0) } },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Rounded.KeyboardArrowUp,
                            contentDescription = "Scroll to top",
                            tint = Paper.copy(alpha = 0.9f),
                            modifier = Modifier.size(22.dp),
                        )
                    }
                }
                if (pages.isNotEmpty() && hudVisible) {
                    val webtoonPage = (listState.firstVisibleItemIndex + 1).coerceIn(1, pages.size)
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 10.dp)
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        if (pageJumpVisible && pages.size > 1) {
                            Slider(
                                value = webtoonPage.toFloat(),
                                onValueChange = { goToPage(it.toInt()) },
                                valueRange = 1f..pages.size.toFloat(),
                                steps = (pages.size - 2).coerceAtLeast(0),
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                        if (brightnessSliderVisible) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Icon(Icons.Rounded.Brightness4, contentDescription = "Brightness", tint = Paper.copy(alpha = 0.85f), modifier = Modifier.size(18.dp))
                                Slider(
                                    value = swipeBrightness,
                                    onValueChange = { swipeBrightness = it },
                                    valueRange = 0.01f..1f,
                                    modifier = Modifier.weight(1f),
                                )
                                Text("${(swipeBrightness * 100).toInt()}%", color = Paper.copy(alpha = 0.8f), fontSize = 11.sp)
                            }
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                "$webtoonPage / ${pages.size}",
                                color = Paper.copy(alpha = 0.9f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .background(Color(0xCC000000), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 5.dp)
                                    .clickable { pageJumpVisible = !pageJumpVisible },
                            )
                            Box(
                                modifier = Modifier
                                    .background(Color(0xCC000000), RoundedCornerShape(8.dp))
                                    .clickable { brightnessSliderVisible = !brightnessSliderVisible }
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                            ) {
                                Icon(Icons.Rounded.Brightness4, contentDescription = "Toggle brightness", tint = if (brightnessSliderVisible) Ember else Paper.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
        // Left-edge brightness swipe strip (20% width, full height, transparent except when dragging)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.20f)
                .align(Alignment.CenterStart)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { showBrightnessIndicator() },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            // Drag up = increase brightness
                            val delta = -dragAmount.y / size.height
                            swipeBrightness = (swipeBrightness + delta).coerceIn(0.01f, 1f)
                            showBrightnessIndicator()
                        },
                    )
                },
        )
        // Brightness indicator pill on the left
        if (brightnessIndicatorVisible) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 10.dp)
                    .background(Color(0xBB000000), RoundedCornerShape(10.dp))
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Brightness4,
                    contentDescription = "Brightness",
                    tint = Paper,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    "${(swipeBrightness * 100).toInt()}%",
                    color = Paper,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        // Reading tint overlay — covers the full content area (sepia / night mode)
        if (tintOverlayColor != Color.Transparent) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(tintOverlayColor),
            )
        }
        } // end outer brightness Box

        val currentPage = if (pages.isEmpty()) 0 else if (readerMode == "paged_double") currentSpreadLeft() else currentPageIndex()
        LaunchedEffect(currentPage, pages.size) {
            if (!settings.incognitoMode && currentPage > 0 && pages.isNotEmpty()) {
                preferences.saveChapterProgress(comic, chapter, currentPage, pages.size)
            }
        }
        ReaderFooter(
            progress = if (pages.isEmpty()) 0f else currentPage / pages.size.toFloat(),
            currentPage = currentPage,
            totalPages = pages.size,
            chapterLabel = "Ch. ${chapter.number}" + if (chapter.title.isNotBlank()) " — ${chapter.title}" else "",
        )

        // Chapter end summary card — shown when last page is reached
        val atLastPage = pages.isNotEmpty() && currentPage >= pages.size
        if (atLastPage && allChapters.isNotEmpty() && onNavigateChapter != null) {
            val currentIndex = allChapters.indexOfFirst { it.id == chapter.id }
            val nextChapter = if (currentIndex > 0) allChapters[currentIndex - 1] else null
            val readMinutes = (elapsedSeconds / 60L).coerceAtLeast(1L)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xF007080D))
                    .padding(horizontal = 18.dp, vertical = 14.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = Mint, modifier = Modifier.size(20.dp))
                        Text(
                            "Chapter ${chapter.number} complete!",
                            color = Mint,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                        )
                    }
                    Text(
                        "${chapter.title.ifBlank { "Ch. ${chapter.number}" }} · ${pages.size} pages · ${readMinutes}m read",
                        color = Color(0xFF9E9080),
                        fontSize = 12.sp,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (nextChapter != null) {
                            Button(
                                onClick = { onNavigateChapter(nextChapter) },
                                modifier = Modifier.weight(1f),
                            ) {
                                Icon(Icons.Rounded.SkipNext, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Ch. ${nextChapter.number}", maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                        OutlinedButton(
                            onClick = onBack,
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("Back", maxLines = 1)
                        }
                    }
                }
            }
        }
    }

    // Long-press page info dialog
    // Chapter navigation drawer overlay
    if (chapterDrawerOpen && allChapters.isNotEmpty() && onNavigateChapter != null) {
        val currentIndex = allChapters.indexOfFirst { it.id == chapter.id }
        val drawerListState = androidx.compose.foundation.lazy.rememberLazyListState(
            initialFirstVisibleItemIndex = (currentIndex - 3).coerceAtLeast(0)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xCC000000))
                .clickable(onClick = { chapterDrawerOpen = false }),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.75f)
                    .align(Alignment.BottomCenter)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(Color(0xFF12131A))
                    .clickable(onClick = {}) // Consume clicks so backdrop click above doesn't fire
                    .navigationBarsPadding(),
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 14.dp),
                    ) {
                        Column {
                            Text(
                                "Chapters",
                                color = Paper,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                            )
                            Text(
                                "${allChapters.size} chapters" +
                                    if (currentIndex >= 0) " · Reading Ch. ${chapter.number}" else "",
                                color = Color(0xFF9E9080),
                                fontSize = 12.sp,
                            )
                        }
                        IconButton(onClick = { chapterDrawerOpen = false }) {
                            Icon(Icons.Rounded.Close, contentDescription = "Close", tint = Color(0xFF9E9080))
                        }
                    }
                    androidx.compose.material3.HorizontalDivider(color = Color(0xFF2A2B35))
                    // Chapter list
                    LazyColumn(
                        state = drawerListState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 6.dp),
                    ) {
                        itemsIndexed(allChapters) { idx, ch ->
                            val isCurrent = ch.id == chapter.id
                            val readProgress = preferences.loadChapterProgress(comic.sourceId, comic.id, ch.id)
                            val isRead = readProgress != null && readProgress.currentPage >= (readProgress.totalPages - 1).coerceAtLeast(0)
                            val isInProgress = readProgress != null && !isRead
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (isCurrent) Ember.copy(alpha = 0.10f) else Color.Transparent
                                    )
                                    .clickable {
                                        onNavigateChapter(ch)
                                        chapterDrawerOpen = false
                                    }
                                    .padding(horizontal = 18.dp, vertical = 10.dp),
                            ) {
                                // Status indicator dot
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isCurrent  -> Ember
                                                isRead     -> Color(0xFF4CAF50)
                                                isInProgress -> Color(0xFFFFB347)
                                                else       -> Color(0xFF3A3B45)
                                            }
                                        ),
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Ch. ${ch.number}" + if (ch.title.isNotBlank()) " — ${ch.title}" else "",
                                        color = if (isCurrent) Ember else Paper,
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 14.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    if (isInProgress && readProgress != null) {
                                        Text(
                                            "p.${readProgress.currentPage + 1}/${readProgress.totalPages}",
                                            color = Color(0xFFFFB347),
                                            fontSize = 11.sp,
                                        )
                                    } else if (isRead) {
                                        Text("Read", color = Color(0xFF4CAF50), fontSize = 11.sp)
                                    }
                                }
                                if (isCurrent) {
                                    Icon(
                                        Icons.Rounded.PlayArrow,
                                        contentDescription = null,
                                        tint = Ember,
                                        modifier = Modifier.size(16.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    pageInfoPopup?.let { (url, idx) ->
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { pageInfoPopup = null },
            title = { Text("Page ${idx + 1} of ${pages.size}", color = Paper, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Chapter ${chapter.number}" + if (chapter.title.isNotBlank()) " — ${chapter.title}" else "", color = Color(0xFFCDBFAD), fontSize = 13.sp)
                    Text(url, color = Color(0xFF9E9080), fontSize = 11.sp, maxLines = 3, overflow = TextOverflow.Ellipsis)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    savePageToGallery(url, idx)
                    pageInfoPopup = null
                }) {
                    Text("Save to gallery", color = Ember)
                }
            },
            dismissButton = {
                TextButton(onClick = { pageInfoPopup = null }) {
                    Text("Close", color = Color(0xFF9E9080))
                }
            },
        )
    }

    androidx.compose.runtime.DisposableEffect(chapter.id) {
        onDispose {
            if (!settings.incognitoMode) {
                val endedAt = System.currentTimeMillis()
                val duration = (endedAt - startedAt).coerceAtLeast(0L)
                if (duration >= 3_000L) {
                    scope.launch(kotlinx.coroutines.Dispatchers.IO) {
                        readingSessionRepository.add(
                            ReadingSessionEntity(
                                sourceId = comic.sourceId,
                                comicId = comic.id,
                                comicTitle = comic.title,
                                chapterId = chapter.id,
                                chapterNumber = chapter.number,
                                startedAt = startedAt,
                                endedAt = endedAt,
                                durationMs = duration,
                            )
                        )
                    }
                }
            }
        }
    }
}

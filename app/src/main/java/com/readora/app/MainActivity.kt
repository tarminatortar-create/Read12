package com.readora.app

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
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
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
import com.readora.app.source.api.OnlineSource
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
import com.readora.app.ui.settings.RepositoryManagerScreen
import com.readora.app.ui.settings.MigrationScreen
import com.readora.app.ui.bookmarks.BookmarksScreen
import com.readora.app.ui.search.SearchScreen
import com.readora.app.ui.downloads.DownloadManagerScreen
import com.readora.app.ui.history.ReadingHistoryScreen
import com.readora.app.ui.stats.StatsScreen
import com.readora.app.core.AppLock
import com.readora.app.core.AppLockState
import com.readora.app.storage.SettingsSerializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReadoraAppWithTheme()
        }
    }
}

enum class AppTab(val label: String, val icon: ImageVector, val route: String) {
    Home("Home", Icons.Rounded.Home, ReadoraRoutes.Home),
    Library("Library", Icons.Rounded.CollectionsBookmark, ReadoraRoutes.Library),
    Updates("Updates", Icons.Rounded.AutoStories, ReadoraRoutes.Updates),
    Discover("Discover", Icons.Rounded.Explore, ReadoraRoutes.Discover),
    Merge("Merge", Icons.Rounded.CallMerge, ReadoraRoutes.Merge),
    Settings("Settings", Icons.Rounded.Settings, ReadoraRoutes.Settings),
}

private data class OnlineReaderRequest(
    val comic: OnlineComicSummary,
    val chapter: OnlineChapter,
    val initialPageIndex: Int? = null,
    val allChapters: List<OnlineChapter> = emptyList(),
)

@Composable
fun ReadoraAppWithTheme() {
    val context = LocalContext.current
    val settingsViewModel: SettingsViewModel = viewModel(factory = ReadoraViewModelFactory(context))
    val settings by settingsViewModel.uiState.collectAsState()
    com.readora.app.ui.theme.ReadoraTheme(
        accentKey = settings.themeAccentColor,
        useDynamicColor = settings.useDynamicColor,
    ) {
        ReadoraApp()
    }
}

@Composable
fun ReadoraApp() {
    val context = LocalContext.current
    val settingsViewModel: SettingsViewModel = viewModel(factory = ReadoraViewModelFactory(context))
    val settings by settingsViewModel.uiState.collectAsState()
    val locked by AppLockState.locked.collectAsState()
    val activity = context as? ComponentActivity
    
    val navController = rememberNavController()
    var selectedComicId by rememberSaveable { mutableStateOf<String?>(null) }
    var readerComicId by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedOnlineComic by remember { mutableStateOf<OnlineComicSummary?>(null) }
    var onlineReaderChapter by remember { mutableStateOf<OnlineReaderRequest?>(null) }
    var localReaderComic by remember { mutableStateOf<SavedLocalComic?>(null) }
    var showSearch by remember { mutableStateOf(false) }
    var showBookmarks by remember { mutableStateOf(false) }
    var showDownloadManager by remember { mutableStateOf(false) }
    var showReadingHistory by remember { mutableStateOf(false) }
    var showStats by remember { mutableStateOf(false) }
    var pendingDiscoverTag by remember { mutableStateOf<String?>(null) }

    val app = context.applicationContext as ReadoraApplication
    val updatesRepository = remember { app.appContainer.updatesRepository }
    val unreadUpdatesCount by updatesRepository.getUnreadCount().collectAsState(initial = 0)

    val readingMode = if (settings.readerMode == "webtoon") ReadingMode.Webtoon else ReadingMode.Paged
    val direction = if (settings.readingDirection == "rtl") Direction.RightToLeft else Direction.LeftToRight
    val selectedComic = DemoCatalog.comics.firstOrNull { it.id == selectedComicId }
    val readerComic = DemoCatalog.comics.firstOrNull { it.id == readerComicId }
    val onlineComic = selectedOnlineComic
    val onlineChapter = onlineReaderChapter

    // Request POST_NOTIFICATIONS permission on first launch (Android 13+)
    val notifPermLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* result handled silently; user can change in system settings */ }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                notifPermLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // When a tag drill-down is requested, navigate to the Discover tab
    LaunchedEffect(pendingDiscoverTag) {
        if (pendingDiscoverTag != null) {
            navController.navigate(ReadoraRoutes.Discover) {
                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                launchSingleTop = true
                restoreState = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    listOf(Color(0xFF2A1C28), Ink, Color(0xFF070A10)),
                    center = Offset(100f, 120f),
                    radius = 900f,
                ),
            ),
    ) {
        Canvas(Modifier.fillMaxSize()) {
            drawAtmosphere()
        }

        when {
            settings.appLockEnabled && !settings.appLockPinHash.isNullOrBlank() && locked -> LockScreen(
                onUnlock = { pin ->
                    if (AppLock.verifyPin(pin, settings.appLockPinHash)) {
                        AppLockState.unlock()
                    }
                }
            )

            localReaderComic != null -> LocalReaderScreen(
                comic = localReaderComic,
                onBack = { localReaderComic = null },
            )

            onlineChapter != null -> OnlineReaderScreen(
                comic = onlineChapter.comic,
                chapter = onlineChapter.chapter,
                onBack = { onlineReaderChapter = null },
                initialPageIndex = onlineChapter.initialPageIndex,
                allChapters = onlineChapter.allChapters,
                onNavigateChapter = { newChapter ->
                    onlineReaderChapter = OnlineReaderRequest(
                        comic = onlineChapter.comic,
                        chapter = newChapter,
                        initialPageIndex = null,
                        allChapters = onlineChapter.allChapters,
                    )
                },
            )

            readerComic != null -> ReaderScreen(
                comic = readerComic,
                mode = readingMode,
                direction = direction,
                onModeChange = {
                    settingsViewModel.setDefaultWebtoon(it == ReadingMode.Webtoon)
                },
                onDirectionChange = {
                    settingsViewModel.setDefaultRtl(it == Direction.RightToLeft)
                },
                onBack = { readerComicId = null },
            )

            onlineComic != null -> OnlineDetailsScreen(
                initialComic = onlineComic,
                onBack = { selectedOnlineComic = null },
                onRead = { chapter, initialPageIndex, allChapters ->
                    selectedOnlineComic = null
                    onlineReaderChapter = OnlineReaderRequest(onlineComic, chapter, initialPageIndex, allChapters)
                },
                onTagSearch = { tag ->
                    pendingDiscoverTag = tag
                    selectedOnlineComic = null
                },
            )

            selectedComic != null -> DetailsScreen(
                comic = selectedComic,
                onBack = { selectedComicId = null },
                onRead = {
                    selectedComicId = null
                    readerComicId = selectedComic.id
                },
            )

            showSearch -> SearchScreen(
                onBack = { showSearch = false },
                onOpenOnlineComic = { comic ->
                    showSearch = false
                    navController.navigate(ReadoraRoutes.details(comic.sourceId, comic.id))
                },
            )

            showBookmarks -> BookmarksScreen(
                onBack = { showBookmarks = false },
            )

            showDownloadManager -> DownloadManagerScreen(
                onBack = { showDownloadManager = false },
            )

            showReadingHistory -> ReadingHistoryScreen(
                onBack = { showReadingHistory = false },
            )

            showStats -> StatsScreen(
                onBack = { showStats = false },
            )

            else -> Scaffold(
                containerColor = Color.Transparent,
                bottomBar = {
                    val backStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = backStackEntry?.destination?.route ?: ReadoraRoutes.Home
                    ReadoraBottomBar(
                        selectedTab = AppTab.entries.firstOrNull { it.route == currentRoute } ?: AppTab.Home,
                        onSelect = { tab ->
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        updatesCount = unreadUpdatesCount,
                    )
                },
            ) { padding ->
                NavHost(
                    navController = navController,
                    startDestination = ReadoraRoutes.Home,
                    modifier = Modifier.padding(padding),
                ) {
                    composable(ReadoraRoutes.Home) {
                        HomeScreen(
                            onOpenComic = { selectedComicId = it.id },
                            onOpenOnlineComic = { navController.navigate(ReadoraRoutes.details(it.sourceId, it.id)) },
                            onRead = { readerComicId = it.id },
                            onOpenOnlineReader = { comic, chapter ->
                                navController.navigate(ReadoraRoutes.reader(comic.sourceId, comic.id, chapter.id))
                            },
                            onOpenSearch = { showSearch = true },
                            onOpenBookmarks = { showBookmarks = true },
                        )
                    }

                    composable(ReadoraRoutes.Library) {
                        LibraryScreen(
                            onOpenComic = { selectedComicId = it.id },
                            onOpenOnlineComic = { navController.navigate(ReadoraRoutes.details(it.sourceId, it.id)) },
                            onOpenLocalComic = { localReaderComic = it },
                        )
                    }

                    composable(ReadoraRoutes.Updates) {
                        UpdatesScreen(
                            onOpen = { comic, chapter ->
                                navController.navigate(ReadoraRoutes.reader(comic.sourceId, comic.id, chapter.id))
                            }
                        )
                    }

                    composable(ReadoraRoutes.Discover) {
                        val tagFilter = pendingDiscoverTag
                        DiscoverScreen(
                            onOpenComic = { selectedComicId = it.id },
                            onOpenOnlineComic = { navController.navigate(ReadoraRoutes.details(it.sourceId, it.id)) },
                            initialTagFilter = tagFilter,
                        )
                        // Clear after passing
                        LaunchedEffect(tagFilter) {
                            if (tagFilter != null) pendingDiscoverTag = null
                        }
                    }

                    composable(
                        ReadoraRoutes.DetailsPattern,
                        arguments = listOf(
                            navArgument("sourceId") { type = NavType.StringType },
                            navArgument("comicId") { type = NavType.StringType },
                        ),
                    ) { backStackEntry ->
                        val sourceId = backStackEntry.arguments?.getString("sourceId") ?: ""
                        val comicId = backStackEntry.arguments?.getString("comicId") ?: ""
                        OnlineDetailsRoute(
                            sourceId = sourceId,
                            comicId = comicId,
                            onBack = { navController.popBackStack() },
                            onOpenReader = { chapterId ->
                                navController.navigate(ReadoraRoutes.reader(sourceId, comicId, chapterId))
                            },
                            onTagSearch = { tag ->
                                pendingDiscoverTag = tag
                                navController.popBackStack()
                            },
                        )
                    }

                    composable(
                        ReadoraRoutes.ReaderPattern,
                        arguments = listOf(
                            navArgument("sourceId") { type = NavType.StringType },
                            navArgument("comicId") { type = NavType.StringType },
                            navArgument("chapterId") { type = NavType.StringType },
                        ),
                    ) { backStackEntry ->
                        val sourceId = backStackEntry.arguments?.getString("sourceId") ?: ""
                        val comicId = backStackEntry.arguments?.getString("comicId") ?: ""
                        val chapterId = backStackEntry.arguments?.getString("chapterId") ?: ""
                        OnlineReaderRoute(
                            sourceId = sourceId,
                            comicId = comicId,
                            chapterId = chapterId,
                            onBack = { navController.popBackStack() },
                            onOpenChapter = { newChapterId ->
                                navController.navigate(ReadoraRoutes.reader(sourceId, comicId, newChapterId)) {
                                    launchSingleTop = true
                                }
                            },
                        )
                    }

                    composable(ReadoraRoutes.Merge) {
                        MergeLabScreen(onOpenComic = { selectedComicId = it.id })
                    }

                    composable(ReadoraRoutes.Settings) {
                        SettingsScreen(
                            onNavigateToRepositories = { navController.navigate(ReadoraRoutes.RepositoryManager) },
                            onNavigateToMigration = { navController.navigate(ReadoraRoutes.SourceMigration) },
                            onNavigateToBookmarks = { showBookmarks = true },
                            onNavigateToDownloads = { showDownloadManager = true },
                            onNavigateToHistory = { showReadingHistory = true },
                            onNavigateToStats = { showStats = true },
                        )
                    }

                    composable(ReadoraRoutes.RepositoryManager) {
                        RepositoryManagerScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(ReadoraRoutes.SourceMigration) {
                        MigrationScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        val openRoute = activity?.intent?.getStringExtra("open_route")
        if (openRoute == "updates") {
            navController.navigate(ReadoraRoutes.Updates) {
                launchSingleTop = true
            }
        }
    }

    androidx.compose.runtime.DisposableEffect(Unit) {
        val lifecycle = activity?.lifecycle
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_STOP) {
                if (SettingsSerializer(context).load().appLockEnabled) {
                    AppLockState.lock()
                }
            }
        }
        if (lifecycle != null) lifecycle.addObserver(observer)
        onDispose { if (lifecycle != null) lifecycle.removeObserver(observer) }
    }
}

fun findOnlineSource(sourceId: String): OnlineSource? = when (sourceId) {
    MangaDexSource.id -> MangaDexSource
    else -> null
}

@Composable
fun OnlineDetailsRoute(
    sourceId: String,
    comicId: String,
    onBack: () -> Unit,
    onOpenReader: (String) -> Unit,
    onTagSearch: ((String) -> Unit)? = null,
) {
    val source = findOnlineSource(sourceId)
    val context = LocalContext.current
    var summary by remember(sourceId, comicId) {
        mutableStateOf<OnlineComicSummary?>(null)
    }
    var details by remember(sourceId, comicId) { mutableStateOf<OnlineComicDetails?>(null) }
    var loading by remember(sourceId, comicId) { mutableStateOf(true) }
    var error by remember(sourceId, comicId) { mutableStateOf<String?>(null) }
    var refreshKey by rememberSaveable(sourceId, comicId) { mutableStateOf(0) }

    LaunchedEffect(sourceId, comicId, refreshKey) {
        loading = true
        error = null
        if (source == null) {
            error = "Source not available"
            loading = false
            return@LaunchedEffect
        }
        runCatching {
            source.getDetails(comicId)
        }.onSuccess {
            details = it
            summary = it.summary
        }.onFailure {
            error = it.message ?: "Could not load title"
        }
        loading = false
    }

    when {
        loading -> LoadingPanel("Loading title...")
        error != null -> ErrorPanel(error ?: "Unknown error", onRetry = { refreshKey++ })
        summary != null -> OnlineDetailsScreen(
            initialComic = summary!!,
            initialDetails = details,
            onBack = onBack,
            onRead = { chapter, _, _ -> onOpenReader(chapter.id) },
            onTagSearch = onTagSearch,
        )
        else -> ErrorPanel("Title data unavailable", onRetry = { refreshKey++ })
    }
}

@Composable
fun OnlineReaderRoute(
    sourceId: String,
    comicId: String,
    chapterId: String,
    onBack: () -> Unit,
    onOpenChapter: (String) -> Unit,
) {
    val source = findOnlineSource(sourceId)
    var details by remember(sourceId, comicId) { mutableStateOf<OnlineComicDetails?>(null) }
    var loading by remember(sourceId, comicId) { mutableStateOf(true) }
    var error by remember(sourceId, comicId) { mutableStateOf<String?>(null) }
    var refreshKey by rememberSaveable(sourceId, comicId, chapterId) { mutableStateOf(0) }

    LaunchedEffect(sourceId, comicId, chapterId, refreshKey) {
        loading = true
        error = null
        if (source == null) {
            error = "Source not available"
            loading = false
            return@LaunchedEffect
        }
        runCatching {
            source.getDetails(comicId)
        }.onSuccess {
            details = it
        }.onFailure {
            error = it.message ?: "Could not load reader"
        }
        loading = false
    }

    when {
        loading -> LoadingPanel("Preparing reader...")
        error != null -> ErrorPanel(error ?: "Unknown error", onRetry = { refreshKey++ })
        details == null -> ErrorPanel("Reader data not available", onRetry = { refreshKey++ })
        else -> {
            val chapter = details!!.chapters.firstOrNull { it.id == chapterId }
            if (chapter == null) {
                ErrorPanel("Chapter not found", onRetry = { refreshKey++ })
            } else {
                OnlineReaderScreen(
                    comic = details!!.summary,
                    chapter = chapter,
                    onBack = onBack,
                    initialPageIndex = null,
                    allChapters = details!!.chapters,
                    onNavigateChapter = { newChapter ->
                        onOpenChapter(newChapter.id)
                    },
                )
            }
        }
    }
}

@Composable
fun LockScreen(onUnlock: (String) -> Unit) {
    var pin by rememberSaveable { mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF07080D))
            .statusBarsPadding(),
        contentAlignment = Alignment.Center,
    ) {
        PremiumPanel(modifier = Modifier.padding(18.dp)) {
            Text("App locked", color = Paper, fontWeight = FontWeight.Black, fontSize = 22.sp)
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = pin,
                onValueChange = { pin = it.take(12) },
                label = { Text("PIN") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = {
                    onUnlock(pin)
                    pin = ""
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Unlock")
            }
            Text(
                "Tip: set/clear your PIN in Settings.",
                color = Color(0xFFCDBFAD),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
fun ReadoraBottomBar(selectedTab: AppTab, onSelect: (AppTab) -> Unit, updatesCount: Int = 0) {
    NavigationBar(
        containerColor = Color(0xF20D111C),
        tonalElevation = 0.dp,
        modifier = Modifier.navigationBarsPadding(),
    ) {
        AppTab.entries.forEach { tab ->
            NavigationBarItem(
                selected = selectedTab == tab,
                onClick = { onSelect(tab) },
                icon = {
                    if (tab == AppTab.Updates && updatesCount > 0) {
                        BadgedBox(badge = {
                            Badge {
                                Text(
                                    if (updatesCount > 99) "99+" else updatesCount.toString(),
                                    fontSize = 9.sp,
                                )
                            }
                        }) {
                            Icon(tab.icon, contentDescription = tab.label)
                        }
                    } else {
                        Icon(tab.icon, contentDescription = tab.label)
                    }
                },
                label = { Text(tab.label, maxLines = 1) },
            )
        }
    }
}

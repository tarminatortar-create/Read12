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
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material.icons.rounded.Wifi
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
import androidx.compose.material3.AlertDialog
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
import com.readora.app.ui.theme.Gold
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
import com.readora.app.core.AppLock
import com.readora.app.worker.LibraryUpdatesWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

@Composable
fun SettingsScreen(
    onNavigateToRepositories: () -> Unit = {},
    onNavigateToMigration: () -> Unit = {},
    onNavigateToBookmarks: () -> Unit = {},
    onNavigateToDownloads: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToStats: () -> Unit = {},
) {
    val context = LocalContext.current
    val settingsViewModel: SettingsViewModel = viewModel(factory = ReadoraViewModelFactory(context))
    val settingsState by settingsViewModel.uiState.collectAsState()
    val sources by settingsViewModel.sources.collectAsState()
    val preferences = remember { ReadoraPreferences(context) }
    val cacheManager = remember { OfflineCacheManager(context) }
    val queueManager = remember { DownloadQueueManager(context) }
    var cacheSizeBytes by remember { mutableStateOf(cacheManager.cacheSizeBytes()) }
    var downloadQueue by remember { mutableStateOf(queueManager.load()) }
    var queueRunning by rememberSaveable { mutableStateOf(false) }
    var repoUrl by rememberSaveable { mutableStateOf("") }
    var addedRepos by remember { mutableStateOf((SourceRegistry.recommendedRepositories + preferences.loadRepositories()).distinctBy { it.url }) }
    var repoLoading by remember { mutableStateOf(false) }
    var repoError by remember { mutableStateOf<String?>(null) }
    var lastRead by remember { mutableStateOf(preferences.loadLastOnlineRead()) }
    val database = remember { (context.applicationContext as ReadoraApplication).database }
    var databaseStats by remember { mutableStateOf<DatabaseStats?>(null) }
    var healthStatus by rememberSaveable { mutableStateOf("Not checked") }
    var healthDetail by rememberSaveable { mutableStateOf("Run a live source check when pages/search feel slow.") }
    var healthChecking by rememberSaveable { mutableStateOf(false) }
    var diagnosticsRefreshKey by rememberSaveable { mutableStateOf(0) }
    var pinDraft by rememberSaveable { mutableStateOf("") }
    var pinStatus by rememberSaveable { mutableStateOf<String?>(null) }
    var clearHistoryDialogOpen by remember { mutableStateOf(false) }
    var readingStats by remember { mutableStateOf<Pair<Long, Long>?>(null) }
    // Export summary stats: (libraryCount, sessionCount, bookmarkCount, noteCount)
    var exportSummary by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    val scope = rememberCoroutineScope()
    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri: Uri? ->
        if (uri != null) {
            runCatching {
                context.contentResolver.openOutputStream(uri)?.use { output -> output.write(preferences.exportBackup().toByteArray()) }
            }.onSuccess {
                Toast.makeText(context, "Backup exported", Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(context, it.message ?: "Export failed", Toast.LENGTH_LONG).show()
            }
        }
    }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri != null) {
            runCatching {
                val raw = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }.orEmpty()
                preferences.importBackup(raw)
            }.onSuccess {
                Toast.makeText(context, "Backup imported", Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(context, it.message ?: "Import failed", Toast.LENGTH_LONG).show()
            }
        }
    }
    LaunchedEffect(Unit) {
        databaseStats = withContext(Dispatchers.IO) {
            DatabaseStats(
                comics = database.comicDao().getAll().first().size,
                sources = database.sourceDao().getAll().first().size,
                mergeGroups = database.mergeGroupDao().getAll().first().size,
                downloadJobs = database.downloadJobDao().getAll().first().size,
            )
        }
        val appContainer = (context.applicationContext as ReadoraApplication).appContainer
        val repo = appContainer.readingSessionRepository
        val now = System.currentTimeMillis()
        val week = 7L * 24L * 60L * 60L * 1000L
        readingStats = withContext(Dispatchers.IO) {
            val weekMs = repo.totalDurationSince(now - week)
            val allMs = repo.totalDurationSince(0L)
            weekMs to allMs
        }
        // Export summary: count library, sessions, bookmarks, notes
        val libraryCount = preferences.loadOnlineLibrary().size
        val sessionCount = withContext(Dispatchers.IO) { repo.getAll().first().size }
        val bookmarkCount = withContext(Dispatchers.IO) { appContainer.bookmarkRepository.getAll().first().size }
        val noteCount = withContext(Dispatchers.IO) { appContainer.chapterNoteRepository.getAll().first().size }
        exportSummary = listOf(
            "Library titles" to "$libraryCount",
            "Reading sessions" to "$sessionCount",
            "Bookmarks" to "$bookmarkCount",
            "Chapter notes" to "$noteCount",
        )
    }

    // Clear reading history confirmation dialog
    if (clearHistoryDialogOpen) {
        AlertDialog(
            onDismissRequest = { clearHistoryDialogOpen = false },
            title = { Text("Clear reading history?") },
            text = { Text("All session records will be permanently deleted. Stats, streaks, and daily goal progress will reset. This cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        settingsViewModel.clearReadingHistory()
                        clearHistoryDialogOpen = false
                        Toast.makeText(context, "Reading history cleared", Toast.LENGTH_SHORT).show()
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7A2020),
                    ),
                ) {
                    Text("Clear all")
                }
            },
            dismissButton = {
                TextButton(onClick = { clearHistoryDialogOpen = false }) {
                    Text("Cancel")
                }
            },
        )
    }

    LazyColumn(
        contentPadding = PaddingValues(start = 18.dp, top = 18.dp, end = 18.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
    ) {
        item {
            Header(
                eyebrow = "Settings",
                title = "Sources, repos, and reader defaults",
                subtitle = "Settings, repository manifests, and online reading history now persist after app restart.",
            )
        }
        if (lastRead != null) {
            item { LastReadPanel(lastRead!!) }
        }
        item {
            BackupCenterPanel(
                onExport = { exportLauncher.launch("readora-backup.json") },
                onImport = { importLauncher.launch(arrayOf("application/json", "text/*", "application/octet-stream")) },
            )
        }
        // Export summary stats panel
        if (exportSummary.isNotEmpty()) {
            item {
                PremiumPanel {
                    SectionTitle("What's in your backup", "Live counts of your saved data")
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        exportSummary.forEach { (label, value) ->
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(10.dp))
                                    .padding(vertical = 10.dp, horizontal = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Text(value, color = Ember, fontWeight = FontWeight.Black, fontSize = 18.sp)
                                Text(label, color = Color(0xFF9E9080), fontSize = 10.sp, maxLines = 1)
                            }
                        }
                    }
                }
            }
        }
        item {
            DatabaseFoundationPanel(databaseStats)
        }
        item {
            CacheStatsPanel(
                bytes = cacheSizeBytes,
                onClear = {
                    cacheManager.clearAll()
                    cacheSizeBytes = cacheManager.cacheSizeBytes()
                },
            )
        }
        item {
            DownloadQueuePanel(
                queue = downloadQueue,
                running = queueRunning,
                onRun = {
                    scope.launch {
                        queueRunning = true
                        processDownloadQueue(queueManager, cacheManager, preferences) { queue ->
                            downloadQueue = queue
                            cacheSizeBytes = cacheManager.cacheSizeBytes()
                        }
                        downloadQueue = queueManager.load()
                        cacheSizeBytes = cacheManager.cacheSizeBytes()
                        queueRunning = false
                    }
                },
                onRetryFailed = {
                    queueManager.load().filter { it.status == QueueStatus.Failed.value }.forEach { failed ->
                        queueManager.update(failed.copy(status = QueueStatus.Queued.value, error = null, progress = 0))
                    }
                    downloadQueue = queueManager.load()
                },
                onClearFinished = { downloadQueue = queueManager.clearFinished() },
                onClearAll = { downloadQueue = queueManager.clearAll() },
                onRemove = { downloadQueue = queueManager.remove(it) },
                onOpenFullManager = onNavigateToDownloads,
            )
        }
        item {
            SourceManagerSection(
                sources = sources,
                onToggleSource = { sourceId, enabled -> settingsViewModel.toggleSource(sourceId, enabled) },
                onManageRepositories = onNavigateToRepositories,
                onOpenMigration = onNavigateToMigration,
            )
        }
        item {
            SourceHealthPanel(
                checking = healthChecking,
                status = healthStatus,
                detail = healthDetail,
                onCheck = {
                    scope.launch {
                        healthChecking = true
                        healthStatus = "Checking..."
                        val started = System.currentTimeMillis()
                        runCatching { withContext(Dispatchers.IO) { MangaDexSource.popular(1).take(1) } }
                            .onSuccess { results ->
                                val elapsed = System.currentTimeMillis() - started
                                healthStatus = if (results.isNotEmpty()) "MangaDex online" else "MangaDex responded"
                                healthDetail = "${results.size} sample title(s) returned in ${elapsed}ms."
                                ReadoraLogger.log("SettingsScreen", "Source health check returned ${results.size} result(s) in ${elapsed}ms")
                            }
                            .onFailure {
                                healthStatus = "Source issue"
                                healthDetail = it.message ?: "Could not reach MangaDex right now."
                                ReadoraLogger.log("SettingsScreen", healthDetail, com.readora.app.core.LogLevel.WARN)
                            }
                        healthChecking = false
                    }
                },
            )
        }
        item {
            PremiumPanel {
                SectionTitle("Reader mode", "Choose the default read pattern for chapters.")
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    listOf(
                        "webtoon" to "Webtoon",
                        "paged" to "Paged",
                        "paged_double" to "Spread",
                    ).forEach { (key, label) ->
                        FilterChip(
                            selected = settingsState.readerMode == key,
                            onClick = { settingsViewModel.setDefaultReaderMode(key) },
                            label = { Text(label) },
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                SettingsToggle(
                    "Auto-detect webtoon chapters",
                    "Switch to vertical scrolling for long page chapters automatically.",
                    settingsState.autoDetectWebtoon,
                    settingsViewModel::setAutoDetectWebtoon,
                )
            }
        }
        item {
            PremiumPanel {
                SectionTitle("Page transition", "Animation style when turning pages in paged mode")
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    listOf("none" to "None", "slide" to "Slide", "fade" to "Fade").forEach { (key, label) ->
                        FilterChip(
                            selected = settingsState.pageTransition == key,
                            onClick = { settingsViewModel.setPageTransition(key) },
                            label = { Text(label) },
                        )
                    }
                }
            }
        }
        item {
            PremiumPanel {
                SectionTitle("Reader background", "Canvas colour behind pages")
                Spacer(Modifier.height(12.dp))
                val bgOptions = listOf(
                    "dark"  to Triple("Dark",  Color(0xFF12100E), Paper),
                    "black" to Triple("Black", Color(0xFF000000), Paper),
                    "sepia" to Triple("Sepia", Color(0xFF2C1F0E), Color(0xFFCDBFAD)),
                    "white" to Triple("White", Color(0xFFFFFFFF), Color(0xFF1A1200)),
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    bgOptions.forEach { (key, triple) ->
                        val (label, bgColor, textColor) = triple
                        val isSelected = settingsState.readerBackground == key
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .background(bgColor, RoundedCornerShape(12.dp))
                                .then(
                                    if (isSelected) Modifier.border(2.dp, Ember, RoundedCornerShape(12.dp))
                                    else Modifier.border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                                )
                                .clickable { settingsViewModel.setReaderBackground(key) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                if (isSelected) {
                                    Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = Ember, modifier = Modifier.size(14.dp))
                                }
                                Text(label, color = textColor, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    }
                }
            }
        }
        item {
            PremiumPanel {
                SectionTitle("Reader text size", "Scale for chapter titles and UI text in the reader")
                Spacer(Modifier.height(10.dp))
                val fontScaleOptions = listOf(0.75f to "Tiny", 1.0f to "Normal", 1.25f to "Large", 1.5f to "X-Large", 2.0f to "XX-Large")
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                ) {
                    fontScaleOptions.forEach { (scale, label) ->
                        FilterChip(
                            selected = settingsState.readerFontScale == scale,
                            onClick = { settingsViewModel.setReaderFontScale(scale) },
                            label = { Text(label) },
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    "Preview: ${(settingsState.readerFontScale * 100).toInt()}% scale",
                    color = Color(0xFFCDBFAD),
                    fontSize = (13 * settingsState.readerFontScale).sp,
                )
            }
        }
        item {
            PremiumPanel {
                SectionTitle("Content languages", "Filter chapters and discovery by language")
                Spacer(Modifier.height(8.dp))
                val langOptions = listOf(
                    "en" to "English",
                    "ja" to "Japanese",
                    "zh" to "Chinese",
                    "ko" to "Korean",
                    "es" to "Spanish",
                    "fr" to "French",
                    "pt" to "Portuguese",
                    "de" to "German",
                    "it" to "Italian",
                    "ru" to "Russian",
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                ) {
                    langOptions.forEach { (code, label) ->
                        FilterChip(
                            selected = code in settingsState.preferredLanguages,
                            onClick = { settingsViewModel.togglePreferredLanguage(code) },
                            label = { Text(label) },
                        )
                    }
                }
                Text(
                    "At least one language must remain selected.",
                    color = Color(0xFFCDBFAD),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
        }
        item {
            PremiumPanel {
                SectionTitle("Library updates", "Background checks and notifications")
                Spacer(Modifier.height(10.dp))
                SettingsToggle(
                    "Auto update library",
                    "Periodically check your saved online library for new chapters.",
                    settingsState.autoUpdateLibrary,
                    settingsViewModel::setAutoUpdateLibrary,
                )
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    listOf(6, 12, 24, 48, 72).forEach { hours ->
                        FilterChip(
                            selected = settingsState.autoUpdateIntervalHours == hours,
                            onClick = { settingsViewModel.setAutoUpdateIntervalHours(hours) },
                            label = { Text("${hours}h") },
                        )
                    }
                }
                Text(
                    "Interval applies next app launch (or next schedule refresh).",
                    color = Color(0xFFCDBFAD),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 8.dp),
                )
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = {
                        LibraryUpdatesWorker.runOnce(context)
                        Toast.makeText(context, "Update check started", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Rounded.Refresh, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Run update check now")
                }
            }
        }
        item {
            PremiumPanel {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 10.dp),
                ) {
                    Icon(Icons.Rounded.NotificationsNone, contentDescription = null, tint = Sky, modifier = Modifier.size(20.dp))
                    SectionTitle("Notifications", "Control how Readora alerts you to new content")
                }
                SettingsToggle(
                    "New chapter alerts",
                    "Show a notification when a library title gets a new chapter.",
                    settingsState.notifyNewChapters,
                    settingsViewModel::setNotifyNewChapters,
                )
                Spacer(Modifier.height(8.dp))
                if (settingsState.notifyNewChapters) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(start = 8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { settingsViewModel.setNotifyOnlyWifi(!settingsState.notifyOnlyWifi) }
                                .padding(vertical = 4.dp),
                        ) {
                            Icon(Icons.Rounded.Wifi, contentDescription = null, tint = if (settingsState.notifyOnlyWifi) Mint else Color(0xFF6B5E4E), modifier = Modifier.size(18.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Only on Wi-Fi", color = Paper, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                Text("Suppress notifications on mobile data.", color = Color(0xFFB8AA98), fontSize = 12.sp)
                            }
                            Switch(
                                checked = settingsState.notifyOnlyWifi,
                                onCheckedChange = { settingsViewModel.setNotifyOnlyWifi(it) },
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { settingsViewModel.setNotifySoundEnabled(!settingsState.notifySoundEnabled) }
                                .padding(vertical = 4.dp),
                        ) {
                            Icon(Icons.Rounded.VolumeUp, contentDescription = null, tint = if (settingsState.notifySoundEnabled) Mint else Color(0xFF6B5E4E), modifier = Modifier.size(18.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Notification sound", color = Paper, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                Text("Play a sound with new chapter alerts.", color = Color(0xFFB8AA98), fontSize = 12.sp)
                            }
                            Switch(
                                checked = settingsState.notifySoundEnabled,
                                onCheckedChange = { settingsViewModel.setNotifySoundEnabled(it) },
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { settingsViewModel.setNotifyVibrateEnabled(!settingsState.notifyVibrateEnabled) }
                                .padding(vertical = 4.dp),
                        ) {
                            Icon(Icons.Rounded.Vibration, contentDescription = null, tint = if (settingsState.notifyVibrateEnabled) Mint else Color(0xFF6B5E4E), modifier = Modifier.size(18.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Vibration", color = Paper, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                Text("Vibrate the device with new chapter alerts.", color = Color(0xFFB8AA98), fontSize = 12.sp)
                            }
                            Switch(
                                checked = settingsState.notifyVibrateEnabled,
                                onCheckedChange = { settingsViewModel.setNotifyVibrateEnabled(it) },
                            )
                        }
                    }
                }
            }
        }
        item {
            SettingsToggle(
                "Right-to-left paging",
                "Use manga-style page direction when paged reader mode is active.",
                settingsState.readingDirection == "rtl",
                settingsViewModel::setDefaultRtl,
            )
        }
        item {
            SettingsToggle(
                "Smart preload",
                "Prepare the next pages before you reach them.",
                settingsState.smartPreload,
                settingsViewModel::setSmartPreload,
            )
        }
        item {
            SettingsToggle(
                "Soft haptics",
                "Tiny feedback for reader controls and chapter actions.",
                settingsState.haptics,
                settingsViewModel::setHaptics,
            )
        }
        item {
            SettingsToggle(
                "Suggest auto-merge",
                "Recommend merges but keep final approval with you.",
                settingsState.autoMerge,
                settingsViewModel::setAutoMerge,
            )
        }
        item {
            SettingsToggle(
                "Incognito mode",
                "While enabled: don't save history, progress, or last-read signals.",
                settingsState.incognitoMode,
                settingsViewModel::setIncognitoMode,
            )
        }
        item {
            PremiumPanel {
                SectionTitle("Reading history", "Your session data and reading timeline")
                Spacer(Modifier.height(10.dp))
                Text(
                    "Clear all reading history — removes every session record used by Stats, streaks, and daily goals. This cannot be undone.",
                    color = Color(0xFFCDBFAD),
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                )
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = { clearHistoryDialogOpen = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7A2020),
                    ),
                ) {
                    Icon(Icons.Rounded.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Clear reading history")
                }
            }
        }
        item {
            PremiumPanel {
                SectionTitle("App lock (PIN)", "Optional privacy gate on app resume")
                Spacer(Modifier.height(10.dp))
                SettingsToggle(
                    "Require PIN on resume",
                    "Locks the app when you leave and come back.",
                    settingsState.appLockEnabled,
                    settingsViewModel::setAppLockEnabled,
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = pinDraft,
                    onValueChange = { pinDraft = it.take(12) },
                    label = { Text("PIN") },
                    placeholder = { Text(if (settingsState.appLockPinHash.isNullOrBlank()) "Set a new PIN" else "Enter new PIN to replace") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                ) {
                    Button(
                        onClick = {
                            val clean = pinDraft.trim()
                            if (clean.length < 4) {
                                pinStatus = "PIN must be at least 4 digits."
                            } else {
                                settingsViewModel.setAppLockPinHash(AppLock.hashPin(clean))
                                pinDraft = ""
                                pinStatus = "PIN saved."
                            }
                        },
                        enabled = pinDraft.trim().isNotBlank(),
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Save PIN")
                    }
                    TextButton(
                        onClick = {
                            settingsViewModel.setAppLockPinHash(null)
                            pinDraft = ""
                            pinStatus = "PIN cleared."
                        },
                        enabled = !settingsState.appLockPinHash.isNullOrBlank(),
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Clear PIN")
                    }
                }
                if (pinStatus != null) {
                    Text(pinStatus ?: "", color = Color(0xFFCDBFAD), fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                }
            }
        }
        item {
            PremiumPanel {
                SectionTitle("Accent colour", "Controls the highlight colour used throughout the app")
                Spacer(Modifier.height(12.dp))
                val accentSwatches = listOf(
                    "ember"   to Ember,
                    "mint"    to Mint,
                    "sky"     to Sky,
                    "coral"   to Coral,
                    "gold"    to Gold,
                    "violet"  to Color(0xFFBB86FC),
                    "rose"    to Color(0xFFFF4081),
                    "ice"     to Color(0xFF82CFFF),
                )
                // Two rows of 4 swatches with label
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    accentSwatches.chunked(4).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            row.forEach { (key, color) ->
                                val selected = settingsState.themeAccentColor == key ||
                                        (key == "ember" && settingsState.themeAccentColor == "default")
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.clickable { settingsViewModel.setThemeAccentColor(key) },
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                            .border(
                                                width = if (selected) 3.dp else 0.dp,
                                                color = if (selected) Paper else Color.Transparent,
                                                shape = CircleShape,
                                            ),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        if (selected) {
                                            Icon(
                                                Icons.Rounded.CheckCircle,
                                                contentDescription = null,
                                                tint = Paper,
                                                modifier = Modifier.size(18.dp),
                                            )
                                        }
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        key.replaceFirstChar { it.uppercaseChar() },
                                        color = if (selected) color else Color(0xFF9E9080),
                                        fontSize = 9.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    "Current: ${settingsState.themeAccentColor.replaceFirstChar { it.uppercaseChar() }}. Changes take effect immediately.",
                    color = Color(0xFFCDBFAD),
                    fontSize = 12.sp,
                )
            }
        }
        item {
            SettingsToggle(
                "Use dynamic theme colors",
                "Apply Android Material You colours when supported.",
                settingsState.useDynamicColor,
                settingsViewModel::setUseDynamicColor,
            )
        }
        item {
            PremiumPanel {
                SectionTitle("Content filter", "Controls what sources are allowed to show")
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    listOf("safe" to "Safe", "suggestive" to "Suggestive", "adult" to "Adult").forEach { (id, label) ->
                        FilterChip(
                            selected = settingsState.contentRating == id,
                            onClick = { settingsViewModel.updateSettings { copy(contentRating = id) } },
                            label = { Text(label) },
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    when (settingsState.contentRating) {
                        "safe" -> "Only safe content."
                        "adult" -> "Includes adult content where the source supports it."
                        else -> "Safe + suggestive (recommended default)."
                    },
                    color = Color(0xFFCDBFAD),
                    fontSize = 12.sp,
                )
            }
        }
        item {
            PremiumPanel {
                SectionTitle("Reader hardware controls", "Volume buttons and auto-scroll")
                Spacer(Modifier.height(10.dp))
                SettingsToggle(
                    "Keep screen on",
                    "Prevent the display from sleeping while reading.",
                    settingsState.keepScreenOn,
                    settingsViewModel::setKeepScreenOn,
                )
                Spacer(Modifier.height(10.dp))
                SettingsToggle(
                    "Volume button navigation",
                    "Use volume keys to turn pages / scroll.",
                    settingsState.volumeButtonNavigation,
                    settingsViewModel::setVolumeButtonNavigation,
                )
                Spacer(Modifier.height(10.dp))
                SettingsToggle(
                    "Invert volume direction",
                    "Swap volume up/down behavior.",
                    settingsState.volumeButtonInverted,
                    settingsViewModel::setVolumeButtonInverted,
                )
                Spacer(Modifier.height(10.dp))
                SettingsToggle(
                    "Auto-scroll (webtoon)",
                    "Continuously scroll webtoon chapters hands-free.",
                    settingsState.autoScrollEnabled,
                    settingsViewModel::setAutoScrollEnabled,
                )
                if (settingsState.autoScrollEnabled) {
                    Spacer(Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Auto-scroll speed",
                                color = Paper,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                            )
                            Text(
                                "${settingsState.autoScrollSpeed.toInt()} dp/s",
                                color = Color(0xFFCDBFAD),
                                fontSize = 12.sp,
                            )
                        }
                    }
                    Slider(
                        value = settingsState.autoScrollSpeed,
                        onValueChange = { settingsViewModel.setAutoScrollSpeed(it) },
                        valueRange = 40f..800f,
                        steps = 37,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                Spacer(Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Reader brightness",
                            color = Paper,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                        )
                        Text(
                            if (settingsState.defaultBrightness < 0f) "System default"
                            else "${(settingsState.defaultBrightness * 100).toInt()}%",
                            color = Color(0xFFCDBFAD),
                            fontSize = 12.sp,
                        )
                    }
                    if (settingsState.defaultBrightness >= 0f) {
                        TextButton(onClick = { settingsViewModel.setDefaultBrightness(-1f) }) {
                            Text("Reset", fontSize = 12.sp)
                        }
                    }
                }
                Slider(
                    value = if (settingsState.defaultBrightness < 0f) 0f else settingsState.defaultBrightness,
                    onValueChange = { settingsViewModel.setDefaultBrightness(it) },
                    valueRange = 0f..1f,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        item {
            DiagnosticsPanel(
                logs = remember(diagnosticsRefreshKey) { ReadoraLogger.recentLines().takeLast(6).asReversed() },
                onRefresh = { diagnosticsRefreshKey++ },
                onClear = {
                    ReadoraLogger.clear()
                    diagnosticsRefreshKey++
                },
            )
        }
        item {
            PremiumPanel {
                SectionTitle("Reading notes & bookmarks", "Your saved places and chapter notes")
                Text(
                    "View and manage all bookmarks and notes saved while reading.",
                    color = Color(0xFFDCCDBB),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 10.dp),
                )
                androidx.compose.material3.Button(
                    onClick = onNavigateToBookmarks,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Open Bookmarks & Notes")
                }
            }
        }
        item {
            PremiumPanel {
                SectionTitle("About Readora", "Build 0.1.0 native Android APK")
                Text(
                    "Readora now has live MangaDex discovery, persistent library data, local imports, offline cache, backup/restore, update checks, source health checks, and reader defaults. Next major blocks are database migration, real source extensions, download queue, and advanced gestures.",
                    color = Color(0xFFDCCDBB),
                    lineHeight = 21.sp,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
        item {
            PremiumPanel {
                SectionTitle("Daily reading goal", "Target minutes to read per day")
                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Goal: ${settingsState.dailyGoalMinutes} min/day",
                            color = Paper,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                        )
                        Text(
                            "Shown as a progress ring on the Home screen",
                            color = Color(0xFFCDBFAD),
                            fontSize = 12.sp,
                        )
                    }
                }
                Slider(
                    value = settingsState.dailyGoalMinutes.toFloat(),
                    onValueChange = { settingsViewModel.setDailyGoalMinutes(it.toInt()) },
                    valueRange = 5f..240f,
                    steps = 46,
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("5 min", color = Color(0xFF9E927F), fontSize = 11.sp)
                    Text("4 hrs", color = Color(0xFF9E927F), fontSize = 11.sp)
                }
            }
        }
        if (readingStats != null) {
            item {
                PremiumPanel {
                    SectionTitle("Reading time", "Device-only stats")
                    val weekMinutes = (readingStats!!.first / 60000L).coerceAtLeast(0L)
                    val allMinutes = (readingStats!!.second / 60000L).coerceAtLeast(0L)
                    Text("This week: ${weekMinutes} min", color = Color(0xFFDCCDBB), fontWeight = FontWeight.Bold)
                    Text("All time: ${allMinutes} min", color = Color(0xFFB8AA98), fontSize = 12.sp)
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        androidx.compose.material3.OutlinedButton(
                            onClick = onNavigateToHistory,
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("History")
                        }
                        androidx.compose.material3.OutlinedButton(
                            onClick = onNavigateToStats,
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("Statistics")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BackupCenterPanel(onExport: () -> Unit, onImport: () -> Unit) {
    PremiumPanel {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Sky.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Rounded.IosShare, contentDescription = null, tint = Sky)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Backup Center", color = Paper, fontWeight = FontWeight.Black, fontSize = 19.sp)
                Text("Export/import library, repos, merges, settings", color = Color(0xFFCDBFAD), fontSize = 13.sp)
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            Button(onClick = onExport, modifier = Modifier.weight(1f)) {
                Icon(Icons.Rounded.IosShare, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Export")
            }
            Button(onClick = onImport, modifier = Modifier.weight(1f)) {
                Icon(Icons.Rounded.Restore, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Import")
            }
        }
    }
}

@Composable
fun DatabaseFoundationPanel(stats: DatabaseStats?) {
    PremiumPanel {
        SectionTitle("Database foundation", "Room migration is active")
        val summary = if (stats == null) {
            "Loading migrated library state..."
        } else {
            "${stats.comics} comics, ${stats.sources} sources, ${stats.mergeGroups} merge groups, and ${stats.downloadJobs} download jobs are available in Room."
        }
        Text(
            summary,
            color = Color(0xFFDCCDBB),
            lineHeight = 21.sp,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
fun CacheStatsPanel(bytes: Long, onClear: () -> Unit) {
    var clearing by remember { mutableStateOf(false) }
    var cleared by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    PremiumPanel {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (cleared) Mint.copy(alpha = 0.22f) else Mint.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center,
            ) {
                if (clearing) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Mint, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Rounded.CloudDownload, contentDescription = null, tint = Mint)
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Offline cache", color = Paper, fontWeight = FontWeight.Black, fontSize = 19.sp)
                when {
                    clearing -> Text("Clearing...", color = Color(0xFFCDBFAD), fontSize = 13.sp)
                    cleared  -> Text("Cache cleared", color = Mint, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    bytes > 0L -> Text(formatBytes(bytes), color = Color(0xFFCDBFAD), fontSize = 13.sp)
                    else -> Text("No offline data cached", color = Color(0xFF9E927F), fontSize = 13.sp)
                }
            }
            TextButton(
                onClick = {
                    scope.launch {
                        clearing = true
                        cleared = false
                        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) { onClear() }
                        clearing = false
                        cleared = true
                    }
                },
                enabled = bytes > 0L && !clearing,
            ) {
                Text(if (cleared) "Done" else "Clear all")
            }
        }
        if (bytes > 0L && !cleared) {
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { 1f },
                color = Mint.copy(alpha = 0.5f),
                trackColor = Color.White.copy(alpha = 0.05f),
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(100.dp)),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Tap 'Clear all' to free ${formatBytes(bytes)} of cached chapter images.",
                color = Color(0xFF9E927F),
                fontSize = 12.sp,
                lineHeight = 16.sp,
            )
        }
    }
}

@Composable
fun DownloadQueuePanel(
    queue: List<QueuedDownload>,
    running: Boolean,
    onRun: () -> Unit,
    onRetryFailed: () -> Unit,
    onClearFinished: () -> Unit,
    onClearAll: () -> Unit,
    onRemove: (String) -> Unit,
    onOpenFullManager: () -> Unit = {},
) {
    val pending = queue.count { it.status == QueueStatus.Queued.value || it.status == QueueStatus.Running.value }
    val failed = queue.count { it.status == QueueStatus.Failed.value }
    PremiumPanel {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Ember.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Rounded.CloudDownload, contentDescription = null, tint = Ember)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Download queue", color = Paper, fontWeight = FontWeight.Black, fontSize = 19.sp)
                Text("${queue.size} jobs - $pending pending - $failed failed", color = Color(0xFFCDBFAD), fontSize = 13.sp)
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
            Button(onClick = onRun, enabled = !running && pending > 0) {
                Text(if (running) "Running" else "Run queue")
            }
            TextButton(onClick = onRetryFailed, enabled = failed > 0 && !running) {
                Text("Retry failed")
            }
            TextButton(onClick = onClearFinished, enabled = queue.any { it.status == QueueStatus.Done.value } && !running) {
                Text("Clear done")
            }
            TextButton(onClick = onClearAll, enabled = queue.isNotEmpty() && !running) {
                Text("Clear all")
            }
        }
        Spacer(Modifier.height(10.dp))
        if (queue.isEmpty()) {
            Text("Queue chapters from online details, then process them here.", color = Color(0xFFB8AA98), fontSize = 13.sp)
        } else {
            queue.take(5).forEach { job ->
                DownloadQueueRow(job = job, running = running, onRemove = onRemove)
                Spacer(Modifier.height(8.dp))
            }
            if (queue.size > 5) {
                Text("+${queue.size - 5} more queued chapters", color = Color(0xFFB8AA98), fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(10.dp))
        androidx.compose.material3.OutlinedButton(
            onClick = onOpenFullManager,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Open Download Manager")
        }
    }
}

@Composable
fun SourceManagerSection(
    sources: List<com.readora.app.data.db.SourceEntity>,
    onToggleSource: (String, Boolean) -> Unit,
    onManageRepositories: () -> Unit,
    onOpenMigration: () -> Unit,
) {
    PremiumPanel {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Sky.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Rounded.Explore, contentDescription = null, tint = Sky)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Source Manager", color = Paper, fontWeight = FontWeight.Black, fontSize = 22.sp)
                Text("Built-in sources now, repo manifests next", color = Color(0xFFCDBFAD), fontSize = 13.sp)
            }
            Pill("${sources.count { it.enabled }} active", Color(0xCC000000))
        }
        Spacer(Modifier.height(16.dp))
        val groupedSources = sources.groupBy { it.language }
        groupedSources.forEach { (language, langSources) ->
            Spacer(Modifier.height(8.dp))
            Text(language.uppercase(), color = Color(0xFFCDBFAD), fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
            
            langSources.forEach { source ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleSource(source.sourceId, !source.enabled) }
                        .padding(vertical = 4.dp)
                ) {
                    if (source.iconUrl != null) {
                        coil.compose.AsyncImage(
                            model = source.iconUrl,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.width(12.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(source.name, color = Paper, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(Modifier.width(6.dp))
                            val isTrusted = source.sourceId == "mangadex" || source.sourceId.contains("official")
                            if (isTrusted) {
                                Box(modifier = Modifier.background(Mint.copy(alpha = 0.2f), RoundedCornerShape(4.dp)).padding(horizontal = 4.dp, vertical = 2.dp)) {
                                    Text("OFFICIAL", color = Mint, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Text("${source.category} • v${source.version}", color = Color(0xFFB8AA98), fontSize = 13.sp)
                    }
                    Switch(checked = source.enabled, onCheckedChange = { onToggleSource(source.sourceId, it) })
                }
            }
            Spacer(Modifier.height(8.dp))
        }
        Spacer(Modifier.height(8.dp))
        SectionTitle("Repository manifests", "Add HTTPS source lists without hardcoding them into the app")
        Spacer(Modifier.height(10.dp))
        Button(onClick = onManageRepositories, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Rounded.Explore, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Manage repositories")
        }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onOpenMigration, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Rounded.CallMerge, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Open source migration tools")
        }
    }
}

@Composable
fun SourceHealthPanel(checking: Boolean, status: String, detail: String, onCheck: () -> Unit) {
    PremiumPanel {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Mint.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Rounded.Refresh, contentDescription = null, tint = Mint)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Source health", color = Paper, fontWeight = FontWeight.Black, fontSize = 19.sp)
                Text(status, color = Color(0xFFDCCDBB), fontWeight = FontWeight.Bold)
                Text(detail, color = Color(0xFFB8AA98), fontSize = 12.sp, lineHeight = 16.sp)
            }
            TextButton(onClick = onCheck, enabled = !checking) {
                Text(if (checking) "Checking" else "Check")
            }
        }
    }
}

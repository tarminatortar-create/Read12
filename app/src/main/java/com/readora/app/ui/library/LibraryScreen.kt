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
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.CallMerge
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.IosShare
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CollectionsBookmark
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.ViewList
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.ChevronRight
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
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
import com.readora.app.storage.UserShelf
import com.readora.app.storage.SavedMergeGroup
import com.readora.app.storage.SavedReadingProgress
import com.readora.app.data.db.UpdateEntity
import com.readora.app.storage.SettingsSerializer
import com.readora.app.worker.LibraryUpdatesWorker
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
import com.readora.app.ui.viewmodel.LibraryViewModel
import com.readora.app.ui.viewmodel.ReadoraViewModelFactory
import com.readora.app.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun LibraryScreen(onOpenComic: (Comic) -> Unit, onOpenOnlineComic: (OnlineComicSummary) -> Unit, onOpenLocalComic: (SavedLocalComic) -> Unit) {
    val context = LocalContext.current
    val app = context.applicationContext as ReadoraApplication
    val libraryViewModel: LibraryViewModel = viewModel(factory = ReadoraViewModelFactory(context))
    val roomComics by libraryViewModel.comics.collectAsState()
    val updatesRepository = remember { app.appContainer.updatesRepository }
    val libraryRepository = remember { app.appContainer.libraryRepository }
    val readingSessionRepository = remember { app.appContainer.readingSessionRepository }
    val settings = remember { SettingsSerializer(context).load() }
    val preferences = remember { ReadoraPreferences(context) }
    val cacheManager = remember { OfflineCacheManager(context) }
    var savedOnline by remember { mutableStateOf(preferences.loadOnlineLibrary()) }
    var savedLocal by remember { mutableStateOf(preferences.loadLocalLibrary()) }
    var userShelves by remember { mutableStateOf(preferences.loadUserShelves()) }
    var newShelfName by remember { mutableStateOf("") }
    var newShelfDialogOpen by remember { mutableStateOf(false) }
    var renamingShelfId by remember { mutableStateOf<String?>(null) }
    var renameShelfDraft by remember { mutableStateOf("") }
    var shelfPickerComicId by remember { mutableStateOf<String?>(null) }
    var updateResults by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var checkingUpdates by remember { mutableStateOf(false) }
    val allUnreadUpdates by updatesRepository.getAll().collectAsState(initial = emptyList())
    val newChapterCounts = remember(allUnreadUpdates) {
        allUnreadUpdates.filter { !it.isRead }.groupBy { it.comicId }.mapValues { it.value.size }
    }
    var readingStats by remember { mutableStateOf<Pair<Long, Long>?>(null) }
    val lastRead = remember(savedOnline, savedLocal) { preferences.loadLastOnlineRead() }
    val scope = rememberCoroutineScope()
    var selectedStatus by rememberSaveable { mutableStateOf("All") }
    // Bulk-select state
    var bulkSelectedIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var bulkSelectMode by remember { mutableStateOf(false) }
    var bulkStatusMenuOpen by remember { mutableStateOf(false) }
    // Library sort, filter, search & view state
    var librarySortOrder by rememberSaveable { mutableStateOf("Added") }   // "Added" | "Title" | "LastRead" | "Rating"
    var librarySourceFilter by rememberSaveable { mutableStateOf("All") }  // "All" | sourceName
    var librarySearchQuery by rememberSaveable { mutableStateOf("") }
    var libraryGridView by rememberSaveable { mutableStateOf(false) }
    // Reading-status filter for online library: "All" | "plan" | "reading" | "completed" | "unset"
    var libraryStatusFilter by rememberSaveable { mutableStateOf("All") }
    val availableSources = remember(savedOnline) {
        listOf("All") + savedOnline.map { it.sourceName }.distinct().sorted()
    }
    // Load all statuses once up front — avoids per-item disk I/O inside the remember derivation
    val allReadingStatuses = remember(savedOnline) {
        savedOnline.associate { it.id to preferences.loadReadingListStatus(it.id) }
    }
    // Load all user ratings once up front for sort support
    val allUserRatings = remember(savedOnline) {
        savedOnline.associate { it.id to preferences.loadUserRating(it.id) }
    }
    val lastReadMap = remember(lastRead) {
        if (lastRead != null) mapOf(lastRead.comicId to lastRead.updatedAt) else emptyMap()
    }
    // Per-comic chapter reading fraction (0f–1f) for the last-opened chapter
    val readFractionMap = remember(savedOnline, lastRead) {
        if (lastRead == null) emptyMap()
        else {
            val prog = preferences.loadChapterProgress(lastRead.sourceId, lastRead.comicId, lastRead.chapterId)
            if (prog != null) mapOf(lastRead.comicId to prog.fraction)
            else emptyMap()
        }
    }
    val displayedOnline = remember(savedOnline, librarySortOrder, librarySourceFilter, librarySearchQuery, libraryStatusFilter, allReadingStatuses, allUserRatings, lastReadMap) {
        val sourceFiltered = if (librarySourceFilter == "All") savedOnline
                             else savedOnline.filter { it.sourceName == librarySourceFilter }
        val queryFiltered = if (librarySearchQuery.isBlank()) sourceFiltered
                            else sourceFiltered.filter { it.title.contains(librarySearchQuery.trim(), ignoreCase = true) }
        val statusFiltered = when (libraryStatusFilter) {
            "All"   -> queryFiltered
            "unset" -> queryFiltered.filter { allReadingStatuses[it.id].isNullOrBlank() }
            else    -> queryFiltered.filter { allReadingStatuses[it.id] == libraryStatusFilter }
        }
        when (librarySortOrder) {
            "Title"    -> statusFiltered.sortedBy { it.title.lowercase() }
            "LastRead" -> statusFiltered.sortedByDescending { lastReadMap[it.id] ?: 0L }
            "Rating"   -> statusFiltered.sortedByDescending { allUserRatings[it.id] ?: 0 }
            "Unread"   -> statusFiltered.sortedByDescending { newChapterCounts[it.id] ?: 0 }
            else       -> statusFiltered.sortedByDescending { it.addedAt }  // "Added" newest first
        }
    }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri != null) {
            runCatching { context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) }
            val title = uri.lastPathSegment?.substringAfterLast('/')?.substringAfterLast(':') ?: "Imported comic"
            val type = if (title.endsWith(".cbz", true) || title.endsWith(".zip", true)) "CBZ/ZIP" else "Document"
            savedLocal = preferences.addLocalComic(uri.toString(), title, type)
            scope.launch {
                libraryRepository.addComic(
                    com.readora.app.data.db.ComicEntity(
                        id = "local:${uri.toString().hashCode()}",
                        title = title,
                        coverUrl = null,
                        sourceId = "local-cbz",
                        addedAt = System.currentTimeMillis(),
                        lastReadAt = null,
                        status = "Local",
                        tags = listOf(type),
                        isLocal = true,
                    )
                )
            }
        }
    }
    val statuses = listOf("All", "Reading", "Plan", "Complete", "Offline")
    val filtered = remember(selectedStatus) {
        when (selectedStatus) {
            "Reading" -> DemoCatalog.comics.filter { it.status.label == "Reading" }
            "Plan" -> DemoCatalog.comics.filter { it.status.label == "Plan to read" }
            "Complete" -> DemoCatalog.comics.filter { it.progress >= 1f }
            "Offline" -> DemoCatalog.comics.filter { comic -> comic.chapters.any { it.downloaded } }
            else -> DemoCatalog.comics
        }
    }

    LaunchedEffect(Unit) {
        val now = System.currentTimeMillis()
        val week = 7L * 24L * 60L * 60L * 1000L
        readingStats = withContext(Dispatchers.IO) {
            val weekMs = readingSessionRepository.totalDurationSince(now - week)
            val allMs = readingSessionRepository.totalDurationSince(0L)
            weekMs to allMs
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(start = 18.dp, top = 18.dp, end = 18.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
    ) {
        item {
            LaunchedEffect(Unit) {
                savedOnline = preferences.loadOnlineLibrary()
                savedLocal = preferences.loadLocalLibrary()
            }
            Header(
                eyebrow = "Library",
                title = "Everything organized without babysitting",
                subtitle = "Status, tags, downloads, reading progress, and mirror health in one calm view.",
            )
        }
        item {
            Button(onClick = { importLauncher.launch(arrayOf("application/zip", "application/x-cbz", "application/octet-stream", "image/*")) }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Rounded.CloudDownload, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Import local CBZ / ZIP / images")
            }
        }
        item {
            LibraryStatsPanel(
                onlineCount = savedOnline.size,
                localCount = savedLocal.size,
                demoCount = filtered.size,
                roomCount = roomComics.size,
                cacheSize = formatBytes(cacheManager.cacheSizeBytes()),
                lastRead = lastRead,
                readingStats = readingStats,
            )
        }
        if (savedLocal.isNotEmpty()) {
            item { SectionTitle("Local imports", "Device files added to your library") }
            items(savedLocal) { comic ->
                SavedLocalLibraryRow(
                    comic = comic,
                    onOpen = { onOpenLocalComic(comic) },
                    onRemove = { savedLocal = preferences.removeLocalComic(comic.uri) },
                )
            }
        }
        if (savedOnline.isNotEmpty()) {
            item {
                LibraryUpdatePanel(
                    checking = checkingUpdates,
                    resultCount = updateResults.size,
                    onCheck = {
                        scope.launch {
                            checkingUpdates = true
                            val results = mutableMapOf<String, String>()
                            savedOnline.forEach { saved ->
                                runCatching { MangaDexSource.details(saved.toOnlineSummary()).chapters.firstOrNull() }
                                    .onSuccess { latest ->
                                        if (latest != null && latest.number.isNotBlank()) {
                                            val key = "${saved.sourceId}:${saved.id}"
                                            results[key] = latest.number
                                            if (!settings.incognitoMode && saved.lastChapterNumber.isNotBlank() && latest.number != saved.lastChapterNumber) {
                                                updatesRepository.add(
                                                    UpdateEntity(
                                                        sourceId = saved.sourceId,
                                                        sourceName = saved.sourceName,
                                                        comicId = saved.id,
                                                        comicTitle = saved.title,
                                                        coverUrl = saved.coverUrl,
                                                        chapterId = latest.id,
                                                        chapterNumber = latest.number,
                                                        chapterTitle = latest.title,
                                                        foundAt = System.currentTimeMillis(),
                                                        isRead = false,
                                                    )
                                                )
                                            }
                                        }
                                    }
                            }
                            updateResults = results
                            checkingUpdates = false
                        }
                    },
                )
            }
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    if (bulkSelectMode) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                            Text("${bulkSelectedIds.size} selected", color = Ember, fontWeight = FontWeight.Black, fontSize = 16.sp, modifier = Modifier.weight(1f))
                            if (bulkSelectedIds.isNotEmpty()) {
                                // Bulk set status
                                Box {
                                    IconButton(onClick = { bulkStatusMenuOpen = true }) {
                                        Icon(Icons.Rounded.CheckCircle, contentDescription = "Set status", tint = Mint)
                                    }
                                    DropdownMenu(
                                        expanded = bulkStatusMenuOpen,
                                        onDismissRequest = { bulkStatusMenuOpen = false },
                                    ) {
                                        listOf(
                                            "reading"   to "Reading",
                                            "plan"      to "Plan to read",
                                            "completed" to "Completed",
                                            "on_hold"   to "On Hold",
                                            "dropped"   to "Dropped",
                                            ""          to "Clear status",
                                        ).forEach { (key, label) ->
                                            DropdownMenuItem(
                                                text = { Text(label) },
                                                onClick = {
                                                    bulkSelectedIds.forEach { id ->
                                                        preferences.saveReadingListStatus(id, key)
                                                    }
                                                    allReadingStatuses  // force recompose via key change
                                                    savedOnline = preferences.loadOnlineLibrary()
                                                    bulkStatusMenuOpen = false
                                                    bulkSelectedIds = emptySet()
                                                    bulkSelectMode = false
                                                },
                                            )
                                        }
                                    }
                                }
                                // Bulk delete
                                IconButton(onClick = {
                                    bulkSelectedIds.forEach { id ->
                                        val comic = savedOnline.firstOrNull { it.id == id }
                                        if (comic != null) preferences.removeOnlineLibrary(comic.sourceId, comic.id)
                                    }
                                    savedOnline = preferences.loadOnlineLibrary()
                                    bulkSelectedIds = emptySet()
                                    bulkSelectMode = false
                                }) {
                                    Icon(Icons.Rounded.Delete, contentDescription = "Delete selected", tint = Ember)
                                }
                            }
                            IconButton(onClick = { bulkSelectMode = false; bulkSelectedIds = emptySet() }) {
                                Icon(Icons.Rounded.Close, contentDescription = "Exit selection", tint = Color(0xFFB8AA98))
                            }
                        }
                    } else {
                        Text("Saved online", color = Paper, fontWeight = FontWeight.Black, fontSize = 18.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${displayedOnline.size} title${if (displayedOnline.size != 1) "s" else ""}", color = Color(0xFFB8AA98), fontSize = 13.sp)
                            IconButton(onClick = { libraryGridView = !libraryGridView }) {
                                Icon(
                                    if (libraryGridView) Icons.Rounded.ViewList else Icons.Rounded.GridView,
                                    contentDescription = if (libraryGridView) "Switch to list" else "Switch to grid",
                                    tint = Color(0xFFB8AA98),
                                )
                            }
                        }
                    }
                }
            }
            item {
                OutlinedTextField(
                    value = librarySearchQuery,
                    onValueChange = { librarySearchQuery = it },
                    placeholder = { Text("Search saved titles…", color = Color(0xFF9E9080)) },
                    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = Color(0xFF9E9080)) },
                    trailingIcon = {
                        if (librarySearchQuery.isNotEmpty()) {
                            IconButton(onClick = { librarySearchQuery = "" }) {
                                Icon(Icons.Rounded.Close, contentDescription = "Clear search", tint = Color(0xFF9E9080))
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                LibrarySortFilterBar(
                    sortOrder = librarySortOrder,
                    onSortChange = { librarySortOrder = it },
                    sourceFilter = librarySourceFilter,
                    availableSources = availableSources,
                    onSourceChange = { librarySourceFilter = it },
                )
            }
            item {
                // Reading-status filter chips with per-status counts
                val statusCounts = remember(savedOnline, allReadingStatuses) {
                    mapOf(
                        "reading"   to savedOnline.count { allReadingStatuses[it.id] == "reading" },
                        "plan"      to savedOnline.count { allReadingStatuses[it.id] == "plan" },
                        "completed" to savedOnline.count { allReadingStatuses[it.id] == "completed" },
                        "on_hold"   to savedOnline.count { allReadingStatuses[it.id] == "on_hold" },
                        "dropped"   to savedOnline.count { allReadingStatuses[it.id] == "dropped" },
                        "unset"     to savedOnline.count { allReadingStatuses[it.id].isNullOrBlank() },
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                ) {
                    listOf(
                        "All"       to "All (${savedOnline.size})",
                        "reading"   to "Reading (${statusCounts["reading"]})",
                        "plan"      to "Plan to Read (${statusCounts["plan"]})",
                        "completed" to "Completed (${statusCounts["completed"]})",
                        "on_hold"   to "On Hold (${statusCounts["on_hold"]})",
                        "dropped"   to "Dropped (${statusCounts["dropped"]})",
                        "unset"     to "Untagged (${statusCounts["unset"]})",
                    ).forEach { (key, label) ->
                        FilterChip(
                            selected = libraryStatusFilter == key,
                            onClick = { libraryStatusFilter = key },
                            label = { Text(label, fontSize = 12.sp) },
                        )
                    }
                }
            }
            if (libraryGridView) {
                items(displayedOnline.chunked(3)) { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        row.forEach { comic ->
                            val gridBadge = newChapterCounts[comic.id] ?: 0
                            Box(modifier = Modifier.weight(1f)) {
                                OnlineCover(
                                    comic = comic.toOnlineSummary(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(0.68f)
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable { onOpenOnlineComic(comic.toOnlineSummary()) },
                                )
                                if (gridBadge > 0) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(4.dp)
                                            .size(20.dp)
                                            .background(Ember, CircleShape),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = if (gridBadge > 9) "9+" else gridBadge.toString(),
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    }
                                }
                            }
                        }
                        // Fill empty slots in last row
                        repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            } else {
                items(displayedOnline, key = { it.id }) { comic ->
                    val isSelected = comic.id in bulkSelectedIds
                    if (bulkSelectMode) {
                        // In bulk-select mode: tap toggles selection, show checkbox indicator
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .combinedClickable(
                                    onClick = {
                                        bulkSelectedIds = if (isSelected) bulkSelectedIds - comic.id
                                        else bulkSelectedIds + comic.id
                                    },
                                    onLongClick = { bulkSelectMode = false; bulkSelectedIds = emptySet() },
                                )
                                .background(
                                    if (isSelected) Ember.copy(alpha = 0.12f) else Color.Transparent,
                                    RoundedCornerShape(14.dp),
                                ),
                        ) {
                            SavedOnlineLibraryRow(
                                comic = comic,
                                latestChapter = updateResults["${comic.sourceId}:${comic.id}"],
                                newChapterCount = newChapterCounts[comic.id] ?: 0,
                                readFraction = readFractionMap[comic.id] ?: 0f,
                                lastReadAt = lastReadMap[comic.id],
                                readingStatus = allReadingStatuses[comic.id] ?: "",
                                onOpen = {},
                                onRemove = {},
                            )
                            // Selection checkbox overlay
                            Icon(
                                if (isSelected) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (isSelected) Ember else Color(0xFFB8AA98),
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(10.dp)
                                    .size(20.dp),
                            )
                        }
                    } else {
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { value ->
                                if (value == SwipeToDismissBoxValue.EndToStart) {
                                    savedOnline = preferences.removeOnlineLibrary(comic.sourceId, comic.id)
                                    true
                                } else false
                            },
                        )
                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = false,
                            backgroundContent = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color(0xFFB00020), RoundedCornerShape(14.dp))
                                        .padding(end = 20.dp),
                                    contentAlignment = Alignment.CenterEnd,
                                ) {
                                    Icon(Icons.Rounded.Delete, contentDescription = "Remove", tint = Color.White)
                                }
                            },
                        ) {
                            SavedOnlineLibraryRow(
                                comic = comic,
                                latestChapter = updateResults["${comic.sourceId}:${comic.id}"],
                                newChapterCount = newChapterCounts[comic.id] ?: 0,
                                readFraction = readFractionMap[comic.id] ?: 0f,
                                lastReadAt = lastReadMap[comic.id],
                                readingStatus = allReadingStatuses[comic.id] ?: "",
                                onOpen = { onOpenOnlineComic(comic.toOnlineSummary()) },
                                onRemove = { savedOnline = preferences.removeOnlineLibrary(comic.sourceId, comic.id) },
                                onLongPress = { bulkSelectMode = true; bulkSelectedIds = setOf(comic.id) },
                                shelves = userShelves,
                                onToggleShelf = { shelfId ->
                                    userShelves = preferences.toggleComicInShelf(shelfId, comic.id)
                                },
                            )
                        }
                    }
                }
            }
        }
        item {
            ChipRow(statuses, selectedStatus) { selectedStatus = it }
        }
        items(filtered.chunked(2)) { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxWidth()) {
                row.forEach { comic ->
                    ComicPoster(
                        comic = comic,
                        modifier = Modifier.weight(1f),
                        onClick = { onOpenComic(comic) },
                    )
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }

        // ── Custom Shelves ──────────────────────────────────────────────────
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                SectionTitle("My shelves", "${userShelves.size} shelf${if (userShelves.size != 1) "ves" else ""}")
                IconButton(onClick = { newShelfDialogOpen = true }) {
                    Icon(Icons.Rounded.Add, contentDescription = "New shelf", tint = Ember)
                }
            }
        }
        if (userShelves.isEmpty()) {
            item {
                PremiumPanel {
                    Text("No custom shelves yet", color = Paper, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Tap + to create a shelf and organise your library your way.", color = Color(0xFFCDBFAD), fontSize = 13.sp)
                }
            }
        }
        items(userShelves) { shelf ->
            PremiumPanel {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Ember.copy(alpha = 0.12f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Rounded.CollectionsBookmark, contentDescription = null, tint = Ember, modifier = Modifier.size(22.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(shelf.name, color = Paper, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("${shelf.comicIds.size} title${if (shelf.comicIds.size != 1) "s" else ""}", color = Color(0xFFB8AA98), fontSize = 12.sp)
                    }
                    IconButton(onClick = {
                        renamingShelfId = shelf.id
                        renameShelfDraft = shelf.name
                    }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Rounded.Edit, contentDescription = "Rename", tint = Color(0xFFB8AA98), modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = {
                        userShelves = preferences.deleteUserShelf(shelf.id)
                    }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Rounded.Delete, contentDescription = "Delete shelf", tint = Color(0xFF9E3030), modifier = Modifier.size(18.dp))
                    }
                }
                // Comics in this shelf shown as a horizontal scroll strip
                val shelfComics = remember(shelf.comicIds, savedOnline) {
                    shelf.comicIds.mapNotNull { id -> savedOnline.firstOrNull { it.id == id } }
                }
                if (shelfComics.isNotEmpty()) {
                    Spacer(Modifier.height(10.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                    ) {
                        for (comic in shelfComics) {
                            OnlineLibraryStripCard(
                                comic = comic,
                                onClick = { onOpenOnlineComic(comic.toOnlineSummary()) },
                            )
                        }
                    }
                }
            }
        }

        // Assign-to-shelf picker for a comic (triggered from long-press elsewhere; or we show on each comic card)
        if (shelfPickerComicId != null) {
            item {
                val comicId = shelfPickerComicId!!
                PremiumPanel {
                    Text("Add to shelf", color = Paper, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    Spacer(Modifier.height(8.dp))
                    if (userShelves.isEmpty()) {
                        Text("No shelves yet — create one first.", color = Color(0xFFB8AA98), fontSize = 13.sp)
                    } else {
                        for (shelf in userShelves) {
                            val isOn = shelf.comicIds.contains(comicId)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        userShelves = preferences.toggleComicInShelf(shelf.id, comicId)
                                    }
                                    .padding(vertical = 8.dp),
                            ) {
                                Icon(
                                    if (isOn) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (isOn) Mint else Color(0xFF7A7A9A),
                                    modifier = Modifier.size(20.dp),
                                )
                                Text(shelf.name, color = Paper, fontSize = 14.sp)
                                Spacer(Modifier.weight(1f))
                                Text("${shelf.comicIds.size}", color = Color(0xFF9E927F), fontSize = 12.sp)
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = { shelfPickerComicId = null }, modifier = Modifier.fillMaxWidth()) {
                        Text("Done")
                    }
                }
            }
        }
    }

    // Create shelf dialog
    if (newShelfDialogOpen) {
        AlertDialog(
            onDismissRequest = { newShelfDialogOpen = false; newShelfName = "" },
            title = { Text("New shelf") },
            text = {
                OutlinedTextField(
                    value = newShelfName,
                    onValueChange = { newShelfName = it },
                    placeholder = { Text("Shelf name…") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newShelfName.isNotBlank()) {
                            userShelves = preferences.createUserShelf(newShelfName)
                            newShelfName = ""
                            newShelfDialogOpen = false
                        }
                    },
                    enabled = newShelfName.isNotBlank(),
                ) { Text("Create") }
            },
            dismissButton = {
                TextButton(onClick = { newShelfDialogOpen = false; newShelfName = "" }) { Text("Cancel") }
            },
        )
    }

    // Rename shelf dialog
    if (renamingShelfId != null) {
        AlertDialog(
            onDismissRequest = { renamingShelfId = null },
            title = { Text("Rename shelf") },
            text = {
                OutlinedTextField(
                    value = renameShelfDraft,
                    onValueChange = { renameShelfDraft = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (renameShelfDraft.isNotBlank()) {
                            userShelves = preferences.renameUserShelf(renamingShelfId!!, renameShelfDraft)
                            renamingShelfId = null
                        }
                    },
                    enabled = renameShelfDraft.isNotBlank(),
                ) { Text("Rename") }
            },
            dismissButton = {
                TextButton(onClick = { renamingShelfId = null }) { Text("Cancel") }
            },
        )
    }
}

@Composable
fun LibraryStatsPanel(
    onlineCount: Int,
    localCount: Int,
    demoCount: Int,
    roomCount: Int,
    cacheSize: String,
    lastRead: LastOnlineRead?,
    readingStats: Pair<Long, Long>?,
) {
    PremiumPanel {
        SectionTitle("Library dashboard", "Real saved titles, imports, cache, and resume signal")
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            StatTile("Online", onlineCount.toString(), Modifier.weight(1f))
            StatTile("Local", localCount.toString(), Modifier.weight(1f))
            StatTile("Room", roomCount.toString(), Modifier.weight(1f))
        }
        Spacer(Modifier.height(10.dp))
        Text("$demoCount demo titles remain available while the database-backed library takes over.", color = Color(0xFFB8AA98), fontSize = 12.sp)
        Text(cacheSize, color = Color(0xFFDCCDBB), fontSize = 13.sp)
        if (readingStats != null) {
            val weekMinutes = (readingStats.first / 60000L).coerceAtLeast(0L)
            val allMinutes = (readingStats.second / 60000L).coerceAtLeast(0L)
            Text("Reading: ${weekMinutes}m this week • ${allMinutes}m all time", color = Color(0xFFB8AA98), fontSize = 12.sp)
        }
        Text(
            lastRead?.let { "Continue: ${it.comicTitle} - Ch. ${it.chapterNumber}" } ?: "No online reading history yet.",
            color = Color(0xFFB8AA98),
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun SavedLocalLibraryRow(comic: SavedLocalComic, onOpen: () -> Unit, onRemove: () -> Unit) {
    PremiumPanel(modifier = Modifier.clickable(onClick = onOpen)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(
                modifier = Modifier
                    .size(72.dp, 102.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Brush.linearGradient(listOf(Color(0xFF263B55), Color(0xFFF9B17A))))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(22.dp)),
                contentAlignment = Alignment.BottomStart,
            ) {
                Text("LOCAL", color = Color.White, fontWeight = FontWeight.Black, modifier = Modifier.padding(10.dp))
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(comic.title, color = Paper, fontWeight = FontWeight.Black, fontSize = 18.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text("${comic.type} - imported from device", color = Color(0xFFCDBFAD), fontSize = 13.sp)
                Text("Natural-sorted local reader ready for CBZ/ZIP pages.", color = Color(0xFF9E927F), fontSize = 12.sp, lineHeight = 16.sp)
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Rounded.Delete, contentDescription = "Remove local import", tint = Color(0xFFB8AA98))
            }
        }
    }
}

@Composable
fun LibraryUpdatePanel(checking: Boolean, resultCount: Int, onCheck: () -> Unit) {
    PremiumPanel {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Sky.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Rounded.Refresh, contentDescription = null, tint = Sky)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Update Center", color = Paper, fontWeight = FontWeight.Black, fontSize = 19.sp)
                Text(
                    if (resultCount == 0) "Check saved online titles for latest chapters" else "$resultCount titles checked",
                    color = Color(0xFFCDBFAD),
                    fontSize = 13.sp,
                )
            }
            Button(onClick = onCheck, enabled = !checking) {
                if (checking) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Rounded.Refresh, contentDescription = null)
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        val context = LocalContext.current
        TextButton(onClick = { LibraryUpdatesWorker.runOnce(context) }) {
            Text("Run background check now")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SavedOnlineLibraryRow(comic: SavedOnlineComic, latestChapter: String?, newChapterCount: Int = 0, onOpen: () -> Unit, onRemove: () -> Unit, onLongPress: (() -> Unit)? = null, readFraction: Float = 0f, lastReadAt: Long? = null, readingStatus: String = "", shelves: List<UserShelf> = emptyList(), onToggleShelf: ((String) -> Unit)? = null) {
    var menuExpanded by remember { mutableStateOf(false) }
    var shelvesMenuExpanded by remember { mutableStateOf(false) }
    val statusBarColor = when (readingStatus) {
        "reading"   -> Sky
        "completed" -> Mint
        "plan"      -> Ember
        "on_hold"   -> Color(0xFFCB8A00)
        "dropped"   -> Color(0xFF8B3A3A)
        else        -> Color.Transparent
    }
    Box {
        PremiumPanel(
            modifier = Modifier.combinedClickable(
                onClick = onOpen,
                onLongClick = { if (onLongPress != null) onLongPress() else menuExpanded = true },
            ),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                // Reading status accent bar on the left
                if (statusBarColor != Color.Transparent) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(102.dp)
                            .background(statusBarColor, RoundedCornerShape(2.dp)),
                    )
                }
                Box {
                    OnlineCover(
                        comic = OnlineComicSummary(
                            id = comic.id,
                            sourceId = comic.sourceId,
                            sourceName = comic.sourceName,
                            title = comic.title,
                            description = comic.description,
                            coverUrl = comic.coverUrl,
                            tags = comic.tags,
                            status = comic.status,
                        ),
                        modifier = Modifier.size(72.dp, 102.dp),
                    )
                    if (newChapterCount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(20.dp)
                                .background(Ember, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = if (newChapterCount > 9) "9+" else newChapterCount.toString(),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(comic.title, color = Paper, fontWeight = FontWeight.Black, fontSize = 18.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                        Pill(comic.sourceName, Color(0xCC000000))
                    }
                    Text(
                        if (comic.lastChapterNumber.isBlank()) "Saved from source - not read yet" else "Last opened Ch. ${comic.lastChapterNumber}: ${comic.lastChapterTitle}",
                        color = Color(0xFFCDBFAD),
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (latestChapter != null) {
                        Text(
                            if (latestChapter == comic.lastChapterNumber) "Up to date" else "Latest online chapter: $latestChapter",
                            color = if (latestChapter == comic.lastChapterNumber) Mint else Ember,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                        comic.tags.take(3).forEach { Pill(it, Color.White.copy(alpha = 0.08f)) }
                        if (comic.status.isNotBlank()) Pill(comic.status, Color.White.copy(alpha = 0.08f))
                    }
                    // Last-read / added date label
                    val dateLabel = if (lastReadAt != null && lastReadAt > 0L) {
                        val diffMs = System.currentTimeMillis() - lastReadAt
                        when {
                            diffMs < TimeUnit.MINUTES.toMillis(1)  -> "Read just now"
                            diffMs < TimeUnit.HOURS.toMillis(1)    -> "Read ${TimeUnit.MILLISECONDS.toMinutes(diffMs)}m ago"
                            diffMs < TimeUnit.HOURS.toMillis(24)   -> "Read ${TimeUnit.MILLISECONDS.toHours(diffMs)}h ago"
                            diffMs < TimeUnit.DAYS.toMillis(7)     -> "Read ${TimeUnit.MILLISECONDS.toDays(diffMs)}d ago"
                            else -> "Last read ${SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(lastReadAt))}"
                        }
                    } else if (comic.addedAt > 0L) {
                        "Added ${SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(comic.addedAt))}"
                    } else null
                    if (dateLabel != null) {
                        Text(
                            dateLabel,
                            color = Color(0xFF7A6E65),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                    // Reading progress bar
                    if (readFraction > 0f) {
                        val clampedFraction = readFraction.coerceIn(0f, 1f)
                        val isComplete = clampedFraction >= 0.97f
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            LinearProgressIndicator(
                                progress = { clampedFraction },
                                color = if (isComplete) Mint else Sky,
                                trackColor = Color.White.copy(alpha = 0.08f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(5.dp)
                                    .clip(CircleShape),
                            )
                            Text(
                                if (isComplete) "Completed ✓" else "${(clampedFraction * 100).toInt()}% through last chapter",
                                color = if (isComplete) Mint else Color(0xFF9E9080),
                                fontSize = 10.sp,
                            )
                        }
                    }
                }
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Rounded.Tune, contentDescription = "More options", tint = Color(0xFFB8AA98))
                }
            }
        }
        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
        ) {
            DropdownMenuItem(
                text = { Text("Open details") },
                onClick = { menuExpanded = false; onOpen() },
                leadingIcon = { Icon(Icons.Rounded.AutoStories, contentDescription = null) },
            )
            if (shelves.isNotEmpty() && onToggleShelf != null) {
                Box {
                    DropdownMenuItem(
                        text = { Text("Move to shelf") },
                        onClick = { shelvesMenuExpanded = true },
                        leadingIcon = { Icon(Icons.Rounded.Bookmark, contentDescription = null) },
                        trailingIcon = { Icon(Icons.Rounded.ChevronRight, contentDescription = null) },
                    )
                    DropdownMenu(
                        expanded = shelvesMenuExpanded,
                        onDismissRequest = { shelvesMenuExpanded = false },
                    ) {
                        shelves.forEach { shelf ->
                            val onShelf = shelf.comicIds.contains(comic.id)
                            DropdownMenuItem(
                                text = { Text(shelf.name) },
                                onClick = {
                                    onToggleShelf(shelf.id)
                                    shelvesMenuExpanded = false
                                    menuExpanded = false
                                },
                                leadingIcon = {
                                    Icon(
                                        if (onShelf) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                                        contentDescription = null,
                                        tint = if (onShelf) Mint else Color(0xFFB8AA98),
                                    )
                                },
                            )
                        }
                    }
                }
            }
            DropdownMenuItem(
                text = { Text("Remove from library", color = Ember) },
                onClick = { menuExpanded = false; onRemove() },
                leadingIcon = { Icon(Icons.Rounded.Delete, contentDescription = null, tint = Ember) },
            )
        }
    }
}

@Composable
fun LibrarySortFilterBar(
    sortOrder: String,
    onSortChange: (String) -> Unit,
    sourceFilter: String,
    availableSources: List<String>,
    onSourceChange: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Sort row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState()),
        ) {
            Icon(Icons.Rounded.Tune, contentDescription = null, tint = Color(0xFFB8AA98), modifier = Modifier.size(16.dp))
            Text("Sort:", color = Color(0xFFB8AA98), fontSize = 12.sp)
            listOf("Added", "Title", "LastRead", "Rating", "Unread").forEach { option ->
                val label = when (option) {
                    "LastRead" -> "Last Read"
                    "Unread"   -> "Most Unread"
                    else       -> option
                }
                FilterChip(
                    selected = sortOrder == option,
                    onClick = { onSortChange(option) },
                    label = { Text(label, fontSize = 12.sp) },
                )
            }
        }
        // Source filter row (only shown when more than 1 real source exists)
        if (availableSources.size > 2) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState()),
            ) {
                Icon(Icons.Rounded.Explore, contentDescription = null, tint = Color(0xFFB8AA98), modifier = Modifier.size(16.dp))
                Text("Source:", color = Color(0xFFB8AA98), fontSize = 12.sp)
                availableSources.forEach { src ->
                    FilterChip(
                        selected = sourceFilter == src,
                        onClick = { onSourceChange(src) },
                        label = { Text(src, fontSize = 12.sp) },
                    )
                }
            }
        }
    }
}

@Composable
fun ChipRow(options: List<String>, selected: String, onSelect: (String) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.horizontalScroll(rememberScrollState()),
    ) {
        options.forEach { option ->
            FilterChip(
                selected = selected == option,
                onClick = { onSelect(option) },
                label = { Text(option) },
            )
        }
    }
}

@Composable
fun ComicPoster(comic: Comic, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        MiniCover(
            comic = comic,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.68f),
        )
        Text(comic.title, color = Paper, fontWeight = FontWeight.Black, maxLines = 2, overflow = TextOverflow.Ellipsis)
        Text("${comic.rating} • ${comic.latestChapter} ch", color = Color(0xFFC7B7A5), fontSize = 13.sp)
    }
}

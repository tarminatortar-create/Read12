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
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.focus.onFocusChanged
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
import com.readora.app.data.db.SourceEntity
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
import com.readora.app.ui.viewmodel.DiscoverFeedTab
import com.readora.app.ui.viewmodel.DiscoverSortOrder
import com.readora.app.ui.viewmodel.DiscoverViewModel
import com.readora.app.ui.viewmodel.ReadoraViewModelFactory
import com.readora.app.ui.viewmodel.SettingsViewModel
import com.readora.app.ui.viewmodel.SourceSearchSection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

@Composable
fun DiscoverScreen(onOpenComic: (Comic) -> Unit, onOpenOnlineComic: (OnlineComicSummary) -> Unit, initialTagFilter: String? = null) {
    val context = LocalContext.current
    val viewModel: DiscoverViewModel = viewModel(factory = ReadoraViewModelFactory(context))
    val settingsViewModel: SettingsViewModel = viewModel(factory = ReadoraViewModelFactory(context))
    val settingsState by settingsViewModel.uiState.collectAsState()
    val sources by settingsViewModel.sources.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val onlineResults = uiState.visibleComics
    val listState = rememberLazyListState()
    val preferences = remember { ReadoraPreferences(context) }
    var recentlyViewed by remember { mutableStateOf(preferences.loadRecentlyViewed()) }

    var previewComic by remember { mutableStateOf<OnlineComicSummary?>(null) }

    // Apply any incoming tag filter from external navigation (e.g. tag tap in Details)
    LaunchedEffect(initialTagFilter) {
        if (!initialTagFilter.isNullOrBlank()) {
            viewModel.selectTag(initialTagFilter)
        }
    }

    fun openOnlineComicTracked(comic: OnlineComicSummary) {
        preferences.addRecentlyViewed(comic)
        recentlyViewed = preferences.loadRecentlyViewed()
        onOpenOnlineComic(comic)
    }

    // Auto-load-more: trigger when within 4 items of the bottom
    LaunchedEffect(listState.layoutInfo.totalItemsCount, listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index) {
        val total = listState.layoutInfo.totalItemsCount
        val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
        if (total > 0 && lastVisible >= total - 4) {
            if (uiState.submittedQuery.isBlank()) {
                if (uiState.feedTab == DiscoverFeedTab.Latest) {
                    if (!uiState.latestLoadingMore && !uiState.latestLoading) viewModel.loadMoreLatest()
                } else {
                    if (!uiState.popularLoadingMore && !uiState.loading) viewModel.loadMorePopular()
                }
            }
        }
    }

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(start = 18.dp, top = 18.dp, end = 18.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
    ) {
        item {
            Header(
                eyebrow = "Discover",
                title = "Live sources, clean results",
                subtitle = "Now connected to a real source engine. MangaDex is the first connector; repos/extensions can plug into the same interface next.",
            )
        }
        // Source info panel — collapsible list of installed/enabled source connectors
        if (sources.isNotEmpty()) {
            item {
                SourceInfoPanel(sources = sources)
            }
        }
        // Recently viewed strip
        if (recentlyViewed.isNotEmpty()) {
            item {
                PremiumPanel {
                    SectionTitle("Recently viewed", "Titles you opened recently")
                    Spacer(Modifier.height(10.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(end = 4.dp),
                    ) {
                        items(recentlyViewed, key = { it.id }) { comic ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .width(80.dp)
                                    .clickable { openOnlineComicTracked(comic) },
                            ) {
                                OnlineCover(
                                    comic = comic,
                                    modifier = Modifier.size(width = 70.dp, height = 98.dp),
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    comic.title,
                                    color = Color(0xFFCDBFAD),
                                    fontSize = 10.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    lineHeight = 13.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                )
                            }
                        }
                    }
                }
            }
        }
        item {
            OnlineSearchPanel(
                query = uiState.draftQuery,
                onQueryChange = viewModel::updateDraftQuery,
                onSubmit = viewModel::submitSearch,
                onQuickSearch = viewModel::quickSearch,
                savedSearches = settingsState.savedSearches,
                popularTags = uiState.availableTags,
            )
        }
        if (settingsState.savedSearches.isNotEmpty()) {
            item {
                PremiumPanel {
                    SectionTitle("Saved searches", "Quick reruns across enabled sources")
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                        settingsState.savedSearches.forEach { query ->
                            FilterChip(
                                selected = uiState.draftQuery.equals(query, true),
                                onClick = { viewModel.quickSearch(query) },
                                modifier = Modifier.combinedClickable(
                                    onClick = { viewModel.quickSearch(query) },
                                    onLongClick = { settingsViewModel.removeSearch(query) },
                                ),
                                label = { Text(query) },
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Button(
                        onClick = { settingsViewModel.saveSearch(uiState.draftQuery) },
                        enabled = uiState.draftQuery.trim().isNotBlank(),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Save current search")
                    }
                }
            }
        } else {
            item {
                PremiumPanel {
                    SectionTitle("Saved searches", "Pin searches you use often")
                    Spacer(Modifier.height(10.dp))
                    Button(
                        onClick = { settingsViewModel.saveSearch(uiState.draftQuery) },
                        enabled = uiState.draftQuery.trim().isNotBlank(),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Save current search")
                    }
                }
            }
        }
        // Pinned searches strip — bookmarked queries shown as persistent chips
        if (settingsState.pinnedSearches.isNotEmpty()) {
            item {
                PremiumPanel {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Rounded.Bookmark, contentDescription = null, tint = Ember, modifier = Modifier.size(16.dp))
                        Text("Pinned searches", color = Paper, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1f))
                        TextButton(
                            onClick = { settingsViewModel.togglePinnedSearch(uiState.draftQuery) },
                            enabled = uiState.draftQuery.trim().isNotBlank(),
                        ) {
                            val isPinned = settingsState.pinnedSearches.any { it.equals(uiState.draftQuery.trim(), true) }
                            Text(if (isPinned) "Unpin" else "Pin current", fontSize = 12.sp)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                        settingsState.pinnedSearches.forEach { query ->
                            FilterChip(
                                selected = uiState.submittedQuery.equals(query, true),
                                onClick = { viewModel.quickSearch(query) },
                                label = { Text(query, fontSize = 13.sp) },
                                leadingIcon = { Icon(Icons.Rounded.Bookmark, contentDescription = null, modifier = Modifier.size(14.dp)) },
                            )
                        }
                    }
                }
            }
        } else if (uiState.draftQuery.trim().isNotBlank()) {
            item {
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth().padding(end = 4.dp),
                ) {
                    TextButton(onClick = { settingsViewModel.togglePinnedSearch(uiState.draftQuery) }) {
                        Icon(Icons.Rounded.Bookmark, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Pin this search", fontSize = 12.sp)
                    }
                }
            }
        }
        if (uiState.availableTags.isNotEmpty()) {
            item {
                var tagCloudExpanded by remember { mutableStateOf(false) }
                PremiumPanel {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column {
                            Text(
                                "Filter by genre",
                                color = Paper,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                            )
                            if (uiState.selectedTags.isNotEmpty()) {
                                Text(
                                    "${uiState.selectedTags.size} active",
                                    color = Ember,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            if (uiState.selectedTags.isNotEmpty()) {
                                TextButton(onClick = { viewModel.clearTagFilter() }) {
                                    Text("Clear", color = Ember, fontSize = 12.sp)
                                }
                            }
                            // Toggle between scroll-list and tag cloud
                            FilterChip(
                                selected = tagCloudExpanded,
                                onClick = { tagCloudExpanded = !tagCloudExpanded },
                                label = { Text(if (tagCloudExpanded) "Cloud ▲" else "Cloud ▼", fontSize = 11.sp) },
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    if (tagCloudExpanded) {
                        // Tag cloud: multi-row wrapped chips, font size varies by tag length (short tags = bigger)
                        val rows = uiState.availableTags.chunked(4)
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            rows.forEach { rowTags ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    rowTags.forEach { tag ->
                                        val isSelected = uiState.selectedTags.any { it.equals(tag, ignoreCase = true) }
                                        val tagFontSize = when {
                                            tag.length <= 5  -> 13.sp
                                            tag.length <= 10 -> 11.sp
                                            else             -> 10.sp
                                        }
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(20.dp))
                                                .background(
                                                    if (isSelected) Ember else Color.White.copy(alpha = 0.08f)
                                                )
                                                .clickable { viewModel.selectTag(tag) }
                                                .padding(horizontal = 10.dp, vertical = 6.dp),
                                        ) {
                                            Text(
                                                tag,
                                                color = if (isSelected) Paper else Color(0xFFCDBFAD),
                                                fontSize = tagFontSize,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                        ) {
                            if (uiState.selectedTags.isNotEmpty()) {
                                FilterChip(
                                    selected = true,
                                    onClick = { viewModel.clearTagFilter() },
                                    label = { Text("✕ Clear all") },
                                )
                            }
                            uiState.availableTags.forEach { tag ->
                                FilterChip(
                                    selected = uiState.selectedTags.any { it.equals(tag, ignoreCase = true) },
                                    onClick = { viewModel.selectTag(tag) },
                                    label = { Text(tag) },
                                )
                            }
                        }
                    }
                    if (uiState.selectedTags.isNotEmpty()) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Showing ${uiState.visibleComics.size} results matching: ${uiState.selectedTags.joinToString(" + ")}",
                            color = Color(0xFFCDBFAD),
                            fontSize = 12.sp,
                        )
                    }
                }
            }
        }
        // Feed tab toggle — only shown when not actively searching
        if (uiState.submittedQuery.isBlank()) {
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 4.dp),
                ) {
                    FilterChip(
                        selected = uiState.feedTab == DiscoverFeedTab.Popular,
                        onClick = { viewModel.selectFeedTab(DiscoverFeedTab.Popular) },
                        label = { Text("Popular") },
                    )
                    FilterChip(
                        selected = uiState.feedTab == DiscoverFeedTab.Latest,
                        onClick = { viewModel.selectFeedTab(DiscoverFeedTab.Latest) },
                        label = { Text("Latest Updates") },
                    )
                }
            }
        }
        // Sort order chips
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 2.dp),
            ) {
                listOf(
                    DiscoverSortOrder.Default    to "Default",
                    DiscoverSortOrder.TitleAZ   to "A → Z",
                    DiscoverSortOrder.TitleZA   to "Z → A",
                    DiscoverSortOrder.MostTags  to "Most tags",
                    DiscoverSortOrder.YearNewest to "Year ↓",
                    DiscoverSortOrder.YearOldest to "Year ↑",
                ).forEach { (order, label) ->
                    FilterChip(
                        selected = uiState.sortOrder == order,
                        onClick = { viewModel.selectSortOrder(order) },
                        label = { Text(label, fontSize = 11.sp) },
                    )
                }
            }
        }
        // Genre browse grid — shown when not actively searching
        if (uiState.submittedQuery.isBlank()) {
            item {
                GenreBrowseGrid(
                    selectedTags = uiState.selectedTags,
                    onSelectTag = viewModel::selectTag,
                    onClearTags = viewModel::clearTagFilter,
                )
            }
        }
        item {
            SectionTitle(
                if (uiState.submittedQuery.isBlank()) {
                    if (uiState.feedTab == DiscoverFeedTab.Latest) "Latest updates" else "Popular titles"
                } else "Global multi-source search",
                if (uiState.submittedQuery.isBlank()) {
                    if (uiState.feedTab == DiscoverFeedTab.Latest) "Recently updated on MangaDex"
                    else "Most followed across all enabled sources"
                } else {
                    "Merged results for '${uiState.submittedQuery}'" +
                        if (uiState.selectedTags.isNotEmpty()) " • genre: ${uiState.selectedTags.joinToString(", ")}" else ""
                },
            )
        }
        if (uiState.sourceSections.isNotEmpty() && uiState.submittedQuery.isNotBlank()) {
            item {
                SourceSectionsPanel(uiState.sourceSections)
            }
        }
        when {
            uiState.feedTab == DiscoverFeedTab.Latest && uiState.submittedQuery.isBlank() -> {
                when {
                    uiState.latestLoading -> item { LoadingPanel("Fetching latest updates...") }
                    uiState.latestError != null -> item {
                        ErrorPanel(uiState.latestError?.displayMessage ?: "Could not load latest", onRetry = viewModel::refreshLatest)
                    }
                    uiState.latestComics.isEmpty() -> item {
                        SkeletonDiscoveryCard()
                        SkeletonDiscoveryCard()
                        SkeletonDiscoveryCard()
                    }
                    else -> items(uiState.latestComics) { comic ->
                        OnlineDiscoveryCard(comic = comic, onClick = { openOnlineComicTracked(comic) }, onLongClick = { previewComic = comic })
                    }
                }
                // Load more — Latest (auto-triggered by scroll; spinner shown while loading)
                if (uiState.latestComics.isNotEmpty() && uiState.submittedQuery.isBlank()) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                            if (uiState.latestLoadingMore) {
                                CircularProgressIndicator(color = Ember, modifier = Modifier.size(28.dp), strokeWidth = 2.5.dp)
                            } else {
                                Text("Scroll for more", color = Color(0xFF9E9080), fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
            uiState.loading -> item { LoadingPanel("Searching enabled sources...") }
            uiState.error != null -> item { ErrorPanel(uiState.error?.displayMessage ?: "Unknown error", onRetry = viewModel::retry) }
            onlineResults.isEmpty() -> item { ErrorPanel("No online results found.", onRetry = { viewModel.quickSearch("") }) }
            else -> items(onlineResults) { comic ->
                OnlineDiscoveryCard(comic = comic, onClick = { openOnlineComicTracked(comic) }, onLongClick = { previewComic = comic })
            }
        }
        // Load more — Popular (auto-triggered by scroll; spinner shown while loading)
        if (uiState.submittedQuery.isBlank() && uiState.feedTab == DiscoverFeedTab.Popular && uiState.popularComics.isNotEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                    if (uiState.popularLoadingMore) {
                        CircularProgressIndicator(color = Ember, modifier = Modifier.size(28.dp), strokeWidth = 2.5.dp)
                    } else {
                        Text("Scroll for more", color = Color(0xFF9E9080), fontSize = 12.sp)
                    }
                }
            }
        }
        item {
            SectionTitle("Local demo shelf", "Offline mock data still available while source repos grow")
        }
        items(DemoCatalog.comics) { comic ->
            DiscoveryCard(comic, onClick = { onOpenComic(comic) })
        }
    }

    // Comic quick-preview dialog — shown on long-press of any online card
    previewComic?.let { comic ->
        AlertDialog(
            onDismissRequest = { previewComic = null },
            containerColor = Color(0xFF1C1712),
            title = null,
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        OnlineCover(comic = comic, modifier = Modifier.size(72.dp, 104.dp))
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(comic.title, color = Paper, fontWeight = FontWeight.Black, fontSize = 16.sp, maxLines = 3, overflow = TextOverflow.Ellipsis)
                            Pill(comic.sourceName, Color(0xCC000000))
                            if (comic.status.isNotBlank()) Pill(comic.status, Color.White.copy(alpha = 0.12f))
                        }
                    }
                    if (comic.description.isNotBlank()) {
                        Text(comic.description, color = Color(0xFFD7C8B6), fontSize = 13.sp, maxLines = 5, overflow = TextOverflow.Ellipsis)
                    }
                    if (comic.tags.isNotEmpty()) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                            comic.tags.take(5).forEach { Pill(it, Color.White.copy(alpha = 0.08f)) }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { previewComic = null; openOnlineComicTracked(comic) }) {
                    Text("Open")
                }
            },
            dismissButton = {
                TextButton(onClick = { previewComic = null }) {
                    Text("Dismiss", color = Color(0xFF9E9080))
                }
            },
        )
    }
}

@Composable
fun SourceSectionsPanel(sections: List<SourceSearchSection>) {
    PremiumPanel {
        Text("Source status", color = Paper, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            sections.forEach { section ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(section.sourceName, color = Color(0xFFE2D5C2), modifier = Modifier.weight(1f))
                    val status = when {
                        section.loading -> "Loading..."
                        section.error != null -> "Error"
                        else -> "${section.resultCount} results"
                    }
                    val statusColor = when {
                        section.loading -> Sky
                        section.error != null -> Ember
                        else -> Mint
                    }
                    Text(status, color = statusColor, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun OnlineSearchPanel(
    query: String,
    onQueryChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onQuickSearch: (String) -> Unit,
    savedSearches: List<String> = emptyList(),
    popularTags: List<String> = emptyList(),
) {
    var isFocused by remember { mutableStateOf(false) }

    // Suggestions: recent searches that match the current query (or all when blank),
    // topped up with popular tags when query is blank
    val suggestions = remember(query, savedSearches, popularTags) {
        val lower = query.trim().lowercase()
        val recentMatches = if (lower.isBlank()) savedSearches
            else savedSearches.filter { it.lowercase().contains(lower) }
        val tagMatches = if (lower.isBlank()) popularTags
            else popularTags.filter { it.lowercase().contains(lower) }
        (recentMatches + tagMatches).distinct().take(8)
    }
    val showSuggestions = isFocused && suggestions.isNotEmpty()

    PremiumPanel {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                label = { Text("Search online") },
                placeholder = { Text("Try dungeon, romance, villainess") },
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { isFocused = it.isFocused },
            )
            Button(onClick = onSubmit) {
                Icon(Icons.Rounded.Search, contentDescription = null)
            }
        }
        if (showSuggestions) {
            Spacer(Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState()),
            ) {
                suggestions.forEach { suggestion ->
                    AssistChip(
                        onClick = { onQuickSearch(suggestion) },
                        label = { Text(suggestion, fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(
                                if (savedSearches.contains(suggestion)) Icons.Rounded.Restore else Icons.Rounded.Search,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                            )
                        },
                    )
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
            listOf("solo leveling", "villainess", "romance", "isekai", "martial arts", "completed").forEach { label ->
                FilterChip(selected = query == label, onClick = { onQuickSearch(label) }, label = { Text(label) })
            }
        }
    }
}

@Composable
fun LoadingPanel(message: String) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Skeleton shimmer cards while loading
        repeat(3) { SkeletonDiscoveryCard() }
    }
}

@Composable
fun SkeletonDiscoveryCard() {
    val transition = rememberInfiniteTransition(label = "skeleton")
    val alpha by transition.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.20f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "shimmer",
    )
    PremiumPanel {
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            // Cover placeholder
            Box(
                modifier = Modifier
                    .size(86.dp, 124.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = alpha)),
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Title line
                Box(modifier = Modifier.fillMaxWidth(0.7f).height(18.dp).clip(RoundedCornerShape(6.dp)).background(Color.White.copy(alpha = alpha)))
                // Description lines
                Box(modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)).background(Color.White.copy(alpha = alpha * 0.7f)))
                Box(modifier = Modifier.fillMaxWidth(0.85f).height(12.dp).clip(RoundedCornerShape(6.dp)).background(Color.White.copy(alpha = alpha * 0.6f)))
                Box(modifier = Modifier.fillMaxWidth(0.6f).height(12.dp).clip(RoundedCornerShape(6.dp)).background(Color.White.copy(alpha = alpha * 0.5f)))
                // Tag pills
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    repeat(3) {
                        Box(modifier = Modifier.size(56.dp, 22.dp).clip(RoundedCornerShape(100.dp)).background(Color.White.copy(alpha = alpha * 0.5f)))
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorPanel(message: String, onRetry: () -> Unit) {
    PremiumPanel {
        Text("Source needs attention", color = Ember, fontWeight = FontWeight.Black, fontSize = 18.sp)
        Text(message, color = Color(0xFFDCCDBB), modifier = Modifier.padding(top = 6.dp), lineHeight = 20.sp)
        TextButton(onClick = onRetry, modifier = Modifier.padding(top = 6.dp)) {
            Text("Retry")
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun OnlineDiscoveryCard(comic: OnlineComicSummary, onClick: () -> Unit, onLongClick: (() -> Unit)? = null) {
    PremiumPanel(modifier = Modifier.combinedClickable(onClick = onClick, onLongClick = onLongClick)) {
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            OnlineCover(comic = comic, modifier = Modifier.size(86.dp, 124.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(comic.title, color = Paper, fontWeight = FontWeight.Black, fontSize = 20.sp, modifier = Modifier.weight(1f), maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Pill(comic.sourceName, Color(0xCC000000))
                }
                Text(comic.description.ifBlank { "No description supplied by source." }, color = Color(0xFFD7C8B6), maxLines = 3, overflow = TextOverflow.Ellipsis)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    comic.tags.take(4).forEach { Pill(it, Color.White.copy(alpha = 0.08f)) }
                    if (comic.status.isNotBlank()) Pill(comic.status, Color.White.copy(alpha = 0.08f))
                    if (comic.year != null) Pill(comic.year.toString(), Color(0xFF2A3A50))
                }
                Text("Online - search/details/chapters/pages via source connector", color = Sky, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun DiscoveryCard(comic: Comic, onClick: () -> Unit) {
    PremiumPanel(modifier = Modifier.clickable(onClick = onClick)) {
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            MiniCover(comic, Modifier.size(86.dp, 124.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(comic.title, color = Paper, fontWeight = FontWeight.Black, fontSize = 20.sp, modifier = Modifier.weight(1f))
                    RatingPill(comic.rating)
                }
                Text(comic.subtitle, color = Color(0xFFD7C8B6), maxLines = 2)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    comic.genres.forEach { Pill(it, Color.White.copy(alpha = 0.08f)) }
                }
                Text("${comic.sources.size} mirrors • best quality ${comic.sources.maxOf { it.quality }}%", color = Color(comic.accent), fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun SourceInfoPanel(sources: List<SourceEntity>) {
    var expanded by remember { mutableStateOf(false) }
    val enabledCount = sources.count { it.enabled }
    PremiumPanel {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
        ) {
            Column {
                Text("Source connectors", color = Paper, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(
                    "$enabledCount enabled • ${sources.size} installed",
                    color = Color(0xFFAA9E8E),
                    fontSize = 12.sp,
                )
            }
            Text(
                if (expanded) "▲ Hide" else "▼ Show",
                color = Ember,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
        if (expanded) {
            Spacer(Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                sources.forEach { source ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(alpha = 0.04f))
                            .padding(10.dp),
                    ) {
                        // Enabled status dot
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (source.enabled) Mint else Color(0xFF666676)),
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Text(source.name, color = Paper, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                // Category badge
                                if (source.category.isNotBlank()) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Ember.copy(alpha = 0.18f))
                                            .padding(horizontal = 5.dp, vertical = 1.dp),
                                    ) {
                                        Text(
                                            source.category.uppercase(),
                                            color = Ember,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    }
                                }
                            }
                            Text(
                                "v${source.version} • ${source.language.uppercase()}",
                                color = Color(0xFFAA9E8E),
                                fontSize = 11.sp,
                            )
                            if (source.baseUrl.isNotBlank()) {
                                Text(
                                    source.baseUrl,
                                    color = Sky.copy(alpha = 0.75f),
                                    fontSize = 10.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                        // Enabled indicator text
                        Text(
                            if (source.enabled) "ON" else "OFF",
                            color = if (source.enabled) Mint else Color(0xFF666676),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

// Genre data: label, accent color, icon
private data class GenreTile(val label: String, val color: Color, val icon: androidx.compose.ui.graphics.vector.ImageVector)

private val genreTiles = listOf(
    GenreTile("Action",      Color(0xFFE05A3A), Icons.Rounded.Star),
    GenreTile("Romance",     Color(0xFFD94F7E), Icons.Rounded.Favorite),
    GenreTile("Fantasy",     Color(0xFF7B52D6), Icons.Rounded.AutoStories),
    GenreTile("Isekai",      Color(0xFF3B8FD4), Icons.Rounded.Explore),
    GenreTile("Comedy",      Color(0xFFE8A22A), Icons.Rounded.PlayArrow),
    GenreTile("Horror",      Color(0xFF4A4A6A), Icons.Rounded.Star),
    GenreTile("Sci-Fi",      Color(0xFF2DB0A0), Icons.Rounded.Refresh),
    GenreTile("Slice of Life", Color(0xFF5BAD6F), Icons.Rounded.MenuBook),
    GenreTile("Drama",       Color(0xFF8C6240), Icons.Rounded.Star),
    GenreTile("Mystery",     Color(0xFF5E5E8E), Icons.Rounded.Search),
    GenreTile("Sports",      Color(0xFF3AAA6A), Icons.Rounded.Star),
    GenreTile("Thriller",    Color(0xFF9E3A50), Icons.Rounded.Star),
)

@Composable
fun GenreBrowseGrid(
    selectedTags: Set<String>,
    onSelectTag: (String) -> Unit,
    onClearTags: () -> Unit,
) {
    PremiumPanel {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                "Browse by genre",
                color = Paper,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
            )
            if (selectedTags.isNotEmpty()) {
                TextButton(onClick = onClearTags) {
                    Text("Clear", color = Ember, fontSize = 12.sp)
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        // 3-column grid via chunked rows
        val rows = genreTiles.chunked(3)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            rows.forEach { rowTiles ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    rowTiles.forEach { tile ->
                        val isSelected = selectedTags.any { it.equals(tile.label, ignoreCase = true) }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(64.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) tile.color
                                    else tile.color.copy(alpha = 0.18f)
                                )
                                .border(
                                    width = if (isSelected) 2.dp else 0.dp,
                                    color = if (isSelected) tile.color else Color.Transparent,
                                    shape = RoundedCornerShape(12.dp),
                                )
                                .clickable { onSelectTag(tile.label) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(
                                    tile.icon,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else tile.color,
                                    modifier = Modifier.size(20.dp),
                                )
                                Text(
                                    tile.label,
                                    color = if (isSelected) Color.White else Paper,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }
                    // Fill remaining slots if row has fewer than 3 tiles
                    repeat(3 - rowTiles.size) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

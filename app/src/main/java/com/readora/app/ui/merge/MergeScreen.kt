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
import androidx.compose.material.icons.rounded.Star
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
fun MergeLabScreen(onOpenComic: (Comic) -> Unit) {
    val context = LocalContext.current
    val preferences = remember { ReadoraPreferences(context) }
    var mergeGroups by remember { mutableStateOf(preferences.loadMergeGroups()) }
    val savedOnline = remember(mergeGroups) { preferences.loadOnlineLibrary() }
    val savedLocal = remember(mergeGroups) { preferences.loadLocalLibrary() }
    var manualTitle by rememberSaveable { mutableStateOf("") }
    var selectedMergeKeys by remember { mutableStateOf<Set<String>>(emptySet()) }
    var suggestedPairs by remember { mutableStateOf<List<Pair<com.readora.app.storage.SavedOnlineComic, com.readora.app.storage.SavedOnlineComic>>>(emptyList()) }

    LaunchedEffect(mergeGroups, savedOnline) {
        suggestedPairs = preferences.suggestDuplicates()
    }

    LazyColumn(
        contentPadding = PaddingValues(start = 18.dp, top = 18.dp, end = 18.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
    ) {
        item {
            Header(
                eyebrow = "Merge Lab",
                title = "One title. Many sources. Zero chaos.",
                subtitle = "Create smart merge groups from saved online and local entries, then keep source priority and progress unified.",
            )
        }
        item {
            Button(onClick = {
                mergeGroups = preferences.createSmartMergeGroups()
                suggestedPairs = preferences.suggestDuplicates()
            }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Rounded.CallMerge, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Detect smart merge groups")
            }
        }
        if (suggestedPairs.isNotEmpty()) {
            item {
                PremiumPanel {
                    SectionTitle("Suggested merges", "${suggestedPairs.size} possible duplicate pair${if (suggestedPairs.size != 1) "s" else ""} found")
                    Spacer(Modifier.height(10.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        suggestedPairs.forEach { (a, b) ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(a.title, color = Paper, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 13.sp)
                                    Text(a.sourceName, color = Color(0xFF9E927F), fontSize = 11.sp)
                                }
                                Icon(Icons.Rounded.CallMerge, contentDescription = null, tint = Sky.copy(alpha = 0.6f), modifier = Modifier.size(18.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(b.title, color = Paper, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 13.sp)
                                    Text(b.sourceName, color = Color(0xFF9E927F), fontSize = 11.sp)
                                }
                                TextButton(
                                    onClick = {
                                        mergeGroups = preferences.createManualMergeGroup(
                                            title = a.title,
                                            itemKeys = listOf("online:${a.sourceId}:${a.id}", "online:${b.sourceId}:${b.id}"),
                                        )
                                        suggestedPairs = preferences.suggestDuplicates()
                                    },
                                    modifier = Modifier.padding(0.dp),
                                ) {
                                    Text("Merge", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
        item {
            ManualMergeBuilder(
                title = manualTitle,
                onTitleChange = { manualTitle = it },
                savedOnline = savedOnline,
                savedLocal = savedLocal,
                selectedKeys = selectedMergeKeys,
                onToggle = { key ->
                    selectedMergeKeys = if (selectedMergeKeys.contains(key)) {
                        selectedMergeKeys - key
                    } else {
                        selectedMergeKeys + key
                    }
                },
                onCreate = {
                    mergeGroups = preferences.createManualMergeGroup(manualTitle, selectedMergeKeys.toList())
                    manualTitle = ""
                    selectedMergeKeys = emptySet()
                },
            )
        }
        if (mergeGroups.isNotEmpty()) {
            item { SectionTitle("Your merge groups", "Persistent groups detected from library titles") }
            items(mergeGroups) { group ->
                SavedMergeGroupCard(
                    group = group,
                    savedOnline = savedOnline,
                    savedLocal = savedLocal,
                    onRemove = { mergeGroups = preferences.removeMergeGroup(group.id) },
                    onMove = { key, delta -> mergeGroups = preferences.moveMergeItem(group.id, key, delta) },
                )
            }
        } else {
            item {
                PremiumPanel {
                    SectionTitle("No saved duplicate groups yet", "Save or import matching titles first")
                    Text(
                        "Example: save the same title from an online source and import a matching local CBZ, then run smart detection.",
                        color = Color(0xFFDCCDBB),
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }
        }
        item { SectionTitle("Demo merge concepts", "Prototype cards from the initial app shell") }
        items(DemoCatalog.comics) { comic ->
            MergeCard(comic = comic, onOpen = { onOpenComic(comic) })
        }
    }
}

@Composable
fun ManualMergeBuilder(
    title: String,
    onTitleChange: (String) -> Unit,
    savedOnline: List<SavedOnlineComic>,
    savedLocal: List<SavedLocalComic>,
    selectedKeys: Set<String>,
    onToggle: (String) -> Unit,
    onCreate: () -> Unit,
) {
    PremiumPanel {
        SectionTitle("Manual merge builder", "Pick 2 or more saved entries and set source priority later")
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            singleLine = true,
            label = { Text("Merged title") },
            placeholder = { Text("Leave blank to use Manual merge group") },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))
        if (savedOnline.isEmpty() && savedLocal.isEmpty()) {
            Text("Save online titles or import local files first.", color = Color(0xFFB8AA98), fontSize = 13.sp)
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                savedOnline.forEach { item ->
                    val key = "online:${item.sourceId}:${item.id}"
                    FilterChip(
                        selected = selectedKeys.contains(key),
                        onClick = { onToggle(key) },
                        label = { Text("Online: ${item.title.take(18)}") },
                    )
                }
                savedLocal.forEach { item ->
                    val key = "local:${item.uri}"
                    FilterChip(
                        selected = selectedKeys.contains(key),
                        onClick = { onToggle(key) },
                        label = { Text("Local: ${item.title.take(18)}") },
                    )
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Button(onClick = onCreate, enabled = selectedKeys.size >= 2, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Rounded.CallMerge, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Create manual merge (${selectedKeys.size})")
        }
    }
}

@Composable
fun SavedMergeGroupCard(
    group: SavedMergeGroup,
    savedOnline: List<SavedOnlineComic>,
    savedLocal: List<SavedLocalComic>,
    onRemove: () -> Unit,
    onMove: (String, Int) -> Unit,
) {
    // Collect cover URLs for stacked preview (up to 3 online entries)
    val coverPreviews = remember(group, savedOnline) {
        group.itemKeys
            .filter { it.startsWith("online:") }
            .mapNotNull { key ->
                val id = key.substringAfterLast(':')
                savedOnline.firstOrNull { it.id == id }
            }
            .take(3)
    }

    PremiumPanel {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Stacked cover thumbnails or fallback icon
            if (coverPreviews.isNotEmpty()) {
                Box(modifier = Modifier.width(64.dp).height(90.dp)) {
                    coverPreviews.reversed().forEachIndexed { revIdx, item ->
                        val offset = (coverPreviews.lastIndex - revIdx) * 6
                        OnlineCover(
                            comic = OnlineComicSummary(
                                id = item.id,
                                sourceId = item.sourceId,
                                sourceName = item.sourceName,
                                title = item.title,
                                description = item.description,
                                coverUrl = item.coverUrl,
                                tags = item.tags,
                                status = item.status,
                            ),
                            modifier = Modifier
                                .size(52.dp, 74.dp)
                                .padding(start = offset.dp)
                                .border(2.dp, Color(0xFF1A1A1A), RoundedCornerShape(10.dp)),
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Ember.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Rounded.CallMerge, contentDescription = null, tint = Ember)
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(group.title, color = Paper, fontWeight = FontWeight.Black, fontSize = 20.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${group.itemKeys.size} linked entries · unified progress", color = Color(0xFFCDBFAD), fontSize = 13.sp)
                // Source badges
                val sourceNames = coverPreviews.map { it.sourceName }.distinct()
                if (sourceNames.isNotEmpty()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 4.dp)) {
                        sourceNames.forEach { src -> Pill(src, Color.White.copy(alpha = 0.10f)) }
                    }
                }
            }
            TextButton(onClick = onRemove) {
                Text("Remove", color = Ember)
            }
        }
        Spacer(Modifier.height(14.dp))
        group.itemKeys.forEachIndexed { index, key ->
            val (entryTitle, entryMeta, entryOnline) = when {
                key.startsWith("online:") -> {
                    val id = key.substringAfterLast(':')
                    val item = savedOnline.firstOrNull { it.id == id }
                    Triple(item?.title ?: key, "Online · ${item?.sourceName ?: "source"}", item)
                }
                key.startsWith("local:") -> {
                    val uri = key.removePrefix("local:")
                    val item = savedLocal.firstOrNull { it.uri == uri }
                    Triple(item?.title ?: key, "Local · ${item?.type ?: "file"}", null)
                }
                else -> Triple(key, "", null)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(vertical = 3.dp),
            ) {
                // Small priority badge
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(if (index == 0) Ember.copy(alpha = 0.22f) else Sky.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "#${index + 1}",
                        color = if (index == 0) Ember else Sky,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp,
                    )
                }
                // Mini cover for online entries
                if (entryOnline != null) {
                    OnlineCover(
                        comic = OnlineComicSummary(
                            id = entryOnline.id,
                            sourceId = entryOnline.sourceId,
                            sourceName = entryOnline.sourceName,
                            title = entryOnline.title,
                            description = entryOnline.description,
                            coverUrl = entryOnline.coverUrl,
                            tags = entryOnline.tags,
                            status = entryOnline.status,
                        ),
                        modifier = Modifier
                            .size(32.dp, 46.dp)
                            .clip(RoundedCornerShape(6.dp)),
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(32.dp, 46.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White.copy(alpha = 0.07f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Rounded.MenuBook, contentDescription = null, tint = Color(0xFFB8AA98), modifier = Modifier.size(16.dp))
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(entryTitle, color = Paper, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(entryMeta, color = Color(0xFF9E927F), fontSize = 11.sp)
                }
                TextButton(onClick = { onMove(key, -1) }, enabled = index > 0) {
                    Text("↑", fontSize = 13.sp)
                }
                TextButton(onClick = { onMove(key, 1) }, enabled = index < group.itemKeys.lastIndex) {
                    Text("↓", fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun MergeCard(comic: Comic, onOpen: () -> Unit) {
    PremiumPanel(modifier = Modifier.clickable(onClick = onOpen)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(contentAlignment = Alignment.Center) {
                MiniCover(comic, Modifier.size(78.dp, 108.dp))
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .clip(CircleShape)
                        .background(Color(comic.accent))
                        .padding(7.dp),
                ) {
                    Icon(Icons.Rounded.CallMerge, contentDescription = null, tint = Ink, modifier = Modifier.size(16.dp))
                }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Text(comic.title, color = Paper, fontSize = 20.sp, fontWeight = FontWeight.Black)
                Text("Aliases matched • chapters normalized • progress unified", color = Color(0xFFCDBFAD), fontSize = 13.sp)
                comic.sources.forEach { source ->
                    LinearProgressIndicator(
                        progress = { source.quality / 100f },
                        color = if (source.isPrimary) Color(comic.accent) else Sky.copy(alpha = 0.7f),
                        trackColor = Color.White.copy(alpha = 0.08f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(CircleShape),
                    )
                }
            }
        }
    }
}

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
import androidx.compose.material3.CircularProgressIndicator
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
import com.readora.app.ui.viewmodel.HomeViewModel
import com.readora.app.ui.viewmodel.ReadoraViewModelFactory
import com.readora.app.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

@Composable
fun HomeScreen(
    onOpenComic: (Comic) -> Unit,
    onOpenOnlineComic: (OnlineComicSummary) -> Unit,
    onRead: (Comic) -> Unit,
    onOpenOnlineReader: ((OnlineComicSummary, OnlineChapter) -> Unit)? = null,
    onOpenSearch: () -> Unit = {},
    onOpenBookmarks: () -> Unit = {},
) {
    val context = LocalContext.current
    val homeViewModel: HomeViewModel = viewModel(factory = ReadoraViewModelFactory(context))
    val databaseContinue by homeViewModel.continueReading.collectAsState()
    val roomLastRead by homeViewModel.lastRead.collectAsState()
    val readingStreak by homeViewModel.readingStreak.collectAsState()
    val totalSessionCount by homeViewModel.totalSessionCount.collectAsState()
    val totalReadingMinutes by homeViewModel.totalReadingMinutes.collectAsState()
    val recentlyUpdatedComicIds by homeViewModel.recentlyUpdatedComicIds.collectAsState()
    val todayMinutes by homeViewModel.todayMinutes.collectAsState()
    val preferences = remember { ReadoraPreferences(context) }
    val settings = remember { SettingsSerializer(context).load() }
    val featured = DemoCatalog.comics.first()
    val lastRead = remember(settings.incognitoMode) {
        if (settings.incognitoMode) null else preferences.loadLastOnlineRead()
    }
    val savedOnlineLibrary = remember(settings.incognitoMode) {
        if (settings.incognitoMode) emptyList() else preferences.loadOnlineLibrary()
    }
    // In-progress carousel: online titles with at least one chapter partially read (not 100%)
    val inProgressTitles = remember(savedOnlineLibrary, settings.incognitoMode) {
        if (settings.incognitoMode) emptyList()
        else savedOnlineLibrary.filter { comic ->
            val lastRead = preferences.loadLastOnlineRead()
            if (lastRead?.comicId == comic.id) {
                val prog = preferences.loadChapterProgress(comic.sourceId, comic.id, lastRead.chapterId)
                prog != null && prog.fraction < 0.97f
            } else false
        }.take(5)
    }
    // Daily recommendation — computed once at composable scope to avoid LazyColumn item{} pitfalls
    val todaySeed = remember {
        val cal = java.util.Calendar.getInstance()
        (cal.get(java.util.Calendar.YEAR) * 1000 + cal.get(java.util.Calendar.DAY_OF_YEAR)).toLong()
    }
    val dailyPick = remember(savedOnlineLibrary, todaySeed) {
        if (savedOnlineLibrary.isEmpty()) null
        else savedOnlineLibrary[(todaySeed % savedOnlineLibrary.size).toInt()]
    }
    val dailyPickSummary = remember(dailyPick) { dailyPick?.toOnlineSummary() }
    // Library titles with unread updates, ordered by most recently updated
    val recentlyUpdated = remember(savedOnlineLibrary, recentlyUpdatedComicIds) {
        recentlyUpdatedComicIds
            .mapNotNull { id -> savedOnlineLibrary.firstOrNull { it.id == id } }
            .take(12)
    }
    LazyColumn(
        contentPadding = PaddingValues(start = 18.dp, top = 18.dp, end = 18.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    val greetingHour = remember { java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY) }
                    val greeting = remember(greetingHour) {
                        when (greetingHour) {
                            in 5..11  -> "Good morning ☀️"
                            in 12..17 -> "Good afternoon 📖"
                            in 18..21 -> "Good evening 🌙"
                            else      -> "Reading late? 🌃"
                        }
                    }
                    Text("Readora", color = Ember, fontWeight = FontWeight.Black, fontSize = 13.sp)
                    Text(greeting, color = Paper, fontWeight = FontWeight.Black, fontSize = 22.sp)
                }
                // Reading streak chip
                if (readingStreak > 0 && !settings.incognitoMode) {
                    Box(
                        modifier = Modifier
                            .background(Ember.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("🔥", fontSize = 14.sp)
                            Text(
                                "$readingStreak",
                                color = Ember,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                            )
                        }
                    }
                }
                IconButton(onClick = onOpenSearch) {
                    Icon(Icons.Rounded.Search, contentDescription = "Search", tint = com.readora.app.ui.theme.Paper)
                }
                IconButton(onClick = onOpenBookmarks) {
                    Icon(Icons.Rounded.Bookmark, contentDescription = "Bookmarks", tint = com.readora.app.ui.theme.Paper)
                }
            }
        }
        if (readingStreak >= 2 && !settings.incognitoMode) {
            item {
                StreakMilestoneBanner(streak = readingStreak)
            }
        }
        // Daily goal compact progress banner (shown below streak when goal is set)
        if (!settings.incognitoMode && settings.dailyGoalMinutes > 0) {
            item {
                val goalMin = settings.dailyGoalMinutes.toLong()
                val goalProgress = (todayMinutes.toFloat() / goalMin.toFloat()).coerceIn(0f, 1f)
                val goalDone = todayMinutes >= goalMin
                val animGoalProgress by animateFloatAsState(targetValue = goalProgress, label = "goal_progress")
                PremiumPanel {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(
                            Icons.Rounded.Star,
                            contentDescription = null,
                            tint = if (goalDone) Mint else Ember,
                            modifier = Modifier.size(18.dp),
                        )
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    if (goalDone) "Daily goal reached!" else "Daily reading goal",
                                    color = if (goalDone) Mint else Paper,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                )
                                Text(
                                    "${todayMinutes}m / ${goalMin}m",
                                    color = Color(0xFF9E9080),
                                    fontSize = 11.sp,
                                )
                            }
                            LinearProgressIndicator(
                                progress = { animGoalProgress },
                                color = if (goalDone) Mint else Ember,
                                trackColor = Color.White.copy(alpha = 0.08f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                            )
                        }
                    }
                }
            }
        }
        // In-progress carousel
        if (inProgressTitles.isNotEmpty() && !settings.incognitoMode) {
            item {
                PremiumPanel {
                    SectionTitle("In progress", "Pick up where you left off")
                    Spacer(Modifier.height(10.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(end = 4.dp),
                    ) {
                        items(inProgressTitles, key = { it.id }) { comic ->
                            val lastRead = remember { preferences.loadLastOnlineRead() }
                            val prog = if (lastRead?.comicId == comic.id)
                                remember { preferences.loadChapterProgress(comic.sourceId, comic.id, lastRead.chapterId) }
                            else null
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .width(100.dp)
                                    .clickable { onOpenOnlineComic(comic.toOnlineSummary()) },
                            ) {
                                Box(modifier = Modifier.size(width = 88.dp, height = 124.dp)) {
                                    OnlineCover(
                                        comic = comic.toOnlineSummary(),
                                        modifier = Modifier.fillMaxSize(),
                                    )
                                    if (prog != null) {
                                        LinearProgressIndicator(
                                            progress = { prog.fraction.coerceIn(0f, 1f) },
                                            color = Ember,
                                            trackColor = Color.Black.copy(alpha = 0.5f),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(4.dp)
                                                .align(Alignment.BottomCenter),
                                        )
                                    }
                                }
                                Spacer(Modifier.height(5.dp))
                                Text(
                                    comic.title,
                                    color = Paper,
                                    fontSize = 11.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    lineHeight = 14.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                )
                                if (prog != null) {
                                    Text(
                                        "Ch.${lastRead?.chapterNumber ?: ""} · p.${prog.currentPage}/${prog.totalPages}",
                                        color = Ember,
                                        fontSize = 9.sp,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        // Quick stats row — 4-tile library mini-dashboard
        if (!settings.incognitoMode && (totalSessionCount > 0 || savedOnlineLibrary.isNotEmpty())) {
            item {
                val completedCount = remember(savedOnlineLibrary) {
                    savedOnlineLibrary.count { preferences.loadReadingListStatus(it.id) == "completed" }
                }
                val readingCount = remember(savedOnlineLibrary) {
                    savedOnlineLibrary.count { preferences.loadReadingListStatus(it.id) == "reading" }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    val statModifier = Modifier
                        .weight(1f)
                        .background(Color(0xFF1A1B22), RoundedCornerShape(14.dp))
                        .padding(vertical = 12.dp, horizontal = 6.dp)
                    Column(modifier = statModifier, horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            savedOnlineLibrary.size.toString(),
                            color = Ember,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                        )
                        Text("Saved", color = Color(0xFFB8AA98), fontSize = 10.sp)
                    }
                    Column(modifier = statModifier, horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            readingCount.toString(),
                            color = Sky,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                        )
                        Text("Reading", color = Color(0xFFB8AA98), fontSize = 10.sp)
                    }
                    Column(modifier = statModifier, horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            completedCount.toString(),
                            color = Mint,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                        )
                        Text("Finished", color = Color(0xFFB8AA98), fontSize = 10.sp)
                    }
                    Column(modifier = statModifier, horizontalAlignment = Alignment.CenterHorizontally) {
                        val hours = totalReadingMinutes / 60L
                        val mins = totalReadingMinutes % 60L
                        Text(
                            if (hours > 0) "${hours}h" else "${mins}m",
                            color = Color(0xFFE0A870),
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                        )
                        Text("Read", color = Color(0xFFB8AA98), fontSize = 10.sp)
                    }
                }
            }
        }
        item {
            FeaturedCard(
                comic = featured,
                onOpen = { onOpenComic(featured) },
                onRead = { onRead(featured) },
            )
        }
        if (savedOnlineLibrary.isNotEmpty()) {
            item {
                SectionTitle("My library", "Tap to open details · ${savedOnlineLibrary.size} saved")
            }
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp),
                ) {
                    items(savedOnlineLibrary.take(12)) { saved ->
                        OnlineLibraryStripCard(
                            comic = saved,
                            onClick = { onOpenOnlineComic(saved.toOnlineSummary()) },
                        )
                    }
                }
            }
        }
        // Recently updated strip — library titles with unread chapter updates
        if (recentlyUpdated.isNotEmpty() && !settings.incognitoMode) {
            item {
                SectionTitle("Recently updated", "${recentlyUpdated.size} titles with new chapters")
            }
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp),
                ) {
                    items(recentlyUpdated, key = { it.id }) { saved ->
                        OnlineLibraryStripCard(
                            comic = saved,
                            onClick = { onOpenOnlineComic(saved.toOnlineSummary()) },
                        )
                    }
                }
            }
        }
        // Daily reading goal widget
        if (!settings.incognitoMode && settings.dailyGoalMinutes > 0) {
            item {
                DailyGoalWidget(todayMinutes = todayMinutes, goalMinutes = settings.dailyGoalMinutes.toLong())
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionTitle("Smart shelves", "Built to solve messy comic libraries")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(DemoCatalog.shelves) { shelf ->
                        ShelfCard(shelf)
                    }
                }
            }
        }
        // Daily recommendation — dailyPick/dailyPickSummary computed above at composable scope
        if (dailyPick != null && dailyPickSummary != null && !settings.incognitoMode) {
            item {
                PremiumPanel {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(bottom = 8.dp),
                    ) {
                        Icon(Icons.Rounded.Star, contentDescription = null, tint = Color(0xFFFFD166), modifier = Modifier.size(16.dp))
                        Text("Today's pick", color = Color(0xFFFFD166), fontWeight = FontWeight.Black, fontSize = 13.sp)
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenOnlineComic(dailyPickSummary) },
                    ) {
                        OnlineCover(
                            comic = dailyPickSummary,
                            modifier = Modifier
                                .size(72.dp, 104.dp)
                                .clip(RoundedCornerShape(10.dp)),
                        )
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(dailyPick.title, color = Paper, fontWeight = FontWeight.Black, fontSize = 16.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                            Text(dailyPick.sourceName, color = Color(0xFFB8AA98), fontSize = 12.sp)
                            if (dailyPick.lastChapterNumber.isNotBlank()) {
                                Text("Latest: Ch. ${dailyPick.lastChapterNumber}", color = Color(0xFF9E927F), fontSize = 12.sp)
                            }
                            AssistChip(
                                onClick = { onOpenOnlineComic(dailyPickSummary) },
                                label = { Text("Open", fontSize = 12.sp) },
                                leadingIcon = { Icon(Icons.Rounded.MenuBook, contentDescription = null, modifier = Modifier.size(14.dp)) },
                            )
                        }
                    }
                }
            }
        }
        item {
            SectionTitle("Continue reading", "Resume across merged mirrors")
        }
        if (roomLastRead.isNotEmpty() && !settings.incognitoMode) {
            item {
                RecentlyReadPanel(comics = roomLastRead.take(5))
            }
        } else if (databaseContinue.isNotEmpty()) {
            item {
                DatabaseContinuePanel(databaseContinue.take(4))
            }
        }
        if (lastRead != null) {
            item {
                LastReadPanel(
                    lastRead = lastRead,
                    onOpen = { onOpenOnlineComic(lastRead.toOnlineSummary()) },
                    onContinue = if (onOpenOnlineReader != null) {
                        { onOpenOnlineReader(lastRead.toOnlineSummary(), lastRead.toOnlineChapter()) }
                    } else null,
                )
            }
        }
        // Onboarding empty-state — shown only when user has no library or history yet
        if (roomLastRead.isEmpty() && lastRead == null && savedOnlineLibrary.isEmpty()) {
            item {
                OnboardingPanel()
            }
            items(DemoCatalog.comics.take(3)) { comic ->
                ContinueRow(comic = comic, onClick = { onOpenComic(comic) }, onRead = { onRead(comic) })
            }
        }
    }
}

@Composable
fun RecentlyReadPanel(comics: List<com.readora.app.data.db.ComicEntity>) {
    PremiumPanel {
        SectionTitle("Recently read", "Pick up where you left off")
        Spacer(Modifier.height(10.dp))
        comics.forEach { comic ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
            ) {
                // Cover thumbnail
                Box(
                    modifier = Modifier
                        .size(width = 46.dp, height = 64.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (comic.isLocal) Mint.copy(alpha = 0.14f) else Sky.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center,
                ) {
                    if (comic.coverUrl != null) {
                        RemoteImage(
                            url = comic.coverUrl,
                            contentDescription = comic.title,
                            modifier = Modifier.fillMaxSize(),
                            shape = RoundedCornerShape(10.dp),
                        )
                    } else {
                        Icon(
                            if (comic.isLocal) Icons.Rounded.MenuBook else Icons.Rounded.AutoStories,
                            contentDescription = null,
                            tint = if (comic.isLocal) Mint else Sky,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        comic.title,
                        color = Paper,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp,
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        buildString {
                            append(comic.sourceId)
                            if (comic.status.isNotBlank()) append(" · ${comic.status}")
                            if (comic.lastReadAt != null) {
                                val daysAgo = ((System.currentTimeMillis() - comic.lastReadAt) / 86_400_000L).toInt()
                                append(
                                    when {
                                        daysAgo == 0 -> " · Today"
                                        daysAgo == 1 -> " · Yesterday"
                                        daysAgo < 30 -> " · ${daysAgo}d ago"
                                        else -> ""
                                    }
                                )
                            }
                        },
                        color = Color(0xFFB8AA98),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Icon(
                    Icons.Rounded.PlayArrow,
                    contentDescription = "Continue",
                    tint = if (comic.isLocal) Mint else Sky,
                    modifier = Modifier.size(22.dp),
                )
            }
            Spacer(Modifier.height(4.dp))
        }
    }
}

@Composable
fun DatabaseContinuePanel(comics: List<com.readora.app.data.db.ComicEntity>) {
    PremiumPanel {
        SectionTitle("Database library", "Room-backed titles now survive larger library migrations")
        Spacer(Modifier.height(10.dp))
        comics.forEach { comic ->
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (comic.isLocal) Mint.copy(alpha = 0.16f) else Sky.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(if (comic.isLocal) "L" else "O", color = if (comic.isLocal) Mint else Sky, fontWeight = FontWeight.Black)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(comic.title, color = Paper, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("${comic.sourceId} • ${comic.status}", color = Color(0xFFB8AA98), fontSize = 12.sp)
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun OnlineLibraryStripCard(comic: SavedOnlineComic, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(100.dp)
            .clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
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
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.68f),
        )
        Text(
            comic.title,
            color = Paper,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 15.sp,
        )
        if (comic.lastChapterNumber.isNotBlank()) {
            Text(
                "Ch. ${comic.lastChapterNumber}",
                color = Color(0xFF9E927F),
                fontSize = 11.sp,
                maxLines = 1,
            )
        }
    }
}

@Composable
fun FeaturedCard(comic: Comic, onOpen: () -> Unit, onRead: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .clip(RoundedCornerShape(34.dp))
            .background(Brush.linearGradient(listOf(Color(comic.coverA), Color(comic.coverB))))
            .clickable(onClick = onOpen),
    ) {
        Canvas(Modifier.fillMaxSize()) {
            drawPosterTexture(Color(comic.accent))
        }
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(22.dp),
        ) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Pill("Merged x${comic.sources.size}", Color(0xCC000000))
                RatingPill(comic.rating)
            }
            Column {
                Text(comic.title, color = Color.White, fontSize = 34.sp, lineHeight = 35.sp, fontWeight = FontWeight.Black)
                Text(comic.subtitle, color = Color(0xFFEFE3D4), maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(onClick = onRead) {
                        Icon(Icons.Rounded.PlayArrow, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Read Ch. ${comic.nextChapter}")
                    }
                    TextButton(onClick = onOpen) {
                        Text("Details")
                    }
                }
            }
        }
    }
}

@Composable
fun ShelfCard(shelf: SmartShelf) {
    PremiumPanel(
        modifier = Modifier
            .width(190.dp)
            .height(132.dp),
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color(shelf.accent).copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(shelf.count.toString(), color = Color(shelf.accent), fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.height(12.dp))
        Text(shelf.title, color = Paper, fontWeight = FontWeight.Black, fontSize = 18.sp)
        Text(shelf.description, color = Color(0xFFCABBA9), fontSize = 13.sp, maxLines = 2)
    }
}

@Composable
fun ContinueRow(comic: Comic, onClick: () -> Unit, onRead: () -> Unit) {
    PremiumPanel(modifier = Modifier.clickable(onClick = onClick)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            MiniCover(comic, Modifier.size(72.dp, 96.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(comic.title, color = Paper, fontWeight = FontWeight.Black, fontSize = 19.sp)
                Text("Ch. ${comic.nextChapter} of ${comic.latestChapter} • ${comic.status.label}", color = Color(0xFFCDBFAD), fontSize = 13.sp)
                Spacer(Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { comic.progress },
                    color = Color(comic.accent),
                    trackColor = Color.White.copy(alpha = 0.1f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                )
            }
            IconButton(onClick = onRead) {
                Icon(Icons.Rounded.PlayArrow, contentDescription = "Read", tint = Color(comic.accent))
            }
        }
    }
}

@Composable
fun LastReadPanel(lastRead: LastOnlineRead, onOpen: (() -> Unit)? = null, onContinue: (() -> Unit)? = null) {
    PremiumPanel(modifier = if (onOpen != null) Modifier.clickable(onClick = onOpen) else Modifier) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Ember.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Rounded.MenuBook, contentDescription = null, tint = Ember)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Continue reading", color = Paper, fontWeight = FontWeight.Black, fontSize = 18.sp)
                Text(lastRead.comicTitle, color = Color(0xFFDCCDBB), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    "Ch. ${lastRead.chapterNumber}" + if (lastRead.chapterTitle.isNotBlank()) " — ${lastRead.chapterTitle}" else "",
                    color = Color(0xFFB8AA98),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (onContinue != null) {
                // Prominent "Continue" button jumps straight into the reader
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Ember)
                        .clickable(onClick = onContinue)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Text("Resume", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            } else if (onOpen != null) {
                Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = Ember)
            }
        }
    }
}

@Composable
private fun DailyGoalWidget(todayMinutes: Long, goalMinutes: Long) {
    val progress = (todayMinutes.toFloat() / goalMinutes.toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "goalProgress")
    val done = todayMinutes >= goalMinutes
    val accentColor = if (done) Mint else Ember
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1A1B22), RoundedCornerShape(16.dp))
            .padding(16.dp),
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(56.dp)) {
            Canvas(modifier = Modifier.size(56.dp)) {
                val stroke = 6.dp.toPx()
                val inset = stroke / 2f
                drawArc(
                    color = Color(0xFF2E2F3A),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = androidx.compose.ui.geometry.Offset(inset, inset),
                    size = androidx.compose.ui.geometry.Size(size.width - stroke, size.height - stroke),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke),
                )
                drawArc(
                    color = accentColor,
                    startAngle = -90f,
                    sweepAngle = 360f * animatedProgress,
                    useCenter = false,
                    topLeft = androidx.compose.ui.geometry.Offset(inset, inset),
                    size = androidx.compose.ui.geometry.Size(size.width - stroke, size.height - stroke),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = stroke,
                        cap = androidx.compose.ui.graphics.StrokeCap.Round,
                    ),
                )
            }
            Text(
                if (done) "✓" else "${todayMinutes}m",
                color = accentColor,
                fontWeight = FontWeight.Black,
                fontSize = if (done) 18.sp else 11.sp,
            )
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                if (done) "Daily goal reached!" else "Daily reading goal",
                color = Paper,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
            )
            Text(
                if (done) "${todayMinutes}m read today — great work!"
                else "${todayMinutes}m of ${goalMinutes}m read today",
                color = Color(0xFFB8AA98),
                fontSize = 12.sp,
            )
            if (!done) {
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    color = accentColor,
                    trackColor = Color(0xFF2E2F3A),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                )
            }
        }
    }
}

@Composable
fun OnboardingPanel() {
    PremiumPanel {
        // Welcome header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(Ember.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Rounded.AutoStories, contentDescription = null, tint = Ember, modifier = Modifier.size(26.dp))
            }
            Column {
                Text("Welcome to Readora", color = com.readora.app.ui.theme.Paper, fontWeight = androidx.compose.ui.text.font.FontWeight.Black, fontSize = 18.sp)
                Text("Your manga & comics hub", color = Color(0xFFCDBFAD), fontSize = 13.sp)
            }
        }
        Spacer(Modifier.height(16.dp))
        // 3 step cards
        val steps = listOf(
            Triple(Icons.Rounded.Explore, "Discover", "Find titles on MangaDex and other sources — search, filter by genre, browse popular & latest."),
            Triple(Icons.Rounded.CollectionsBookmark, "Build your library", "Save comics to your library. Track status (Reading, Completed, Plan to Read…) and organise with custom shelves."),
            Triple(Icons.Rounded.MenuBook, "Read & track", "Open any chapter. Your progress, streaks, and session stats are recorded automatically as you read."),
        )
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            steps.forEachIndexed { index, (icon, title, desc) ->
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.04f))
                        .padding(12.dp),
                ) {
                    // Step number circle
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(Ember.copy(alpha = 0.20f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("${index + 1}", color = Ember, fontWeight = androidx.compose.ui.text.font.FontWeight.Black, fontSize = 13.sp)
                    }
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(icon, contentDescription = null, tint = Ember, modifier = Modifier.size(16.dp))
                            Text(title, color = com.readora.app.ui.theme.Paper, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, fontSize = 14.sp)
                        }
                        Text(desc, color = Color(0xFFBBAA98), fontSize = 12.sp, lineHeight = 17.sp)
                    }
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        // Feature highlights
        Text("What's included", color = Color(0xFF9E9080), fontSize = 11.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        val features = listOf(
            Icons.Rounded.Star to "Paged, double-page & webtoon reading modes",
            Icons.Rounded.Bookmark to "Bookmarks + chapter notes",
            Icons.Rounded.CloudDownload to "Offline chapter downloads",
            Icons.Rounded.Tune to "Smart shelves (auto-categorise your library)",
            Icons.Rounded.Search to "Global multi-source search with genre filters",
        )
        Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
            features.forEach { (icon, label) ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(icon, contentDescription = null, tint = com.readora.app.ui.theme.Mint, modifier = Modifier.size(14.dp))
                    Text(label, color = Color(0xFFCDBFAD), fontSize = 12.sp)
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        // Demo note
        Text(
            "The demo comics below are offline samples so you can try the reader right away.",
            color = Color(0xFF7A7A9A),
            fontSize = 11.sp,
            lineHeight = 15.sp,
        )
    }
}

// ---------------------------------------------------------------------------
// Streak milestone banner
// ---------------------------------------------------------------------------

private data class StreakMilestone(
    val threshold: Int,
    val emoji: String,
    val label: String,
    val subtitle: String,
    val accentColor: Color,
)

private val streakMilestones = listOf(
    StreakMilestone(100, "💎", "100-Day Legend!", "A century of reading — you're extraordinary.", Color(0xFF82CFFF)),
    StreakMilestone( 50, "🏆",  "50-Day Champion!", "Half a century of daily reading. Phenomenal!", Color(0xFFFFD966)),
    StreakMilestone( 30, "🌟",  "30-Day Master!", "One full month of reading every day!", Color(0xFFFFB347)),
    StreakMilestone( 21, "⚡",  "3-Week Habit!", "21 days — reading is now part of who you are.", Color(0xFFBB86FC)),
    StreakMilestone( 14, "🔥",  "2-Week Streak!", "Two straight weeks — you're on fire!", Color(0xFFFF6E40)),
    StreakMilestone(  7, "🎯",  "Week Warrior!", "Seven days straight — incredible dedication!", Color(0xFF69F0AE)),
    StreakMilestone(  3, "✨",  "3-Day Streak!", "Three days in a row — great momentum!", Color(0xFFFFD54F)),
)

@Composable
private fun StreakMilestoneBanner(streak: Int) {
    // Find the highest milestone the streak qualifies for
    val milestone = streakMilestones.firstOrNull { streak >= it.threshold }

    val accentColor = milestone?.accentColor ?: Ember
    val emoji       = milestone?.emoji       ?: "🔥"
    val label       = milestone?.label       ?: "$streak-day streak!"
    val subtitle    = milestone?.subtitle    ?: "You've read $streak days in a row. Keep it up!"

    val isMilestone = milestone != null && (milestone.threshold == streak ||
            streakMilestones.none { it.threshold > milestone.threshold && streak >= it.threshold &&
                    it.threshold == streak })

    // Background gradient shifts based on whether it's an exact milestone day
    val bgBrush = if (milestone != null && streak == milestone.threshold) {
        Brush.horizontalGradient(
            listOf(accentColor.copy(alpha = 0.18f), Color(0xFF1A1B22), accentColor.copy(alpha = 0.10f))
        )
    } else {
        Brush.horizontalGradient(
            listOf(Color(0xFF23222E), Color(0xFF1A1B22))
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bgBrush)
            .border(
                width = if (milestone != null && streak == milestone.threshold) 1.5.dp else 0.dp,
                color = if (milestone != null && streak == milestone.threshold) accentColor.copy(alpha = 0.55f) else Color.Transparent,
                shape = RoundedCornerShape(16.dp),
            )
            .padding(horizontal = 18.dp, vertical = 14.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            // Emoji badge in a tinted circle
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(emoji, fontSize = 26.sp)
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    label,
                    color = accentColor,
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp,
                )
                Text(
                    subtitle,
                    color = Color(0xFFCDBFAD),
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                )
            }

            // Streak count pill on the right
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.18f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "$streak",
                        color = accentColor,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                    )
                    Text(
                        "days",
                        color = accentColor.copy(alpha = 0.70f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}

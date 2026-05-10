package com.readora.app.ui.stats

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.readora.app.ui.viewmodel.ReadoraViewModelFactory
import com.readora.app.ui.viewmodel.SettingsViewModel
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import java.util.Calendar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.CollectionsBookmark
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.readora.app.ReadoraApplication
import com.readora.app.storage.DownloadQueueManager
import com.readora.app.storage.OfflineCacheManager
import com.readora.app.storage.QueueStatus
import com.readora.app.storage.ReadoraPreferences
import com.readora.app.ui.theme.Coral
import com.readora.app.ui.theme.Ember
import com.readora.app.ui.theme.Mint
import com.readora.app.ui.theme.Paper
import com.readora.app.ui.theme.Sky
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun StatsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val app = context.applicationContext as ReadoraApplication
    val preferences = remember { ReadoraPreferences(context) }
    val settingsViewModel: SettingsViewModel = viewModel(factory = ReadoraViewModelFactory(context))
    val settings by settingsViewModel.uiState.collectAsState()
    val cacheManager = remember { OfflineCacheManager(context) }
    val queueManager = remember { DownloadQueueManager(context) }
    val scope = rememberCoroutineScope()

    var onlineCount by remember { mutableStateOf(0) }
    var localCount by remember { mutableStateOf(0) }
    var roomCount by remember { mutableStateOf(0) }
    var bookmarkCount by remember { mutableStateOf(0) }
    var noteCount by remember { mutableStateOf(0) }
    var sessionCount by remember { mutableStateOf(0) }
    var weekMinutes by remember { mutableStateOf(0L) }
    var allTimeMinutes by remember { mutableStateOf(0L) }
    var cacheSize by remember { mutableStateOf("") }
    var queueDone by remember { mutableStateOf(0) }
    var queueTotal by remember { mutableStateOf(0) }
    var updateCount by remember { mutableStateOf(0) }
    var unreadCount by remember { mutableStateOf(0) }
    // Per-day minutes for the last 7 days (index 0 = 6 days ago … index 6 = today)
    var weekDayMinutes by remember { mutableStateOf(LongArray(7)) }
    // Top-read comics: list of (title, totalMinutes) sorted descending, capped at 5
    var topComics by remember { mutableStateOf<List<Pair<String, Long>>>(emptyList()) }
    // 91-day heatmap: minutes per day for the last 13 weeks (index 0 = 90 days ago, 90 = today)
    var heatmapMinutes by remember { mutableStateOf(LongArray(91)) }
    // Monthly calendar: minutes per day for the current calendar month (index = dayOfMonth - 1)
    var monthCalMinutes by remember { mutableStateOf(LongArray(31)) }
    var monthCalDaysInMonth by remember { mutableStateOf(30) }
    var monthCalFirstDow by remember { mutableStateOf(0) } // 0=Sun day-of-week of 1st
    // Session insights
    var longestSessionMs by remember { mutableStateOf(0L) }
    var avgSessionMs by remember { mutableStateOf(0L) }
    var longestSessionTitle by remember { mutableStateOf("") }
    // Per-weekday average minutes: index 0 = Monday, 6 = Sunday
    var weekdayAvgMinutes by remember { mutableStateOf(LongArray(7)) }
    // Total pages read estimate: all-time minutes × 10 pages/min
    var totalPagesEst by remember { mutableStateOf(0L) }
    // Streak counters
    var currentStreak by remember { mutableStateOf(0) }
    var longestStreak by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val online = preferences.loadOnlineLibrary()
            val local = preferences.loadLocalLibrary()
            onlineCount = online.size
            localCount = local.size

            val db = app.database
            roomCount = db.comicDao().getAll().first().size
            bookmarkCount = db.bookmarkDao().getAll().first().size
            noteCount = db.chapterNoteDao().getAll().first().size

            val sessions = db.readingSessionDao().getAll().first()
            sessionCount = sessions.size
            val weekAgo = System.currentTimeMillis() - 7 * 24 * 3600 * 1000L
            weekMinutes = sessions.filter { it.startedAt >= weekAgo }.sumOf { it.durationMs } / 60_000L
            allTimeMinutes = sessions.sumOf { it.durationMs } / 60_000L
            totalPagesEst = allTimeMinutes * 10L
            // Build per-day minute buckets: index 0 = 6 days ago, 6 = today
            val dayBuckets = LongArray(7)
            val nowCal = Calendar.getInstance()
            sessions.filter { it.startedAt >= weekAgo }.forEach { session ->
                val sessionCal = Calendar.getInstance().apply { timeInMillis = session.startedAt }
                val daysDiff = ((nowCal.timeInMillis - sessionCal.timeInMillis) / (24 * 3600 * 1000L)).toInt().coerceIn(0, 6)
                dayBuckets[6 - daysDiff] += session.durationMs / 60_000L
            }
            weekDayMinutes = dayBuckets

            // Build 91-day heatmap buckets: index 0 = 90 days ago, 90 = today
            val heatBuckets = LongArray(91)
            val ninetyOneDaysAgo = System.currentTimeMillis() - 91 * 24 * 3600 * 1000L
            sessions.filter { it.startedAt >= ninetyOneDaysAgo }.forEach { session ->
                val sessionCal = Calendar.getInstance().apply { timeInMillis = session.startedAt }
                val daysDiff = ((nowCal.timeInMillis - sessionCal.timeInMillis) / (24 * 3600 * 1000L)).toInt().coerceIn(0, 90)
                heatBuckets[90 - daysDiff] += session.durationMs / 60_000L
            }
            heatmapMinutes = heatBuckets

            // Monthly calendar: current month, minutes per day
            val monthCal = Calendar.getInstance()
            val thisYear = monthCal.get(Calendar.YEAR)
            val thisMonth = monthCal.get(Calendar.MONTH)
            val daysInMonth = monthCal.getActualMaximum(Calendar.DAY_OF_MONTH)
            val firstDayCal = Calendar.getInstance().apply { set(thisYear, thisMonth, 1) }
            val firstDow = firstDayCal.get(Calendar.DAY_OF_WEEK) - 1 // 0=Sun
            val monthStart = firstDayCal.timeInMillis
            val monthEnd = monthCal.apply { set(thisYear, thisMonth, daysInMonth, 23, 59, 59) }.timeInMillis
            val monthBuckets = LongArray(31)
            sessions.filter { it.startedAt in monthStart..monthEnd }.forEach { session ->
                val sCal = Calendar.getInstance().apply { timeInMillis = session.startedAt }
                val dayIdx = sCal.get(Calendar.DAY_OF_MONTH) - 1
                if (dayIdx in 0 until 31) monthBuckets[dayIdx] += session.durationMs / 60_000L
            }
            monthCalMinutes = monthBuckets
            monthCalDaysInMonth = daysInMonth
            monthCalFirstDow = firstDow

            // Session insights: longest session + average duration
            if (sessions.isNotEmpty()) {
                val longest = sessions.maxByOrNull { it.durationMs }
                longestSessionMs = longest?.durationMs ?: 0L
                longestSessionTitle = longest?.comicTitle ?: ""
                avgSessionMs = sessions.sumOf { it.durationMs } / sessions.size
            }

            // Per-weekday average minutes (all-time): group by Calendar.DAY_OF_WEEK
            // Calendar.MONDAY=2 .. Calendar.SUNDAY=1; map to index 0=Mon..6=Sun
            if (sessions.isNotEmpty()) {
                val dayTotals = LongArray(7)     // sum of minutes per weekday
                val dayCounts = IntArray(7)      // count of days that had sessions
                // Group all sessions by their calendar date, then sum per date, then average per weekday
                val dayDateMap = mutableMapOf<Long, LongArray>() // dateKey -> [weekdayIdx, totalMs]
                sessions.forEach { session ->
                    val cal = Calendar.getInstance().apply { timeInMillis = session.startedAt }
                    val dateKey = cal.apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }.timeInMillis
                    val dowRaw = cal.get(Calendar.DAY_OF_WEEK) // 1=Sun, 2=Mon ... 7=Sat
                    val idx = if (dowRaw == Calendar.SUNDAY) 6 else dowRaw - 2  // 0=Mon..6=Sun
                    val existing = dayDateMap[dateKey]
                    if (existing == null) dayDateMap[dateKey] = longArrayOf(idx.toLong(), session.durationMs)
                    else existing[1] += session.durationMs
                }
                dayDateMap.values.forEach { entry ->
                    val idx = entry[0].toInt()
                    dayTotals[idx] += entry[1] / 60_000L
                    dayCounts[idx]++
                }
                weekdayAvgMinutes = LongArray(7) { i -> if (dayCounts[i] > 0) dayTotals[i] / dayCounts[i] else 0L }
            }

            // Top-read comics: group sessions by comicTitle, sum duration, take top 5
            topComics = sessions
                .groupBy { it.comicTitle }
                .mapValues { (_, v) -> v.sumOf { it.durationMs } / 60_000L }
                .entries
                .sortedByDescending { it.value }
                .take(5)
                .map { it.key to it.value }

            // Streak computation: find unique reading days (midnight-aligned), compute current + longest
            val readingDays: Set<Long> = sessions.map { session ->
                Calendar.getInstance().apply {
                    timeInMillis = session.startedAt
                    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                }.timeInMillis
            }.toSortedSet()

            // Current streak: count consecutive days ending today (or yesterday)
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            val dayMs = 24 * 3600 * 1000L
            var streak = 0
            var checkDay = today
            // Allow today or yesterday as starting anchor (so streak shows even if not read today yet)
            if (readingDays.contains(checkDay) || readingDays.contains(checkDay - dayMs)) {
                if (!readingDays.contains(checkDay)) checkDay -= dayMs
                while (readingDays.contains(checkDay)) {
                    streak++
                    checkDay -= dayMs
                }
            }
            currentStreak = streak

            // Longest streak: sort days, find max run
            val sortedDays = readingDays.sorted()
            var best = 0
            var run = if (sortedDays.isNotEmpty()) 1 else 0
            for (i in 1 until sortedDays.size) {
                if (sortedDays[i] - sortedDays[i - 1] == dayMs) {
                    run++
                    if (run > best) best = run
                } else {
                    if (run > best) best = run
                    run = 1
                }
            }
            if (run > best) best = run
            longestStreak = best

            val bytes = cacheManager.cacheSizeBytes()
            cacheSize = when {
                bytes >= 1_073_741_824L -> "${"%.1f".format(bytes / 1_073_741_824.0)} GB"
                bytes >= 1_048_576L -> "${"%.1f".format(bytes / 1_048_576.0)} MB"
                bytes >= 1_024L -> "${"%.0f".format(bytes / 1_024.0)} KB"
                else -> "$bytes B"
            }

            val queue = queueManager.load()
            queueTotal = queue.size
            queueDone = queue.count { it.status == QueueStatus.Done.value }

            val updates = db.updateDao().getAll().first()
            updateCount = updates.size
            unreadCount = updates.count { !it.isRead }
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = Paper)
                }
                Text(
                    "Statistics",
                    color = Paper,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // Library section
        item {
            StatSection(title = "Library", subtitle = "Your saved titles and imports") {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    BigStatCard("Online saved", onlineCount.toString(), Icons.Rounded.CollectionsBookmark, Ember, Modifier.weight(1f))
                    BigStatCard("Local imports", localCount.toString(), Icons.Rounded.Folder, Sky, Modifier.weight(1f))
                    BigStatCard("In database", roomCount.toString(), Icons.Rounded.AutoStories, Mint, Modifier.weight(1f))
                }
            }
        }

        // Reading section
        item {
            StatSection(title = "Reading", subtitle = "Time and sessions on device") {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    BigStatCard("Sessions", sessionCount.toString(), Icons.Rounded.MenuBook, Ember, Modifier.weight(1f))
                    BigStatCard("This week", "${weekMinutes}m", Icons.Rounded.Timer, Mint, Modifier.weight(1f))
                    BigStatCard("All time", "${allTimeMinutes}m", Icons.Rounded.Timer, Sky, Modifier.weight(1f))
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    BigStatCard(
                        "~Pages read",
                        if (totalPagesEst >= 1000) "${totalPagesEst / 1000}k+" else "$totalPagesEst",
                        Icons.Rounded.AutoStories,
                        Ember,
                        Modifier.weight(1f),
                    )
                    BigStatCard(
                        "Avg session",
                        if (avgSessionMs > 0) "${avgSessionMs / 60_000L}m" else "—",
                        Icons.Rounded.Timer,
                        Sky,
                        Modifier.weight(1f),
                    )
                }
                if (currentStreak > 0 || longestStreak > 0) {
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        BigStatCard(
                            "Current streak",
                            if (currentStreak > 0) "${currentStreak}d 🔥" else "—",
                            Icons.Rounded.Timer,
                            Ember,
                            Modifier.weight(1f),
                        )
                        BigStatCard(
                            "Longest streak",
                            if (longestStreak > 0) "${longestStreak}d" else "—",
                            Icons.Rounded.Timer,
                            Mint,
                            Modifier.weight(1f),
                        )
                    }
                }
            }
        }

        // Daily goal progress ring
        if (settings.dailyGoalMinutes > 0) {
            item {
                val todayMs = remember(weekDayMinutes) { weekDayMinutes.lastOrNull() ?: 0L }
                DailyGoalRing(
                    todayMinutes = todayMs,
                    goalMinutes = settings.dailyGoalMinutes.toLong(),
                )
            }
        }

        // Weekly reading chart
        item {
            WeeklyReadingChart(weekDayMinutes)
        }

        // 91-day reading heatmap
        item {
            ReadingHeatmap(heatmapMinutes)
        }

        // Monthly calendar view
        if (monthCalMinutes.any { it > 0 } || monthCalDaysInMonth > 0) {
            item {
                MonthCalendarView(
                    dayMinutes = monthCalMinutes,
                    daysInMonth = monthCalDaysInMonth,
                    firstDow = monthCalFirstDow,
                )
            }
        }

        // Per-weekday average reading bar chart
        if (weekdayAvgMinutes.any { it > 0 }) {
            item {
                WeekdayBarChart(weekdayAvgMinutes)
            }
        }

        // Top-read comics leaderboard
        if (topComics.isNotEmpty()) {
            item {
                StatSection(title = "Top read comics", subtitle = "Most time spent, all time") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        val maxMin = topComics.firstOrNull()?.second?.coerceAtLeast(1L) ?: 1L
                        topComics.forEachIndexed { idx, (title, minutes) ->
                            val fraction = minutes.toFloat() / maxMin.toFloat()
                            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Text(
                                        "#${idx + 1}",
                                        color = if (idx == 0) Ember else Color(0xFF7A7A9A),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp,
                                        modifier = Modifier.width(26.dp),
                                    )
                                    Text(
                                        title,
                                        color = Paper,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f),
                                    )
                                    Text(
                                        "${minutes}m",
                                        color = Mint,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(Color.White.copy(alpha = 0.07f)),
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(fraction)
                                            .fillMaxHeight()
                                            .background(
                                                if (idx == 0) Ember else Ember.copy(alpha = 0.4f + (4 - idx) * 0.1f),
                                                RoundedCornerShape(2.dp),
                                            ),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Session insights: longest session + reading speed estimate
        if (sessionCount > 0) {
            item {
                StatSection(title = "Session insights", subtitle = "Pace and personal records") {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Longest session highlight
                        val longestMin = longestSessionMs / 60_000L
                        val longestSec = (longestSessionMs % 60_000L) / 1_000L
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Ember.copy(alpha = 0.12f))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Icon(Icons.Rounded.Timer, contentDescription = null, tint = Ember, modifier = Modifier.size(28.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Longest session", color = Ember, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Text(
                                    if (longestMin > 0) "${longestMin}m ${longestSec}s" else "${longestSec}s",
                                    color = Paper,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 20.sp,
                                )
                                if (longestSessionTitle.isNotBlank()) {
                                    Text(
                                        longestSessionTitle,
                                        color = Color(0xFFCDBFAD),
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }
                        }
                        // Average session duration + estimated pages per session
                        val avgMin = avgSessionMs / 60_000L
                        val avgSec = (avgSessionMs % 60_000L) / 1_000L
                        // Estimate: ~10 pages/min midpoint (manga ~8, webtoon ~12)
                        val estPagesPerSession = (avgSessionMs / 60_000L) * 10L +
                            (avgSessionMs % 60_000L) / 1_000L * 10L / 60L
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            // Avg session
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White.copy(alpha = 0.05f))
                                    .padding(12.dp),
                            ) {
                                Text("Avg session", color = Color(0xFF9E9080), fontSize = 11.sp)
                                Text(
                                    if (avgMin > 0) "${avgMin}m ${avgSec}s" else "${avgSec}s",
                                    color = Sky,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                )
                            }
                            // Estimated pages per session
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White.copy(alpha = 0.05f))
                                    .padding(12.dp),
                            ) {
                                Text("Est. pages/session", color = Color(0xFF9E9080), fontSize = 11.sp)
                                Text(
                                    "~$estPagesPerSession",
                                    color = Mint,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                )
                                Text("at ~10 pg/min", color = Color(0xFF7A7A9A), fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }

        // Notes & bookmarks
        item {
            StatSection(title = "Notes & Bookmarks", subtitle = "Saved places and thoughts") {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    BigStatCard("Bookmarks", bookmarkCount.toString(), Icons.Rounded.Bookmark, Ember, Modifier.weight(1f))
                    BigStatCard("Notes", noteCount.toString(), Icons.Rounded.Edit, Mint, Modifier.weight(1f))
                }
            }
        }

        // Downloads & cache
        item {
            StatSection(title = "Downloads & Cache", subtitle = "Offline storage on this device") {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    BigStatCard("Downloaded", "$queueDone / $queueTotal", Icons.Rounded.CloudDownload, Ember, Modifier.weight(1f))
                    BigStatCard("Cache size", cacheSize.ifBlank { "0 B" }, Icons.Rounded.Folder, Sky, Modifier.weight(1f))
                }
            }
        }

        // Updates
        item {
            StatSection(title = "Updates", subtitle = "Chapter update feed") {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    BigStatCard("Total updates", updateCount.toString(), Icons.Rounded.Refresh, Ember, Modifier.weight(1f))
                    BigStatCard("Unread", unreadCount.toString(), Icons.Rounded.AutoStories, if (unreadCount > 0) Coral else Mint, Modifier.weight(1f))
                }
            }
        }

        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun StatSection(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF0F1220))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column {
            Text(title, color = Paper, fontWeight = FontWeight.Black, fontSize = 16.sp)
            Text(subtitle, color = Color(0xFF7A7A9A), fontSize = 12.sp)
        }
        content()
    }
}

@Composable
private fun BigStatCard(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.10f))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
        Text(value, color = Paper, fontWeight = FontWeight.Black, fontSize = 20.sp)
        Text(label, color = color.copy(alpha = 0.8f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun WeeklyReadingChart(dayMinutes: LongArray) {
    val dayLabels = remember {
        val cal = Calendar.getInstance()
        Array(7) { i ->
            cal.timeInMillis = System.currentTimeMillis() - (6 - i) * 24 * 3600 * 1000L
            val dow = cal.get(Calendar.DAY_OF_WEEK)
            arrayOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")[dow - 1]
        }
    }
    val maxMinutes = dayMinutes.maxOrNull()?.coerceAtLeast(1L) ?: 1L
    val barColor = Ember
    val emptyColor = Color(0xFF1E1E30)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF0F1220))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Column {
            Text("Weekly Reading", color = Paper, fontWeight = FontWeight.Black, fontSize = 16.sp)
            Text("Minutes read per day (last 7 days)", color = Color(0xFF7A7A9A), fontSize = 12.sp)
        }
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp),
        ) {
            val barCount = 7
            val gap = 6.dp.toPx()
            val barWidth = (size.width - gap * (barCount - 1)) / barCount
            val maxH = size.height - 4.dp.toPx()  // leave small bottom margin
            for (i in 0 until barCount) {
                val fraction = dayMinutes[i].toFloat() / maxMinutes.toFloat()
                val barH = (fraction * maxH).coerceAtLeast(4.dp.toPx())
                val x = i * (barWidth + gap)
                val y = size.height - barH
                val color = if (dayMinutes[i] > 0L) barColor else emptyColor
                drawRoundRect(
                    color = color,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barH),
                    cornerRadius = CornerRadius(6.dp.toPx()),
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth(),
        ) {
            dayLabels.forEachIndexed { i, label ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(label, color = Color(0xFF7A7A9A), fontSize = 10.sp)
                    if (dayMinutes[i] > 0L) {
                        Text("${dayMinutes[i]}m", color = Ember, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReadingHeatmap(dayMinutes: LongArray) {
    // dayMinutes has 91 entries: index 0 = 90 days ago, 90 = today
    val maxMinutes = dayMinutes.maxOrNull()?.coerceAtLeast(1L) ?: 1L
    val cellSize = 13.dp
    val cellGap = 3.dp
    val dayLabels = listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")
    // Determine the day-of-week of today so we can align the grid correctly
    val todayDow = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1 // 0=Sun … 6=Sat
    // Index in the 91-day array for the start cell (top-left of grid)
    // The heatmap grid shows 13 cols × 7 rows = 91 cells,
    // arranged so row = day-of-week, column = week index (0=oldest).
    // Cell (col, row) corresponds to day index = col*7 + row
    // We rotate so that today lands in column 12, row = todayDow.
    StatSection(title = "Activity heatmap", subtitle = "Reading days — last 13 weeks") {
        Column(verticalArrangement = Arrangement.spacedBy(cellGap)) {
            Row(horizontalArrangement = Arrangement.spacedBy(cellGap)) {
                // Day-of-week label column
                Column(
                    verticalArrangement = Arrangement.spacedBy(cellGap),
                    modifier = Modifier.width(18.dp),
                ) {
                    dayLabels.forEach { label ->
                        Box(modifier = Modifier.size(cellSize), contentAlignment = Alignment.Center) {
                            Text(label, color = Color(0xFF7A7A9A), fontSize = 8.sp)
                        }
                    }
                }
                // 13 week columns
                for (col in 0 until 13) {
                    Column(verticalArrangement = Arrangement.spacedBy(cellGap)) {
                        for (row in 0 until 7) {
                            val dayIndex = 90 - (if (col == 12) (row - todayDow + 7) % 7 else
                                (12 - col) * 7 + (7 - todayDow + row) % 7)
                            val safeIdx = dayIndex.coerceIn(0, 90)
                            val isFuture = col == 12 && row > todayDow
                            val mins = if (isFuture) 0L else dayMinutes[safeIdx]
                            val intensity = if (isFuture || maxMinutes == 0L) 0f else (mins.toFloat() / maxMinutes).coerceIn(0f, 1f)
                            val cellColor = when {
                                isFuture -> Color(0xFF1A1B22)
                                intensity == 0f -> Color(0xFF1E2030)
                                intensity < 0.25f -> Ember.copy(alpha = 0.25f)
                                intensity < 0.5f -> Ember.copy(alpha = 0.5f)
                                intensity < 0.75f -> Ember.copy(alpha = 0.75f)
                                else -> Ember
                            }
                            Box(
                                modifier = Modifier
                                    .size(cellSize)
                                    .background(cellColor, RoundedCornerShape(3.dp)),
                            )
                        }
                    }
                }
            }
            // Legend
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 20.dp, top = 4.dp),
            ) {
                Text("Less", color = Color(0xFF7A7A9A), fontSize = 9.sp)
                listOf(0f, 0.25f, 0.5f, 0.75f, 1f).forEach { level ->
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(
                                if (level == 0f) Color(0xFF1E2030) else Ember.copy(alpha = level),
                                RoundedCornerShape(2.dp),
                            ),
                    )
                }
                Text("More", color = Color(0xFF7A7A9A), fontSize = 9.sp)
            }
        }
    }
}

@Composable
fun WeekdayBarChart(weekdayAvgMinutes: LongArray) {
    val dayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val maxVal = weekdayAvgMinutes.maxOrNull()?.coerceAtLeast(1L) ?: 1L
    StatSection(title = "Best reading days", subtitle = "Average minutes per weekday, all time") {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
        ) {
            weekdayAvgMinutes.forEachIndexed { idx, minutes ->
                val fraction = minutes.toFloat() / maxVal.toFloat()
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                ) {
                    if (minutes > 0L) {
                        Text(
                            "${minutes}m",
                            color = Color(0xFF9E9080),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    Spacer(Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(fraction.coerceAtLeast(0.04f))
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(
                                if (fraction >= 0.9f) Ember
                                else Ember.copy(alpha = 0.35f + fraction * 0.45f)
                            ),
                    )
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            dayLabels.forEach { label ->
                Text(
                    label,
                    color = Color(0xFF7A7A9A),
                    fontSize = 9.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Daily goal progress ring
// ---------------------------------------------------------------------------

@Composable
private fun DailyGoalRing(todayMinutes: Long, goalMinutes: Long) {
    val fraction = if (goalMinutes > 0) (todayMinutes.toFloat() / goalMinutes.toFloat()).coerceIn(0f, 1f) else 0f
    val exceeded = todayMinutes >= goalMinutes
    val ringColor = if (exceeded) Mint else Ember
    val animFraction by animateFloatAsState(targetValue = fraction, animationSpec = tween(900), label = "goalRing")

    StatSection(
        title = "Today's reading goal",
        subtitle = "Daily target set in Settings",
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(22.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            // Arc ring
            Box(
                modifier = Modifier.size(110.dp),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.size(110.dp)) {
                    val stroke = 12f
                    val inset = stroke / 2f
                    val arcSize = Size(size.width - stroke, size.height - stroke)
                    val topLeft = Offset(inset, inset)
                    // Track
                    drawArc(
                        color = Color.White.copy(alpha = 0.07f),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = stroke,
                            cap = androidx.compose.ui.graphics.StrokeCap.Round,
                        ),
                        topLeft = topLeft,
                        size = arcSize,
                    )
                    // Progress arc
                    if (animFraction > 0f) {
                        drawArc(
                            color = ringColor,
                            startAngle = -90f,
                            sweepAngle = 360f * animFraction,
                            useCenter = false,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(
                                width = stroke,
                                cap = androidx.compose.ui.graphics.StrokeCap.Round,
                            ),
                            topLeft = topLeft,
                            size = arcSize,
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        if (exceeded) "✓" else "${(fraction * 100).toInt()}%",
                        color = ringColor,
                        fontWeight = FontWeight.Black,
                        fontSize = if (exceeded) 28.sp else 22.sp,
                    )
                    if (!exceeded) {
                        Text("done", color = Color(0xFF9E9080), fontSize = 10.sp)
                    }
                }
            }
            // Right-side detail
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        "$todayMinutes",
                        color = ringColor,
                        fontWeight = FontWeight.Black,
                        fontSize = 32.sp,
                    )
                    Text(
                        "min read",
                        color = Color(0xFF9E9080),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(bottom = 5.dp),
                    )
                }
                Text(
                    "Goal: $goalMinutes min / day",
                    color = Color(0xFFCDBFAD),
                    fontSize = 13.sp,
                )
                if (exceeded) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        modifier = Modifier
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                            .background(Mint.copy(alpha = 0.14f))
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                    ) {
                        Text("🎉", fontSize = 14.sp)
                        Text(
                            "Goal crushed! +${todayMinutes - goalMinutes}m",
                            color = Mint,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                        )
                    }
                } else {
                    val remaining = goalMinutes - todayMinutes
                    Text(
                        "$remaining min remaining",
                        color = Color(0xFF7A7A9A),
                        fontSize = 12.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthCalendarView(dayMinutes: LongArray, daysInMonth: Int, firstDow: Int) {
    val today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    val maxMinutes = dayMinutes.take(daysInMonth).maxOrNull()?.coerceAtLeast(1L) ?: 1L
    val monthName = java.text.SimpleDateFormat("MMMM yyyy", java.util.Locale.getDefault()).format(java.util.Date())
    val dayHeaders = listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")
    StatSection(title = "This month", subtitle = monthName) {
        // Day-of-week headers
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            dayHeaders.forEach { d ->
                Text(d, color = Color(0xFF7A7A9A), fontSize = 10.sp, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        }
        Spacer(Modifier.height(4.dp))
        // Calendar grid: fill first row blank cells, then day 1..daysInMonth
        val totalCells = firstDow + daysInMonth
        val rows = (totalCells + 6) / 7
        for (row in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                for (col in 0 until 7) {
                    val cellIdx = row * 7 + col
                    val dayNum = cellIdx - firstDow + 1
                    val isValid = dayNum in 1..daysInMonth
                    val mins = if (isValid) dayMinutes[dayNum - 1] else 0L
                    val intensity = if (!isValid || maxMinutes == 0L) 0f else (mins.toFloat() / maxMinutes).coerceIn(0f, 1f)
                    val isToday = isValid && dayNum == today
                    val cellColor = when {
                        !isValid        -> Color.Transparent
                        isToday         -> Sky.copy(alpha = 0.85f)
                        intensity == 0f -> Color(0xFF1E2030)
                        intensity < 0.33f -> Ember.copy(alpha = 0.3f)
                        intensity < 0.66f -> Ember.copy(alpha = 0.6f)
                        else            -> Ember
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp)
                            .background(cellColor, RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (isValid) {
                            Text(
                                dayNum.toString(),
                                fontSize = 9.sp,
                                color = if (isToday) Color.White else if (mins > 0) Paper else Color(0xFF5A5070),
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                            )
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        // Legend row
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(10.dp).background(Color(0xFF1E2030), RoundedCornerShape(2.dp)))
            Text("No reading", color = Color(0xFF7A7A9A), fontSize = 9.sp)
            Spacer(Modifier.width(4.dp))
            Box(Modifier.size(10.dp).background(Ember.copy(alpha = 0.3f), RoundedCornerShape(2.dp)))
            Box(Modifier.size(10.dp).background(Ember.copy(alpha = 0.6f), RoundedCornerShape(2.dp)))
            Box(Modifier.size(10.dp).background(Ember, RoundedCornerShape(2.dp)))
            Text("Low → High", color = Color(0xFF7A7A9A), fontSize = 9.sp)
            Spacer(Modifier.width(4.dp))
            Box(Modifier.size(10.dp).background(Sky.copy(alpha = 0.85f), RoundedCornerShape(2.dp)))
            Text("Today", color = Color(0xFF7A7A9A), fontSize = 9.sp)
        }
    }
}

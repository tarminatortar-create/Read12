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
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.OpenInBrowser
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
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
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
fun Header(eyebrow: String, title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(eyebrow.uppercase(), color = Ember, fontSize = 12.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
        Text(title, color = Paper, fontSize = 32.sp, lineHeight = 34.sp, fontWeight = FontWeight.Black)
        Text(subtitle, color = Color(0xFFCDBFAD), lineHeight = 21.sp)
    }
}

@Composable
fun SectionTitle(title: String, subtitle: String) {
    Column {
        Text(title, color = Paper, fontSize = 21.sp, fontWeight = FontWeight.Black)
        Text(subtitle, color = Color(0xFFB8AA98), fontSize = 13.sp)
    }
}

@Composable
fun PremiumPanel(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xCC141927))
            .border(1.dp, Color.White.copy(alpha = 0.07f), RoundedCornerShape(28.dp))
            .padding(16.dp),
        content = content,
    )
}

fun formatBytes(bytes: Long): String {
    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    val gb = mb / 1024.0
    return when {
        gb >= 1.0 -> "%.2f GB cached".format(gb)
        mb >= 1.0 -> "%.1f MB cached".format(mb)
        kb >= 1.0 -> "%.0f KB cached".format(kb)
        else -> "No chapters cached yet"
    }
}











data class DatabaseStats(
    val comics: Int,
    val sources: Int,
    val mergeGroups: Int,
    val downloadJobs: Int,
)



@Composable
fun DiagnosticsPanel(logs: List<String>, onRefresh: () -> Unit, onClear: () -> Unit) {
    PremiumPanel {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Coral.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Rounded.Tune, contentDescription = null, tint = Coral)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Diagnostics", color = Paper, fontWeight = FontWeight.Black, fontSize = 19.sp)
                Text("Recent internal events", color = Color(0xFFB8AA98), fontSize = 12.sp)
            }
            TextButton(onClick = onRefresh) {
                Text("Refresh")
            }
            TextButton(onClick = onClear) {
                Text("Clear")
            }
        }
        Spacer(Modifier.height(12.dp))
        if (logs.isEmpty()) {
            Text("No diagnostic events yet.", color = Color(0xFFDCCDBB), lineHeight = 20.sp)
        } else {
            logs.forEach { line ->
                Text(
                    line,
                    color = Color(0xFFDCCDBB),
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = 6.dp),
                )
            }
        }
    }
}





















@Composable
fun SearchPanel(query: String, onQueryChange: (String) -> Unit) {
    PremiumPanel {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(Icons.Rounded.Search, contentDescription = null, tint = Ember)
            Text(query, color = Paper, modifier = Modifier.weight(1f))
            Icon(Icons.Rounded.Tune, contentDescription = null, tint = Color(0xFFCDBFAD))
        }
        Spacer(Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
            listOf("Official", "Manhwa", "RTL manga", "Completed", "High quality", "Offline").forEach {
                FilterChip(selected = it == "High quality", onClick = { onQueryChange(it) }, label = { Text(it) })
            }
        }
    }
}



@Composable
fun SourceRow(name: String, quality: Int, chapters: Int, language: String, speed: String, primary: Boolean) {
    PremiumPanel {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Icon(
                imageVector = if (primary) Icons.Rounded.CheckCircle else Icons.Rounded.CloudDownload,
                contentDescription = null,
                tint = if (primary) Mint else Sky,
                modifier = Modifier.size(30.dp),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(name, color = Paper, fontWeight = FontWeight.Black)
                Text("$chapters chapters • $language • $speed", color = Color(0xFFCDBFAD), fontSize = 13.sp)
            }
            Text("$quality%", color = if (primary) Mint else Color(0xFFDCCDBB), fontWeight = FontWeight.Black)
        }
    }
}

@Composable
fun ChapterRow(number: String, title: String, pages: Int, readProgress: Float, downloaded: Boolean) {
    PremiumPanel {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.07f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(number, color = Paper, fontWeight = FontWeight.Black)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Paper, fontWeight = FontWeight.SemiBold)
                Text("$pages pages", color = Color(0xFFCDBFAD), fontSize = 13.sp)
                if (readProgress > 0f && readProgress < 1f) {
                    LinearProgressIndicator(
                        progress = { readProgress },
                        color = Ember,
                        trackColor = Color.White.copy(alpha = 0.08f),
                        modifier = Modifier
                            .padding(top = 7.dp)
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(CircleShape),
                    )
                }
            }
            Icon(
                if (downloaded) Icons.Rounded.CloudDownload else Icons.Rounded.Bookmark,
                contentDescription = null,
                tint = if (downloaded) Mint else Color(0xFF8F826F),
            )
        }
    }
}

@Composable
fun ReaderToolbar(
    title: String,
    mode: ReadingMode,
    direction: Direction,
    onBack: () -> Unit,
    onModeChange: (ReadingMode) -> Unit,
    onDirectionChange: (Direction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xF207080D))
            .padding(horizontal = 8.dp, vertical = 8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = Paper)
            }
            Text(title, color = Paper, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f), maxLines = 1)
            Icon(Icons.Rounded.Favorite, contentDescription = null, tint = Coral)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
            AssistChip(
                onClick = { onModeChange(if (mode == ReadingMode.Webtoon) ReadingMode.Paged else ReadingMode.Webtoon) },
                label = { Text(if (mode == ReadingMode.Webtoon) "Webtoon scroll" else "Paged mode") },
                leadingIcon = { Icon(Icons.Rounded.AutoStories, contentDescription = null, modifier = Modifier.size(18.dp)) },
            )
            AssistChip(
                onClick = { onDirectionChange(if (direction == Direction.RightToLeft) Direction.LeftToRight else Direction.RightToLeft) },
                label = { Text(if (direction == Direction.RightToLeft) "RTL" else "LTR") },
            )
            AssistChip(onClick = {}, label = { Text("Fit width") })
            AssistChip(onClick = {}, label = { Text("OLED dim") })
        }
    }
}

@Composable
fun ReaderFooter(
    progress: Float,
    currentPage: Int = 0,
    totalPages: Int = 0,
    chapterLabel: String = "",
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xF207080D))
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        LinearProgressIndicator(
            progress = { progress },
            color = Ember,
            trackColor = Color.White.copy(alpha = 0.08f),
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(CircleShape),
        )
        Spacer(Modifier.height(7.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                chapterLabel.ifBlank { "Reading" },
                color = Color(0xFFCDBFAD),
                fontSize = 12.sp,
                maxLines = 1,
            )
            if (totalPages > 0) {
                Text(
                    "$currentPage / $totalPages",
                    color = Ember,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                )
            } else {
                Text(
                    "${(progress * 100).toInt()}%",
                    color = Color(0xFFCDBFAD),
                    fontSize = 12.sp,
                )
            }
        }
    }
}

@Composable
fun ReaderPage(comic: Comic, index: Int, tall: Boolean) {
    val height = if (tall) 620.dp else 500.dp
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.verticalGradient(listOf(Color(comic.coverA), Color(0xFF10131E), Color(comic.coverB).copy(alpha = 0.7f))))
            .border(1.dp, Color.White.copy(alpha = 0.07f), RoundedCornerShape(20.dp)),
    ) {
        Canvas(Modifier.fillMaxSize()) {
            drawComicPanels(index, Color(comic.accent))
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp),
        ) {
            Text("Page $index", color = Color.White.copy(alpha = 0.72f), fontWeight = FontWeight.Black)
            Text(
                when (index % 4) {
                    0 -> "Silence before the panel break."
                    1 -> "A wide cinematic beat."
                    2 -> "The merge keeps the best scan."
                    else -> "Tap zones will land here next."
                },
                color = Color.White,
                fontSize = 24.sp,
                lineHeight = 26.sp,
                fontWeight = FontWeight.Black,
            )
        }
    }
}

@Composable
fun SettingsToggle(title: String, description: String, checked: Boolean, onChecked: (Boolean) -> Unit) {
    PremiumPanel {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Paper, fontWeight = FontWeight.Black, fontSize = 18.sp)
                Text(description, color = Color(0xFFCDBFAD), fontSize = 13.sp)
            }
            Switch(checked = checked, onCheckedChange = onChecked)
        }
    }
}





@Composable
fun MiniCover(comic: Comic, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(Brush.linearGradient(listOf(Color(comic.coverA), Color(comic.coverB))))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(22.dp)),
    ) {
        Canvas(Modifier.fillMaxSize()) {
            drawPosterTexture(Color(comic.accent))
        }
        Text(
            comic.title.split(" ").take(2).joinToString("\n"),
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            lineHeight = 18.sp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp),
        )
    }
}

@Composable
fun Pill(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(color)
            .padding(horizontal = 10.dp, vertical = 6.dp),
    ) {
        Text(text, color = Paper, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun RatingPill(rating: Float) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .clip(CircleShape)
            .background(Color(0xCC000000))
            .padding(horizontal = 10.dp, vertical = 6.dp),
    ) {
        Icon(Icons.Rounded.Star, contentDescription = null, tint = Ember, modifier = Modifier.size(15.dp))
        Text(rating.toString(), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black)
    }
}

fun DrawScope.drawAtmosphere() {
    drawCircle(Color(0x22F9B17A), radius = size.width * 0.45f, center = Offset(size.width * 0.2f, size.height * 0.05f))
    drawCircle(Color(0x167AD7F9), radius = size.width * 0.6f, center = Offset(size.width * 0.9f, size.height * 0.3f))
    drawCircle(Color(0x12A7F97A), radius = size.width * 0.35f, center = Offset(size.width * 0.15f, size.height * 0.85f))
}

fun DrawScope.drawPosterTexture(accent: Color) {
    drawCircle(accent.copy(alpha = 0.28f), radius = size.minDimension * 0.65f, center = Offset(size.width * 0.86f, size.height * 0.18f))
    drawCircle(Color.White.copy(alpha = 0.12f), radius = size.minDimension * 0.26f, center = Offset(size.width * 0.18f, size.height * 0.2f))
    drawRect(Color.Black.copy(alpha = 0.24f), topLeft = Offset(0f, size.height * 0.62f), size = Size(size.width, size.height * 0.38f))
    val slash = Path().apply {
        moveTo(size.width * 0.58f, 0f)
        lineTo(size.width, 0f)
        lineTo(size.width * 0.42f, size.height)
        lineTo(size.width * 0.12f, size.height)
        close()
    }
    drawPath(slash, Color.White.copy(alpha = 0.09f))
}

fun DrawScope.drawComicPanels(index: Int, accent: Color) {
    val gap = size.width * 0.035f
    val panel = Color.White.copy(alpha = 0.12f)
    val ink = Color.Black.copy(alpha = 0.22f)
    drawRoundRect(panel, topLeft = Offset(gap, gap), size = Size(size.width * 0.58f, size.height * 0.22f))
    drawRoundRect(ink, topLeft = Offset(size.width * 0.64f, gap), size = Size(size.width * 0.32f, size.height * 0.22f))
    drawRoundRect(accent.copy(alpha = 0.30f), topLeft = Offset(gap, size.height * 0.29f), size = Size(size.width * 0.92f, size.height * 0.25f))
    drawRoundRect(panel, topLeft = Offset(gap, size.height * 0.60f), size = Size(size.width * 0.42f, size.height * 0.20f))
    drawRoundRect(ink, topLeft = Offset(size.width * 0.50f, size.height * 0.60f), size = Size(size.width * 0.45f, size.height * 0.20f))
    drawCircle(accent.copy(alpha = 0.20f + (index % 3) * 0.06f), radius = size.width * 0.18f, center = Offset(size.width * 0.72f, size.height * 0.42f))
}












@Composable
fun OnlineChapterRow(
    chapter: OnlineChapter,
    progress: SavedReadingProgress?,
    cached: Boolean,
    downloading: Boolean,
    queued: Boolean,
    onClick: () -> Unit,
    onDownload: () -> Unit,
    onQueue: () -> Unit,
    onToggleRead: (() -> Unit)? = null,
    onShare: (() -> Unit)? = null,
    onOpenInBrowser: (() -> Unit)? = null,
    onCopyUrl: (() -> Unit)? = null,
    downloadProgress: Float? = null,  // 0f–1f while downloading, null otherwise
    noteSnippet: String? = null,      // inline note preview from chapter notes
) {
    val readFraction = progress?.fraction?.coerceIn(0f, 1f) ?: 0f
    val isCompleted = readFraction >= 0.97f
    PremiumPanel(modifier = Modifier.clickable(onClick = onClick)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            // Chapter number box with optional progress ring overlay
            Box(
                modifier = Modifier.size(52.dp),
                contentAlignment = Alignment.Center,
            ) {
                // Background fill
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            when {
                                isCompleted -> Mint.copy(alpha = 0.18f)
                                readFraction > 0f -> Sky.copy(alpha = 0.10f)
                                else -> Sky.copy(alpha = 0.13f)
                            }
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    if (isCompleted) {
                        Icon(
                            Icons.Rounded.CheckCircle,
                            contentDescription = "Completed",
                            tint = Mint,
                            modifier = Modifier.size(22.dp),
                        )
                    } else {
                        Text(
                            chapter.number,
                            color = if (readFraction > 0f) Sky else Sky.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Black,
                            maxLines = 1,
                        )
                    }
                }
                // Progress arc ring drawn over the box
                if (readFraction > 0f && !isCompleted) {
                    Canvas(modifier = Modifier.size(52.dp)) {
                        val strokeWidth = 3.5f
                        val inset = strokeWidth / 2f
                        drawArc(
                            color = Sky.copy(alpha = 0.18f),
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                            topLeft = androidx.compose.ui.geometry.Offset(inset, inset),
                            size = androidx.compose.ui.geometry.Size(size.width - strokeWidth, size.height - strokeWidth),
                        )
                        drawArc(
                            color = Sky,
                            startAngle = -90f,
                            sweepAngle = 360f * readFraction,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                            topLeft = androidx.compose.ui.geometry.Offset(inset, inset),
                            size = androidx.compose.ui.geometry.Size(size.width - strokeWidth, size.height - strokeWidth),
                        )
                    }
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(chapter.title, color = Paper, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                // Scanlator badge + language tag row
                if (!chapter.scanlator.isNullOrBlank() || !chapter.language.isNullOrBlank()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 3.dp),
                    ) {
                        if (!chapter.language.isNullOrBlank()) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Sky.copy(alpha = 0.18f))
                                    .padding(horizontal = 5.dp, vertical = 1.dp),
                            ) {
                                Text(
                                    chapter.language!!.uppercase(),
                                    color = Sky,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp,
                                )
                            }
                        }
                        if (!chapter.scanlator.isNullOrBlank()) {
                            Text(
                                chapter.scanlator!!,
                                color = Color(0xFF9E9080),
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
                Text(
                    if (progress == null) {
                        "${chapter.pages} pages • ${chapter.readableAt}"
                    } else {
                        "Page ${progress.currentPage}/${progress.totalPages} • ${chapter.pages} pages"
                    },
                    color = Color(0xFFCDBFAD),
                    fontSize = 13.sp,
                )
                if (progress != null) {
                    LinearProgressIndicator(
                        progress = { progress.fraction.coerceIn(0f, 1f) },
                        color = Sky,
                        trackColor = Color.White.copy(alpha = 0.08f),
                        modifier = Modifier
                            .padding(top = 7.dp)
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(CircleShape),
                    )
                }
                // Download progress bar (shown while downloading)
                if (downloading && downloadProgress != null) {
                    LinearProgressIndicator(
                        progress = { downloadProgress.coerceIn(0f, 1f) },
                        color = Ember,
                        trackColor = Ember.copy(alpha = 0.15f),
                        modifier = Modifier
                            .padding(top = 5.dp)
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(CircleShape),
                    )
                    Text(
                        "${(downloadProgress * 100).toInt()}%",
                        color = Ember,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                } else if (downloading) {
                    LinearProgressIndicator(
                        color = Ember,
                        trackColor = Ember.copy(alpha = 0.15f),
                        modifier = Modifier
                            .padding(top = 5.dp)
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(CircleShape),
                    )
                }
                // Inline note preview
                if (!noteSnippet.isNullOrBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .fillMaxWidth()
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(6.dp))
                            .background(Color(0xFFFFB347).copy(alpha = 0.10f))
                            .padding(horizontal = 7.dp, vertical = 4.dp),
                    ) {
                        Icon(
                            Icons.Rounded.Edit,
                            contentDescription = null,
                            tint = Color(0xFFFFB347),
                            modifier = Modifier.size(11.dp),
                        )
                        Text(
                            noteSnippet,
                            color = Color(0xFFFFB347),
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Read/unread toggle
                if (onToggleRead != null) {
                    IconButton(onClick = onToggleRead, modifier = Modifier.size(32.dp)) {
                        Icon(
                            if (isCompleted) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                            contentDescription = if (isCompleted) "Mark unread" else "Mark read",
                            tint = if (isCompleted) Mint else Color(0xFFB8AA98),
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
                // Share chapter link
                if (onShare != null) {
                    IconButton(onClick = onShare, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Rounded.IosShare,
                            contentDescription = "Share chapter link",
                            tint = Color(0xFFB8AA98),
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
                // Open in browser
                if (onOpenInBrowser != null) {
                    IconButton(onClick = onOpenInBrowser, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Rounded.OpenInBrowser,
                            contentDescription = "Open chapter in browser",
                            tint = Color(0xFFB8AA98),
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
                // Copy URL
                if (onCopyUrl != null) {
                    IconButton(onClick = onCopyUrl, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Rounded.ContentCopy,
                            contentDescription = "Copy chapter URL",
                            tint = Color(0xFFB8AA98),
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
                Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = if (progress == null) Sky else Mint)
                when {
                    cached -> Text("Offline", color = Mint, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    downloading -> CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Ember, strokeWidth = 2.dp)
                    else -> {
                        IconButton(onClick = onDownload, modifier = Modifier.size(34.dp)) {
                            Icon(Icons.Rounded.CloudDownload, contentDescription = "Save chapter offline", tint = Color(0xFFB8AA98), modifier = Modifier.size(19.dp))
                        }
                        TextButton(onClick = onQueue, enabled = !queued) {
                            Text(if (queued) "Queued" else "Queue", fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun OnlineCover(comic: OnlineComicSummary, modifier: Modifier = Modifier) {
    if (comic.coverUrl != null) {
        RemoteImage(
            url = comic.coverUrl,
            contentDescription = comic.title,
            modifier = modifier,
            shape = RoundedCornerShape(22.dp),
            placeholderText = comic.title.split(" ").take(2).joinToString("\n"),
        )
    } else {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(22.dp))
                .background(Brush.linearGradient(listOf(Color(0xFF1D2638), Color(0xFF7AD7F9))))
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(22.dp)),
            contentAlignment = Alignment.BottomStart,
        ) {
            Text(comic.title.take(18), color = Color.White, fontWeight = FontWeight.Black, modifier = Modifier.padding(12.dp))
        }
    }
}

@Composable
fun RemoteImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier.fillMaxWidth(),
    shape: RoundedCornerShape = RoundedCornerShape(18.dp),
    placeholderText: String = "Loading",
) {
    var bitmap by remember(url) { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }
    var failed by remember(url) { mutableStateOf(false) }

    LaunchedEffect(url) {
        failed = false
        bitmap = withContext(Dispatchers.IO) {
            runCatching {
                if (url.startsWith("file:")) {
                    URL(url).openStream().use { stream -> BitmapFactory.decodeStream(stream)?.asImageBitmap() }
                } else {
                    URL(url).openStream().use { stream -> BitmapFactory.decodeStream(stream)?.asImageBitmap() }
                }
            }.getOrNull()
        }
        failed = bitmap == null
    }

    val image = bitmap
    if (image != null) {
        val ratio = (image.width.toFloat() / image.height.toFloat()).coerceIn(0.2f, 3.5f)
        Image(
            bitmap = image,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .clip(shape)
                .then(if (modifier == Modifier.fillMaxWidth()) Modifier.aspectRatio(ratio) else Modifier),
        )
    } else {
        Box(
            modifier = modifier
                .clip(shape)
                .background(Brush.linearGradient(listOf(Color(0xFF172033), Color(0xFF263B55))))
                .border(1.dp, Color.White.copy(alpha = 0.08f), shape),
            contentAlignment = Alignment.Center,
        ) {
            if (failed) {
                Text("Image unavailable", color = Color(0xFFCDBFAD), fontSize = 12.sp, modifier = Modifier.padding(8.dp))
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Sky, strokeWidth = 2.dp)
                    Text(placeholderText, color = Color.White, fontWeight = FontWeight.Black, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(horizontal = 8.dp))
                }
            }
        }
    }
}



@Composable
fun SourceDescriptorRow(source: SourceDescriptor) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White.copy(alpha = 0.055f))
            .padding(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(if (source.installed) Mint.copy(alpha = 0.14f) else Color.White.copy(alpha = 0.07f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = when (source.kind) {
                    SourceKind.BuiltIn -> Icons.Rounded.CheckCircle
                    SourceKind.Repository -> Icons.Rounded.CloudDownload
                    SourceKind.Local -> Icons.Rounded.CollectionsBookmark
                },
                contentDescription = null,
                tint = if (source.installed) Mint else Color(0xFF9E927F),
                modifier = Modifier.size(22.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(source.name, color = Paper, fontWeight = FontWeight.Black)
                Text(source.language, color = Sky, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Text(source.description, color = Color(0xFFCDBFAD), fontSize = 12.sp, lineHeight = 16.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text("${source.kind.label} - v${source.version} - ${if (source.trusted) "trusted" else "untrusted"}", color = Color(0xFF9E927F), fontSize = 11.sp)
        }
        Switch(checked = source.installed, onCheckedChange = null)
    }
}

@Composable
fun SourceRepositoryRow(repository: SourceRepository) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White.copy(alpha = 0.045f))
            .padding(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(if (repository.trusted) Ember.copy(alpha = 0.15f) else Coral.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Rounded.CloudDownload, contentDescription = null, tint = if (repository.trusted) Ember else Coral)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(repository.name, color = Paper, fontWeight = FontWeight.Black)
            Text(repository.description, color = Color(0xFFCDBFAD), fontSize = 12.sp, lineHeight = 16.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text("${repository.sourceCount} sources - ${if (repository.trusted) "trusted" else "review before enabling"}", color = Color(0xFF9E927F), fontSize = 11.sp)
        }
    }
}





fun LastOnlineRead.toOnlineSummary(): OnlineComicSummary = OnlineComicSummary(
    id = comicId,
    sourceId = sourceId,
    sourceName = sourceName,
    title = comicTitle,
    description = "Resume from Chapter $chapterNumber",
    coverUrl = coverUrl,
    tags = emptyList(),
    status = "",
)

fun LastOnlineRead.toOnlineChapter(): OnlineChapter = OnlineChapter(
    id = chapterId,
    number = chapterNumber,
    title = chapterTitle,
    pages = 0,
    readableAt = "",
)





@Composable
fun StatTile(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.055f))
            .padding(vertical = 12.dp),
    ) {
        Text(value, color = Paper, fontSize = 20.sp, fontWeight = FontWeight.Black)
        Text(label, color = Color(0xFFB8AA98), fontSize = 11.sp)
    }
}

fun SavedOnlineComic.toOnlineSummary(): OnlineComicSummary = OnlineComicSummary(
    id = id,
    sourceId = sourceId,
    sourceName = sourceName,
    title = title,
    description = description,
    coverUrl = coverUrl,
    tags = tags,
    status = status,
)











@Composable
fun DownloadQueueRow(job: QueuedDownload, running: Boolean, onRemove: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.045f))
            .padding(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(job.comicTitle, color = Paper, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("Ch. ${job.chapterNumber}: ${job.chapterTitle}", color = Color(0xFFCDBFAD), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(job.error ?: job.status, color = if (job.status == QueueStatus.Failed.value) Coral else Color(0xFFB8AA98), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            IconButton(onClick = { onRemove(job.id) }, enabled = !running) {
                Icon(Icons.Rounded.Delete, contentDescription = "Remove queued download", tint = Color(0xFFB8AA98))
            }
        }
        LinearProgressIndicator(
            progress = { job.fraction.coerceIn(0f, 1f) },
            color = if (job.status == QueueStatus.Done.value) Mint else Ember,
            trackColor = Color.White.copy(alpha = 0.08f),
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                .height(5.dp)
                .clip(CircleShape),
        )
    }
}

suspend fun processDownloadQueue(
    queueManager: DownloadQueueManager,
    cacheManager: OfflineCacheManager,
    preferences: ReadoraPreferences,
    onQueueChanged: (List<QueuedDownload>) -> Unit,
) {
    queueManager.load()
        .filter { it.status == QueueStatus.Queued.value || it.status == QueueStatus.Running.value }
        .forEach { queued ->
            val running = queued.copy(status = QueueStatus.Running.value, error = null)
            onQueueChanged(queueManager.update(running))
            runCatching {
                val pages = MangaDexSource.pages(queued.chapterId)
                val chapter = queued.toOnlineChapter().copy(pages = pages.size)
                withContext(Dispatchers.IO) {
                    cacheManager.cacheChapter(queued.toOnlineSummary(), chapter, pages) { current, total ->
                        queueManager.update(running.copy(progress = current, totalPages = total))
                    }
                }
                preferences.addOnlineLibrary(queued.toOnlineSummary(), chapter)
                queueManager.update(running.copy(status = QueueStatus.Done.value, progress = pages.size, totalPages = pages.size))
            }.onFailure { error ->
                queueManager.update(running.copy(status = QueueStatus.Failed.value, error = error.message ?: "Download failed"))
            }
            onQueueChanged(queueManager.load())
        }
}









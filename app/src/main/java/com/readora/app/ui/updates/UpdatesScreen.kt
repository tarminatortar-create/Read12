package com.readora.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.readora.app.data.db.UpdateEntity
import java.util.Calendar
import com.readora.app.source.OnlineChapter
import com.readora.app.source.OnlineComicSummary
import com.readora.app.ui.theme.Ember
import com.readora.app.ui.theme.Mint
import com.readora.app.ui.theme.Paper
import com.readora.app.ui.viewmodel.ReadoraViewModelFactory
import com.readora.app.ui.viewmodel.UpdatesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdatesScreen(onOpen: (OnlineComicSummary, OnlineChapter) -> Unit = { _, _ -> }) {
    val context = LocalContext.current
    val viewModel: UpdatesViewModel = viewModel(factory = ReadoraViewModelFactory(context))
    val uiState by viewModel.uiState.collectAsState()
    var unreadOnly by remember { mutableStateOf(true) }
    var groupByComic by remember { mutableStateOf(false) }
    var expandedComics by remember { mutableStateOf<Set<String>>(emptySet()) }
    val visible = if (unreadOnly) uiState.updates.filter { !it.isRead } else uiState.updates

    LazyColumn(
        contentPadding = PaddingValues(start = 18.dp, top = 18.dp, end = 18.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Updates",
                        color = Paper,
                        fontWeight = FontWeight.Black,
                        fontSize = 28.sp,
                    )
                    if (uiState.unreadCount > 0) {
                        Text(
                            "${uiState.unreadCount} unread",
                            color = Ember,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                        )
                    } else {
                        Text(
                            "What's new in your library",
                            color = Color(0xFFCDBFAD),
                            fontSize = 13.sp,
                        )
                    }
                }
                // Check now button
                if (uiState.isChecking) {
                    CircularProgressIndicator(
                        color = Ember,
                        modifier = Modifier.size(28.dp),
                        strokeWidth = 2.5.dp,
                    )
                } else {
                    IconButton(onClick = { viewModel.checkNow(context) }) {
                        Icon(Icons.Rounded.Refresh, contentDescription = "Check for updates", tint = Ember)
                    }
                }
            }
        }

        item {
            PremiumPanel {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Button(onClick = viewModel::markAllRead, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Rounded.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Mark read", fontSize = 12.sp)
                    }
                    OutlinedButton(onClick = viewModel::deleteAllRead, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Rounded.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Del read", fontSize = 12.sp)
                    }
                    OutlinedButton(onClick = viewModel::clearAll, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Rounded.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Clear all", fontSize = 12.sp)
                    }
                }
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = unreadOnly,
                        onClick = { unreadOnly = true },
                        label = {
                            if (uiState.unreadCount > 0 && unreadOnly) {
                                BadgedBox(badge = {
                                    Badge { Text("${uiState.unreadCount}") }
                                }) {
                                    Text("Unread")
                                }
                            } else {
                                Text("Unread")
                            }
                        },
                    )
                    FilterChip(
                        selected = !unreadOnly,
                        onClick = { unreadOnly = false },
                        label = { Text("All (${uiState.updates.size})") },
                    )
                    FilterChip(
                        selected = groupByComic,
                        onClick = { groupByComic = !groupByComic },
                        label = { Text("Group") },
                    )
                }
            }
        }

        when {
            visible.isEmpty() -> item {
                PremiumPanel {
                    Text(
                        if (unreadOnly) "All caught up!" else "No updates yet",
                        color = Paper,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        if (unreadOnly)
                            "Tap the refresh icon above or switch to All to see history."
                        else
                            "Tap the refresh icon to check your saved online titles for new chapters.",
                        color = Color(0xFFCDBFAD),
                        lineHeight = 20.sp,
                        fontSize = 14.sp,
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.checkNow(context) },
                        enabled = !uiState.isChecking,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        if (uiState.isChecking) {
                            CircularProgressIndicator(color = Paper, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            Spacer(Modifier.width(8.dp))
                            Text("Checking...")
                        } else {
                            Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Check for updates now")
                        }
                    }
                }
            }
            groupByComic -> {
                val groups = visible.groupBy { it.comicTitle }
                groups.forEach { (title, groupUpdates) ->
                    item(key = "header_$title") {
                        val comicId = groupUpdates.firstOrNull()?.comicId ?: ""
                        GroupedComicHeader(
                            title = title,
                            coverUrl = groupUpdates.firstOrNull()?.coverUrl,
                            unreadCount = groupUpdates.count { !it.isRead },
                            expanded = expandedComics.contains(title),
                            onToggle = {
                                expandedComics = if (expandedComics.contains(title))
                                    expandedComics - title else expandedComics + title
                            },
                            onMarkAllRead = if (comicId.isNotBlank()) {
                                { viewModel.markAllReadForComic(comicId) }
                            } else null,
                        )
                    }
                    if (expandedComics.contains(title)) {
                        items(groupUpdates, key = { it.id }) { update ->
                            DismissableUpdateRow(
                                update = update,
                                onMarkRead = { viewModel.markRead(update.id) },
                                onDelete = { viewModel.delete(update.id) },
                                onOpen = {
                                    viewModel.markRead(update.id)
                                    onOpen(
                                        OnlineComicSummary(
                                            id = update.comicId,
                                            sourceId = update.sourceId,
                                            sourceName = update.sourceName,
                                            title = update.comicTitle,
                                            description = "",
                                            coverUrl = update.coverUrl,
                                            tags = emptyList(),
                                            status = "",
                                        ),
                                        OnlineChapter(
                                            id = update.chapterId,
                                            number = update.chapterNumber,
                                            title = update.chapterTitle,
                                            pages = 0,
                                            readableAt = "",
                                        ),
                                    )
                                },
                            )
                        }
                    }
                }
            }
            else -> {
                // Date-grouped flat list: Today / This week / Earlier
                val now = System.currentTimeMillis()
                val startOfToday = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                val startOfWeek = startOfToday - 6 * 24 * 3600 * 1000L
                data class DateGroup(val label: String, val items: List<UpdateEntity>)
                val dateGroups = listOf(
                    DateGroup("Today", visible.filter { it.foundAt >= startOfToday }),
                    DateGroup("This week", visible.filter { it.foundAt >= startOfWeek && it.foundAt < startOfToday }),
                    DateGroup("Earlier", visible.filter { it.foundAt < startOfWeek }),
                ).filter { it.items.isNotEmpty() }
                dateGroups.forEach { group ->
                    item(key = "datehdr_${group.label}") {
                        Text(
                            group.label,
                            color = Color(0xFF9E9080),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 6.dp, bottom = 2.dp),
                        )
                    }
                    items(group.items, key = { it.id }) { update ->
                        DismissableUpdateRow(
                            update = update,
                            onMarkRead = { viewModel.markRead(update.id) },
                            onDelete = { viewModel.delete(update.id) },
                            onOpen = {
                                viewModel.markRead(update.id)
                                onOpen(
                                    OnlineComicSummary(
                                        id = update.comicId,
                                        sourceId = update.sourceId,
                                        sourceName = update.sourceName,
                                        title = update.comicTitle,
                                        description = "",
                                        coverUrl = update.coverUrl,
                                        tags = emptyList(),
                                        status = "",
                                    ),
                                    OnlineChapter(
                                        id = update.chapterId,
                                        number = update.chapterNumber,
                                        title = update.chapterTitle,
                                        pages = 0,
                                        readableAt = "",
                                    ),
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UpdateRow(update: UpdateEntity, onMarkRead: () -> Unit, onOpen: () -> Unit) {
    val accent = if (update.isRead) Mint.copy(alpha = 0.45f) else Ember
    PremiumPanel(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onOpen),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Cover thumbnail
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(accent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                if (!update.coverUrl.isNullOrBlank()) {
                    coil.compose.AsyncImage(
                        model = update.coverUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Icon(
                        Icons.Rounded.AutoStories,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(28.dp),
                    )
                }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    update.comicTitle,
                    color = Paper,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    "Ch. ${update.chapterNumber}" + if (update.chapterTitle.isNotBlank()) " — ${update.chapterTitle}" else "",
                    color = Color(0xFFCDBFAD),
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    update.sourceName + if (update.isRead) " • Read" else " • New",
                    color = accent,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                )
            }
            IconButton(onClick = onMarkRead) {
                Icon(
                    Icons.Rounded.CheckCircle,
                    contentDescription = if (update.isRead) "Already read" else "Mark read",
                    tint = accent,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DismissableUpdateRow(
    update: UpdateEntity,
    onMarkRead: () -> Unit,
    onDelete: () -> Unit,
    onOpen: () -> Unit,
) {
    val state = rememberSwipeToDismissBoxState()
    LaunchedEffect(state.currentValue) {
        when (state.currentValue) {
            SwipeToDismissBoxValue.StartToEnd -> onMarkRead()
            SwipeToDismissBoxValue.EndToStart -> onDelete()
            else -> Unit
        }
    }
    SwipeToDismissBox(
        state = state,
        backgroundContent = {
            val color = when (state.dismissDirection) {
                SwipeToDismissBoxValue.StartToEnd -> Mint.copy(alpha = 0.18f)
                SwipeToDismissBoxValue.EndToStart -> Color(0xFF3A1010)
                else -> Color.Transparent
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(18.dp))
                    .background(color)
                    .padding(horizontal = 24.dp),
                contentAlignment = if (state.dismissDirection == SwipeToDismissBoxValue.StartToEnd)
                    Alignment.CenterStart else Alignment.CenterEnd,
            ) {
                Icon(
                    if (state.dismissDirection == SwipeToDismissBoxValue.StartToEnd)
                        Icons.Rounded.CheckCircle else Icons.Rounded.Delete,
                    contentDescription = null,
                    tint = if (state.dismissDirection == SwipeToDismissBoxValue.StartToEnd) Mint else Ember,
                )
            }
        },
        content = {
            UpdateRow(update = update, onMarkRead = onMarkRead, onOpen = onOpen)
        },
    )
}

@Composable
private fun GroupedComicHeader(
    title: String,
    coverUrl: String?,
    unreadCount: Int,
    expanded: Boolean,
    onToggle: () -> Unit,
    onMarkAllRead: (() -> Unit)? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF141520))
            .clickable(onClick = onToggle)
            .padding(horizontal = 14.dp, vertical = 10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Ember.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            if (!coverUrl.isNullOrBlank()) {
                coil.compose.AsyncImage(
                    model = coverUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Icon(Icons.Rounded.AutoStories, contentDescription = null, tint = Ember, modifier = Modifier.size(22.dp))
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Paper, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (unreadCount > 0) {
                Text("$unreadCount new chapter${if (unreadCount > 1) "s" else ""}", color = Ember, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
        if (onMarkAllRead != null && unreadCount > 0) {
            IconButton(onClick = onMarkAllRead, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Rounded.CheckCircle,
                    contentDescription = "Mark all read for $title",
                    tint = Mint,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
        Icon(
            if (expanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
            contentDescription = if (expanded) "Collapse" else "Expand",
            tint = Color(0xFFB8AA98),
            modifier = Modifier.size(22.dp),
        )
    }
}

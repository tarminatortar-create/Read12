package com.readora.app.ui.downloads

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.HourglassBottom
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.readora.app.storage.DownloadQueueManager
import com.readora.app.storage.OfflineCacheManager
import com.readora.app.storage.QueueStatus
import com.readora.app.storage.QueuedDownload
import com.readora.app.storage.ReadoraPreferences
import com.readora.app.processDownloadQueue
import com.readora.app.ui.theme.Coral
import com.readora.app.ui.theme.Ember
import com.readora.app.ui.theme.Mint
import com.readora.app.ui.theme.Paper
import com.readora.app.ui.viewmodel.ReadoraViewModelFactory
import kotlinx.coroutines.launch

@Composable
fun DownloadManagerScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val queueManager = remember { DownloadQueueManager(context) }
    val cacheManager = remember { OfflineCacheManager(context) }
    val preferences = remember { ReadoraPreferences(context) }
    val scope = rememberCoroutineScope()

    var queue by remember { mutableStateOf(queueManager.load()) }
    var running by remember { mutableStateOf(false) }
    var filterTab by remember { mutableStateOf(0) } // 0=All,1=Pending,2=Done,3=Failed
    var clearAllConfirm by remember { mutableStateOf(false) }

    // Refresh queue periodically while running
    LaunchedEffect(running) {
        if (running) {
            while (running) {
                queue = queueManager.load()
                kotlinx.coroutines.delay(600)
            }
        }
    }

    val filtered = when (filterTab) {
        1 -> queue.filter { it.status == QueueStatus.Queued.value || it.status == QueueStatus.Running.value || it.status == QueueStatus.Paused.value }
        2 -> queue.filter { it.status == QueueStatus.Done.value }
        3 -> queue.filter { it.status == QueueStatus.Failed.value }
        else -> queue
    }

    val pending = queue.count { it.status == QueueStatus.Queued.value || it.status == QueueStatus.Running.value }
    val done = queue.count { it.status == QueueStatus.Done.value }
    val failed = queue.count { it.status == QueueStatus.Failed.value }
    val paused = queue.count { it.status == QueueStatus.Paused.value }

    if (clearAllConfirm) {
        AlertDialog(
            onDismissRequest = { clearAllConfirm = false },
            title = { Text("Clear all downloads?") },
            text = { Text("This will remove all jobs from the queue (downloaded files are kept).") },
            confirmButton = {
                TextButton(onClick = {
                    queue = queueManager.clearAll()
                    clearAllConfirm = false
                }) { Text("Clear all", color = Ember) }
            },
            dismissButton = {
                TextButton(onClick = { clearAllConfirm = false }) { Text("Cancel") }
            },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
    ) {
        // Top bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = Paper)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Download Manager", color = Paper, fontWeight = FontWeight.Black, fontSize = 20.sp)
                Text(
                    "${queue.size} total • $pending pending • $done done • $failed failed",
                    color = Color(0xFFCDBFAD),
                    fontSize = 12.sp,
                )
            }
            if (running) {
                CircularProgressIndicator(color = Ember, modifier = Modifier.size(24.dp), strokeWidth = 2.5.dp)
            }
        }

        // Action row
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
        ) {
            Button(
                onClick = {
                    scope.launch {
                        running = true
                        processDownloadQueue(queueManager, cacheManager, preferences) { updated: List<com.readora.app.storage.QueuedDownload> ->
                            queue = updated
                        }
                        queue = queueManager.load()
                        running = false
                    }
                },
                enabled = !running && pending > 0,
                modifier = Modifier.weight(1f),
            ) {
                Icon(
                    if (running) Icons.Rounded.HourglassBottom else Icons.Rounded.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text(if (running) "Downloading..." else "Start queue")
            }
            OutlinedButton(
                onClick = {
                    if (running) {
                        // Pause: mark all Running as Paused
                        queue.filter { it.status == QueueStatus.Running.value }.forEach { job ->
                            queueManager.update(job.copy(status = QueueStatus.Paused.value))
                        }
                        queue = queueManager.load()
                        running = false
                    }
                },
                enabled = running,
                modifier = Modifier.weight(1f),
            ) {
                Icon(Icons.Rounded.Pause, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Pause")
            }
        }

        // Secondary action row
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp),
        ) {
            TextButton(
                onClick = {
                    queue.filter { it.status == QueueStatus.Failed.value }.forEach { failed ->
                        queueManager.update(failed.copy(status = QueueStatus.Queued.value, error = null, progress = 0))
                    }
                    queue = queueManager.load()
                },
                enabled = failed > 0 && !running,
            ) {
                Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Retry failed ($failed)")
            }
            TextButton(
                onClick = {
                    queue = queueManager.clearFinished()
                },
                enabled = done > 0 && !running,
            ) {
                Icon(Icons.Rounded.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Clear done ($done)")
            }
            TextButton(
                onClick = { clearAllConfirm = true },
                enabled = queue.isNotEmpty() && !running,
            ) {
                Icon(Icons.Rounded.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Clear all")
            }
        }

        // Filter tabs
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        ) {
            listOf("All (${queue.size})", "Pending ($pending)", "Done ($done)", "Failed ($failed)").forEachIndexed { i, label ->
                FilterChip(
                    selected = filterTab == i,
                    onClick = { filterTab = i },
                    label = { Text(label, fontSize = 12.sp) },
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        if (filtered.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(40.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(
                        Icons.Rounded.CloudDownload,
                        contentDescription = null,
                        tint = Paper.copy(alpha = 0.15f),
                        modifier = Modifier.size(56.dp),
                    )
                    Text(
                        if (queue.isEmpty()) "No downloads queued" else "No items in this filter",
                        color = Color(0xFF7A7A9A),
                        fontSize = 15.sp,
                    )
                    if (queue.isEmpty()) {
                        Text(
                            "Open a title, then tap a chapter's cloud icon to queue it for offline download.",
                            color = Color(0xFF5A5A7A),
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(filtered, key = { it.id }) { job ->
                    DismissableDownloadJobRow(
                        job = job,
                        running = running,
                        onRemove = {
                            queue = queueManager.remove(job.id)
                        },
                        onRetry = {
                            queueManager.update(job.copy(status = QueueStatus.Queued.value, error = null, progress = 0))
                            queue = queueManager.load()
                        },
                        onPause = {
                            queueManager.update(job.copy(status = QueueStatus.Paused.value))
                            queue = queueManager.load()
                        },
                        onResume = {
                            queueManager.update(job.copy(status = QueueStatus.Queued.value))
                            queue = queueManager.load()
                        },
                        onCancel = {
                            queueManager.update(job.copy(status = QueueStatus.Failed.value, error = "Cancelled"))
                            queue = queueManager.load()
                        },
                    )
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DismissableDownloadJobRow(
    job: QueuedDownload,
    running: Boolean,
    onRemove: () -> Unit,
    onRetry: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onCancel: () -> Unit,
) {
    val state = rememberSwipeToDismissBoxState()
    LaunchedEffect(state.currentValue) {
        if (state.currentValue == SwipeToDismissBoxValue.EndToStart) onRemove()
    }
    SwipeToDismissBox(
        state = state,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF3A1010))
                    .padding(end = 24.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Icon(Icons.Rounded.Delete, contentDescription = null, tint = Ember)
            }
        },
        content = {
            DownloadJobRow(
                job = job, running = running,
                onRemove = onRemove, onRetry = onRetry,
                onPause = onPause, onResume = onResume, onCancel = onCancel,
            )
        },
    )
}

@Composable
private fun DownloadJobRow(
    job: QueuedDownload,
    running: Boolean,
    onRemove: () -> Unit,
    onRetry: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onCancel: () -> Unit,
) {
    val statusColor = when (job.status) {
        QueueStatus.Done.value -> Mint
        QueueStatus.Failed.value -> Coral
        QueueStatus.Running.value -> Ember
        QueueStatus.Paused.value -> Color(0xFFB8A060)
        else -> Color(0xFF7A7A9A)
    }
    val statusIcon: ImageVector = when (job.status) {
        QueueStatus.Done.value -> Icons.Rounded.CheckCircle
        QueueStatus.Failed.value -> Icons.Rounded.ErrorOutline
        QueueStatus.Running.value -> Icons.Rounded.CloudDownload
        QueueStatus.Paused.value -> Icons.Rounded.Pause
        else -> Icons.Rounded.HourglassBottom
    }
    val animatedProgress by animateFloatAsState(
        targetValue = job.fraction.coerceIn(0f, 1f),
        label = "download_progress",
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF14182A))
            .padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(statusColor.copy(alpha = 0.13f)),
                contentAlignment = Alignment.Center,
            ) {
                if (job.status == QueueStatus.Running.value) {
                    CircularProgressIndicator(
                        color = Ember,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Icon(statusIcon, contentDescription = null, tint = statusColor, modifier = Modifier.size(22.dp))
                }
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    job.comicTitle,
                    color = Paper,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    "Ch. ${job.chapterNumber}" + if (job.chapterTitle.isNotBlank()) " — ${job.chapterTitle}" else "",
                    color = Color(0xFFCDBFAD),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        job.status,
                        color = statusColor,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                    )
                    if (job.status == QueueStatus.Running.value && job.totalPages > 0) {
                        Text(
                            "${job.progress} / ${job.totalPages} pages",
                            color = Color(0xFF7A7A9A),
                            fontSize = 11.sp,
                        )
                    }
                    if (job.status == QueueStatus.Failed.value && !job.error.isNullOrBlank()) {
                        Text(
                            job.error,
                            color = Coral.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            // Action buttons based on status
            when (job.status) {
                QueueStatus.Failed.value -> IconButton(onClick = onRetry, enabled = !running) {
                    Icon(Icons.Rounded.Refresh, contentDescription = "Retry", tint = Ember)
                }
                QueueStatus.Running.value -> {
                    IconButton(onClick = onPause, enabled = running) {
                        Icon(Icons.Rounded.Pause, contentDescription = "Pause", tint = Color(0xFFB8A060))
                    }
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Rounded.Close, contentDescription = "Cancel", tint = Ember.copy(alpha = 0.7f))
                    }
                }
                QueueStatus.Paused.value -> IconButton(onClick = onResume, enabled = !running) {
                    Icon(Icons.Rounded.PlayArrow, contentDescription = "Resume", tint = Mint)
                }
                else -> {
                    // Queued — allow cancel
                    IconButton(onClick = onCancel, enabled = !running) {
                        Icon(Icons.Rounded.Close, contentDescription = "Cancel", tint = Color(0xFF7A7A9A))
                    }
                }
            }
        }

        // Progress bar
        if (job.status != QueueStatus.Queued.value || job.totalPages > 0) {
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { animatedProgress },
                color = when (job.status) {
                    QueueStatus.Done.value -> Mint
                    QueueStatus.Failed.value -> Coral
                    QueueStatus.Paused.value -> Color(0xFFB8A060)
                    else -> Ember
                },
                trackColor = Color.White.copy(alpha = 0.07f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(CircleShape),
            )
        }
    }
}

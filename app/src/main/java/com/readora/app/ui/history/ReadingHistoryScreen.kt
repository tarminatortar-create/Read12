package com.readora.app.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.readora.app.ReadoraApplication
import com.readora.app.data.db.ReadingSessionEntity
import com.readora.app.data.repository.ReadingSessionRepository
import com.readora.app.ui.theme.Ember
import com.readora.app.ui.theme.Mint
import com.readora.app.ui.theme.Paper
import com.readora.app.ui.viewmodel.ReadoraViewModelFactory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

// ViewModel
data class ReadingHistoryUiState(
    val sessions: List<ReadingSessionEntity> = emptyList(),
    val totalMinutesAllTime: Long = 0L,
)

class ReadingHistoryViewModel(
    private val repository: ReadingSessionRepository,
) : ViewModel() {
    val uiState: StateFlow<ReadingHistoryUiState> =
        repository.getAll().map { sessions ->
            val totalMs = sessions.sumOf { it.durationMs }
            ReadingHistoryUiState(
                sessions = sessions,
                totalMinutesAllTime = totalMs / 60_000L,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ReadingHistoryUiState())
}

// Factory shim — ReadingHistoryViewModel is wired in ReadoraViewModelFactory
@Composable
fun ReadingHistoryScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val app = context.applicationContext as ReadoraApplication
    val viewModel: ReadingHistoryViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                ReadingHistoryViewModel(app.appContainer.readingSessionRepository) as T
        }
    )
    val uiState by viewModel.uiState.collectAsState()
    val fmt = remember { SimpleDateFormat("MMM d, yyyy  HH:mm", Locale.getDefault()) }
    var searchQuery by remember { mutableStateOf("") }
    var groupByComic by remember { mutableStateOf(false) }
    var expandedComics by remember { mutableStateOf<Set<String>>(emptySet()) }

    val visibleSessions = remember(uiState.sessions, searchQuery) {
        if (searchQuery.isBlank()) uiState.sessions
        else {
            val q = searchQuery.trim()
            uiState.sessions.filter {
                it.comicTitle.contains(q, ignoreCase = true) ||
                it.chapterNumber?.contains(q, ignoreCase = true) == true
            }
        }
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
                Text("Reading History", color = Paper, fontWeight = FontWeight.Black, fontSize = 20.sp)
                Text(
                    "${uiState.sessions.size} sessions • ${uiState.totalMinutesAllTime} min total",
                    color = Color(0xFFCDBFAD),
                    fontSize = 12.sp,
                )
            }
            Icon(Icons.Rounded.Timer, contentDescription = null, tint = Mint.copy(alpha = 0.7f), modifier = Modifier.size(22.dp))
        }

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search title or chapter…", fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = Color(0xFFB8AA98)) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Rounded.Close, contentDescription = "Clear", tint = Color(0xFFB8AA98))
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        )

        // View mode chips
        Spacer(Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .horizontalScroll(rememberScrollState()),
        ) {
            FilterChip(
                selected = !groupByComic,
                onClick = { groupByComic = false },
                label = { Text("All (${visibleSessions.size})", fontSize = 12.sp) },
            )
            FilterChip(
                selected = groupByComic,
                onClick = { groupByComic = true },
                label = { Text("Group by comic", fontSize = 12.sp) },
            )
        }
        Spacer(Modifier.height(4.dp))

        if (uiState.sessions.isEmpty()) {
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
                    Icon(Icons.Rounded.MenuBook, contentDescription = null, tint = Paper.copy(alpha = 0.15f), modifier = Modifier.size(56.dp))
                    Text("No reading history yet", color = Color(0xFF7A7A9A), fontSize = 15.sp)
                    Text("Start reading online chapters to build your history.", color = Color(0xFF5A5A7A), fontSize = 13.sp)
                }
            }
        } else if (groupByComic) {
            val groups = visibleSessions.groupBy { it.comicTitle }
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                groups.forEach { (comicTitle, comicSessions) ->
                    item(key = "header_$comicTitle") {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF1A1E30))
                                .clickable {
                                    expandedComics = if (expandedComics.contains(comicTitle))
                                        expandedComics - comicTitle else expandedComics + comicTitle
                                }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Ember.copy(alpha = 0.13f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(Icons.Rounded.MenuBook, contentDescription = null, tint = Ember, modifier = Modifier.size(20.dp))
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(comicTitle, color = Paper, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                val totalMin = comicSessions.sumOf { it.durationMs } / 60_000L
                                Text("${comicSessions.size} sessions • ${totalMin}m total", color = Color(0xFFCDBFAD), fontSize = 11.sp)
                            }
                            Icon(
                                if (expandedComics.contains(comicTitle)) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                                contentDescription = null,
                                tint = Color(0xFFB8AA98),
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                    if (expandedComics.contains(comicTitle)) {
                        items(comicSessions, key = { it.id }) { session ->
                            SessionRow(session = session, fmt = fmt)
                        }
                    }
                }
                item { Spacer(Modifier.height(24.dp)) }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(visibleSessions, key = { it.id }) { session ->
                    SessionRow(session = session, fmt = fmt)
                }
                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
private fun SessionRow(session: ReadingSessionEntity, fmt: SimpleDateFormat) {
    val durationMin = (session.durationMs / 60_000L).coerceAtLeast(0L)
    val durationSec = ((session.durationMs % 60_000L) / 1_000L).coerceAtLeast(0L)
    val durationStr = if (durationMin > 0) "${durationMin}m ${durationSec}s" else "${durationSec}s"

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF14182A))
            .padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Ember.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Rounded.MenuBook, contentDescription = null, tint = Ember, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                session.comicTitle,
                color = Paper,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (session.chapterNumber != null) {
                Text(
                    "Chapter ${session.chapterNumber}",
                    color = Color(0xFFCDBFAD),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                fmt.format(Date(session.startedAt)),
                color = Color(0xFF7A7A9A),
                fontSize = 11.sp,
            )
        }
        Text(
            durationStr,
            color = Mint,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
        )
    }
}

package com.readora.app.ui.search

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CollectionsBookmark
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.TextButton
import androidx.lifecycle.viewmodel.compose.viewModel
import com.readora.app.source.OnlineComicSummary
import com.readora.app.storage.SavedOnlineComic
import com.readora.app.ui.theme.Ember
import com.readora.app.ui.theme.Mint
import com.readora.app.ui.theme.Paper
import com.readora.app.ui.theme.Sky
import com.readora.app.ui.viewmodel.ReadoraViewModelFactory
import com.readora.app.ui.viewmodel.SearchViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onOpenOnlineComic: (OnlineComicSummary) -> Unit,
) {
    val context = LocalContext.current
    val viewModel: SearchViewModel = viewModel(factory = ReadoraViewModelFactory(context))
    val uiState by viewModel.uiState.collectAsState()

    fun onQueryChanged(q: String) {
        viewModel.updateQuery(q)
        viewModel.search(q)
    }

    fun onRecentSearchClicked(recent: String) {
        viewModel.quickSearch(recent)
    }

    fun onRemoveRecentSearch(recent: String) {
        viewModel.removeRecentSearch(recent)
    }

    fun onClearHistory() {
        viewModel.clearHistory()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 12.dp, top = 6.dp, bottom = 6.dp),
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = Paper)
            }
            OutlinedTextField(
                value = uiState.query,
                onValueChange = { onQueryChanged(it) },
                placeholder = { Text("Search titles, manga, manhwa...") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Ember,
                    unfocusedBorderColor = Color(0xFF2A2D3E),
                    focusedTextColor = Paper,
                    unfocusedTextColor = Paper,
                ),
            )
        }

        // Suggestion chips: saved searches that match the current partial query
        val suggestions = remember(uiState.query, uiState.recentSearches) {
            if (uiState.query.isBlank()) emptyList()
            else uiState.recentSearches.filter {
                it.contains(uiState.query.trim(), ignoreCase = true) && !it.equals(uiState.query.trim(), ignoreCase = true)
            }.take(6)
        }
        if (suggestions.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp, vertical = 4.dp),
            ) {
                for (suggestion in suggestions) {
                    FilterChip(
                        selected = false,
                        onClick = { onQueryChanged(suggestion) },
                        label = { Text(suggestion, fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Rounded.History, contentDescription = null, modifier = Modifier.size(13.dp)) },
                    )
                }
            }
        }

        if (uiState.loading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = Ember, modifier = Modifier.size(28.dp), strokeWidth = 2.5.dp)
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp, ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (uiState.libraryResults.isNotEmpty()) {
                item {
                    Text(
                        "In your library",
                        color = Mint,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(vertical = 6.dp),
                    )
                }
                items(uiState.libraryResults, key = { "lib_${it.id}" }) { saved ->
                    SearchResultRow(
                        title = saved.title,
                        subtitle = "${saved.sourceName} • In library",
                        accentColor = Mint,
                        onClick = {
                            onOpenOnlineComic(
                                OnlineComicSummary(
                                    id = saved.id,
                                    sourceId = saved.sourceId,
                                    sourceName = saved.sourceName,
                                    title = saved.title,
                                    description = "",
                                    coverUrl = saved.coverUrl,
                                    tags = emptyList(),
                                    status = "",
                                )
                            )
                        },
                    )
                }
                item { Spacer(Modifier.height(4.dp)) }
            }

            if (uiState.sourceSections.isNotEmpty()) {
                item {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        uiState.sourceSections.forEach { section ->
                            val label = if (section.error != null) {
                                "${section.sourceName}: error"
                            } else {
                                "${section.sourceName}: ${section.resultCount}"
                            }
                            FilterChip(
                                selected = false,
                                onClick = {},
                                label = { Text(label, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = if (section.error != null) Color(0xFF4C2B2B) else Color(0xFF1E293B),
                                ),
                            )
                        }
                    }
                }
            }

            if (uiState.searchResults.isNotEmpty()) {
                item {
                    Text(
                        "Online results",
                        color = Ember,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(vertical = 6.dp),
                    )
                }
                items(uiState.searchResults, key = { "online_${it.id}" }) { comic ->
                    SearchResultRow(
                        title = comic.title,
                        subtitle = "${comic.sourceName}${if (comic.status.isNotBlank()) " • ${comic.status}" else ""}",
                        accentColor = Ember,
                        onClick = { onOpenOnlineComic(comic) },
                    )
                }
            }

            if (!uiState.loading && uiState.query.isNotBlank() && uiState.searchResults.isEmpty() && uiState.libraryResults.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Rounded.Search, contentDescription = null, tint = Paper.copy(alpha = 0.2f), modifier = Modifier.size(48.dp))
                            Text("No results for \"${uiState.query}\"", color = Color(0xFFCDBFAD), fontSize = 15.sp)
                        }
                    }
                }
            }

            if (uiState.query.isBlank()) {
                if (uiState.recentSearches.isNotEmpty()) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp, bottom = 4.dp),
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Rounded.History, contentDescription = null, tint = Sky.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                                Text("Recent searches", color = Sky.copy(alpha = 0.9f), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            }
                            TextButton(onClick = { onClearHistory() }) {
                                Text("Clear all", fontSize = 12.sp, color = Color(0xFFB8AA98))
                            }
                        }
                    }
                    item {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            uiState.recentSearches.forEach { recent ->
                                FilterChip(
                                    selected = false,
                                    onClick = {
                                        onRecentSearchClicked(recent)
                                    },
                                    label = { Text(recent, fontSize = 13.sp) },
                                    leadingIcon = {
                                        Icon(Icons.Rounded.History, contentDescription = null, modifier = Modifier.size(14.dp))
                                    },
                                    trailingIcon = {
                                        Icon(
                                            Icons.Rounded.Clear,
                                            contentDescription = "Remove",
                                            modifier = Modifier
                                                .size(14.dp)
                                                .clickable {
                                                    onRemoveRecentSearch(recent)
                                                },
                                        )
                                    },
                                )
                            }
                        }
                    }
                } else {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 48.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(Icons.Rounded.Search, contentDescription = null, tint = Paper.copy(alpha = 0.15f), modifier = Modifier.size(56.dp))
                                Text("Search your library and online sources", color = Color(0xFF7A7A9A), fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultRow(
    title: String,
    subtitle: String,
    accentColor: Color,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF14182A)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Rounded.CollectionsBookmark, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    title,
                    color = Paper,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    subtitle,
                    color = accentColor.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

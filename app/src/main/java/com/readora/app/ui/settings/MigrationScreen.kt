package com.readora.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.readora.app.data.db.ReadoraDatabase
import com.readora.app.data.db.SourceEntity
import com.readora.app.ui.theme.Ember
import com.readora.app.ui.theme.InkRaised
import com.readora.app.ui.theme.Mint
import com.readora.app.ui.theme.Paper
import com.readora.app.ui.theme.Sky

/**
 * Phase 53 — Source Migration Screen
 *
 * Allows users to move their entire library from one source to another.
 * Use case: when a source goes offline, a replacement source is available,
 * and the user wants to keep reading from where they left off.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MigrationScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val database = remember { (context.applicationContext as com.readora.app.ReadoraApplication).database }
    val viewModel = remember { MigrationViewModel(database) }
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Source Migration", color = Paper) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = Paper)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = InkRaised)
            )
        },
        containerColor = Color(0xFF14110E)
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                top = padding.calculateTopPadding() + 16.dp,
                end = 16.dp,
                bottom = 32.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header description
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = InkRaised),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Icon(Icons.Rounded.SwapHoriz, contentDescription = null, tint = Sky, modifier = Modifier.size(32.dp))
                        Spacer(Modifier.height(12.dp))
                        Text("Source Migration", color = Paper, fontWeight = FontWeight.Black, fontSize = 20.sp)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Move your entire library from one source to another. " +
                                "Use this when a source goes offline and a replacement is available. " +
                                "Your reading progress and chapter history are preserved.",
                            color = Color(0xFFCDBFAD),
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            // Done banner
            if (state.isDone) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Mint.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            Modifier.fillMaxWidth().padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = Mint, modifier = Modifier.size(28.dp))
                            Column {
                                Text("Migration complete!", color = Mint, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("${state.migratedCount} comic(s) migrated successfully.", color = Color(0xFFCDBFAD), fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            // Error banner
            state.error?.let { errorMsg ->
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Ember.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            errorMsg,
                            color = Ember,
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // From source selector
            item {
                SourcePickerCard(
                    label = "Migrate FROM (old/dead source)",
                    sources = state.fromSources,
                    selectedId = state.selectedFromId,
                    onSelect = viewModel::selectFromSource,
                    tint = Ember
                )
            }

            // Affected comics preview
            if (state.affectedComics.isNotEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = InkRaised),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                "${state.affectedComics.size} comic(s) will be migrated",
                                color = Paper,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Spacer(Modifier.height(8.dp))
                            state.affectedComics.take(5).forEach { comic ->
                                Text("• ${comic.title}", color = Color(0xFFCDBFAD), fontSize = 13.sp)
                            }
                            if (state.affectedComics.size > 5) {
                                Text("…and ${state.affectedComics.size - 5} more", color = Color(0xFF8A7E6E), fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // To source selector
            item {
                SourcePickerCard(
                    label = "Migrate TO (new/replacement source)",
                    sources = state.toSources.filter { it.sourceId != state.selectedFromId },
                    selectedId = state.selectedToId,
                    onSelect = viewModel::selectToSource,
                    tint = Mint
                )
            }

            // Action button
            item {
                Button(
                    onClick = viewModel::startMigration,
                    enabled = state.selectedFromId != null &&
                            state.selectedToId != null &&
                            state.affectedComics.isNotEmpty() &&
                            !state.isMigrating && !state.isDone,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    if (state.isMigrating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = Paper
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Migrating…")
                    } else {
                        Icon(Icons.Rounded.ArrowForward, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Start Migration")
                    }
                }
            }

            if (state.isDone) {
                item {
                    TextButton(onClick = viewModel::reset, modifier = Modifier.fillMaxWidth()) {
                        Text("Start another migration")
                    }
                }
            }
        }
    }
}

@Composable
private fun SourcePickerCard(
    label: String,
    sources: List<SourceEntity>,
    selectedId: String?,
    onSelect: (String) -> Unit,
    tint: Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = InkRaised),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(label, color = tint, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.height(12.dp))
            if (sources.isEmpty()) {
                Text("No sources available.", color = Color(0xFF8A7E6E), fontSize = 13.sp)
            } else {
                sources.forEach { source ->
                    val isSelected = source.sourceId == selectedId
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) tint.copy(alpha = 0.15f) else Color.Transparent)
                            .clickable { onSelect(source.sourceId) }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { onSelect(source.sourceId) },
                            colors = RadioButtonDefaults.colors(selectedColor = tint)
                        )
                        Column {
                            Text(source.name, color = Paper, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                            Text("${source.language.uppercase()} • ${source.category}", color = Color(0xFFB8AA98), fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

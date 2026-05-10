package com.readora.app.ui.settings

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
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.readora.app.source.SourceRepositoryClient
import com.readora.app.source.manifest.ManifestParser
import com.readora.app.source.manifest.RepositoryManifest
import com.readora.app.PremiumPanel
import com.readora.app.ui.theme.Coral
import com.readora.app.ui.theme.Mint
import com.readora.app.ui.theme.Paper
import com.readora.app.ui.theme.Sky
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import androidx.lifecycle.viewmodel.compose.viewModel
import com.readora.app.ui.viewmodel.ReadoraViewModelFactory
import com.readora.app.ui.viewmodel.RepositoryViewModel
import androidx.compose.runtime.collectAsState

@Composable
fun RepositoryManagerScreen(
    viewModel: RepositoryViewModel = viewModel(factory = ReadoraViewModelFactory(androidx.compose.ui.platform.LocalContext.current)),
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var showAddDialog by remember { mutableStateOf(false) }
    var repoUrl by rememberSaveable { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val repositories by viewModel.repositories.collectAsState()

    LazyColumn(
        contentPadding = PaddingValues(start = 18.dp, top = 18.dp, end = 18.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
                IconButton(onClick = onBack, modifier = Modifier.padding(end = 12.dp)) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = Paper)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Repositories", color = Paper, fontWeight = FontWeight.Black, fontSize = 28.sp)
                    Text("Add external source lists", color = Color(0xFFCDBFAD), fontSize = 14.sp)
                }
                IconButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Rounded.Add, contentDescription = "Add Repository", tint = Sky)
                }
            }
        }

        if (repositories.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No repositories added.\nTap + to add an external source manifest.",
                        color = Color(0xFFB8AA98),
                        fontSize = 14.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        items(repositories) { repo ->
            RepositoryRow(
                manifest = repo,
                onDelete = { viewModel.removeRepository(repo.repositoryId) }
            )
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { 
                showAddDialog = false
                error = null
            },
            title = { Text("Add Repository") },
            text = {
                Column {
                    Text("Enter the URL of the repository manifest JSON.", fontSize = 14.sp, color = Color(0xFFCDBFAD))
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = repoUrl,
                        onValueChange = { repoUrl = it },
                        singleLine = true,
                        label = { Text("URL") },
                        placeholder = { Text("https://example.com/sources.json") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (error != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(error!!, color = Coral, fontSize = 13.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val url = repoUrl.trim()
                        if (url.isBlank()) {
                            error = "URL cannot be empty."
                            return@Button
                        }
                        loading = true
                        error = null
                        viewModel.addRepository(
                            urlStr = url,
                            onSuccess = {
                                showAddDialog = false
                                repoUrl = ""
                                loading = false
                            },
                            onError = { err ->
                                error = err
                                loading = false
                            }
                        )
                    },
                    enabled = !loading
                ) {
                    if (loading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Add")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showAddDialog = false
                        error = null
                    },
                    enabled = !loading
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun RepositoryRow(manifest: RepositoryManifest, onDelete: () -> Unit) {
    PremiumPanel {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Sky.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Rounded.Explore, contentDescription = null, tint = Sky)
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(manifest.name, color = Paper, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.width(8.dp))
                    val trustColor = when (manifest.trustLevel.lowercase()) {
                        "official" -> Mint
                        "community" -> Color(0xFFFACC15)
                        else -> Coral
                    }
                    Box(modifier = Modifier.background(trustColor.copy(alpha = 0.2f), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                        Text(manifest.trustLevel.uppercase(), color = trustColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Text("${manifest.sources.size} sources • by ${manifest.maintainer}", color = Color(0xFFCDBFAD), fontSize = 13.sp)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Rounded.Delete, contentDescription = "Delete", tint = Coral)
            }
        }
    }
}

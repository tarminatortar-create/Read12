package com.readora.app.ui.bookmarks

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
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.readora.app.data.db.BookmarkEntity
import com.readora.app.data.db.ChapterNoteEntity
import com.readora.app.ui.theme.Ember
import com.readora.app.ui.theme.Mint
import com.readora.app.ui.theme.Paper
import com.readora.app.ui.viewmodel.BookmarksViewModel
import com.readora.app.ui.viewmodel.ReadoraViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BookmarksScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val viewModel: BookmarksViewModel = viewModel(factory = ReadoraViewModelFactory(context))
    val uiState by viewModel.uiState.collectAsState()
    var tab by remember { mutableStateOf(0) } // 0 = bookmarks, 1 = notes
    var clearConfirm by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    if (clearConfirm) {
        AlertDialog(
            onDismissRequest = { clearConfirm = false },
            title = { Text("Clear all?") },
            text = { Text(if (tab == 0) "Delete all bookmarks?" else "Delete all notes?") },
            confirmButton = {
                TextButton(onClick = {
                    if (tab == 0) viewModel.clearAllBookmarks() else viewModel.clearAllNotes()
                    clearConfirm = false
                }) { Text("Delete", color = Ember) }
            },
            dismissButton = {
                TextButton(onClick = { clearConfirm = false }) { Text("Cancel") }
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
            Text(
                "Bookmarks & Notes",
                color = Paper,
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = { clearConfirm = true }) {
                Icon(Icons.Rounded.Delete, contentDescription = "Clear all", tint = Ember.copy(alpha = 0.7f))
            }
        }

        // Tab row
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp),
        ) {
            FilterChip(
                selected = tab == 0,
                onClick = { tab = 0 },
                label = { Text("Bookmarks (${uiState.bookmarks.size})") },
                leadingIcon = { Icon(Icons.Rounded.Bookmark, contentDescription = null, modifier = Modifier.size(16.dp)) },
            )
            FilterChip(
                selected = tab == 1,
                onClick = { tab = 1 },
                label = { Text("Notes (${uiState.notes.size})") },
                leadingIcon = { Icon(Icons.Rounded.Edit, contentDescription = null, modifier = Modifier.size(16.dp)) },
            )
        }

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by comic or chapter…", fontSize = 14.sp) },
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
                .padding(horizontal = 18.dp),
        )
        Spacer(Modifier.height(4.dp))

        if (tab == 0) {
            val filteredBookmarks = if (searchQuery.isBlank()) uiState.bookmarks
            else {
                val q = searchQuery.trim()
                uiState.bookmarks.filter {
                    it.comicTitle.contains(q, ignoreCase = true) ||
                    it.chapterNumber.contains(q, ignoreCase = true)
                }
            }
            BookmarksList(
                bookmarks = filteredBookmarks,
                onDelete = { viewModel.deleteBookmark(it) },
            )
        } else {
            val filteredNotes = if (searchQuery.isBlank()) uiState.notes
            else {
                val q = searchQuery.trim()
                uiState.notes.filter {
                    it.content.contains(q, ignoreCase = true) ||
                    it.chapterTitle.contains(q, ignoreCase = true) ||
                    it.chapterNumber.contains(q, ignoreCase = true)
                }
            }
            NotesList(
                notes = filteredNotes,
                onDelete = { viewModel.deleteNote(it) },
            )
        }
    }
}

@Composable
private fun BookmarksList(
    bookmarks: List<BookmarkEntity>,
    onDelete: (Long) -> Unit,
) {
    if (bookmarks.isEmpty()) {
        EmptyState(
            icon = Icons.Rounded.Bookmark,
            message = "No bookmarks yet",
            hint = "While reading, tap the Bookmark button in the reader toolbar to save your place.",
        )
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(bookmarks, key = { it.id }) { bm ->
            BookmarkRow(bookmark = bm, onDelete = { onDelete(bm.id) })
        }
    }
}

@Composable
private fun NotesList(
    notes: List<ChapterNoteEntity>,
    onDelete: (Long) -> Unit,
) {
    if (notes.isEmpty()) {
        EmptyState(
            icon = Icons.Rounded.Edit,
            message = "No notes yet",
            hint = "While reading, tap the Note button in the reader toolbar to jot something down.",
        )
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(notes, key = { it.id }) { note ->
            NoteRow(note = note, onDelete = { onDelete(note.id) })
        }
    }
}

@Composable
private fun BookmarkRow(bookmark: BookmarkEntity, onDelete: () -> Unit) {
    val fmt = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF14182A))
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Ember.copy(alpha = 0.13f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Rounded.Bookmark, contentDescription = null, tint = Ember, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(bookmark.comicTitle, color = Paper, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                "Ch. ${bookmark.chapterNumber} — Page ${bookmark.pageIndex}",
                color = Ember.copy(alpha = 0.8f),
                fontSize = 12.sp,
            )
            if (!bookmark.note.isNullOrBlank()) {
                Text(bookmark.note, color = Color(0xFFCDBFAD), fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            Text(fmt.format(Date(bookmark.createdAt)), color = Color(0xFF7A7A9A), fontSize = 11.sp)
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Rounded.Delete, contentDescription = "Delete", tint = Ember.copy(alpha = 0.6f))
        }
    }
}

@Composable
private fun NoteRow(note: ChapterNoteEntity, onDelete: () -> Unit) {
    val fmt = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF14182A))
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Mint.copy(alpha = 0.13f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Rounded.Edit, contentDescription = null, tint = Mint, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(note.chapterTitle.ifBlank { "Ch. ${note.chapterNumber}" }, color = Paper, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (note.pageIndex != null) {
                Text("Page ${note.pageIndex}", color = Mint.copy(alpha = 0.8f), fontSize = 12.sp)
            }
            Spacer(Modifier.height(4.dp))
            Text(note.content, color = Color(0xFFCDBFAD), fontSize = 13.sp, lineHeight = 18.sp)
            Spacer(Modifier.height(2.dp))
            Text(fmt.format(Date(note.createdAt)), color = Color(0xFF7A7A9A), fontSize = 11.sp)
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Rounded.Delete, contentDescription = "Delete", tint = Ember.copy(alpha = 0.6f))
        }
    }
}

@Composable
private fun EmptyState(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    message: String,
    hint: String,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(icon, contentDescription = null, tint = Paper.copy(alpha = 0.2f), modifier = Modifier.size(56.dp))
            Text(message, color = Paper.copy(alpha = 0.6f), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(hint, color = Color(0xFF7A7A9A), fontSize = 13.sp, lineHeight = 18.sp)
        }
    }
}

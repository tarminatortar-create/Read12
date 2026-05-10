package com.readora.app

import android.app.Application
import com.readora.app.data.db.ReadoraDatabase
import com.readora.app.data.db.migration.MigrationHelper
import com.readora.app.data.repository.DownloadRepository
import com.readora.app.data.repository.BookmarkRepository
import com.readora.app.data.repository.ChapterNoteRepository
import com.readora.app.data.repository.LibraryRepository
import com.readora.app.data.repository.MergeRepository
import com.readora.app.data.repository.ProgressRepository
import com.readora.app.data.repository.SourceRepository
import com.readora.app.data.repository.UpdatesRepository
import com.readora.app.data.repository.ReadingSessionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import com.readora.app.worker.LibraryUpdatesWorker
import com.readora.app.worker.RepositoryUpdateWorker

class ReadoraApplication : Application() {
    val database: ReadoraDatabase by lazy { ReadoraDatabase.getInstance(this) }
    val appContainer: AppContainer by lazy { AppContainer(database) }

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            MigrationHelper(this@ReadoraApplication, database).migrateIfNeeded()
        }
        // Phase 52 — schedule the daily repository update check
        RepositoryUpdateWorker.schedule(this)
        // Phase 110B extension — schedule periodic unread updates check
        LibraryUpdatesWorker.schedule(this)
    }
}

class AppContainer(database: ReadoraDatabase) {
    val libraryRepository = LibraryRepository(database)
    val progressRepository = ProgressRepository(database)
    val downloadRepository = DownloadRepository(database)
    val mergeRepository = MergeRepository(database)
    val sourceRepository = SourceRepository(database)
    val updatesRepository = UpdatesRepository(database)
    val bookmarkRepository = BookmarkRepository(database)
    val chapterNoteRepository = ChapterNoteRepository(database)
    val readingSessionRepository = ReadingSessionRepository(database)
}

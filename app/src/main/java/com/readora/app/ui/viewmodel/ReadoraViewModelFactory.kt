package com.readora.app.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.readora.app.ReadoraApplication
import com.readora.app.storage.ReadoraPreferences
import com.readora.app.storage.SettingsSerializer

class ReadoraViewModelFactory(context: Context) : ViewModelProvider.Factory {
    val app = context.applicationContext as ReadoraApplication

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val container = app.appContainer
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) ->
                HomeViewModel(container.libraryRepository, container.readingSessionRepository, container.updatesRepository) as T
            modelClass.isAssignableFrom(LibraryViewModel::class.java) ->
                LibraryViewModel(container.libraryRepository) as T
            modelClass.isAssignableFrom(DiscoverViewModel::class.java) ->
                DiscoverViewModel(container.sourceRepository, com.readora.app.storage.SettingsSerializer(app)) as T
            modelClass.isAssignableFrom(SearchViewModel::class.java) ->
                SearchViewModel(container.sourceRepository, SettingsSerializer(app), ReadoraPreferences(app)) as T
            modelClass.isAssignableFrom(ReaderViewModel::class.java) ->
                ReaderViewModel(container.progressRepository) as T
            modelClass.isAssignableFrom(UpdatesViewModel::class.java) ->
                UpdatesViewModel(container.updatesRepository) as T
            modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
                SettingsViewModel(
                    com.readora.app.storage.SettingsSerializer(app),
                    com.readora.app.source.SourceRegistryManager(app.database.sourceDao()),
                    container.readingSessionRepository,
                ) as T
            modelClass.isAssignableFrom(RepositoryViewModel::class.java) -> {
                val sourceRegistryManager = com.readora.app.source.SourceRegistryManager(app.database.sourceDao())
                val sourceInstaller = com.readora.app.source.manifest.SourceInstaller(sourceRegistryManager)
                val manifestStore = com.readora.app.source.manifest.RepositoryManifestStore(app)
                RepositoryViewModel(sourceInstaller, manifestStore) as T
            }
            modelClass.isAssignableFrom(BookmarksViewModel::class.java) ->
                BookmarksViewModel(container.bookmarkRepository, container.chapterNoteRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel ${modelClass.name}")
        }
    }
}

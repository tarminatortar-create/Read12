package com.readora.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.readora.app.source.manifest.ManifestParser
import com.readora.app.source.manifest.RepositoryManifestStore
import com.readora.app.source.manifest.RepositoryManifest
import com.readora.app.source.manifest.SourceInstaller
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class RepositoryViewModel(
    private val sourceInstaller: SourceInstaller,
    private val manifestStore: RepositoryManifestStore,
) : ViewModel() {

    private val _repositories = MutableStateFlow(manifestStore.load())
    val repositories: StateFlow<List<RepositoryManifest>> = _repositories.asStateFlow()

    fun addRepository(urlStr: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val jsonString = withContext(Dispatchers.IO) {
                    val connection = (URL(urlStr).openConnection() as HttpURLConnection).apply {
                        connectTimeout = 15_000
                        readTimeout = 20_000
                        requestMethod = "GET"
                    }
                    if (connection.responseCode !in 200..299) {
                        throw IllegalStateException("HTTP ${connection.responseCode}")
                    }
                    connection.inputStream.bufferedReader().use { it.readText() }
                }
                
                val manifest = ManifestParser.parse(jsonString)
                
                // Install all sources from the manifest
                for (source in manifest.sources) {
                    sourceInstaller.install(source)
                }

                _repositories.value = manifestStore.upsert(manifest)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Failed to add repository")
            }
        }
    }

    fun removeRepository(repositoryId: String) {
        viewModelScope.launch {
            val repo = _repositories.value.find { it.repositoryId == repositoryId }
            if (repo != null) {
                // Uninstall all sources from this repository
                for (source in repo.sources) {
                    sourceInstaller.uninstall(source.id)
                }
                _repositories.value = manifestStore.remove(repositoryId)
            }
        }
    }
}

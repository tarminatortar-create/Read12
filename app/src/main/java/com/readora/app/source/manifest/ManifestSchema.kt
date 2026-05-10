package com.readora.app.source.manifest

data class RepositoryManifest(
    val schemaVersion: Int,
    val repositoryId: String,
    val name: String,
    val maintainer: String,
    val description: String,
    val trustLevel: String, // official, community, untrusted
    val sources: List<SourceManifestEntry>
)

data class SourceManifestEntry(
    val id: String,
    val name: String,
    val version: String,
    val language: String,
    val categories: List<String>,
    val iconUrl: String?,
    val parserType: String,
    val baseUrl: String,
    val minAppVersion: Int,
    val repositoryId: String? = null,
    val trustLevel: String? = null,
    val definitionJson: String? = null,
)

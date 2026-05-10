package com.readora.app.source.manifest

import org.json.JSONArray
import org.json.JSONObject

object ManifestParser {

    fun parse(jsonString: String): RepositoryManifest {
        val json = JSONObject(jsonString)
        val schemaVersion = json.optInt("schemaVersion", 1)
        
        ManifestValidator.validateVersion(schemaVersion)

        val sourcesArray = json.optJSONArray("sources") ?: JSONArray()
        val sources = buildList {
            for (i in 0 until sourcesArray.length()) {
                val sourceJson = sourcesArray.optJSONObject(i) ?: continue
                
                val categoriesArray = sourceJson.optJSONArray("categories") ?: JSONArray()
                val categories = buildList {
                    for (j in 0 until categoriesArray.length()) {
                        add(categoriesArray.optString(j))
                    }
                }

                val repositoryId = json.optString("repositoryId", jsonString.hashCode().toString())
                val trustLevel = json.optString("trustLevel", "untrusted")
                add(
                    SourceManifestEntry(
                        id = sourceJson.optString("id"),
                        name = sourceJson.optString("name"),
                        version = sourceJson.optString("version", "1.0.0"),
                        language = sourceJson.optString("language", "en"),
                        categories = categories,
                        iconUrl = sourceJson.optString("iconUrl", "").ifBlank { null },
                        parserType = sourceJson.optString("parserType", "custom"),
                        baseUrl = sourceJson.optString("baseUrl"),
                        minAppVersion = sourceJson.optInt("minAppVersion", 1),
                        repositoryId = repositoryId,
                        trustLevel = trustLevel,
                        definitionJson = sourceJson.optJSONObject("definition")?.toString(),
                    )
                )
            }
        }

        val manifest = RepositoryManifest(
            schemaVersion = schemaVersion,
            repositoryId = json.optString("repositoryId", jsonString.hashCode().toString()),
            name = json.optString("name", "Unknown Repository"),
            maintainer = json.optString("maintainer", "Unknown"),
            description = json.optString("description", ""),
            trustLevel = json.optString("trustLevel", "untrusted"),
            sources = sources
        )
        ManifestValidator.validateManifest(manifest)
        return manifest
    }
}

object ManifestValidator {
    const val CURRENT_SCHEMA_VERSION = 1
    const val MIN_SUPPORTED_SCHEMA = 1

    fun validateVersion(version: Int) {
        if (version < MIN_SUPPORTED_SCHEMA) {
            throw IllegalArgumentException("Unsupported schema version: $version. Minimum supported is $MIN_SUPPORTED_SCHEMA.")
        }
        if (version > CURRENT_SCHEMA_VERSION) {
            throw IllegalArgumentException("Repository schema $version is newer than this Readora build supports.")
        }
    }

    fun validateManifest(manifest: RepositoryManifest) {
        require(manifest.repositoryId.isNotBlank()) { "Repository ID is required." }
        require(manifest.name.isNotBlank()) { "Repository name is required." }
        require(manifest.trustLevel in setOf("official", "community", "untrusted")) {
            "trustLevel must be official, community, or untrusted."
        }
        manifest.sources.forEach { source ->
            require(source.id.isNotBlank()) { "Source ID is required." }
            require(source.name.isNotBlank()) { "Source name is required for ${source.id}." }
            require(source.baseUrl.startsWith("https://")) { "Source ${source.id} must use an HTTPS baseUrl." }
            require(source.parserType.uppercase() in setOf("JSON_API", "JSON", "HTML_CSS", "HTML", "RSS")) {
                "Source ${source.id} has unsupported parserType ${source.parserType}."
            }
            if (source.parserType.uppercase() != "RSS") {
                require(!source.definitionJson.isNullOrBlank()) { "Source ${source.id} is missing a parser definition." }
            }
        }
    }
}

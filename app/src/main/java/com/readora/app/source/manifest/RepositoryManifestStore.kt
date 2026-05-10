package com.readora.app.source.manifest

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class RepositoryManifestStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("readora_repository_manifests", Context.MODE_PRIVATE)

    fun load(): List<RepositoryManifest> {
        val raw = prefs.getString(KEY_MANIFESTS, null) ?: return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (index in 0 until array.length()) {
                    val item = array.optJSONObject(index) ?: continue
                    add(ManifestParser.parse(item.toString()))
                }
            }
        }.getOrDefault(emptyList())
    }

    fun save(manifests: List<RepositoryManifest>) {
        val array = JSONArray()
        manifests.distinctBy { it.repositoryId }.forEach { manifest ->
            array.put(manifest.toJson())
        }
        prefs.edit().putString(KEY_MANIFESTS, array.toString()).apply()
    }

    fun upsert(manifest: RepositoryManifest): List<RepositoryManifest> {
        val updated = (listOf(manifest) + load().filterNot { it.repositoryId == manifest.repositoryId })
        save(updated)
        return updated
    }

    fun remove(repositoryId: String): List<RepositoryManifest> {
        val updated = load().filterNot { it.repositoryId == repositoryId }
        save(updated)
        return updated
    }

    private fun RepositoryManifest.toJson(): JSONObject = JSONObject()
        .put("schemaVersion", schemaVersion)
        .put("repositoryId", repositoryId)
        .put("name", name)
        .put("maintainer", maintainer)
        .put("description", description)
        .put("trustLevel", trustLevel)
        .put("sources", JSONArray().apply {
            sources.forEach { source ->
                put(
                    JSONObject()
                        .put("id", source.id)
                        .put("name", source.name)
                        .put("version", source.version)
                        .put("language", source.language)
                        .put("categories", JSONArray(source.categories))
                        .put("iconUrl", source.iconUrl)
                        .put("parserType", source.parserType)
                        .put("baseUrl", source.baseUrl)
                        .put("minAppVersion", source.minAppVersion)
                        .put("definition", source.definitionJson?.let { JSONObject(it) }),
                )
            }
        })

    private companion object {
        const val KEY_MANIFESTS = "manifests"
    }
}

package com.readora.app.storage

import android.content.Context
import com.readora.app.source.OnlineChapter
import com.readora.app.source.OnlineComicSummary
import com.readora.app.source.SourceRepository
import org.json.JSONArray
import org.json.JSONObject

class ReadoraPreferences(context: Context) {
    val prefs = context.applicationContext.getSharedPreferences("readora_state", Context.MODE_PRIVATE)

    fun getBoolean(key: String, defaultValue: Boolean): Boolean = prefs.getBoolean(key, defaultValue)

    fun exportBackup(): String {
        val payload = JSONObject()
            .put("version", 1)
            .put("createdAt", System.currentTimeMillis())
            .put("settings", JSONObject()
                .put(KEY_SMART_PRELOAD, getBoolean(KEY_SMART_PRELOAD, true))
                .put(KEY_HAPTICS, getBoolean(KEY_HAPTICS, true))
                .put(KEY_AUTO_MERGE, getBoolean(KEY_AUTO_MERGE, false))
                .put(KEY_DEFAULT_WEBTOON, getBoolean(KEY_DEFAULT_WEBTOON, true))
                .put(KEY_DEFAULT_RTL, getBoolean(KEY_DEFAULT_RTL, true)),
            )
            .put("repositories", JSONArray(prefs.getString(KEY_REPOSITORIES, "[]")))
            .put("onlineLibrary", JSONArray(prefs.getString(KEY_ONLINE_LIBRARY, "[]")))
            .put("localLibrary", JSONArray(prefs.getString(KEY_LOCAL_LIBRARY, "[]")))
            .put("mergeGroups", JSONArray(prefs.getString(KEY_MERGE_GROUPS, "[]")))
            .put("lastOnlineRead", prefs.getString(KEY_LAST_ONLINE_READ, null))
        return payload.toString(2)
    }

    fun importBackup(raw: String) {
        val payload = JSONObject(raw)
        val settings = payload.optJSONObject("settings") ?: JSONObject()
        prefs.edit()
            .putBoolean(KEY_SMART_PRELOAD, settings.optBoolean(KEY_SMART_PRELOAD, true))
            .putBoolean(KEY_HAPTICS, settings.optBoolean(KEY_HAPTICS, true))
            .putBoolean(KEY_AUTO_MERGE, settings.optBoolean(KEY_AUTO_MERGE, false))
            .putBoolean(KEY_DEFAULT_WEBTOON, settings.optBoolean(KEY_DEFAULT_WEBTOON, true))
            .putBoolean(KEY_DEFAULT_RTL, settings.optBoolean(KEY_DEFAULT_RTL, true))
            .putString(KEY_REPOSITORIES, payload.optJSONArray("repositories")?.toString() ?: "[]")
            .putString(KEY_ONLINE_LIBRARY, payload.optJSONArray("onlineLibrary")?.toString() ?: "[]")
            .putString(KEY_LOCAL_LIBRARY, payload.optJSONArray("localLibrary")?.toString() ?: "[]")
            .putString(KEY_MERGE_GROUPS, payload.optJSONArray("mergeGroups")?.toString() ?: "[]")
            .putString(KEY_LAST_ONLINE_READ, payload.optString("lastOnlineRead").ifBlank { null })
            .apply()
    }

    fun putBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun loadRepositories(): List<SourceRepository> {
        val raw = prefs.getString(KEY_REPOSITORIES, null) ?: return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (index in 0 until array.length()) {
                    val item = array.optJSONObject(index) ?: continue
                    add(
                        SourceRepository(
                            id = item.optString("id"),
                            name = item.optString("name"),
                            url = item.optString("url"),
                            trusted = item.optBoolean("trusted"),
                            sourceCount = item.optInt("sourceCount"),
                            description = item.optString("description"),
                        ),
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    fun saveRepositories(repositories: List<SourceRepository>) {
        val array = JSONArray()
        repositories.forEach { repository ->
            array.put(
                JSONObject()
                    .put("id", repository.id)
                    .put("name", repository.name)
                    .put("url", repository.url)
                    .put("trusted", repository.trusted)
                    .put("sourceCount", repository.sourceCount)
                    .put("description", repository.description),
            )
        }
        prefs.edit().putString(KEY_REPOSITORIES, array.toString()).apply()
    }

    fun loadOnlineLibrary(): List<SavedOnlineComic> {
        val raw = prefs.getString(KEY_ONLINE_LIBRARY, null) ?: return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (index in 0 until array.length()) {
                    val item = array.optJSONObject(index) ?: continue
                    add(
                        SavedOnlineComic(
                            id = item.optString("id"),
                            sourceId = item.optString("sourceId"),
                            sourceName = item.optString("sourceName"),
                            title = item.optString("title"),
                            description = item.optString("description"),
                            coverUrl = item.optString("coverUrl").ifBlank { null },
                            status = item.optString("status"),
                            tags = item.optJSONArray("tags")?.let { tags ->
                                buildList {
                                    for (tagIndex in 0 until tags.length()) add(tags.optString(tagIndex))
                                }
                            }.orEmpty(),
                            addedAt = item.optLong("addedAt"),
                            lastChapterNumber = item.optString("lastChapterNumber"),
                            lastChapterTitle = item.optString("lastChapterTitle"),
                        ),
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    fun saveOnlineLibrary(items: List<SavedOnlineComic>) {
        val array = JSONArray()
        items.forEach { item ->
            val tags = JSONArray()
            item.tags.forEach { tags.put(it) }
            array.put(
                JSONObject()
                    .put("id", item.id)
                    .put("sourceId", item.sourceId)
                    .put("sourceName", item.sourceName)
                    .put("title", item.title)
                    .put("description", item.description)
                    .put("coverUrl", item.coverUrl)
                    .put("status", item.status)
                    .put("tags", tags)
                    .put("addedAt", item.addedAt)
                    .put("lastChapterNumber", item.lastChapterNumber)
                    .put("lastChapterTitle", item.lastChapterTitle),
            )
        }
        prefs.edit().putString(KEY_ONLINE_LIBRARY, array.toString()).apply()
    }

    fun addOnlineLibrary(comic: OnlineComicSummary, chapter: OnlineChapter? = null): List<SavedOnlineComic> {
        val existing = loadOnlineLibrary().filterNot { it.id == comic.id && it.sourceId == comic.sourceId }
        val saved = SavedOnlineComic(
            id = comic.id,
            sourceId = comic.sourceId,
            sourceName = comic.sourceName,
            title = comic.title,
            description = comic.description,
            coverUrl = comic.coverUrl,
            status = comic.status,
            tags = comic.tags,
            addedAt = System.currentTimeMillis(),
            lastChapterNumber = chapter?.number.orEmpty(),
            lastChapterTitle = chapter?.title.orEmpty(),
        )
        val updated = (listOf(saved) + existing).take(300)
        saveOnlineLibrary(updated)
        return updated
    }

    fun isInOnlineLibrary(comic: OnlineComicSummary): Boolean =
        loadOnlineLibrary().any { it.id == comic.id && it.sourceId == comic.sourceId }

    fun removeOnlineLibrary(sourceId: String, id: String): List<SavedOnlineComic> {
        val updated = loadOnlineLibrary().filterNot { it.id == id && it.sourceId == sourceId }
        saveOnlineLibrary(updated)
        return updated
    }

    fun saveChapterProgress(comic: OnlineComicSummary, chapter: OnlineChapter, currentPage: Int, totalPages: Int) {
        val safeTotal = totalPages.coerceAtLeast(1)
        val safePage = currentPage.coerceIn(1, safeTotal)
        val payload = JSONObject()
            .put("sourceId", comic.sourceId)
            .put("comicId", comic.id)
            .put("comicTitle", comic.title)
            .put("chapterId", chapter.id)
            .put("chapterNumber", chapter.number)
            .put("chapterTitle", chapter.title)
            .put("currentPage", safePage)
            .put("totalPages", safeTotal)
            .put("updatedAt", System.currentTimeMillis())
        prefs.edit().putString(progressKey(comic.sourceId, comic.id, chapter.id), payload.toString()).apply()
    }

    fun loadChapterProgress(sourceId: String, comicId: String, chapterId: String): SavedReadingProgress? {
        val raw = prefs.getString(progressKey(sourceId, comicId, chapterId), null) ?: return null
        return runCatching {
            val item = JSONObject(raw)
            SavedReadingProgress(
                sourceId = item.optString("sourceId"),
                comicId = item.optString("comicId"),
                comicTitle = item.optString("comicTitle"),
                chapterId = item.optString("chapterId"),
                chapterNumber = item.optString("chapterNumber"),
                chapterTitle = item.optString("chapterTitle"),
                currentPage = item.optInt("currentPage", 1),
                totalPages = item.optInt("totalPages", 1),
                updatedAt = item.optLong("updatedAt"),
            )
        }.getOrNull()
    }

    fun saveReaderModeOverride(sourceId: String, comicId: String, mode: String?) {
        if (mode.isNullOrBlank()) {
            prefs.edit().remove(readerModeOverrideKey(sourceId, comicId)).apply()
        } else {
            prefs.edit().putString(readerModeOverrideKey(sourceId, comicId), mode).apply()
        }
    }

    fun loadReaderModeOverride(sourceId: String, comicId: String): String? {
        return prefs.getString(readerModeOverrideKey(sourceId, comicId), null)
    }

    private fun readerModeOverrideKey(sourceId: String, comicId: String) =
        "reader_mode_override_${sourceId}_$comicId"

    fun clearChapterProgress(sourceId: String, comicId: String, chapterId: String) {
        prefs.edit().remove(progressKey(sourceId, comicId, chapterId)).apply()
    }

    fun progressKey(sourceId: String, comicId: String, chapterId: String): String =
        "progress_" + sourceId + "_" + comicId + "_" + chapterId

    fun savePreferredScanlator(sourceId: String, comicId: String, scanlator: String?) {
        val key = "scanlator_pref_${sourceId}_$comicId"
        if (scanlator.isNullOrBlank()) {
            prefs.edit().remove(key).apply()
        } else {
            prefs.edit().putString(key, scanlator).apply()
        }
    }

    fun loadPreferredScanlator(sourceId: String, comicId: String): String? {
        val key = "scanlator_pref_${sourceId}_$comicId"
        return prefs.getString(key, null)
    }

    fun loadMergeGroups(): List<SavedMergeGroup> {
        val raw = prefs.getString(KEY_MERGE_GROUPS, null) ?: return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (index in 0 until array.length()) {
                    val item = array.optJSONObject(index) ?: continue
                    val keys = item.optJSONArray("itemKeys") ?: JSONArray()
                    add(
                        SavedMergeGroup(
                            id = item.optString("id"),
                            title = item.optString("title"),
                            itemKeys = buildList {
                                for (keyIndex in 0 until keys.length()) add(keys.optString(keyIndex))
                            },
                            createdAt = item.optLong("createdAt"),
                        ),
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    fun saveMergeGroups(groups: List<SavedMergeGroup>) {
        val array = JSONArray()
        groups.forEach { group ->
            val keys = JSONArray()
            group.itemKeys.forEach { keys.put(it) }
            array.put(
                JSONObject()
                    .put("id", group.id)
                    .put("title", group.title)
                    .put("itemKeys", keys)
                    .put("createdAt", group.createdAt),
            )
        }
        prefs.edit().putString(KEY_MERGE_GROUPS, array.toString()).apply()
    }

    fun createSmartMergeGroups(): List<SavedMergeGroup> {
        val online = loadOnlineLibrary().map { SavedMergeCandidate("online:${it.sourceId}:${it.id}", it.title) }
        val local = loadLocalLibrary().map { SavedMergeCandidate("local:${it.uri}", it.title) }
        val groups = (online + local)
            .groupBy { normalizeTitle(it.title) }
            .filter { it.value.size > 1 && it.key.isNotBlank() }
            .map { (normalized, candidates) ->
                SavedMergeGroup(
                    id = "merge_$normalized",
                    title = candidates.first().title,
                    itemKeys = candidates.map { it.key },
                    createdAt = System.currentTimeMillis(),
                )
            }
        saveMergeGroups(groups)
        return groups
    }

    fun createManualMergeGroup(title: String, itemKeys: List<String>): List<SavedMergeGroup> {
        val cleanKeys = itemKeys.distinct().filter { it.isNotBlank() }
        if (cleanKeys.size < 2) return loadMergeGroups()
        val cleanTitle = title.ifBlank { "Manual merge group" }
        val group = SavedMergeGroup(
            id = "manual_${System.currentTimeMillis()}",
            title = cleanTitle,
            itemKeys = cleanKeys,
            createdAt = System.currentTimeMillis(),
        )
        val updated = (listOf(group) + loadMergeGroups()).take(100)
        saveMergeGroups(updated)
        return updated
    }

    fun removeMergeGroup(id: String): List<SavedMergeGroup> {
        val updated = loadMergeGroups().filterNot { it.id == id }
        saveMergeGroups(updated)
        return updated
    }

    fun moveMergeItem(groupId: String, itemKey: String, delta: Int): List<SavedMergeGroup> {
        val updated = loadMergeGroups().map { group ->
            if (group.id != groupId) return@map group
            val currentIndex = group.itemKeys.indexOf(itemKey)
            val nextIndex = (currentIndex + delta).coerceIn(0, group.itemKeys.lastIndex)
            if (currentIndex < 0 || currentIndex == nextIndex) {
                group
            } else {
                val keys = group.itemKeys.toMutableList()
                val moved = keys.removeAt(currentIndex)
                keys.add(nextIndex, moved)
                group.copy(itemKeys = keys)
            }
        }
        saveMergeGroups(updated)
        return updated
    }

    fun normalizeTitle(title: String): String = title
        .lowercase()
        .replace(Regex("""\.[a-z0-9]{2,5}$"""), "")
        .replace(Regex("[^a-z0-9]+"), "-")
        .trim('-')

    /** Returns pairs of online library entries whose titles share ≥2 significant words. */
    fun suggestDuplicates(): List<Pair<SavedOnlineComic, SavedOnlineComic>> {
        val stopWords = setOf("the", "a", "an", "of", "in", "on", "at", "to", "is", "and", "or", "i")
        fun words(title: String) = title.lowercase()
            .replace(Regex("[^a-z0-9 ]+"), " ")
            .split(" ")
            .filter { it.length > 2 && it !in stopWords }
            .toSet()

        val items = loadOnlineLibrary()
        val suggestions = mutableListOf<Pair<SavedOnlineComic, SavedOnlineComic>>()
        val alreadyGrouped = loadMergeGroups().flatMap { it.itemKeys }.toSet()
        for (i in items.indices) {
            for (j in i + 1 until items.size) {
                val a = items[i]
                val b = items[j]
                val keyA = "online:${a.sourceId}:${a.id}"
                val keyB = "online:${b.sourceId}:${b.id}"
                // Skip pairs already in a merge group
                if (alreadyGrouped.contains(keyA) && alreadyGrouped.contains(keyB)) continue
                val wordsA = words(a.title)
                val wordsB = words(b.title)
                val shared = wordsA.intersect(wordsB).size
                if (shared >= 2) suggestions.add(a to b)
            }
        }
        return suggestions.take(10)
    }

    fun loadLocalLibrary(): List<SavedLocalComic> {
        val raw = prefs.getString(KEY_LOCAL_LIBRARY, null) ?: return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (index in 0 until array.length()) {
                    val item = array.optJSONObject(index) ?: continue
                    add(
                        SavedLocalComic(
                            uri = item.optString("uri"),
                            title = item.optString("title"),
                            type = item.optString("type"),
                            importedAt = item.optLong("importedAt"),
                        ),
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    fun saveLocalLibrary(items: List<SavedLocalComic>) {
        val array = JSONArray()
        items.forEach { item ->
            array.put(
                JSONObject()
                    .put("uri", item.uri)
                    .put("title", item.title)
                    .put("type", item.type)
                    .put("importedAt", item.importedAt),
            )
        }
        prefs.edit().putString(KEY_LOCAL_LIBRARY, array.toString()).apply()
    }

    fun addLocalComic(uri: String, title: String, type: String): List<SavedLocalComic> {
        val existing = loadLocalLibrary().filterNot { it.uri == uri }
        val updated = (listOf(SavedLocalComic(uri, title, type, System.currentTimeMillis())) + existing).take(300)
        saveLocalLibrary(updated)
        return updated
    }

    fun removeLocalComic(uri: String): List<SavedLocalComic> {
        val updated = loadLocalLibrary().filterNot { it.uri == uri }
        saveLocalLibrary(updated)
        return updated
    }

    fun saveLastOnlineRead(comic: OnlineComicSummary, chapter: OnlineChapter) {
        val payload = JSONObject()
            .put("comicId", comic.id)
            .put("sourceId", comic.sourceId)
            .put("sourceName", comic.sourceName)
            .put("title", comic.title)
            .put("coverUrl", comic.coverUrl)
            .put("chapterId", chapter.id)
            .put("chapterNumber", chapter.number)
            .put("chapterTitle", chapter.title)
            .put("updatedAt", System.currentTimeMillis())
        prefs.edit().putString(KEY_LAST_ONLINE_READ, payload.toString()).apply()
    }

    fun loadLastOnlineRead(): LastOnlineRead? {
        val raw = prefs.getString(KEY_LAST_ONLINE_READ, null) ?: return null
        return runCatching {
            val item = JSONObject(raw)
            LastOnlineRead(
                comicId = item.optString("comicId"),
                sourceId = item.optString("sourceId"),
                comicTitle = item.optString("title"),
                coverUrl = item.optString("coverUrl").ifBlank { null },
                chapterId = item.optString("chapterId"),
                sourceName = item.optString("sourceName"),
                chapterNumber = item.optString("chapterNumber"),
                chapterTitle = item.optString("chapterTitle"),
                updatedAt = item.optLong("updatedAt"),
            )
        }.getOrNull()
    }

    // User star rating: 0 = unrated, 1-5 stars
    fun saveUserRating(comicId: String, stars: Int) {
        prefs.edit().putInt("user_rating_$comicId", stars).apply()
    }

    fun loadUserRating(comicId: String): Int =
        prefs.getInt("user_rating_$comicId", 0)

    // Reading-list status: "plan" | "reading" | "completed" | "" (none)
    fun saveReadingListStatus(comicId: String, status: String) {
        prefs.edit().putString("rl_status_$comicId", status).apply()
    }

    fun loadReadingListStatus(comicId: String): String =
        prefs.getString("rl_status_$comicId", "") ?: ""

    // ── Custom user shelves ──────────────────────────────────────────────────
    fun loadUserShelves(): List<UserShelf> {
        val json = prefs.getString("user_shelves", null) ?: return emptyList()
        return runCatching {
            val arr = org.json.JSONArray(json)
            (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                val ids = o.getJSONArray("comicIds")
                UserShelf(
                    id = o.getString("id"),
                    name = o.getString("name"),
                    comicIds = (0 until ids.length()).map { ids.getString(it) },
                    createdAt = o.optLong("createdAt", 0L),
                )
            }
        }.getOrDefault(emptyList())
    }

    fun saveUserShelves(shelves: List<UserShelf>) {
        val arr = org.json.JSONArray()
        shelves.forEach { shelf ->
            arr.put(org.json.JSONObject().apply {
                put("id", shelf.id)
                put("name", shelf.name)
                put("comicIds", org.json.JSONArray(shelf.comicIds))
                put("createdAt", shelf.createdAt)
            })
        }
        prefs.edit().putString("user_shelves", arr.toString()).apply()
    }

    fun createUserShelf(name: String): List<UserShelf> {
        val shelves = loadUserShelves().toMutableList()
        shelves.add(UserShelf(id = java.util.UUID.randomUUID().toString(), name = name.trim(), comicIds = emptyList(), createdAt = System.currentTimeMillis()))
        saveUserShelves(shelves)
        return shelves
    }

    fun renameUserShelf(shelfId: String, newName: String): List<UserShelf> {
        val shelves = loadUserShelves().map { if (it.id == shelfId) it.copy(name = newName.trim()) else it }
        saveUserShelves(shelves)
        return shelves
    }

    fun deleteUserShelf(shelfId: String): List<UserShelf> {
        val shelves = loadUserShelves().filterNot { it.id == shelfId }
        saveUserShelves(shelves)
        return shelves
    }

    fun toggleComicInShelf(shelfId: String, comicId: String): List<UserShelf> {
        val shelves = loadUserShelves().map { shelf ->
            if (shelf.id != shelfId) shelf
            else if (shelf.comicIds.contains(comicId))
                shelf.copy(comicIds = shelf.comicIds - comicId)
            else
                shelf.copy(comicIds = shelf.comicIds + comicId)
        }
        saveUserShelves(shelves)
        return shelves
    }

    // Recently viewed titles in Discover (up to 20, most recent first)
    fun addRecentlyViewed(comic: OnlineComicSummary) {
        val current = loadRecentlyViewed().toMutableList()
        current.removeAll { it.id == comic.id }
        current.add(0, comic)
        val arr = org.json.JSONArray()
        current.take(20).forEach { c ->
            arr.put(org.json.JSONObject().apply {
                put("id", c.id)
                put("sourceId", c.sourceId)
                put("sourceName", c.sourceName)
                put("title", c.title)
                put("description", c.description)
                put("coverUrl", c.coverUrl ?: "")
                put("tags", org.json.JSONArray(c.tags))
                put("status", c.status)
            })
        }
        prefs.edit().putString("recently_viewed", arr.toString()).apply()
    }

    fun loadRecentlyViewed(): List<OnlineComicSummary> {
        val raw = prefs.getString("recently_viewed", null) ?: return emptyList()
        return runCatching {
            val arr = org.json.JSONArray(raw)
            (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                OnlineComicSummary(
                    id = o.getString("id"),
                    sourceId = o.getString("sourceId"),
                    sourceName = o.optString("sourceName", ""),
                    title = o.getString("title"),
                    description = o.optString("description", ""),
                    coverUrl = o.optString("coverUrl").ifBlank { null },
                    tags = (0 until o.getJSONArray("tags").length()).map { j -> o.getJSONArray("tags").getString(j) },
                    status = o.optString("status", ""),
                )
            }
        }.getOrElse { emptyList() }
    }

    companion object {
        const val KEY_SMART_PRELOAD = "smart_preload"
        const val KEY_HAPTICS = "haptics"
        const val KEY_AUTO_MERGE = "auto_merge"
        const val KEY_DEFAULT_WEBTOON = "default_webtoon"
        const val KEY_DEFAULT_RTL = "default_rtl"
        private const val KEY_REPOSITORIES = "repositories"
        private const val KEY_ONLINE_LIBRARY = "online_library"
        private const val KEY_LOCAL_LIBRARY = "local_library"
        private const val KEY_MERGE_GROUPS = "merge_groups"
        private const val KEY_LAST_ONLINE_READ = "last_online_read"
    }
}

data class LastOnlineRead(
    val comicId: String,
    val sourceId: String,
    val comicTitle: String,
    val coverUrl: String?,
    val chapterId: String,
    val sourceName: String,
    val chapterNumber: String,
    val chapterTitle: String,
    val updatedAt: Long,
)


data class SavedOnlineComic(
    val id: String,
    val sourceId: String,
    val sourceName: String,
    val title: String,
    val description: String,
    val coverUrl: String?,
    val status: String,
    val tags: List<String>,
    val addedAt: Long,
    val lastChapterNumber: String,
    val lastChapterTitle: String,
)


data class SavedReadingProgress(
    val sourceId: String,
    val comicId: String,
    val comicTitle: String,
    val chapterId: String,
    val chapterNumber: String,
    val chapterTitle: String,
    val currentPage: Int,
    val totalPages: Int,
    val updatedAt: Long,
) {
    val fraction: Float get() = if (totalPages <= 0) 0f else currentPage.toFloat() / totalPages.toFloat()
}


data class SavedLocalComic(
    val uri: String,
    val title: String,
    val type: String,
    val importedAt: Long,
)

data class SavedMergeGroup(
    val id: String,
    val title: String,
    val itemKeys: List<String>,
    val createdAt: Long,
)

data class SavedMergeCandidate(
    val key: String,
    val title: String,
)

data class UserShelf(
    val id: String,
    val name: String,
    val comicIds: List<String>,
    val createdAt: Long,
)

package com.readora.app.storage

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class ReadoraSettings(
    val readerMode: String = "webtoon", // "webtoon" | "paged" | "paged_double"
    val readingDirection: String = "rtl", // "rtl" or "ltr"
    val defaultBrightness: Float = -1f, // -1 means system default
    val keepScreenOn: Boolean = true,
    val downloadOnWifiOnly: Boolean = false,
    val maxParallelDownloads: Int = 3,
    val preferredLanguages: List<String> = listOf("en"),
    val enabledSourceIds: List<String> = emptyList(),
    val themeAccentColor: String = "default",
    val oledMode: Boolean = false,
    val autoUpdateLibrary: Boolean = true,
    val autoUpdateIntervalHours: Int = 12,
    val smartPreload: Boolean = true,
    val haptics: Boolean = true,
    val autoMerge: Boolean = false,
    val incognitoMode: Boolean = false,
    val appLockEnabled: Boolean = false,
    val appLockPinHash: String? = null,
    val contentRating: String = "suggestive", // safe/suggestive/adult
    val volumeButtonNavigation: Boolean = false,
    val volumeButtonInverted: Boolean = false,
    val autoScrollEnabled: Boolean = false,
    val autoScrollSpeed: Float = 220f,
    val savedSearches: List<String> = emptyList(),
    val pinnedSearches: List<String> = emptyList(),
    val discoverTagFilter: String = "",
    val pageTransition: String = "slide", // "none" | "slide" | "fade"
    val readerBackground: String = "dark", // "dark" | "black" | "sepia" | "white"
    val autoDetectWebtoon: Boolean = true,
    val useDynamicColor: Boolean = false,
    // Notification preferences
    val notifyNewChapters: Boolean = true,
    val notifyOnlyWifi: Boolean = false,
    val notifySoundEnabled: Boolean = true,
    val notifyVibrateEnabled: Boolean = true,
    val dailyGoalMinutes: Int = 30,
    val readerFontScale: Float = 1.0f, // 0.75 | 1.0 | 1.25 | 1.5 | 2.0
    val version: Int = 2
)

class SettingsSerializer(context: Context) {
    val prefs = context.applicationContext.getSharedPreferences("readora_settings", Context.MODE_PRIVATE)
    val oldPrefs = context.applicationContext.getSharedPreferences("readora_state", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_SETTINGS_JSON = "settings_json"
        private const val CURRENT_VERSION = 2
    }

    init {
        migrateFromOldPreferences()
    }

    fun migrateFromOldPreferences() {
        if (!prefs.contains(KEY_SETTINGS_JSON) && oldPrefs.all.isNotEmpty()) {
            val defaultWebtoon = oldPrefs.getBoolean("default_webtoon", true)
            val defaultRtl = oldPrefs.getBoolean("default_rtl", true)
            val smartPreload = oldPrefs.getBoolean("smart_preload", true)
            val haptics = oldPrefs.getBoolean("haptics", true)
            val autoMerge = oldPrefs.getBoolean("auto_merge", false)

            val migratedSettings = ReadoraSettings(
                readerMode = if (defaultWebtoon) "webtoon" else "paged",
                readingDirection = if (defaultRtl) "rtl" else "ltr",
                smartPreload = smartPreload,
                haptics = haptics,
                autoMerge = autoMerge,
                version = CURRENT_VERSION
            )
            save(migratedSettings)
        }
    }

    fun load(): ReadoraSettings {
        val jsonString = prefs.getString(KEY_SETTINGS_JSON, null) ?: return ReadoraSettings()
        return runCatching {
            val json = JSONObject(jsonString)
            val version = json.optInt("version", 1)
            var settings = ReadoraSettings(
                readerMode = json.optString("readerMode", "webtoon"),
                readingDirection = json.optString("readingDirection", "rtl"),
                defaultBrightness = json.optDouble("defaultBrightness", -1.0).toFloat(),
                keepScreenOn = json.optBoolean("keepScreenOn", true),
                downloadOnWifiOnly = json.optBoolean("downloadOnWifiOnly", false),
                maxParallelDownloads = json.optInt("maxParallelDownloads", 3),
                preferredLanguages = json.optJSONArray("preferredLanguages")?.let { array ->
                    List(array.length()) { array.getString(it) }
                } ?: listOf("en"),
                enabledSourceIds = json.optJSONArray("enabledSourceIds")?.let { array ->
                    List(array.length()) { array.getString(it) }
                } ?: emptyList(),
                themeAccentColor = json.optString("themeAccentColor", "default"),
                oledMode = json.optBoolean("oledMode", false),
                autoUpdateLibrary = json.optBoolean("autoUpdateLibrary", true),
                autoUpdateIntervalHours = json.optInt("autoUpdateIntervalHours", 12),
                smartPreload = json.optBoolean("smartPreload", true),
                haptics = json.optBoolean("haptics", true),
                autoMerge = json.optBoolean("autoMerge", false),
                incognitoMode = json.optBoolean("incognitoMode", false),
                appLockEnabled = json.optBoolean("appLockEnabled", false),
                appLockPinHash = json.optString("appLockPinHash").ifBlank { null },
                contentRating = json.optString("contentRating", "suggestive"),
                volumeButtonNavigation = json.optBoolean("volumeButtonNavigation", false),
                volumeButtonInverted = json.optBoolean("volumeButtonInverted", false),
                autoScrollEnabled = json.optBoolean("autoScrollEnabled", false),
                autoScrollSpeed = json.optDouble("autoScrollSpeed", 220.0).toFloat(),
                savedSearches = json.optJSONArray("savedSearches")?.let { array ->
                    List(array.length()) { array.getString(it) }
                } ?: emptyList(),
                pinnedSearches = json.optJSONArray("pinnedSearches")?.let { array ->
                    List(array.length()) { array.getString(it) }
                } ?: emptyList(),
                discoverTagFilter = json.optString("discoverTagFilter", ""),
                pageTransition = json.optString("pageTransition", "slide"),
                readerBackground = json.optString("readerBackground", "dark"),
                autoDetectWebtoon = json.optBoolean("autoDetectWebtoon", true),
                useDynamicColor = json.optBoolean("useDynamicColor", false),
                notifyNewChapters = json.optBoolean("notifyNewChapters", true),
                notifyOnlyWifi = json.optBoolean("notifyOnlyWifi", false),
                notifySoundEnabled = json.optBoolean("notifySoundEnabled", true),
                notifyVibrateEnabled = json.optBoolean("notifyVibrateEnabled", true),
                dailyGoalMinutes = json.optInt("dailyGoalMinutes", 30),
                readerFontScale = json.optDouble("readerFontScale", 1.0).toFloat(),
                version = version
            )
            
            // Apply version upgrades here if version < CURRENT_VERSION
            if (settings.version < CURRENT_VERSION) {
                settings = settings.copy(version = CURRENT_VERSION)
                save(settings)
            }
            
            settings
        }.getOrDefault(ReadoraSettings())
    }

    fun save(settings: ReadoraSettings) {
        val json = JSONObject().apply {
            put("readerMode", settings.readerMode)
            put("readingDirection", settings.readingDirection)
            put("defaultBrightness", settings.defaultBrightness.toDouble())
            put("keepScreenOn", settings.keepScreenOn)
            put("downloadOnWifiOnly", settings.downloadOnWifiOnly)
            put("maxParallelDownloads", settings.maxParallelDownloads)
            put("preferredLanguages", JSONArray(settings.preferredLanguages))
            put("enabledSourceIds", JSONArray(settings.enabledSourceIds))
            put("themeAccentColor", settings.themeAccentColor)
            put("oledMode", settings.oledMode)
            put("autoUpdateLibrary", settings.autoUpdateLibrary)
            put("autoUpdateIntervalHours", settings.autoUpdateIntervalHours)
            put("smartPreload", settings.smartPreload)
            put("haptics", settings.haptics)
            put("autoMerge", settings.autoMerge)
            put("incognitoMode", settings.incognitoMode)
            put("appLockEnabled", settings.appLockEnabled)
            put("appLockPinHash", settings.appLockPinHash)
            put("contentRating", settings.contentRating)
            put("volumeButtonNavigation", settings.volumeButtonNavigation)
            put("volumeButtonInverted", settings.volumeButtonInverted)
            put("autoScrollEnabled", settings.autoScrollEnabled)
            put("autoScrollSpeed", settings.autoScrollSpeed.toDouble())
            put("savedSearches", JSONArray(settings.savedSearches))
            put("pinnedSearches", JSONArray(settings.pinnedSearches))
            put("discoverTagFilter", settings.discoverTagFilter)
            put("pageTransition", settings.pageTransition)
            put("readerBackground", settings.readerBackground)
            put("autoDetectWebtoon", settings.autoDetectWebtoon)
            put("useDynamicColor", settings.useDynamicColor)
            put("notifyNewChapters", settings.notifyNewChapters)
            put("notifyOnlyWifi", settings.notifyOnlyWifi)
            put("notifySoundEnabled", settings.notifySoundEnabled)
            put("notifyVibrateEnabled", settings.notifyVibrateEnabled)
            put("dailyGoalMinutes", settings.dailyGoalMinutes)
            put("readerFontScale", settings.readerFontScale.toDouble())
            put("version", settings.version)
        }
        prefs.edit().putString(KEY_SETTINGS_JSON, json.toString()).apply()
    }
}

package com.readora.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.readora.app.data.repository.ReadingSessionRepository
import com.readora.app.storage.ReadoraSettings
import com.readora.app.storage.SettingsSerializer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class SettingsViewModel(
    private val settingsSerializer: SettingsSerializer,
    private val sourceRegistryManager: com.readora.app.source.SourceRegistryManager,
    private val readingSessionRepository: ReadingSessionRepository? = null,
) : ViewModel() {
    private val _uiState = MutableStateFlow(settingsSerializer.load())
    val uiState: StateFlow<ReadoraSettings> = _uiState.asStateFlow()

    val sources = sourceRegistryManager.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun toggleSource(sourceId: String, enabled: Boolean) {
        viewModelScope.launch {
            if (enabled) {
                sourceRegistryManager.enable(sourceId)
            } else {
                sourceRegistryManager.disable(sourceId)
            }
        }
    }

    fun updateSettings(reducer: ReadoraSettings.() -> ReadoraSettings) {
        _uiState.update { current ->
            val updated = current.reducer()
            settingsSerializer.save(updated)
            updated
        }
    }

    fun setSmartPreload(value: Boolean) = updateSettings { copy(smartPreload = value) }
    fun setHaptics(value: Boolean) = updateSettings { copy(haptics = value) }
    fun setAutoMerge(value: Boolean) = updateSettings { copy(autoMerge = value) }
    fun setDefaultReaderMode(value: String) = updateSettings { copy(readerMode = value) }
    fun setDefaultWebtoon(value: Boolean) = setDefaultReaderMode(if (value) "webtoon" else "paged")
    fun setAutoDetectWebtoon(value: Boolean) = updateSettings { copy(autoDetectWebtoon = value) }
    fun setUseDynamicColor(value: Boolean) = updateSettings { copy(useDynamicColor = value) }
    fun setDefaultRtl(value: Boolean) = updateSettings { copy(readingDirection = if (value) "rtl" else "ltr") }
    fun setOledMode(value: Boolean) = updateSettings { copy(oledMode = value) }
    fun setKeepScreenOn(value: Boolean) = updateSettings { copy(keepScreenOn = value) }
    fun setDownloadOnWifiOnly(value: Boolean) = updateSettings { copy(downloadOnWifiOnly = value) }
    fun setAutoUpdateLibrary(value: Boolean) = updateSettings { copy(autoUpdateLibrary = value) }
    fun setAutoUpdateIntervalHours(value: Int) = updateSettings { copy(autoUpdateIntervalHours = value.coerceIn(3, 72)) }
    fun setIncognitoMode(value: Boolean) = updateSettings { copy(incognitoMode = value) }

    fun setAppLockEnabled(value: Boolean) = updateSettings { copy(appLockEnabled = value) }
    fun setAppLockPinHash(value: String?) = updateSettings { copy(appLockPinHash = value) }

    fun setVolumeButtonNavigation(value: Boolean) = updateSettings { copy(volumeButtonNavigation = value) }
    fun setVolumeButtonInverted(value: Boolean) = updateSettings { copy(volumeButtonInverted = value) }
    fun setAutoScrollEnabled(value: Boolean) = updateSettings { copy(autoScrollEnabled = value) }
    fun setAutoScrollSpeed(value: Float) = updateSettings { copy(autoScrollSpeed = value.coerceIn(40f, 800f)) }
    fun setDefaultBrightness(value: Float) = updateSettings { copy(defaultBrightness = value.coerceIn(-1f, 1f)) }
    fun setThemeAccentColor(value: String) = updateSettings { copy(themeAccentColor = value) }
    fun setPageTransition(value: String) = updateSettings { copy(pageTransition = value) }
    fun setReaderBackground(value: String) = updateSettings { copy(readerBackground = value) }
    fun togglePreferredLanguage(lang: String) = updateSettings {
        val current = preferredLanguages.toMutableList()
        if (current.contains(lang)) {
            if (current.size > 1) current.remove(lang) // keep at least one
        } else {
            current.add(lang)
        }
        copy(preferredLanguages = current)
    }
    fun saveSearch(query: String) = updateSettings {
        val cleaned = query.trim()
        if (cleaned.isBlank()) this
        else copy(savedSearches = (listOf(cleaned) + savedSearches.filterNot { it.equals(cleaned, true) }).take(12))
    }
    fun removeSearch(query: String) = updateSettings { copy(savedSearches = savedSearches.filterNot { it.equals(query, true) }) }
    fun togglePinnedSearch(query: String) = updateSettings {
        val cleaned = query.trim()
        if (cleaned.isBlank()) this
        else if (pinnedSearches.any { it.equals(cleaned, true) })
            copy(pinnedSearches = pinnedSearches.filterNot { it.equals(cleaned, true) })
        else copy(pinnedSearches = (pinnedSearches + cleaned).take(10))
    }

    // Notification preferences
    fun setNotifyNewChapters(value: Boolean) = updateSettings { copy(notifyNewChapters = value) }
    fun setNotifyOnlyWifi(value: Boolean) = updateSettings { copy(notifyOnlyWifi = value) }
    fun setNotifySoundEnabled(value: Boolean) = updateSettings { copy(notifySoundEnabled = value) }
    fun setNotifyVibrateEnabled(value: Boolean) = updateSettings { copy(notifyVibrateEnabled = value) }

    fun setDailyGoalMinutes(value: Int) = updateSettings { copy(dailyGoalMinutes = value.coerceIn(5, 240)) }
    fun setReaderFontScale(value: Float) = updateSettings { copy(readerFontScale = value) }

    fun clearReadingHistory() {
        viewModelScope.launch {
            readingSessionRepository?.clearAll()
        }
    }
}

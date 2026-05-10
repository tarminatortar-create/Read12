package com.readora.app.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.readora.app.ReadoraApplication
import com.readora.app.MainActivity
import com.readora.app.core.ReadoraLogger
import com.readora.app.data.db.UpdateEntity
import com.readora.app.source.MangaDexSource
import com.readora.app.storage.ReadoraPreferences
import com.readora.app.storage.SettingsSerializer
import com.readora.app.toOnlineSummary
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class LibraryUpdatesWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {
    companion object {
        private const val WORK_NAME = "LibraryUpdatesCheck"
        private const val CHANNEL_ID = "readora_library_updates"
        private const val NOTIFICATION_ID = 2002
        private const val ONE_TIME_NAME = "LibraryUpdatesCheckNow"

        fun schedule(context: Context) {
            val settings = SettingsSerializer(context).load()
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(if (settings.downloadOnWifiOnly) NetworkType.UNMETERED else NetworkType.CONNECTED)
                .build()
            val interval = settings.autoUpdateIntervalHours.toLong().coerceIn(3L, 72L)
            val request = PeriodicWorkRequestBuilder<LibraryUpdatesWorker>(interval, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request,
            )
            ReadoraLogger.log("LibraryUpdatesWorker", "Scheduled library updates worker (${interval}h interval)")
        }

        fun runOnce(context: Context) {
            val settings = SettingsSerializer(context).load()
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(if (settings.downloadOnWifiOnly) NetworkType.UNMETERED else NetworkType.CONNECTED)
                .build()
            val request = OneTimeWorkRequestBuilder<LibraryUpdatesWorker>()
                .setConstraints(constraints)
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                ONE_TIME_NAME,
                androidx.work.ExistingWorkPolicy.REPLACE,
                request,
            )
            ReadoraLogger.log("LibraryUpdatesWorker", "Enqueued one-time library updates check")
        }
    }

    override suspend fun doWork(): Result {
        return try {
            val settings = SettingsSerializer(applicationContext).load()
            if (!settings.autoUpdateLibrary || settings.incognitoMode) {
                return Result.success()
            }
            val db = (applicationContext as ReadoraApplication).database
            val preferences = ReadoraPreferences(applicationContext)
            val saved = preferences.loadOnlineLibrary()
            if (saved.isEmpty()) return Result.success()

            var inserted = 0
            withContext(Dispatchers.IO) {
                saved.forEach { item ->
                    if (item.sourceId != MangaDexSource.id) return@forEach
                    val summary = item.toOnlineSummary()
                    val latest = runCatching { MangaDexSource.details(summary).chapters.firstOrNull() }.getOrNull()
                    if (latest != null && item.lastChapterNumber.isNotBlank() && latest.number.isNotBlank() && latest.number != item.lastChapterNumber) {
                        val id = db.updateDao().insert(
                            UpdateEntity(
                                sourceId = item.sourceId,
                                sourceName = item.sourceName,
                                comicId = item.id,
                                comicTitle = item.title,
                                coverUrl = item.coverUrl,
                                chapterId = latest.id,
                                chapterNumber = latest.number,
                                chapterTitle = latest.title,
                                foundAt = System.currentTimeMillis(),
                                isRead = false,
                            )
                        )
                        if (id > 0) inserted++
                    }
                }
            }

            val unreadCount = db.updateDao().getUnread().first().size
            if (unreadCount > 0 && inserted > 0) {
                postNotification(unreadCount)
            }
            Result.success()
        } catch (e: Exception) {
            ReadoraLogger.log("LibraryUpdatesWorker", "Failed update check: ${e.message}")
            Result.retry()
        }
    }

    private suspend fun postNotification(unreadCount: Int) {
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Library Updates",
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = "Notifications for newly found library chapters"
            }
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("open_route", "updates")
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= 23) PendingIntent.FLAG_IMMUTABLE else 0),
        )

        val db = (applicationContext as ReadoraApplication).database
        val lines = runCatching {
            db.updateDao().getUnread().first().take(6).map { "${it.comicTitle} — Ch. ${it.chapterNumber}" }
        }.getOrDefault(emptyList())
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Readora updates available")
            .setContentText("$unreadCount unread chapter updates found.")
            .setStyle(NotificationCompat.BigTextStyle().bigText(lines.joinToString("\n").ifBlank { "$unreadCount unread chapter updates found." }))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        manager.notify(NOTIFICATION_ID, notification)
    }
}


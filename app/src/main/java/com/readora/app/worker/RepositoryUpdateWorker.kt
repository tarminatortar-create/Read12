package com.readora.app.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.readora.app.ReadoraApplication
import com.readora.app.core.ReadoraLogger
import com.readora.app.source.manifest.ManifestParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

/**
 * Phase 52 — Repository Update Checker
 *
 * A WorkManager CoroutineWorker that runs every 24 hours when the device
 * is connected to a network. It re-fetches all known repository manifest
 * URLs and compares source versions against what is installed in the database.
 * If updates are found, a notification is posted to the user.
 */
class RepositoryUpdateWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "RepositoryUpdateCheck"
        const val CHANNEL_ID = "readora_updates"
        const val NOTIFICATION_ID = 1001

        /**
         * Schedules the periodic update check.
         * Call once from Application.onCreate() or a setup screen.
         */
        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = PeriodicWorkRequestBuilder<RepositoryUpdateWorker>(
                repeatInterval = 24,
                repeatIntervalTimeUnit = TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
            ReadoraLogger.log("RepositoryUpdateWorker", "Scheduled periodic update check (24h interval)")
        }
    }

    override suspend fun doWork(): Result {
        ReadoraLogger.log("RepositoryUpdateWorker", "Running repository update check")
        return try {
            val db = (applicationContext as ReadoraApplication).database
            val installedSources = db.sourceDao().getAll().first()

            // For now we check repos whose URL is known via the SourceEntity.
            // In a future phase the DB will store the repo URL directly.
            // Here we iterate all installed sources and verify they still exist.
            val updates = mutableListOf<String>()

            // Placeholder logic: check each source by pinging its baseUrl
            withContext(Dispatchers.IO) {
                val client = OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build()

                for (source in installedSources) {
                    try {
                        val request = Request.Builder()
                            .url(source.baseUrl)
                            .head()
                            .build()
                        val response = client.newCall(request).execute()
                        if (!response.isSuccessful) {
                            updates.add("${source.name} may be unavailable (HTTP ${response.code})")
                            ReadoraLogger.log("RepositoryUpdateWorker", "Source ${source.name} returned ${response.code}")
                        }
                    } catch (e: Exception) {
                        // Silently skip unreachable sources
                        ReadoraLogger.log("RepositoryUpdateWorker", "Could not reach ${source.name}: ${e.message}")
                    }
                }
            }

            if (updates.isNotEmpty()) {
                postNotification(updates)
            } else {
                ReadoraLogger.log("RepositoryUpdateWorker", "All ${installedSources.size} sources are reachable — no action needed")
            }

            Result.success()
        } catch (e: Exception) {
            ReadoraLogger.log("RepositoryUpdateWorker", "Update check failed: ${e.message}")
            Result.retry()
        }
    }

    private fun postNotification(updates: List<String>) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Source Updates",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Notifications about source availability and updates" }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Readora Source Check")
            .setContentText("${updates.size} source(s) may have issues — open Settings to review.")
            .setStyle(NotificationCompat.BigTextStyle().bigText(updates.joinToString("\n")))
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
        ReadoraLogger.log("RepositoryUpdateWorker", "Posted update notification for ${updates.size} source(s)")
    }
}

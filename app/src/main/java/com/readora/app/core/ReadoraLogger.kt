package com.readora.app.core

import android.util.Log
import com.readora.app.BuildConfig
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.ArrayDeque

object ReadoraLogger {
    private const val MAX_LINES = 500
    val dateFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.US)
    val lines = ArrayDeque<String>(MAX_LINES)

    @Synchronized
    fun log(tag: String, message: String, level: LogLevel = LogLevel.INFO) {
        val line = "${dateFormat.format(Date())} ${level.name}/$tag: $message"
        if (lines.size == MAX_LINES) lines.removeFirst()
        lines.addLast(line)
        if (BuildConfig.DEBUG) {
            when (level) {
                LogLevel.DEBUG -> Log.d(tag, message)
                LogLevel.INFO -> Log.i(tag, message)
                LogLevel.WARN -> Log.w(tag, message)
                LogLevel.ERROR -> Log.e(tag, message)
            }
        }
    }

    @Synchronized
    fun recentLines(): List<String> = lines.toList()

    @Synchronized
    fun clear() {
        lines.clear()
    }
}

enum class LogLevel {
    DEBUG,
    INFO,
    WARN,
    ERROR,
}

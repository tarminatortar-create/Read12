package com.readora.app.core

import java.security.MessageDigest

object AppLock {
    fun hashPin(pin: String): String {
        val normalized = pin.trim()
        val bytes = MessageDigest.getInstance("SHA-256").digest(normalized.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun verifyPin(pin: String, expectedHash: String?): Boolean {
        if (expectedHash.isNullOrBlank()) return false
        return hashPin(pin) == expectedHash
    }
}


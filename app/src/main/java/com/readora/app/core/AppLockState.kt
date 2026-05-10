package com.readora.app.core

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object AppLockState {
    private val _locked = MutableStateFlow(false)
    val locked: StateFlow<Boolean> = _locked

    fun lock() {
        _locked.value = true
    }

    fun unlock() {
        _locked.value = false
    }
}


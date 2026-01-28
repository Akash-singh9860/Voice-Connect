package com.app.voiceconnect.domain.model

sealed class CallState {
    object Idle : CallState()
    object Connecting : CallState()
    object Ringing : CallState()
    object Connected : CallState()
    data class Disconnected(val reason: String? = null) : CallState()
    data class Error(val message: String) : CallState()
}
package com.app.voiceconnect.data.remote

data class TokenResponse(
    val token: String,
    val identity: String? = null
)
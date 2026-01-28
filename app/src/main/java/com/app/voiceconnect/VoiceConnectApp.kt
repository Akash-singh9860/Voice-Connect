package com.app.voiceconnect

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * The [Application] class for the VoiceConnect app.
 * Annotated with [HiltAndroidApp] to trigger Hilt's code generation,
 * which serves as the application-level dependency container.
 */
@HiltAndroidApp
class VoiceConnectApp: Application()

package com.app.voiceconnect

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.app.voiceconnect.presentation.voice.VoiceScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * The main entry point of the VoiceConnect application.
 * This activity hosts the [VoiceScreen] and manages the lifecycle of incoming call intents.
 * It uses Hilt for dependency injection and Jetpack Compose for the UI.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    /**
     * A [MutableStateFlow] that holds the most recent [Intent] received by the activity.
     * This is used to reactively pass incoming call data to the UI layer.
     */
    private val _intentFlow = MutableStateFlow<Intent?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Capture the initial intent that started the activity (e.g., from a notification).
        _intentFlow.value = intent
        
        setContent {
            val currentIntent by _intentFlow.collectAsState()
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    VoiceScreen(currentIntent = currentIntent)
                }
            }
        }
    }

    /**
     * Called when the activity is already running and a new [Intent] is delivered to it.
     * This is crucial for handling incoming call notifications when the app is in the background or foreground.
     *
     * @param intent The new intent delivered to this activity.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Update the activity's intent and our reactive flow.
        setIntent(intent)
        _intentFlow.value = intent
    }
}

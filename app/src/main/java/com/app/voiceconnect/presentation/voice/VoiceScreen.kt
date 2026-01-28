package com.app.voiceconnect.presentation.voice

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.voiceconnect.domain.model.CallState
import com.twilio.voice.CallInvite

/**
 * The main UI entry point for the Voice call functionality.
 * This screen handles user registration, outgoing call initiation, and displays the
 * incoming call UI when a call invite is received.
 *
 * @param viewModel The [VoiceViewModel] that manages the state and logic for this screen.
 * @param currentIntent The [Intent] that might contain an incoming call invitation.
 */
@Composable
fun VoiceScreen(
    viewModel: VoiceViewModel = hiltViewModel(),
    currentIntent: Intent?
) {
    val state by viewModel.callState.collectAsState()
    val storedIdentity by viewModel.storedIdentity.collectAsState()
    val isLoggedIn = !storedIdentity.isNullOrBlank()

    // Handle incoming call invitations from the Intent.
    HandleIncomingIntent(currentIntent, viewModel)

    // Request necessary runtime permissions.
    RequestPermissions()

    Box(modifier = Modifier.fillMaxSize()) {
        if (viewModel.activeCallInvite != null) {
            // Display the full-screen incoming call UI if an invite is active.
            IncomingCallFullScreen(
                invite = viewModel.activeCallInvite!!,
                onAccept = { viewModel.acceptCall() },
                onReject = { viewModel.rejectCall() }
            )
        }
        else {
            // Display the main dashboard for login and making calls.
            MainDashboard(
                viewModel = viewModel,
                state = state,
                isLoggedIn = isLoggedIn,
                storedIdentity = storedIdentity
            )
        }
    }
}

/**
 * A full-screen UI displayed when an incoming call is received.
 * Shows the caller's identity and provides options to accept or decline the call.
 *
 * @param invite The [CallInvite] details.
 * @param onAccept Callback when the user accepts the call.
 * @param onReject Callback when the user rejects the call.
 */
@Composable
fun IncomingCallFullScreen(
    invite: CallInvite,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF232526), Color(0xFF414345))
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 64.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
                    .padding(16.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Incoming Call",
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = invite.from ?: "Unknown",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 64.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CallActionButton(
                icon = Icons.Default.CallEnd,
                color = Color(0xFFFF3B30),
                label = "Decline",
                onClick = onReject
            )
            CallActionButton(
                icon = Icons.Default.Call,
                color = Color(0xFF34C759),
                label = "Accept",
                onClick = onAccept
            )
        }
    }
}

/**
 * A reusable circular action button used in call screens.
 *
 * @param icon The [ImageVector] to display as the button icon.
 * @param color The background color of the button.
 * @param label The text label displayed below the button.
 * @param onClick Callback triggered when the button is clicked.
 */
@Composable
fun CallActionButton(
    icon: ImageVector,
    color: Color,
    label: String,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = onClick,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = color),
            modifier = Modifier.size(72.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, color = Color.White, style = MaterialTheme.typography.bodyMedium)
    }
}

/**
 * The main dashboard UI shown when the user is not in a call.
 * Handles user identity setup and provides the dialer interface.
 *
 * @param viewModel The [VoiceViewModel] for managing actions.
 * @param state The current [CallState].
 * @param isLoggedIn Whether the user has a stored identity.
 * @param storedIdentity The identity string of the logged-in user.
 */
@Composable
fun MainDashboard(
    viewModel: VoiceViewModel,
    state: CallState,
    isLoggedIn: Boolean,
    storedIdentity: String?
) {
    var inputIdentity by remember { mutableStateOf("") }
    var targetName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Twilio Voice",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(48.dp))
        if (!isLoggedIn) {
            Text(text = "Identity Setup", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = inputIdentity,
                onValueChange = { inputIdentity = it },
                label = { Text("Enter your ID (e.g. mobile)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { if (inputIdentity.isNotBlank()) viewModel.initialize(inputIdentity) },
                enabled = inputIdentity.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Login & Register")
            }
        } else {
            Text("Logged in as: $storedIdentity", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(12.dp))
            StatusIndicator(state)
            Spacer(modifier = Modifier.height(32.dp))

            if (state is CallState.Connected || state is CallState.Ringing || state is CallState.Connecting) {
                Button(
                    onClick = { viewModel.disconnect() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("End Call")
                }
            } else {
                OutlinedTextField(
                    value = targetName,
                    onValueChange = { targetName = it },
                    label = { Text("Call who? (e.g. emulator)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.makeCall(targetName) },
                    enabled = state is CallState.Idle || state is CallState.Disconnected || state is CallState.Error,
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("Call")
                }
            }
        }
    }
}

/**
 * Displays a text indicator for the current [CallState].
 *
 * @param state The current call state to display.
 */
@Composable
fun StatusIndicator(state: CallState) {
    val (text, color) = when (state) {
        is CallState.Idle -> "Ready to Call" to MaterialTheme.colorScheme.onSurface
        is CallState.Connecting -> "Connecting..." to Color.Cyan
        is CallState.Ringing -> "Ringing..." to Color.Cyan
        is CallState.Connected -> "In Call" to Color.Green
        is CallState.Disconnected -> "Call Ended" to MaterialTheme.colorScheme.onSurface
        is CallState.Error -> "Error: ${(state as CallState.Error).message}" to Color.Red
    }
    Text(text = text, style = MaterialTheme.typography.bodyLarge, color = color)
}

/**
 * Extracts call invitation data from the incoming [Intent] and notifies the [VoiceViewModel].
 *
 * @param intent The [Intent] received by the activity.
 * @param viewModel The [VoiceViewModel] to handle the call invitation.
 */
@Composable
fun HandleIncomingIntent(intent: Intent?, viewModel: VoiceViewModel) {
    LaunchedEffect(intent) {
        intent?.let {
            if (it.action == "ACTION_INCOMING_CALL") {
                val invite = if (Build.VERSION.SDK_INT >= 33) {
                    it.getParcelableExtra("INCOMING_CALL_INVITE", CallInvite::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    it.getParcelableExtra("INCOMING_CALL_INVITE")
                }
                if (invite != null) viewModel.handleIncomingCall(invite)
            }
        }
    }
}

/**
 * Requests necessary runtime permissions for the Voice call functionality.
 * Asks for RECORD_AUDIO and POST_NOTIFICATIONS (on Android 13+).
 */
@Composable
fun RequestPermissions() {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}
    LaunchedEffect(Unit) {
        val perms = mutableListOf(Manifest.permission.RECORD_AUDIO)
        if (Build.VERSION.SDK_INT >= 33) perms.add(Manifest.permission.POST_NOTIFICATIONS)
        launcher.launch(perms.toTypedArray())
    }
}

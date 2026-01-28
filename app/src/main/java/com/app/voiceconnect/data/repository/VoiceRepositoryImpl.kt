package com.app.voiceconnect.data.repository

import android.content.Context
import android.media.AudioManager
import android.util.Log
import com.app.voiceconnect.data.remote.TwilioApi
import com.app.voiceconnect.domain.model.CallState
import com.app.voiceconnect.domain.repository.VoiceRepository
import com.google.firebase.messaging.FirebaseMessaging
import com.twilio.voice.AcceptOptions
import com.twilio.voice.Call
import com.twilio.voice.CallException
import com.twilio.voice.CallInvite
import com.twilio.voice.ConnectOptions
import com.twilio.voice.RegistrationException
import com.twilio.voice.RegistrationListener
import com.twilio.voice.Voice
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * Implementation of [VoiceRepository] that manages Twilio Voice calls.
 * This class handles registration for incoming calls, initiating outgoing calls,
 * and managing the lifecycle and state of active calls.
 *
 * @property appContext The application context used for Twilio Voice and Audio Management.
 * @property api The API service to fetch Twilio access tokens.
 */
class VoiceRepositoryImpl @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val api: TwilioApi
) : VoiceRepository {

    override val context: Context
        get() = appContext
    private val _callState = MutableStateFlow<CallState>(CallState.Idle)

    /**
     * A [Flow] representing the current state of the voice call.
     */
    override val callState = _callState.asStateFlow()
    private var activeCall: Call? = null
    private var accessToken: String? = null
    private var audioManager: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    /**
     * Connects to the Twilio service using the provided [identity].
     * Fetches an access token and registers the device for incoming calls via FCM.
     *
     * @param identity The unique identifier for the user.
     */
    override suspend fun connect(identity: String) {
        try {
            _callState.value = CallState.Connecting
            val response = api.getAccessToken(identity)
            accessToken = response.token
            FirebaseMessaging.getInstance().token.addOnSuccessListener { fcmToken ->
                Log.d("VoiceRepo", "Registering with FCM: $fcmToken")
                Voice.register(accessToken!!, Voice.RegistrationChannel.FCM, fcmToken, object : RegistrationListener {
                    override fun onRegistered(accessToken: String, fcmToken: String) {
                        Log.d("VoiceRepo", "Successfully Registered for Incoming Calls")
                        _callState.value = CallState.Idle
                    }

                    override fun onError(error: RegistrationException, accessToken: String, fcmToken: String) {
                        _callState.value = CallState.Error("Registration Failed: ${error.message}")
                    }
                })
            }

        } catch (e: Exception) {
            _callState.value = CallState.Error("Login Failed: ${e.message}")
        }
    }

    /**
     * Initiates an outgoing call to the specified recipient.
     *
     * @param to The identity or phone number of the recipient.
     */
    override fun makeCall(to: String) {
        val token = accessToken
        if (token == null) {
            _callState.value = CallState.Error("Not logged in. Restart app.")
            return
        }
        Log.d("VoiceRepo", "Starting call to: $to")
        val params = mapOf("To" to to)
        val connectOptions = ConnectOptions.Builder(token)
            .params(params)
            .build()
        activeCall = Voice.connect(context, connectOptions, callListener)
        setAudioFocus(true)
    }

    /**
     * Disconnects the currently active call.
     */
    override fun disconnect() {
        activeCall?.disconnect()
        activeCall = null
        setAudioFocus(false)
        _callState.value = CallState.Disconnected("Call ended by user")
    }

    /**
     * Accepts an incoming call invitation.
     *
     * @param invite The incoming call invitation from Twilio.
     */
    override fun acceptIncomingCall(invite: CallInvite) {
        val options = AcceptOptions.Builder().build()
        invite.accept(context, options, callListener)
        setAudioFocus(true)
    }

    /**
     * Listener that monitors the state of a [Call] and updates the [_callState] accordingly.
     */
    private val callListener = object : Call.Listener {
        override fun onConnectFailure(call: Call, error: CallException) {
            setAudioFocus(false)
            Log.e("VoiceRepo", "Connect Failure: ${error.message}")
            _callState.value = CallState.Error("Failed: ${error.message}")
        }
        override fun onConnected(call: Call) {
            activeCall = call
            _callState.value = CallState.Connected
        }
        override fun onDisconnected(call: Call, error: CallException?) {
            setAudioFocus(false)
            activeCall = null
            if (error != null) {
                Log.e("VoiceRepo", "Disconnected with error: ${error.message}")
                _callState.value = CallState.Error("Disconnected: ${error.message}")
            } else {
                _callState.value = CallState.Disconnected("Call ended")
            }
        }
        override fun onRinging(call: Call) {
            _callState.value = CallState.Ringing
        }
        override fun onReconnecting(call: Call, error: CallException) {
            _callState.value = CallState.Connecting
        }
        override fun onReconnected(call: Call) {
            _callState.value = CallState.Connected
        }
    }

    /**
     * Configures the [AudioManager] for voice communication or resets it to normal.
     *
     * @param focus True to request audio focus for a call, false to abandon it.
     */
    private fun setAudioFocus(focus: Boolean) {
        if (focus) {
            audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
            audioManager.requestAudioFocus(null, AudioManager.STREAM_VOICE_CALL, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
        } else {
            audioManager.mode = AudioManager.MODE_NORMAL
            audioManager.abandonAudioFocus(null)
        }
    }
}

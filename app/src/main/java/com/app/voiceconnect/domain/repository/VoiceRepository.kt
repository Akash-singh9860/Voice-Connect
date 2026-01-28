package com.app.voiceconnect.domain.repository

import android.content.Context
import com.app.voiceconnect.domain.model.CallState
import com.twilio.voice.CallInvite
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining the contract for voice communication operations.
 * Handles connecting to the service, initiating calls, and managing call states.
 */
interface VoiceRepository {
    /**
     * A [Flow] that emits the current state of the voice call.
     */
    val callState: Flow<CallState>

    /**
     * The application [Context] used for internal operations.
     */
    val context: Context

    /**
     * Connects to the voice service using the provided [identity].
     * Typically involves fetching an access token and registering for incoming calls.
     *
     * @param identity The unique identifier for the user.
     */
    suspend fun connect(identity: String)

    /**
     * Initiates an outgoing call to the specified recipient.
     *
     * @param to The identity or destination address for the call.
     */
    fun makeCall(to: String)

    /**
     * Disconnects the currently active call or cancels a pending call.
     */
    fun disconnect()

    /**
     * Accepts an incoming call invitation.
     *
     * @param invite The [CallInvite] received from the service.
     */
    fun acceptIncomingCall(invite: CallInvite)
}

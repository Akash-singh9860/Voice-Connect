package com.app.voiceconnect.presentation.voice

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.voiceconnect.data.local.UserPreferences
import com.app.voiceconnect.domain.model.CallState
import com.app.voiceconnect.domain.repository.VoiceRepository
import com.twilio.voice.CallInvite
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Voice screen.
 * Manages the state of voice calls, user identity, and interactions with the [VoiceRepository].
 *
 * @property repository The repository handling Twilio Voice operations.
 * @property userPreferences The data store for persisting user identity.
 */
@HiltViewModel
class VoiceViewModel @Inject constructor(
    private val repository: VoiceRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    /**
     * A [StateFlow] representing the current [CallState], synchronized with the repository.
     */
    val callState = repository.callState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        CallState.Idle
    )

    /**
     * The current incoming call invitation, if any.
     */
    var activeCallInvite by mutableStateOf<CallInvite?>(null)
        private set

    private val _storedIdentity = MutableStateFlow<String?>(null)

    /**
     * A [StateFlow] that emits the currently logged-in user identity.
     */
    val storedIdentity = _storedIdentity.asStateFlow()

    init {
        // Automatically connect if an identity is already saved.
        viewModelScope.launch {
            val savedId = userPreferences.userIdentity.first()
            if (!savedId.isNullOrBlank()) {
                _storedIdentity.value = savedId
                repository.connect(savedId)
            }
        }
    }

    /**
     * Initializes the voice service with the given [identity] and saves it.
     *
     * @param identity The unique identifier for the user.
     */
    fun initialize(identity: String) {
        viewModelScope.launch {
            userPreferences.saveIdentity(identity)
            _storedIdentity.value = identity
            repository.connect(identity)
        }
    }

    /**
     * Logs out the current user and clears stored preferences.
     */
    fun logout() {
        viewModelScope.launch {
            userPreferences.clear()
            _storedIdentity.value = null
        }
    }

    /**
     * Initiates an outgoing call to the specified recipient.
     *
     * @param to The identity of the recipient.
     */
    fun makeCall(to: String) {
        if (to.isNotBlank()) {
            repository.makeCall(to)
        }
    }

    /**
     * Disconnects the active call.
     */
    fun disconnect() {
        repository.disconnect()
    }

    /**
     * Sets the [activeCallInvite] when an incoming call is received.
     *
     * @param invite The incoming call invitation.
     */
    fun handleIncomingCall(invite: CallInvite) {
        activeCallInvite = invite
    }

    /**
     * Accepts the current [activeCallInvite].
     */
    fun acceptCall() {
        activeCallInvite?.let { invite ->
            repository.acceptIncomingCall(invite)
        }
        activeCallInvite = null
    }

    /**
     * Rejects the current [activeCallInvite].
     */
    fun rejectCall() {
        activeCallInvite?.reject(repository.context)
        activeCallInvite = null
    }
}

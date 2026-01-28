package com.app.voiceconnect.presentation.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.voiceconnect.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.twilio.voice.CallException
import com.twilio.voice.CallInvite
import com.twilio.voice.CancelledCallInvite
import com.twilio.voice.MessageListener
import com.twilio.voice.Voice

/**
 * Service that handles Firebase Cloud Messaging (FCM) messages for Twilio Voice.
 * This service is responsible for receiving call invitations and cancellation events
 * from Twilio and notifying the user.
 */
class VoiceFirebaseMessagingService : FirebaseMessagingService() {

    /**
     * Called when a new FCM registration token is generated.
     * Broadcasts the new token locally so that the repository can update its registration with Twilio.
     *
     * @param token The new FCM registration token.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Refreshed token: $token")
        val intent = Intent("ACTION_FCM_TOKEN_UPDATED")
        intent.putExtra("token", token)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    /**
     * Called when an FCM message is received.
     * It checks if the message is a Twilio Voice message and handles it accordingly.
     *
     * @param remoteMessage The message received from Firebase Cloud Messaging.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCM", "Message Received")
        val isVoiceMessage = Voice.handleMessage(this, remoteMessage.data, object : MessageListener {
            override fun onCallInvite(callInvite: CallInvite) {
                Log.d("FCM", "Incoming Call from: ${callInvite.from}")
                showIncomingCallNotification(callInvite)
            }

            override fun onCancelledCallInvite(cancelledCallInvite: CancelledCallInvite, exception: CallException?) {
                Log.d("FCM", "Call Cancelled")
                val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(NOTIFICATION_ID)
            }
        })

        if (!isVoiceMessage) {
            // Handle other non-voice FCM messages here if needed.
        }
    }

    /**
     * Displays a high-priority notification for an incoming voice call.
     * The notification is configured with a full-screen intent to ensure it captures
     * the user's attention, especially when the device is locked.
     *
     * @param callInvite The incoming call invitation data from Twilio.
     */
    private fun showIncomingCallNotification(callInvite: CallInvite) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "voice_call_channel"
        val intent = Intent(this, MainActivity::class.java).apply {
            action = "ACTION_INCOMING_CALL"
            putExtra("INCOMING_CALL_INVITE", callInvite)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val channel = NotificationChannel(
            channelId,
            "Incoming Calls",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Channel for incoming VoIP calls"
            enableVibration(true)
        }
        notificationManager.createNotificationChannel(channel)
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_menu_call)
            .setContentTitle("Incoming Call")
            .setContentText("Call from ${callInvite.from}")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setAutoCancel(true)
            .setOngoing(true)
            .setFullScreenIntent(pendingIntent, true)
            .setContentIntent(pendingIntent)
            .build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        /**
         * Unique identifier for the incoming call notification.
         */
        const val NOTIFICATION_ID = 12345
    }
}

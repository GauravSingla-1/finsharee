package com.finshare.android.data.remote

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.finshare.android.MainActivity
import com.finshare.android.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationManager: NotificationManager

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Handle FCM messages here
        remoteMessage.notification?.let { notification ->
            showNotification(
                title = notification.title ?: "FinShare",
                body = notification.body ?: "",
                data = remoteMessage.data
            )
        }

        // Handle data payload
        if (remoteMessage.data.isNotEmpty()) {
            handleDataMessage(remoteMessage.data)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Send token to server
        sendTokenToServer(token)
    }

    private fun showNotification(title: String, body: String, data: Map<String, String>) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Add extra data for navigation
            data.forEach { (key, value) ->
                putExtra(key, value)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "finshare_notifications"
        createNotificationChannel(channelId)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    private fun createNotificationChannel(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "FinShare Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for FinShare app"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun handleDataMessage(data: Map<String, String>) {
        when (data["type"]) {
            "expense_added" -> {
                // Handle expense added notification
                val groupId = data["groupId"]
                val expenseId = data["expenseId"]
                // Store notification for in-app display
            }
            "settlement_request" -> {
                // Handle settlement request
                val settlementId = data["settlementId"]
                // Show high-priority notification
            }
            "group_invitation" -> {
                // Handle group invitation
                val groupId = data["groupId"]
                val inviterId = data["inviterId"]
                // Show invitation notification
            }
        }
    }

    private fun sendTokenToServer(token: String) {
        // TODO: Send token to backend server
        // This should be called when user is authenticated
    }
}
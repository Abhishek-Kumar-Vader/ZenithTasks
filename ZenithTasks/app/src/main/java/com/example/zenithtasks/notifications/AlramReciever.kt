package com.example.zenithtasks.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.zenithtasks.R // Make sure you have a notification icon in res/drawable

class AlarmReceiver : BroadcastReceiver() {

    private val CHANNEL_ID = "task_notification_channel"
    private val CHANNEL_NAME = "Task Reminders"
    private val NOTIFICATION_ID_BASE = 1000 // Base ID for notifications

    override fun onReceive(context: Context, intent: Intent?) {
        intent?.let {
            val taskId = it.getLongExtra("TASK_ID", -1L)
            val title = it.getStringExtra("TASK_TITLE") ?: "Task Reminder"
            val description = it.getStringExtra("TASK_DESCRIPTION") ?: "Your task is due!"

            if (taskId != -1L) {
                Log.d("AlarmReceiver", "Received alarm for Task ID: $taskId, Title: $title")
                showNotification(context, taskId.toInt(), title, description)
            } else {
                Log.e("AlarmReceiver", "Received alarm with invalid Task ID.")
            }
        }
    }

    private fun showNotification(context: Context, notificationId: Int, title: String, description: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create Notification Channel for Android O (API 26) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH // High importance for reminders
            ).apply {
                this.description = "Channel for task reminder notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Build the notification
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Use your app's notification icon
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Match channel importance
            .setAutoCancel(true) // Notification disappears when user taps it

        notificationManager.notify(notificationId, builder.build()) // Use task ID as notification ID
    }
}
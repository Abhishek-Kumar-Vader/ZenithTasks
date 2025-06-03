package com.example.zenithtasks.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class ReminderBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra("TASK_ID", -1L)
        val taskTitle = intent.getStringExtra("TASK_TITLE")
        val taskDescription = intent.getStringExtra("TASK_DESCRIPTION") ?: "No description provided."

        Log.d("ReminderReceiver", "Alarm received for Task ID: $taskId, Title: $taskTitle")

        if (taskId != -1L && taskTitle != null) {
            val notificationHelper = NotificationHelper(context)
            notificationHelper.showNotification(
                taskId = taskId,
                title = "Task Reminder: $taskTitle",
                message = taskDescription
            )
        } else {
            Log.e("ReminderReceiver", "Received alarm with invalid Task ID or Title. Task ID: $taskId, Title: $taskTitle")
        }
    }
}
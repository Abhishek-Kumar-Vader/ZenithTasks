package com.example.zenithtasks.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.zenithtasks.data.Task
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

interface AlarmScheduler {
    fun scheduleAlarm(task: Task) // <--- RENAMED
    fun cancelAlarm(task: Task)   // <--- RENAMED
}

@Singleton
class AlarmSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun scheduleAlarm(task: Task) { // <--- RENAMED
        // ... (rest of your schedule function, it should be correct from previous fixes)
        // Ensure this part is consistent with the latest logic for PendingIntent flags
        task.dueDate?.let { dueDate ->
            val calendar = Calendar.getInstance().apply {
                time = dueDate
            }
            if (calendar.timeInMillis > System.currentTimeMillis()) {
                val intent = Intent(context, AlarmReceiver::class.java).apply {
                    putExtra("TASK_TITLE", task.title)
                    putExtra("TASK_DESCRIPTION", task.description)
                    putExtra("TASK_ID", task.id)
                }
                val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    task.id.toInt(),
                    intent,
                    pendingIntentFlags
                )
                Log.d("AlarmScheduler", "Scheduling alarm for Task: '${task.title}' (ID: ${task.id}) at ${dueDate}")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }
            } else {
                Log.d("AlarmScheduler", "Task due date is in the past or present, not scheduling: '${task.title}' (ID: ${task.id})")
            }
        } ?: run {
            Log.d("AlarmScheduler", "Task due date is null, not scheduling alarm for: '${task.title}' (ID: ${task.id})")
        }
    }

    override fun cancelAlarm(task: Task) { // <--- RENAMED
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("TASK_ID", task.id)
        }
        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.toInt(),
            intent,
            pendingIntentFlags
        )
        alarmManager.cancel(pendingIntent)
        Log.d("AlarmScheduler", "Cancelled alarm for Task: '${task.title}' (ID: ${task.id})")
    }
}
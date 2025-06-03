package com.example.zenithtasks.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.zenithtasks.data.Task
import dagger.hilt.android.qualifiers.ApplicationContext // NEW IMPORT (if not already there)
import java.util.Calendar // NEW IMPORT (if not already there)
import java.util.Date
import javax.inject.Inject // NEW IMPORT
import javax.inject.Singleton // NEW IMPORT

// Interface for AlarmScheduler
interface AlarmScheduler {
    fun schedule(task: Task)
    fun cancel(task: Task)
}

// Implementation of AlarmScheduler
@Singleton // <--- ADD THIS ANNOTATION
class AlarmSchedulerImpl @Inject constructor( // <--- ADD @Inject TO THE CONSTRUCTOR
    @ApplicationContext private val context: Context // <--- Requires Hilt's @ApplicationContext
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun schedule(task: Task) {
        // ... (rest of your schedule function, it should be correct from previous fixes)
    }

    override fun cancel(task: Task) {
        // ... (rest of your cancel function, it should be correct from previous fixes)
    }
}
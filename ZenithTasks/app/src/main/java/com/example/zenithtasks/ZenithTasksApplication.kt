package com.example.zenithtasks

import android.app.Application
import com.example.zenithtasks.data.AppDatabase // Adjust import if your AppDatabase is in a different package
import com.example.zenithtasks.data.TaskDao // <-- Add this line
import com.example.zenithtasks.data.TaskRepository
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ZenithTasksApplication : Application() {

    // Using lazy delegate to ensure the database instance is created only when needed
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    val repository: TaskRepository by lazy { TaskRepository(database.taskDao()) }
}
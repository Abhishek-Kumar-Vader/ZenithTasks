package com.example.zenithtasks.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.zenithtasks.data.TaskRepository

class TaskViewModelFactory(private val taskDao: TaskRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST") // Room usually handles this safely
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            return TaskViewModel(taskDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
package com.example.zenithtasks.data

import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {

    // --- Data Streams from DAO ---
    // Expose Flow from DAO to ViewModel/UI
    fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks()
    }

    fun getTaskById(taskId: Long): Flow<Task?> {
        return taskDao.getTaskById(taskId)
    }

    // --- CRUD Operations (suspend functions for background execution) ---

    suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    // This is the primary method for deletion we'll use first
    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    suspend fun deleteAllTasks() {
        taskDao.deleteAllTasks()
    }
}
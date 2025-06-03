package com.example.zenithtasks.data

import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {

    fun getTaskById(taskId: Long): Flow<Task?> {
        return taskDao.getTaskById(taskId)
    }

    suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    fun getTasksByStatus(status: TaskStatus): Flow<List<Task>> {
        return taskDao.getTasksByStatus(status)
    }

    suspend fun upsert(task: Task): Long {
        return if (task.id == 0L) {
            taskDao.insertTask(task) // Returns the new row ID (Long)
        } else {
            taskDao.updateTask(task) // Returns Int (rows affected), but we need task.id for upsert's return
            task.id // <--- Return the existing ID for updates
        }
    }
}
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

    // MODIFIED: This now performs a soft delete (archives the task)
    suspend fun archiveTask(task: Task) {
        val archivedTask = task.copy(status = TaskStatus.ARCHIVED)
        taskDao.updateTask(archivedTask)
    }

    // NEW: Function to permanently delete a task (only if needed, e.g., from archive screen)
    suspend fun permanentlyDeleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    // NEW: Function to restore an archived task
    suspend fun restoreTask(task: Task) {
        // When restoring, set status back to TO_DO (or IN_PROGRESS if that makes more sense for your app flow)
        val restoredTask = task.copy(status = TaskStatus.TODO)
        taskDao.updateTask(restoredTask)
    }

    fun getTasksByStatus(status: TaskStatus): Flow<List<Task>> {
        return taskDao.getTasksByStatus(status)
    }

    // NEW: Function to get all archived tasks
    fun getArchivedTasks(): Flow<List<Task>> {
        return taskDao.getArchivedTasks()
    }

    suspend fun upsert(task: Task): Long {
        return if (task.id == 0L) {
            taskDao.insertTask(task) // Returns the new row ID (Long)
        } else {
            taskDao.updateTask(task) // Returns Int (rows affected), but we need task.id for upsert's return
            task.id // <--- Return the existing ID for updates
        }
    }

    fun getAllTasks(): Flow<List<Task>> { // NEW: Function to get all tasks (consider if still needed or just use getTasksByStatus)
        return taskDao.getAllTasks()
    }
}
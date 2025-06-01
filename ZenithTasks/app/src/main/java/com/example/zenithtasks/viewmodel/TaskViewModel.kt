package com.example.zenithtasks.viewmodel

import android.util.Log // Added for logging
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zenithtasks.data.Task
import com.example.zenithtasks.data.TaskRepository
import com.example.zenithtasks.data.TaskStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first // <--- IMPORTANT: ADD THIS IMPORT

class TaskViewModel(private val taskRepository: TaskRepository) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _currentTask = MutableStateFlow<Task?>(null)
    val currentTask: StateFlow<Task?> = _currentTask.asStateFlow()

    init {
        viewModelScope.launch {
            taskRepository.getAllTasks().collect { tasksList ->
                // When tasks are collected, ensure they are ordered by orderIndex
                _tasks.value = tasksList.sortedBy { it.orderIndex }
            }
        }
    }

    fun getTask(taskId: Long) {
        viewModelScope.launch {
            _currentTask.value = taskRepository.getTaskById(taskId).firstOrNull()
        }
    }

    // Consolidated saveTask function (insert or update)
    fun saveTask(task: Task) {
        viewModelScope.launch {
            if (task.id == 0L) { // If ID is 0, it's a new task
                // For new tasks, set orderIndex to the end of its column
                val tasksInStatus = tasks.value.filter { it.status == task.status }
                val newOrderIndex = tasksInStatus.size
                taskRepository.insertTask(task.copy(orderIndex = newOrderIndex))
            } else { // Otherwise, it's an existing task to update
                taskRepository.updateTask(task)
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
            // After deleting, re-index the remaining tasks in that status to maintain order
            reindexTasksInStatus(task.status)
        }
    }

    fun clearCurrentTask() {
        _currentTask.value = null
    }

    fun updateTaskCompletion(taskId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            val task = taskRepository.getTaskById(taskId).firstOrNull()
            task?.let {
                taskRepository.updateTask(it.copy(isCompleted = isCompleted))
            }
        }
    }

    fun updateTaskStatus(taskId: Long, newStatus: TaskStatus) {
        viewModelScope.launch {
            val task = taskRepository.getTaskById(taskId).firstOrNull()
            task?.let {
                taskRepository.updateTask(it.copy(status = newStatus))
                // Re-index both old and new status columns
                reindexTasksInStatus(it.status) // Old status
                reindexTasksInStatus(newStatus) // New status
            }
        }
    }

    /**
     * Moves a task to a new status and/or position within a column.
     * This function handles re-indexing of affected columns.
     */
    fun moveTaskToPosition(taskId: Long, newStatus: TaskStatus, newIndex: Int) = viewModelScope.launch {
        // 1. Get the task that was dragged
        val draggedTask = taskRepository.getTaskById(taskId).firstOrNull() ?: return@launch
        val oldStatus = draggedTask.status

        // 2. If status changed, remove from old column's order
        if (oldStatus != newStatus) {
            val tasksInOldStatus = tasks.value
                .filter { it.status == oldStatus && it.id != draggedTask.id } // Exclude the moved task
                .sortedBy { it.orderIndex } // Ensure current order
                .toMutableList()
            tasksInOldStatus.forEachIndexed { index, task ->
                if (task.orderIndex != index) {
                    taskRepository.updateTask(task.copy(orderIndex = index))
                }
            }
        }

        // 3. Get all tasks for the new status (excluding the dragged task's old version)
        val tasksInTargetStatus = tasks.value
            .filter { it.status == newStatus && it.id != draggedTask.id } // Filter for the target status, exclude dragged task
            .sortedBy { it.orderIndex } // Ensure current order
            .toMutableList()

        // 4. Insert the dragged task into its new position in this temporary list
        val insertAt = minOf(newIndex, tasksInTargetStatus.size)
        tasksInTargetStatus.add(insertAt, draggedTask.copy(status = newStatus))

        // 5. Update the orderIndex for all tasks in this potentially modified list
        // This is crucial for persisting the new order
        tasksInTargetStatus.forEachIndexed { index, task ->
            if (task.orderIndex != index || task.status != newStatus) { // Only update if order or status changed
                taskRepository.updateTask(task.copy(orderIndex = index, status = newStatus))
            }
        }

        Log.d("TaskViewModel", "Task ${draggedTask.title} moved to status $newStatus at index $newIndex. Database updated.")
    }

    /**
     * Helper function to re-index tasks within a specific status column.
     * Call this after adding, deleting, or changing status of a task.
     */
    private fun reindexTasksInStatus(status: TaskStatus) = viewModelScope.launch {
        val tasksToReindex = tasks.value
            .filter { it.status == status }
            .sortedBy { it.orderIndex }
            .toMutableList()

        tasksToReindex.forEachIndexed { index, task ->
            if (task.orderIndex != index) {
                taskRepository.updateTask(task.copy(orderIndex = index))
            }
        }
        Log.d("TaskViewModel", "Re-indexed tasks for status: $status")
    }
}
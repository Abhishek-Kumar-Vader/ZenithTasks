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
import dagger.hilt.android.lifecycle.HiltViewModel // <--- ADD THIS IMPORT
import javax.inject.Inject // <--- ADD THIS IMPORT

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

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
                Log.d("TaskViewModel", "New task added: ${task.title}, assigned orderIndex: $newOrderIndex")
            } else { // Otherwise, it's an existing task to update
                taskRepository.updateTask(task)
                Log.d("TaskViewModel", "Existing task updated: ${task.title}")
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
            Log.d("TaskViewModel", "Task deleted: ${task.title}. Re-indexing status: ${task.status}")
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
                Log.d("TaskViewModel", "Task completion updated for ${it.title} to $isCompleted")
            }
        }
    }

    fun updateTaskStatus(taskId: Long, newStatus: TaskStatus) {
        viewModelScope.launch {
            val task = taskRepository.getTaskById(taskId).firstOrNull()
            task?.let {
                val oldStatus = it.status
                taskRepository.updateTask(it.copy(status = newStatus))
                Log.d("TaskViewModel", "Task status updated for ${it.title} from $oldStatus to $newStatus")
                // Re-index both old and new status columns
                reindexTasksInStatus(oldStatus) // Old status
                reindexTasksInStatus(newStatus) // New status
            }
        }
    }

    /**
     * Moves a task to a new status and/or position within a column.
     * This function handles re-indexing of affected columns.
     */
    fun moveTaskToPosition(taskId: Long, newStatus: TaskStatus, newIndex: Int) = viewModelScope.launch {
        Log.d("TaskViewModel", "moveTaskToPosition called for taskId: $taskId, newStatus: $newStatus, newIndex: $newIndex")
        val draggedTask = taskRepository.getTaskById(taskId).firstOrNull() ?: run {
            Log.e("TaskViewModel", "Dragged task with ID $taskId not found.")
            return@launch
        }
        val oldStatus = draggedTask.status
        Log.d("TaskViewModel", "Dragged task: ${draggedTask.title} (ID: ${draggedTask.id}), Old Status: $oldStatus")

        // 2. If status changed, remove from old column's order
        if (oldStatus != newStatus) {
            Log.d("TaskViewModel", "Status changed from $oldStatus to $newStatus. Re-indexing old column.")
            // Get current tasks for old status, excluding the task that moved out
            val tasksInOldStatus = tasks.value
                .filter { it.status == oldStatus && it.id != draggedTask.id }
                .sortedBy { it.orderIndex } // Ensure current order
                .toMutableList()
            tasksInOldStatus.forEachIndexed { index, task ->
                if (task.orderIndex != index) { // Only update if order changed
                    taskRepository.updateTask(task.copy(orderIndex = index))
                    Log.d("TaskViewModel", "Updated order for task ${task.title} (ID: ${task.id}) in $oldStatus to index $index")
                }
            }
        }

        // 3. Get all tasks for the new status (excluding the dragged task if it was already filtered in step 2 or is being moved within the same status)
        Log.d("TaskViewModel", "Preparing tasks for new status: $newStatus")
        val tasksInTargetStatus = tasks.value
            .filter { it.status == newStatus && it.id != draggedTask.id } // Filter for the target status, exclude dragged task's old version
            .sortedBy { it.orderIndex } // Ensure current order
            .toMutableList()

        // 4. Insert the dragged task into its new position in this temporary list
        val insertAt = minOf(newIndex, tasksInTargetStatus.size)
        tasksInTargetStatus.add(insertAt, draggedTask.copy(status = newStatus)) // Create a copy with the new status
        Log.d("TaskViewModel", "Inserted dragged task ${draggedTask.title} (ID: ${draggedTask.id}) into $newStatus at visual index $insertAt")

        // 5. Update the orderIndex for all tasks in this potentially modified list
        // This is crucial for persisting the new order
        tasksInTargetStatus.forEachIndexed { index, task ->
            if (task.orderIndex != index || task.status != newStatus) { // Only update if order or status changed
                taskRepository.updateTask(task.copy(orderIndex = index, status = newStatus))
                Log.d("TaskViewModel", "Final DB update for task ${task.title} (ID: ${task.id}) in $newStatus: orderIndex $index, status $newStatus")
            }
        }
        Log.d("TaskViewModel", "Task ${draggedTask.title} (ID: ${draggedTask.id}) move process completed. UI should recompose.")
    }

    /**
     * Helper function to re-index tasks within a specific status column.
     * Call this after adding, deleting, or changing status of a task.
     */
    private fun reindexTasksInStatus(status: TaskStatus) = viewModelScope.launch {
        Log.d("TaskViewModel", "Re-indexing tasks for status: $status")
        val tasksToReindex = tasks.value
            .filter { it.status == status }
            .sortedBy { it.orderIndex }
            .toMutableList()

        tasksToReindex.forEachIndexed { index, task ->
            if (task.orderIndex != index) { // Only update if the order has actually changed
                taskRepository.updateTask(task.copy(orderIndex = index))
                Log.d("TaskViewModel", "Updated order for task ${task.title} (ID: ${task.id}) in $status to index $index during re-index.")
            }
        }
        Log.d("TaskViewModel", "Re-indexing for status: $status completed.")
    }
}
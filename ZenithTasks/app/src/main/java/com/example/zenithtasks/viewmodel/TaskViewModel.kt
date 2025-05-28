package com.example.zenithtasks.viewmodel

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

class TaskViewModel(private val taskRepository: TaskRepository) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _currentTask = MutableStateFlow<Task?>(null)
    val currentTask: StateFlow<Task?> = _currentTask.asStateFlow()

    init {
        viewModelScope.launch {
            taskRepository.getAllTasks().collect { tasksList ->
                _tasks.value = tasksList
            }
        }
    }

    // FIXED: Better error handling and flow collection
    fun getTask(taskId: Long) {
        viewModelScope.launch {
            try {
                // Collect the task from the repository
                taskRepository.getTaskById(taskId).collect { task ->
                    _currentTask.value = task
                    // Only collect the first emission to avoid continuous updates
                    if (task != null) {
                        return@collect
                    }
                }
            } catch (e: Exception) {
                // Handle any potential errors
                android.util.Log.e("TaskViewModel", "Error fetching task with ID $taskId", e)
                _currentTask.value = null
            }
        }
    }

    fun saveTask(task: Task) {
        viewModelScope.launch {
            try {
                if (task.id == 0L) {
                    taskRepository.insertTask(task)
                } else {
                    taskRepository.updateTask(task)
                }
            } catch (e: Exception) {
                android.util.Log.e("TaskViewModel", "Error saving task", e)
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            try {
                taskRepository.deleteTask(task)
            } catch (e: Exception) {
                android.util.Log.e("TaskViewModel", "Error deleting task", e)
            }
        }
    }

    fun clearCurrentTask() {
        _currentTask.value = null
    }

    fun updateTaskCompletion(taskId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            try {
                val task = taskRepository.getTaskById(taskId).firstOrNull()
                task?.let {
                    taskRepository.updateTask(it.copy(isCompleted = isCompleted))
                }
            } catch (e: Exception) {
                android.util.Log.e("TaskViewModel", "Error updating task completion", e)
            }
        }
    }

    fun updateTaskStatus(taskId: Long, newStatus: TaskStatus) {
        viewModelScope.launch {
            try {
                val task = taskRepository.getTaskById(taskId).firstOrNull()
                task?.let {
                    taskRepository.updateTask(it.copy(status = newStatus))
                }
            } catch (e: Exception) {
                android.util.Log.e("TaskViewModel", "Error updating task status", e)
            }
        }
    }
}
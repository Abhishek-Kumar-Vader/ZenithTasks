package com.example.zenithtasks.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zenithtasks.data.Task
import com.example.zenithtasks.data.TaskRepository
import com.example.zenithtasks.data.TaskStatus
import com.example.zenithtasks.data.Priority
import com.example.zenithtasks.notifications.AlarmScheduler // NEW IMPORT
import dagger.hilt.android.lifecycle.HiltViewModel // NEW IMPORT
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject // NEW IMPORT

@HiltViewModel // <--- ADD THIS ANNOTATION
class TaskViewModel @Inject constructor( // <--- ADD @Inject TO THE CONSTRUCTOR
    private val repository: TaskRepository,
    private val alarmScheduler: AlarmScheduler // <--- ENSURE AlarmScheduler IS HERE
) : ViewModel() {

    val todoTasks: StateFlow<List<Task>> = repository.getTasksByStatus(TaskStatus.TODO)
        .map { tasks -> sortTasksByPriority(tasks) }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val inProgressTasks: StateFlow<List<Task>> = repository.getTasksByStatus(TaskStatus.IN_PROGRESS)
        .map { tasks -> sortTasksByPriority(tasks) }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val doneTasks: StateFlow<List<Task>> = repository.getTasksByStatus(TaskStatus.DONE)
        .map { tasks -> sortTasksByPriority(tasks) }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val cancelledTasks: StateFlow<List<Task>> = repository.getTasksByStatus(TaskStatus.CANCELLED)
        .map { tasks -> sortTasksByPriority(tasks) }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    private fun sortTasksByPriority(tasks: List<Task>): List<Task> {
        val priorityOrder = Priority.getOrderedPriorities().withIndex().associate { it.value to it.index }
        return tasks.sortedWith(compareBy { priorityOrder[it.priority] })
    }

    fun insertTask(task: Task) {
        viewModelScope.launch {
            val newTaskId = repository.upsert(task) // Use upsert for both insert/update
            val newTaskWithId = task.copy(id = newTaskId) // Ensure task has correct ID for scheduling
            if (newTaskWithId.dueDate != null) {
                alarmScheduler.schedule(newTaskWithId) // Schedule alarm for new task
            }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.upsert(task) // Use upsert for both insert/update
            if (task.dueDate != null) {
                alarmScheduler.schedule(task) // Reschedule/update alarm for updated task
            } else {
                alarmScheduler.cancel(task) // Cancel if due date removed
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
            alarmScheduler.cancel(task) // Cancel alarm when task is deleted
        }
    }

    fun getTaskById(taskId: Long): Flow<Task?> {
        return repository.getTaskById(taskId)
    }

    fun toggleTaskCompletion(task: Task, isCompleted: Boolean) {
        val newStatus = if (isCompleted) TaskStatus.DONE else task.status // Or TaskStatus.TODO if un-checking reverts
        val updatedTask = task.copy(isCompleted = isCompleted, status = newStatus)
        updateTask(updatedTask) // This will trigger reschedule/cancel
    }

    // This function is removed as drag and drop is removed
    // fun moveTaskToPosition(taskId: Long, newStatusName: String, newIndex: Int? = null) { }
}
package com.example.zenithtasks.viewmodel

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zenithtasks.data.Priority
import com.example.zenithtasks.data.Task
import com.example.zenithtasks.data.TaskRepository
import com.example.zenithtasks.data.TaskStatus
import com.example.zenithtasks.notifications.AlarmScheduler
import com.example.zenithtasks.notifications.AlarmSchedulerImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map // NEW IMPORT - already present, but good to confirm
import kotlinx.coroutines.flow.stateIn // NEW IMPORT: Changed from shareIn to stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository,
    private val alarmScheduler: AlarmScheduler // Injected through Hilt
) : ViewModel() {

    // Existing StateFlows for filter selections for each status
    private val _todoFilterPriority = MutableStateFlow<Priority?>(null)
    val todoFilterPriority: StateFlow<Priority?> = _todoFilterPriority.asStateFlow()

    private val _inProgressFilterPriority = MutableStateFlow<Priority?>(null)
    val inProgressFilterPriority: StateFlow<Priority?> = _inProgressFilterPriority.asStateFlow()

    private val _doneFilterPriority = MutableStateFlow<Priority?>(null)
    val doneFilterPriority: StateFlow<Priority?> = _doneFilterPriority.asStateFlow()

    private val _cancelledFilterPriority = MutableStateFlow<Priority?>(null)
    val cancelledFilterPriority: StateFlow<Priority?> = _cancelledFilterPriority.asStateFlow()

    private val _archivedFilterPriority = MutableStateFlow<Priority?>(null)
    val archivedFilterPriority: StateFlow<Priority?> = _archivedFilterPriority.asStateFlow()

    // Search functionality StateFlows
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchFilterPriority = MutableStateFlow<Priority?>(null)
    val searchFilterPriority: StateFlow<Priority?> = _searchFilterPriority.asStateFlow()

    // StateFlow for the currently selected task for editing
    private val _selectedTask = MutableStateFlow<Task?>(null)
    val selectedTask: StateFlow<Task?> = _selectedTask.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResults: StateFlow<List<Task>> = combine(
        searchQuery,
        searchFilterPriority, // Combine with the new search filter priority
        repository.getAllTasks()
    ) { query, priorityFilter, allTasks ->
        if (query.isBlank() && priorityFilter == null) {
            emptyList()
        } else {
            allTasks.filter { task ->
                val matchesQuery = task.title.contains(query, ignoreCase = true) ||
                        (task.description?.contains(query, ignoreCase = true) == true)

                val matchesPriority = priorityFilter == null || task.priority == priorityFilter

                matchesQuery && matchesPriority
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSearchFilterPriority(priority: Priority?) {
        _searchFilterPriority.value = priority
    }


    // Existing flows for tasks by status with priority filtering
    @OptIn(ExperimentalCoroutinesApi::class)
    val todoTasks: StateFlow<List<Task>> = _todoFilterPriority.flatMapLatest { priority ->
        repository.getTasksByStatus(TaskStatus.TODO).map { tasks ->
            if (priority == null) {
                tasks
            } else {
                tasks.filter { it.priority == priority }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val inProgressTasks: StateFlow<List<Task>> = _inProgressFilterPriority.flatMapLatest { priority ->
        repository.getTasksByStatus(TaskStatus.IN_PROGRESS).map { tasks ->
            if (priority == null) {
                tasks
            } else {
                tasks.filter { it.priority == priority }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val doneTasks: StateFlow<List<Task>> = _doneFilterPriority.flatMapLatest { priority ->
        repository.getTasksByStatus(TaskStatus.DONE).map { tasks ->
            if (priority == null) {
                tasks
            } else {
                tasks.filter { it.priority == priority }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val cancelledTasks: StateFlow<List<Task>> = _cancelledFilterPriority.flatMapLatest { priority ->
        repository.getTasksByStatus(TaskStatus.CANCELLED).map { tasks ->
            if (priority == null) {
                tasks
            } else {
                tasks.filter { it.priority == priority }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val archivedTasks: StateFlow<List<Task>> = _archivedFilterPriority.flatMapLatest { priority ->
        repository.getTasksByStatus(TaskStatus.ARCHIVED).map { tasks ->
            if (priority == null) {
                tasks
            } else {
                tasks.filter { it.priority == priority }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Existing functions (insert, update, delete, toggle completion, archive, restore)

    fun insertTask(task: Task) = viewModelScope.launch {
        val newTaskId = repository.upsert(task)
        val taskWithId = task.copy(id = newTaskId)

        taskWithId.dueDate?.let {
            if (it.time > System.currentTimeMillis()) {
                alarmScheduler.scheduleAlarm(taskWithId)
            }
        }
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        repository.upsert(task)
        if (task.dueDate != null && task.dueDate.time > System.currentTimeMillis() &&
            task.status != TaskStatus.DONE && task.status != TaskStatus.CANCELLED) {
            alarmScheduler.scheduleAlarm(task)
        } else {
            alarmScheduler.cancelAlarm(task)
        }
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        repository.archiveTask(task)
        alarmScheduler.cancelAlarm(task)
    }

    fun permanentlyDeleteTask(task: Task) = viewModelScope.launch {
        Log.d("TaskViewModel", "Permanently deleting task: ${task.id}")
        repository.permanentlyDeleteTask(task)
        alarmScheduler.cancelAlarm(task)
    }

    fun restoreTask(task: Task) = viewModelScope.launch {
        repository.restoreTask(task)
        if (task.dueDate != null && task.dueDate.time > System.currentTimeMillis() &&
            task.status != TaskStatus.DONE && task.status != TaskStatus.CANCELLED) {
            alarmScheduler.scheduleAlarm(task)
        }
    }

    fun toggleTaskCompletion(task: Task, isCompleted: Boolean) = viewModelScope.launch {
        val updatedStatus = if (isCompleted) TaskStatus.DONE else TaskStatus.TODO
        val updatedTask = task.copy(isCompleted = isCompleted, status = updatedStatus)
        repository.updateTask(updatedTask)

        if (isCompleted) {
            alarmScheduler.cancelAlarm(updatedTask)
        } else {
            updatedTask.dueDate?.let {
                if (it.time > System.currentTimeMillis()) {
                    alarmScheduler.scheduleAlarm(updatedTask)
                }
            }
        }
    }

    fun archiveTask(task: Task) = viewModelScope.launch {
        val updatedTask = task.copy(status = TaskStatus.ARCHIVED)
        repository.updateTask(updatedTask)
        alarmScheduler.cancelAlarm(task) // Cancel any pending alarms
    }

    // Function to load a single task for editing
    fun loadTask(taskId: Long) = viewModelScope.launch {
        repository.getTaskById(taskId).collect { task ->
            _selectedTask.value = task
        }
    }

    // Filter setters
    fun setTodoFilterPriority(priority: Priority?) {
        _todoFilterPriority.value = priority
    }

    fun setInProgressFilterPriority(priority: Priority?) {
        _inProgressFilterPriority.value = priority
    }

    fun setDoneFilterPriority(priority: Priority?) {
        _doneFilterPriority.value = priority
    }

    fun setCancelledFilterPriority(priority: Priority?) {
        _cancelledFilterPriority.value = priority
    }

    fun setArchivedFilterPriority(priority: Priority?) {
        _archivedFilterPriority.value = priority
    }
}
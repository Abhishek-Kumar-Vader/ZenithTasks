//package com.example.zenithtasks.viewmodel
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.zenithtasks.data.Task
//import com.example.zenithtasks.data.TaskDao
//import com.example.zenithtasks.data.TaskStatus
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.Flow // Ensure this is imported
//import kotlinx.coroutines.launch
//
//class TaskViewModel(private val taskDao: TaskDao) : ViewModel() {
//
//    val tasks: Flow<List<Task>> = taskDao.getAllTasks()
//
//    // Function to add a new task (already exists)
//    fun addTask(title: String, description: String) {
//        val task = Task(title = title, description = description, status = TaskStatus.TODO)
//        viewModelScope.launch(Dispatchers.IO) {
//            taskDao.insertTask(task)
//        }
//    }
//
//    // Function to get a task by ID (used for editing)
//    fun getTaskById(taskId: Long): Flow<Task?> {
//        return taskDao.getTaskById(taskId)
//    }
//
//    // Function to update an existing task (NEW)
//    fun updateTask(task: Task) {
//        viewModelScope.launch(Dispatchers.IO) {
//            taskDao.updateTask(task)
//        }
//    }
//
//    // We will add deleteTask and updateTaskStatus later
//    /*
//    fun deleteTask(task: Task) {
//        viewModelScope.launch(Dispatchers.IO) {
//            taskDao.deleteTask(task)
//        }
//    }
//    fun updateTaskStatus(taskId: Long, newStatus: TaskStatus) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val task = taskDao.getTaskById(taskId).firstOrNull() // Get current task state
//            task?.let {
//                taskDao.updateTask(it.copy(status = newStatus))
//            }
//        }
//    }
//    */
//}

package com.example.zenithtasks.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zenithtasks.data.Task // Import your Task entity
import com.example.zenithtasks.data.TaskRepository // Import your TaskRepository
import com.example.zenithtasks.data.TaskStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull // For getting a single task once
import kotlinx.coroutines.launch

// Pass TaskRepository as a constructor parameter
class TaskViewModel(private val taskRepository: TaskRepository) : ViewModel() {

    // A StateFlow to hold the list of all tasks, collected from the repository's Flow
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    // A StateFlow to hold the currently selected/edited task, useful for AddEditTaskScreen
    private val _currentTask = MutableStateFlow<Task?>(null)
    val currentTask: StateFlow<Task?> = _currentTask.asStateFlow()

    init {
        // Launch a coroutine in the ViewModel's scope to collect all tasks
        viewModelScope.launch {
            taskRepository.getAllTasks().collect { tasksList ->
                _tasks.value = tasksList
            }
        }
    }

    // Function to get a specific task by ID (for editing)
    fun getTask(taskId: Long) {
        viewModelScope.launch {
            // Collect the flow and get the first (and only) item, or null
            _currentTask.value = taskRepository.getTaskById(taskId).firstOrNull()
        }
    }

    // Function to insert a new task or update an existing one
    fun saveTask(task: Task) {
        viewModelScope.launch {
            if (task.id == 0L) { // If ID is 0, it's a new task
                taskRepository.insertTask(task)
            } else { // Otherwise, it's an existing task to update
                taskRepository.updateTask(task)
            }
        }
    }

    // Function to delete a task (for TaskBoardScreen)
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }
    }

    // Clear current task when done with editing
    fun clearCurrentTask() {
        _currentTask.value = null
    }

    // --- (Optional: Add more functions for status updates later) ---
    // For example:
    fun updateTaskCompletion(taskId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            // First, get the task
            val task = taskRepository.getTaskById(taskId).firstOrNull()
            // If found, update its completion status and save it back
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
            }
        }
    }
}
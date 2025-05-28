package com.example.zenithtasks.ui.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import com.example.zenithtasks.data.Task // Import your Task data class
import com.example.zenithtasks.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    navController: NavController,
    taskViewModel: TaskViewModel, // <--- NOW ACCEPTING TASKVIEWMODEL
    taskId: Long? = null // Now accepting optional taskId
) {
    val context = LocalContext.current

    // State for input fields
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Observe the currentTask from the ViewModel
    // This will be updated by taskViewModel.getTask() call in LaunchedEffect
    val currentTask by taskViewModel.currentTask.collectAsState()

    // Use LaunchedEffect to trigger task fetching and update UI state
    LaunchedEffect(taskId) { // Rerun this effect if taskId changes
        val toastMessage = if (taskId != null && taskId != -1L) {
            taskViewModel.getTask(taskId)
            "Edit Task ID: $taskId"
        } else {
            taskViewModel.clearCurrentTask()
            title = ""
            description = ""
            "Add New Task"
        }
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
    }

    // Use LaunchedEffect to update the local UI state when currentTask (from ViewModel) changes
    // This ensures input fields are pre-filled when editing an existing task
    LaunchedEffect(currentTask) { // Rerun this effect if currentTask from ViewModel changes
        currentTask?.let { task ->
            title = task.title
            description = task.description ?: ""
            val taskDetails = "Task loaded: ${task.title} (ID: ${task.id})"
            Toast.makeText(context, taskDetails, Toast.LENGTH_LONG).show() // <--- ADD/MODIFY THIS TOAST
            Log.d("AddEditTaskScreen", "Current Task loaded: Title=${task.title}, ID=${task.id}")
        } ?: run {
            // This 'else' block for currentTask being null will run when clearing for new tasks
            // or if no task is found for an ID.
            if (taskId != null && taskId != -1L) {
                // Only show this if we *expected* a task but didn't get one
                Toast.makeText(context, "No task found for ID: $taskId", Toast.LENGTH_LONG).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (taskId != null && taskId != -1L) "Edit Task" else "Add New Task") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp), // Add some screen padding
            horizontalAlignment = Alignment.CenterHorizontally,
            // Arrangement.Top is the default, and it's what you want for fields at the top
            // verticalArrangement = Arrangement.Top // No need to explicitly set if it's the default
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Task Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true // Typically titles are single line
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Task Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp) // Provide more space for description
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        // Create a Task object. If taskId is -1L, it's new. Otherwise, use currentTask's ID.
                        val taskToSave = currentTask?.copy( // Use currentTask if editing, or create new Task
                            title = title,
                            description = description.takeIf { it.isNotBlank() }, // Save null if description is empty
                            // Keep other properties from currentTask or default
                            // (e.g., isCompleted, dueDate, status will retain original values if not changed on this screen yet)
                        ) ?: Task(
                            title = title,
                            description = description.takeIf { it.isNotBlank() },
                            // Default values for new task:
                            id = 0L, // Room auto-generates for 0L
                            isCompleted = false,
                            dueDate = null,
                            status = com.example.zenithtasks.data.TaskStatus.TODO // Specify full path
                        )

                        taskViewModel.saveTask(taskToSave) // <--- Use the consolidated saveTask
                        Toast.makeText(context, "Task saved!", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    } else {
                        Toast.makeText(context, "Title cannot be empty!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (taskId != null && taskId != -1L) "Update Task" else "Save Task")
            }
        }
    }
}
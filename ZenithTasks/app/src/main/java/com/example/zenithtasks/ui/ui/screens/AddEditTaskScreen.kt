package com.example.zenithtasks.ui.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults // Explicitly imported for OutlinedTextFieldDefaults.colors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.zenithtasks.R
import com.example.zenithtasks.data.Task
import com.example.zenithtasks.data.TaskStatus
import com.example.zenithtasks.viewmodel.TaskViewModel

// NEW IMPORTS FOR DROPDOWN AND PRIORITY
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import com.example.zenithtasks.data.TaskPriority // Import TaskPriority enum

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    navController: NavController,
    taskViewModel: TaskViewModel,
    taskId: Long? = null
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf(TaskPriority.MEDIUM) } // State for selected priority
    var expanded by remember { mutableStateOf(false) } // State for dropdown menu expansion

    val currentTask by taskViewModel.currentTask.collectAsState()

    LaunchedEffect(taskId) {
        val toastMessage = if (taskId != null && taskId != -1L) {
            taskViewModel.getTask(taskId)
            "Edit Task ID: $taskId"
        } else {
            taskViewModel.clearCurrentTask()
            title = ""
            description = ""
            selectedPriority = TaskPriority.MEDIUM // Set default priority for new tasks
            "Add New Task"
        }
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
    }

    LaunchedEffect(currentTask) {
        currentTask?.let { task ->
            title = task.title
            description = task.description ?: ""
            selectedPriority = task.priority // Pre-fill priority for editing
            val taskDetails = "Task loaded: ${task.title} (ID: ${task.id})"
            Toast.makeText(context, taskDetails, Toast.LENGTH_LONG).show()
            Log.d("AddEditTaskScreen", "Current Task loaded: Title=${task.title}, ID=${task.id}")
        } ?: run {
            if (taskId != null && taskId != -1L) {
                Toast.makeText(context, "No task found for ID: $taskId", Toast.LENGTH_LONG).show()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            // Using your specified background image name
            painter = painterResource(id = R.drawable.add_edit_bg),
            contentDescription = "Add/Edit Task Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Scaffold(
            containerColor = Color.Transparent,
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
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        actionIconContentColor = MaterialTheme.colorScheme.onSurface,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors( // Corrected usage of OutlinedTextFieldDefaults.colors
                        focusedContainerColor = Color.DarkGray.copy(alpha = 0.7f),
                        unfocusedContainerColor = Color.DarkGray.copy(alpha = 0.7f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = Color.LightGray,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Task Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    colors = OutlinedTextFieldDefaults.colors( // Corrected usage of OutlinedTextFieldDefaults.colors
                        focusedContainerColor = Color.DarkGray.copy(alpha = 0.7f),
                        unfocusedContainerColor = Color.DarkGray.copy(alpha = 0.7f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = Color.LightGray,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // --- PRIORITY DROPDOWN MENU ---
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedPriority.name.replace("_", " "), // Display readable name
                        onValueChange = { /* Read only */ },
                        readOnly = true,
                        label = { Text("Priority") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor() // REQUIRED for ExposedDropdownMenuBox
                            .fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors( // Corrected usage for dropdown's text field
                            focusedContainerColor = Color.DarkGray.copy(alpha = 0.7f),
                            unfocusedContainerColor = Color.DarkGray.copy(alpha = 0.7f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = Color.LightGray,
                            cursorColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        TaskPriority.entries.forEach { priorityOption ->
                            DropdownMenuItem(
                                text = { Text(priorityOption.name.replace("_", " ")) },
                                onClick = {
                                    selectedPriority = priorityOption
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                // --- END PRIORITY DROPDOWN MENU ---

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            val taskToSave = currentTask?.copy(
                                title = title,
                                description = description.takeIf { it.isNotBlank() },
                                priority = selectedPriority // Save selected priority
                            ) ?: Task(
                                title = title,
                                description = description.takeIf { it.isNotBlank() },
                                id = 0L,
                                isCompleted = false,
                                dueDate = null,
                                status = TaskStatus.TODO,
                                priority = selectedPriority // Save selected priority for new task
                            )

                            taskViewModel.saveTask(taskToSave)
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
}
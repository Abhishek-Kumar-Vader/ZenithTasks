package com.example.zenithtasks.ui.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults // NEW IMPORT
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.zenithtasks.Navigation.Screen
import com.example.zenithtasks.R
import com.example.zenithtasks.data.Priority
import com.example.zenithtasks.data.Task
import com.example.zenithtasks.data.TaskStatus
import com.example.zenithtasks.ui.ui.themes.ZenithTasksTheme
import com.example.zenithtasks.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import androidx.compose.runtime.collectAsState
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    navController: NavController,
    taskId: Long = 0L,
    taskViewModel: TaskViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedDueDate by remember { mutableStateOf<Date?>(null) }
    var selectedPriority by remember { mutableStateOf(Priority.LOW) } // Default priority
    var priorityExpanded by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf(TaskStatus.TODO) } // Default status
    var statusExpanded by remember { mutableStateOf(false) }
    var isCompleted by remember { mutableStateOf(false) }
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val isEditMode = taskId != 1L
    // Fetch task if ID is provided (for editing)
    LaunchedEffect(taskId) {
        if (isEditMode) {
            taskViewModel.loadTask(taskId!!)
        }
    }
    val task by taskViewModel.selectedTask.collectAsState()
    LaunchedEffect(task) {
        task?.let { task ->
            title = task.title
            description = task.description ?: ""
            selectedDueDate = task.dueDate
            selectedStatus = task.status
            selectedPriority = task.priority
            isCompleted = task.isCompleted // Initialize isCompleted from the task
        }
    }

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val newCalendar = Calendar.getInstance()
                newCalendar.set(year, month, dayOfMonth)
                val currentDueDate = selectedDueDate ?: Date() // Use current due date if available, otherwise new Date
                val currentCalendar = Calendar.getInstance().apply { time = currentDueDate }
                newCalendar.set(Calendar.HOUR_OF_DAY, currentCalendar.get(Calendar.HOUR_OF_DAY))
                newCalendar.set(Calendar.MINUTE, currentCalendar.get(Calendar.MINUTE))
                selectedDueDate = newCalendar.time
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )
    }

    val timePickerDialog = remember {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val newCalendar = Calendar.getInstance()
                val currentDueDate = selectedDueDate ?: Date() // Use current due date if available, otherwise new Date
                newCalendar.time = currentDueDate
                newCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                newCalendar.set(Calendar.MINUTE, minute)
                selectedDueDate = newCalendar.time
            },
            Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
            Calendar.getInstance().get(Calendar.MINUTE),
            false // 24 hour view
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (taskId == 0L) "Add Task" else "Edit Task") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (title.isBlank()) {
                                Toast.makeText(context, "Title cannot be empty", Toast.LENGTH_SHORT).show()
                                return@IconButton
                            }

                            val task = if (taskId == 0L) {
                                Task(
                                    title = title,
                                    description = description.ifBlank { null },
                                    dueDate = selectedDueDate,
                                    priority = selectedPriority,
                                    status = selectedStatus // Use selected status for new tasks
                                )
                            } else {
                                Task(
                                    id = taskId,
                                    title = title,
                                    description = description.ifBlank { null },
                                    dueDate = selectedDueDate,
                                    priority = selectedPriority,
                                    status = selectedStatus, // Update status for existing tasks
                                    isCompleted = selectedStatus == TaskStatus.DONE // Sync isCompleted with status
                                )
                            }

                            if (taskId == 0L) {
                                taskViewModel.updateTask(task)
                                Toast.makeText(context, "Task Added", Toast.LENGTH_SHORT).show()
                            } else {
                                taskViewModel.insertTask(task)
                                Toast.makeText(context, "Task Updated", Toast.LENGTH_SHORT).show()
                            }
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save Task",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Image(
                painter = painterResource(id = R.drawable.add_edit_bg),
                contentDescription = "Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors( // Use MaterialTheme colors
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp), // Increased height for description
                    singleLine = false,
                    colors = OutlinedTextFieldDefaults.colors( // Use MaterialTheme colors
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Date Picker
                    OutlinedTextField(
                        value = selectedDueDate?.let { dateFormatter.format(it) } ?: "",
                        onValueChange = { /* Read-only */ },
                        label = { Text("Due Date") },
                        readOnly = true,
                        modifier = Modifier.weight(1f),
                        trailingIcon = {
                            IconButton(onClick = { datePickerDialog.show() }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.outline_calendar_month_24),
                                    contentDescription = "Select Date"
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors( // Use MaterialTheme colors
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    // Time Picker
                    OutlinedTextField(
                        value = selectedDueDate?.let { timeFormatter.format(it) } ?: "",
                        onValueChange = { /* Read-only */ },
                        label = { Text("Due Time") },
                        readOnly = true,
                        modifier = Modifier.weight(1f),
                        trailingIcon = {
                            IconButton(onClick = { timePickerDialog.show() }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.outline_alarm_24),
                                    contentDescription = "Select Time"
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors( // Use MaterialTheme colors
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                // Priority Dropdown
                ExposedDropdownMenuBox(
                    expanded = priorityExpanded,
                    onExpandedChange = { priorityExpanded = !priorityExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedPriority.name,
                        onValueChange = { /* Read-only */ },
                        readOnly = true,
                        label = { Text("Priority") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors( // Use MaterialTheme colors
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = priorityExpanded,
                        onDismissRequest = { priorityExpanded = false }
                    ) {
                        Priority.entries.forEach { priority ->
                            DropdownMenuItem(
                                text = { Text(priority.name, color = MaterialTheme.colorScheme.onSurface) }, // Ensure text color adapts
                                onClick = {
                                    selectedPriority = priority
                                    priorityExpanded = false
                                }
                            )
                        }
                    }
                }

                // Status Dropdown (only for existing tasks, or if you want to explicitly set initial status)
                // If it's a new task, it's typically TODO by default.
                // If editing, allow changing status.
                if (taskId != 0L) { // Only show status dropdown when editing an existing task
                    ExposedDropdownMenuBox(
                        expanded = statusExpanded,
                        onExpandedChange = { statusExpanded = !statusExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedStatus.name,
                            onValueChange = { /* Read-only */ },
                            readOnly = true,
                            label = { Text("Status") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors( // Use MaterialTheme colors
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                focusedLabelColor = MaterialTheme.colorScheme.primary,
                                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = statusExpanded,
                            onDismissRequest = { statusExpanded = false }
                        ) {
                            TaskStatus.entries.forEach { status ->
                                DropdownMenuItem(
                                    text = { Text(status.name, color = MaterialTheme.colorScheme.onSurface) }, // Ensure text color adapts
                                    onClick = {
                                        selectedStatus = status
                                        statusExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddEditTaskScreenPreview() {
    ZenithTasksTheme {
        AddEditTaskScreen(navController = rememberNavController())
    }
}
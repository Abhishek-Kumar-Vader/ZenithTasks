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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
// import androidx.compose.material3.TextFieldDefaults // <--- DELETE THIS LINE
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
import com.example.zenithtasks.Navigation.Screens
import com.example.zenithtasks.R
import com.example.zenithtasks.data.Priority
import com.example.zenithtasks.data.Task
import com.example.zenithtasks.data.TaskStatus
import com.example.zenithtasks.viewmodel.TaskViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import java.util.Calendar
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.material3.OutlinedTextFieldDefaults // Make sure this is imported!


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    navController: NavController,
    taskId: Long?,
    taskViewModel: TaskViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isEditMode = taskId != -1L && taskId != null

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dueDate: Date? by remember { mutableStateOf(null) }
    var isCompleted by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf(TaskStatus.TODO) }
    var selectedPriority by remember { mutableStateOf(Priority.MEDIUM) }

    var statusExpanded by remember { mutableStateOf(false) }
    var priorityExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(taskId) {
        if (isEditMode) {
            taskViewModel.getTaskById(taskId!!).collect { task ->
                task?.let {
                    title = it.title
                    description = it.description ?: ""
                    dueDate = it.dueDate
                    isCompleted = it.isCompleted
                    selectedStatus = it.status
                    selectedPriority = it.priority
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = if (isEditMode) "Edit Task" else "Add New Task", color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val currentTask = Task(
                            id = taskId ?: 0L,
                            title = title,
                            description = description.ifBlank { null },
                            dueDate = dueDate,
                            isCompleted = isCompleted,
                            status = selectedStatus,
                            priority = selectedPriority
                        )

                        if (title.isBlank()) {
                            Toast.makeText(context, "Task title cannot be empty", Toast.LENGTH_SHORT).show()
                            return@IconButton
                        }

                        if (isEditMode) {
                            taskViewModel.updateTask(currentTask)
                            Toast.makeText(context, "Task Updated", Toast.LENGTH_SHORT).show()
                        } else {
                            taskViewModel.insertTask(currentTask)
                            Toast.makeText(context, "Task Added", Toast.LENGTH_SHORT).show()
                        }
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.Check, contentDescription = "Save Task", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
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
                contentDescription = "Background wallpaper",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Task Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors( // <--- CORRECTED HERE
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.DarkGray,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.DarkGray,
                        focusedTextColor = Color.Black, // <--- Use Color.Black for pitch black text
                        unfocusedTextColor = Color.Black // <--- Use Color.Black for pitch black text
                    )
                )

                // Task Description Input
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors( // <--- CORRECTED HERE
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.DarkGray,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.DarkGray,
                        focusedTextColor = Color.Black, // <--- Use Color.Black for pitch black text
                        unfocusedTextColor = Color.Black // <--- Use Color.Black for pitch black text
                    )
                )

                // Due Date Picker (no direct OutlinedTextField, so no change needed here)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Due Date: ${dueDate?.let { SimpleDateFormat("MMM dd,yyyy HH:mm", Locale.getDefault()).format(it) } ?: "Not set"}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black // Make text darker
                    )
                    Button(onClick = {
                        val calendar = Calendar.getInstance().apply {
                            dueDate?.let { time = it }
                        }
                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                calendar.set(year, month, dayOfMonth)
                                TimePickerDialog(
                                    context,
                                    { _, hourOfDay, minute ->
                                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                        calendar.set(Calendar.MINUTE, minute)
                                        dueDate = calendar.time
                                    },
                                    calendar.get(Calendar.HOUR_OF_DAY),
                                    calendar.get(Calendar.MINUTE),
                                    false
                                ).show()
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }) {
                        Text("Set Due Date")
                    }
                }

                // Is Completed Checkbox (no direct OutlinedTextField, but making text darker)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Checkbox(
                        checked = isCompleted,
                        onCheckedChange = { checked ->
                            isCompleted = checked
                            selectedStatus = if (checked) TaskStatus.DONE else TaskStatus.TODO
                        }
                    )
                    Text("Mark as Completed", color = Color.Black) // Make text darker
                }

                // Task Status Dropdown
                ExposedDropdownMenuBox(
                    expanded = statusExpanded,
                    onExpandedChange = { statusExpanded = !statusExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedStatus.name.replace("_", " "),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Status") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors( // <--- CORRECTED HERE
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.DarkGray,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.DarkGray,
                            focusedTextColor = Color.Black, // <--- Use Color.Black for pitch black text
                            unfocusedTextColor = Color.Black // <--- Use Color.Black for pitch black text
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = statusExpanded,
                        onDismissRequest = { statusExpanded = false }
                    ) {
                        TaskStatus.entries.forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status.name.replace("_", " "), color = Color.Black) }, // Make dropdown text darker
                                onClick = {
                                    selectedStatus = status
                                    isCompleted = status == TaskStatus.DONE
                                    statusExpanded = false
                                }
                            )
                        }
                    }
                }

                // Task Priority Dropdown
                ExposedDropdownMenuBox(
                    expanded = priorityExpanded,
                    onExpandedChange = { priorityExpanded = !priorityExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedPriority.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Priority") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors( // <--- CORRECTED HERE
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.DarkGray,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.DarkGray,
                            focusedTextColor = Color.Black, // <--- Use Color.Black for pitch black text
                            unfocusedTextColor = Color.Black // <--- Use Color.Black for pitch black text
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = priorityExpanded,
                        onDismissRequest = { priorityExpanded = false }
                    ) {
                        Priority.entries.forEach { priority ->
                            DropdownMenuItem(
                                text = { Text(priority.name, color = Color.Black) }, // Make dropdown text darker
                                onClick = {
                                    selectedPriority = priority
                                    priorityExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
package com.example.zenithtasks.ui.ui.screens

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.zenithtasks.Navigation.Screen
import com.example.zenithtasks.data.Task // Import Task entity
import com.example.zenithtasks.data.TaskStatus
import com.example.zenithtasks.ui.ui.components.TaskItem
import com.example.zenithtasks.viewmodel.TaskViewModel // Import TaskViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TaskBoardScreen(
    navController: NavController,
    taskViewModel: TaskViewModel // <--- NOW ACCEPTING TASKVIEWMODEL
) {
    // Collect the list of tasks from the ViewModel's StateFlow
    val tasks by taskViewModel.tasks.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Task Board") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screen.AddEditTaskScreen.createRoute())
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { paddingValues ->
        LazyRow(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(TaskStatus.values()) { status ->
                val tasksInColumn = tasks.filter { it.status == status }

                Card(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(280.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = status.name.replace("_", " "),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 8.dp)
                        ) {
                            items(tasksInColumn, key = { task -> task.id }) { task ->
                                TaskItem(
                                    task = task,
                                    onTaskClick = { taskId ->
                                        Log.d("TaskBoardScreen", "Navigating to AddEditTaskScreen with taskId: $taskId")
                                        navController.navigate(Screen.AddEditTaskScreen.createRoute(taskId))
                                    },
                                    // *** NEW: Pass the delete action to TaskItem ***
                                    onDeleteClick = { taskToDelete ->
                                        taskViewModel.deleteTask(taskToDelete)
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
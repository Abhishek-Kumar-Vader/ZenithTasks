package com.example.zenithtasks.ui.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.zenithtasks.Navigation.Screen
import com.example.zenithtasks.R
import com.example.zenithtasks.data.TaskStatus
import com.example.zenithtasks.ui.ui.components.EmptyStateVisuals
import com.example.zenithtasks.ui.ui.components.TaskItem
import com.example.zenithtasks.ui.ui.themes.ZenithTasksTheme
import com.example.zenithtasks.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchivedTasksScreen(
    navController: NavController,
    taskViewModel: TaskViewModel = hiltViewModel()
) {
    val archivedTasks by taskViewModel.archivedTasks.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Archived Tasks") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Image(
                painter = painterResource(id = R.drawable.main_screen),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                if (archivedTasks.isEmpty()) {
                    EmptyStateVisuals(status = TaskStatus.ARCHIVED)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 4.dp),
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
                    ) {
                        items(archivedTasks, key = { it.id }) { task ->
                            TaskItem(
                                task = task,
                                onTaskClick = { taskId ->
                                    navController.navigate(Screen.AddEditTask.createRoute(taskId))
                                },
                                onDeleteClick = { taskToDelete ->
                                    // On archived screen, "delete" means PERMANENTLY DELETE
                                    taskViewModel.permanentlyDeleteTask(taskToDelete)
                                },
                                onToggleCompletion = { taskToUpdate, isChecked ->
                                    // Archived tasks should ideally not be toggled completion directly.
                                    // Instead, they should be restored first.
                                    // For now, we'll keep it, but it might not be relevant for archived tasks.
                                    taskViewModel.toggleTaskCompletion(taskToUpdate, isChecked)
                                },
                                // NEW: Add a restore action for archived tasks
                                onRestoreClick = { taskToRestore ->
                                    taskViewModel.restoreTask(taskToRestore)
                                },
                                onArchiveClick = {} // RESOLVED: Pass an empty lambda for onArchiveClick
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ArchivedTasksScreenPreview() {
    ZenithTasksTheme {
        ArchivedTasksScreen(navController = rememberNavController())
    }
}
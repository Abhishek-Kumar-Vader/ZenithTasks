package com.example.zenithtasks.ui.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.zenithtasks.data.Priority
import com.example.zenithtasks.data.TaskStatus
import com.example.zenithtasks.ui.ui.components.CommonTaskScreenTopAppBar
import com.example.zenithtasks.ui.ui.components.EmptyStateVisuals
import com.example.zenithtasks.ui.ui.components.TaskItem
import com.example.zenithtasks.ui.ui.themes.ZenithTasksTheme
import com.example.zenithtasks.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InProgressTasksScreen(
    navController: NavController,
    taskViewModel: TaskViewModel = hiltViewModel()
) {
    val tasksForStatus by taskViewModel.inProgressTasks.collectAsState()
    val filterPriority by taskViewModel.inProgressFilterPriority.collectAsState()

    Scaffold(
        topBar = {
            CommonTaskScreenTopAppBar(
                title = "In Progress Tasks",
                navController = navController,
                currentFilterPriority = filterPriority,
                onFilterPrioritySelected = { priority ->
                    taskViewModel.setInProgressFilterPriority(priority)
                },
                showSearchIcon = true
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddEditTask.createRoute(0L)) },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ) {
                Icon(Icons.Filled.Add, "Add new task")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
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
                Spacer(modifier = Modifier.height(4.dp))

                if (tasksForStatus.isEmpty()) {
                    EmptyStateVisuals(status = TaskStatus.IN_PROGRESS)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(tasksForStatus, key = { it.id }) { task ->
                            TaskItem(
                                task = task,
                                onTaskClick = { taskId ->
                                    navController.navigate(Screen.AddEditTask.createRoute(taskId))
                                },
                                onDeleteClick = { taskToDelete ->
                                    taskViewModel.deleteTask(taskToDelete)
                                },
                                onToggleCompletion = { taskToUpdate, isChecked ->
                                    taskViewModel.toggleTaskCompletion(taskToUpdate, isChecked)
                                },
                                onArchiveClick = { taskToArchive -> // NEW
                                    taskViewModel.archiveTask(taskToArchive)
                                },
                                onRestoreClick = {}
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
fun InProgressTasksScreenPreview() {
    ZenithTasksTheme {
        InProgressTasksScreen(navController = rememberNavController())
    }
}
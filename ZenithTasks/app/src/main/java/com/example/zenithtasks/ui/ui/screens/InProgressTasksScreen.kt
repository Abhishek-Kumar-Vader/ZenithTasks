package com.example.zenithtasks.ui.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.zenithtasks.Navigation.Screens // CORRECT: Using the consolidated 'object Screens'
import com.example.zenithtasks.R
import com.example.zenithtasks.data.Task
import com.example.zenithtasks.data.TaskStatus
import com.example.zenithtasks.ui.ui.components.TaskItem // CORRECT: Using the non-draggable TaskItem
import com.example.zenithtasks.ui.ui.components.EmptyStateVisuals
import com.example.zenithtasks.viewmodel.TaskViewModel
import androidx.compose.foundation.layout.Arrangement // Required for LazyColumn spacing
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import com.example.zenithtasks.Navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InProgressTasksScreen(
    navController: NavController,
    taskViewModel: TaskViewModel
) {
    // Collect tasks specifically for the IN_PROGRESS status, already sorted by priority in ViewModel
    val tasksForStatus by taskViewModel.inProgressTasks.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "In Progress Tasks", color = MaterialTheme.colorScheme.onPrimary) }, // Screen-specific title
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddEditTask.createRoute(null)) }, // CORRECT: Navigates to Add/Edit screen for new task
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add New Task")
            }
        },
        floatingActionButtonPosition = androidx.compose.material3.FabPosition.End
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Background image for the screen
            Image(
                painter = painterResource(id = R.drawable.main_screen), // Use your chosen background image
                contentDescription = "Background wallpaper",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize() // This Column still fills the box
                    .padding(horizontal = 8.dp)
            ) {
                // Each 'status column' within this screen
                // This is the Column that will have the variable height and minimum space
                Column(
                    modifier = Modifier
                        .fillMaxWidth() // The column takes full width
                        .fillMaxHeight() // Allows it to expand vertically
                        .heightIn(min = 100.dp) // <--- Minimum height so background is visible
                        .clip(RoundedCornerShape(8.dp)) // Rounded corners for the column
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)) // Semi-transparent background for the column itself
                        .padding(8.dp) // Padding inside the column for content
                ) {
                    Text(
                        text = "In Progress Tasks",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    if (tasksForStatus.isEmpty()) {
                        EmptyStateVisuals(status = TaskStatus.IN_PROGRESS) // e.g., TaskStatus.TODO
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(), // LazyColumn fills the remaining space within its parent Column
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
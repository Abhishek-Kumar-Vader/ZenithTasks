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
import androidx.compose.foundation.layout.heightIn // Import for heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color // Import Color for custom alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.zenithtasks.Navigation.Screen
import com.example.zenithtasks.Navigation.Screens
import com.example.zenithtasks.R
import com.example.zenithtasks.data.Task
import com.example.zenithtasks.data.TaskStatus
import com.example.zenithtasks.data.Priority
import com.example.zenithtasks.ui.ui.components.TaskItem
import com.example.zenithtasks.ui.ui.components.EmptyStateVisuals
import com.example.zenithtasks.viewmodel.TaskViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoTasksScreen(
    navController: NavController,
    taskViewModel: TaskViewModel
) {
    val todoTasks by taskViewModel.todoTasks.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "To Do Tasks", color = MaterialTheme.colorScheme.onPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddEditTask.createRoute(null)) },
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
                .padding(paddingValues) // Apply scaffold padding here
        ) {
            // Background image for the whole screen
            Image(
                painter = painterResource(id = R.drawable.main_screen),
                contentDescription = "Background wallpaper",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Outer Column for screen content.
            // This is currently a single column as per our last conversation about removing LazyRow for tabs.
            // If you intend to have multiple *vertical* columns on one screen, this structure will change.
            Column(
                modifier = Modifier
                    .fillMaxSize() // This column fills the space within the Box (which already has scaffold padding)
                    .padding(horizontal = 8.dp), // Additional horizontal padding for content within the screen
                verticalArrangement = Arrangement.Top // Align content to the top
            ) {
                // The actual "column" for the task list
                Column(
                    modifier = Modifier
                        .fillMaxWidth() // Takes full width of its parent Column
                        .heightIn(min = 100.dp) // <--- Ensures minimum 100dp space is always visible
                        // If you want it to take *all* remaining space if tasks are many, add weight or fillMaxHeight
                        .weight(1f, fill = true) // <--- NEW: Allows this column to take all available height.
                        // This makes it "variable length" and takes remaining space
                        .clip(RoundedCornerShape(8.dp)) // Rounded corners for the column background
                        // Using a slightly lower alpha for better background visibility if needed, or adjust color
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)) // Adjusted alpha for better background visibility
                        .padding(8.dp) // Padding inside this task list column
                ) {
                    Text(
                        text = "To Do Tasks",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    if (todoTasks.isEmpty()) {
                        EmptyStateVisuals(status = TaskStatus.TODO)
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(), // LazyColumn fills its parent Column
                            contentPadding = PaddingValues(vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(todoTasks, key = { it.id }) { task ->
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


package com.example.zenithtasks.ui.ui.screens

import android.provider.CalendarContract.Colors
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.FilterList // NEW IMPORT
import androidx.compose.material3.DropdownMenu // NEW IMPORT
import androidx.compose.material3.DropdownMenuItem // NEW IMPORT
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.zenithtasks.data.Task
import com.example.zenithtasks.ui.ui.components.EmptyStateVisuals
import com.example.zenithtasks.ui.ui.components.TaskItem
import com.example.zenithtasks.ui.ui.themes.ZenithTasksTheme
import com.example.zenithtasks.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    taskViewModel: TaskViewModel = hiltViewModel()
) {
    val searchQuery by taskViewModel.searchQuery.collectAsState()
    val searchResults by taskViewModel.searchResults.collectAsState()
    val searchFilterPriority by taskViewModel.searchFilterPriority.collectAsState()
    var showPriorityFilterMenu by remember { mutableStateOf(false) }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
    )


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search Tasks") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(paddingValues)
        ) {
            Image(
                painter = painterResource(id = R.drawable.main_screen), // Your background image
                contentDescription = "Background Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { taskViewModel.setSearchQuery(it) },
                    label = { Text("Search tasks...") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { taskViewModel.setSearchQuery("") }) {
                                Icon(Icons.Filled.Clear, contentDescription = "Clear search")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = textFieldColors // Apply the defined colors
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Box {
                        IconButton(onClick = { showPriorityFilterMenu = true }) {
                            Icon(Icons.Filled.FilterList, contentDescription = "Filter by Priority")
                        }
                        DropdownMenu(
                            expanded = showPriorityFilterMenu,
                            onDismissRequest = { showPriorityFilterMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("All Priorities") },
                                onClick = {
                                    taskViewModel.setSearchFilterPriority(null)
                                    showPriorityFilterMenu = false
                                }
                            )
                            Priority.entries.forEach { priority ->
                                DropdownMenuItem(
                                    text = { Text(priority.name) },
                                    onClick = {
                                        taskViewModel.setSearchFilterPriority(priority)
                                        showPriorityFilterMenu = false
                                    }
                                )
                            }
                        }
                    }
                    searchFilterPriority?.let {
                        Text(
                            text = "Priority: ${it.name}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp, top = 12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (searchQuery.isBlank() && searchFilterPriority == null) {
                    EmptyStateVisuals(
                        status = null,
                        message = "Start typing or apply filters to search for tasks."
                    )
                } else if (searchResults.isEmpty()) {
                    EmptyStateVisuals(
                        status = null,
                        message = "No tasks found matching your query."
                    )
                }
                else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(searchResults, key = { it.id }) { task ->
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
                                onArchiveClick = { taskToArchive ->
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
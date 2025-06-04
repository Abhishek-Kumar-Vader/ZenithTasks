package com.example.zenithtasks.ui.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.zenithtasks.Navigation.Screen
import com.example.zenithtasks.data.Priority

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonTaskScreenTopAppBar(
    title: String,
    navController: NavController,
    currentFilterPriority: Priority?,
    onFilterPrioritySelected: (Priority?) -> Unit,
    showSearchIcon: Boolean = true,
    showArchiveIcon: Boolean = true
) {
    var showPriorityMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text(title, color = MaterialTheme.colorScheme.onPrimary) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        actions = {
            if (showSearchIcon) {
                IconButton(onClick = { navController.navigate(Screen.Search.route) }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Tasks",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            // NEW: Archive Icon
            if (showArchiveIcon) {
                IconButton(onClick = { navController.navigate(Screen.ArchivedTasks.route) }) {
                    Icon(
                        imageVector = Icons.Default.Archive, // Using a generic archive icon
                        contentDescription = "Archived Tasks",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            Box {
                IconButton(onClick = { showPriorityMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filter by Priority",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                DropdownMenu(
                    expanded = showPriorityMenu,
                    onDismissRequest = { showPriorityMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("All Priorities") },
                        onClick = {
                            onFilterPrioritySelected(null) // Clear filter
                            showPriorityMenu = false
                        }
                    )
                    Priority.entries.forEach { priority ->
                        DropdownMenuItem(
                            text = { Text(priority.name) },
                            onClick = {
                                onFilterPrioritySelected(priority)
                                showPriorityMenu = false
                            }
                        )
                    }
                }
            }
        }
    )
}
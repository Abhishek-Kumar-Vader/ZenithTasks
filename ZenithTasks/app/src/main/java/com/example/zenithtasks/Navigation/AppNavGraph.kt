package com.example.zenithtasks.Navigation

import android.util.Log // Added import
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel // <--- Ensure this is imported
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.zenithtasks.ui.ui.screens.AddEditTaskScreen
import com.example.zenithtasks.ui.ui.screens.ArchivedTasksScreen
import com.example.zenithtasks.ui.ui.screens.CancelledTasksScreen
import com.example.zenithtasks.ui.ui.screens.DoneTasksScreen
import com.example.zenithtasks.ui.ui.screens.InProgressTasksScreen
import com.example.zenithtasks.ui.ui.screens.SearchScreen
import com.example.zenithtasks.ui.ui.screens.ToDoTaskScreen
import com.example.zenithtasks.viewmodel.TaskViewModel // <--- Ensure this is imported

// Define data class for Bottom Navigation Items (moved here from MainActivity for cleaner separation)
data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavGraph(
    taskViewModel: TaskViewModel = hiltViewModel() // Default Hilt injection
) {
    val navController = rememberNavController()

    val bottomNavItems = listOf(
        BottomNavItem("To Do", Icons.Default.List, Screens.TodoTasks),
        BottomNavItem("In Progress", Icons.Default.PlayCircle, Screens.InProgressTasks),
        BottomNavItem("Done", Icons.Default.CheckCircle, Screens.DoneTasks),
        BottomNavItem("Cancelled", Icons.Default.Cancel, Screens.CancelledTasks),
//        BottomNavItem("Search", Icons.Default.Search, Screens.Search),
//        BottomNavItem("Archived", Icons.Default.Archive, Screen.ArchivedTasks.route)
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().route!!) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            BadgedBox(badge = {
                                // You can optionally add badges here, e.g., for task counts
                                // based on the taskViewModel if you add state collection
                            }) {
                                Icon(item.icon, contentDescription = item.title)
                            }
                        },
                        label = { Text(item.title) }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screens.TodoTasks, // Start with the To Do screen
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screens.TodoTasks) {
                ToDoTaskScreen(navController = navController, taskViewModel = taskViewModel)
            }
            composable(Screens.InProgressTasks) {
                InProgressTasksScreen(navController = navController, taskViewModel = taskViewModel)
            }
            composable(Screens.DoneTasks) {
                DoneTasksScreen(navController = navController, taskViewModel = taskViewModel)
            }
            composable(Screens.CancelledTasks) {
                CancelledTasksScreen(navController = navController, taskViewModel = taskViewModel)
            }
            composable(Screens.Search) {
                SearchScreen(navController = navController, taskViewModel = taskViewModel)
            }
            composable(Screen.ArchivedTasks.route) {
                ArchivedTasksScreen(navController = navController)
            }
            composable(
                route = "${Screen.AddEditTask.route}?taskId={taskId}",
                arguments = listOf(
                    navArgument("taskId") {
                        type = NavType.LongType      // must be non-nullable
                        defaultValue = -1L           // sentinel for “no task”
                        // remove `nullable = true`
                    }
                )
            ) { backStackEntry ->
                val taskId = backStackEntry.arguments!!.getLong("taskId")
                Log.d("AppNavGraph", "Navigating with taskId: $taskId")
                AddEditTaskScreen(
                    navController   = navController,
                    taskViewModel   = taskViewModel,
                    taskId          = taskId
                )
            }
        }
    }
}
package com.example.zenithtasks.Navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel // <--- ADD THIS IMPORT
import com.example.zenithtasks.ui.ui.screens.AddEditTaskScreen
import com.example.zenithtasks.ui.ui.screens.TaskBoardScreen
import com.example.zenithtasks.viewmodel.TaskViewModel // Ensure TaskViewModel is imported

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.TaskBoardScreen.route
    ) {
        composable(route = Screen.TaskBoardScreen.route) { backStackEntry ->
            val taskViewModel: TaskViewModel = hiltViewModel(backStackEntry) // <--- Get ViewModel via Hilt
            TaskBoardScreen(navController = navController, taskViewModel = taskViewModel)
        }
        composable(
            route = Screen.AddEditTaskScreen.route,
            arguments = Screen.AddEditTaskScreen.arguments
        ) { backStackEntry ->
            val taskViewModel: TaskViewModel = hiltViewModel(backStackEntry) // <--- Get ViewModel via Hilt
            val taskId = backStackEntry.arguments?.getLong("taskId")
            AddEditTaskScreen(navController = navController, taskViewModel = taskViewModel, taskId = taskId)
        }
    }
}
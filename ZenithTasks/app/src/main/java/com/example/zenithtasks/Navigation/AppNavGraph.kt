package com.example.zenithtasks.Navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.zenithtasks.ui.ui.screens.AddEditTaskScreen
import com.example.zenithtasks.ui.ui.screens.TaskBoardScreen
import com.example.zenithtasks.viewmodel.TaskViewModel

// This is where you define your overall navigation graph
@Composable
fun AppNavigation(navController: NavHostController, taskViewModel: TaskViewModel) {
    NavHost(
        navController = navController,
        startDestination = Screen.TaskBoardScreen.route // Define your starting screen
    ) {
        // Define the TaskBoardScreen as a composable destination
        composable(route = Screen.TaskBoardScreen.route) {
            TaskBoardScreen(navController = navController, taskViewModel = taskViewModel) // Pass the navController
        }

        // Define the AddEditTaskScreen as another composable destination
        composable(
            route = Screen.AddEditTaskScreen.route,
            arguments = listOf(navArgument("taskId") {
                type = NavType.LongType     // Remove .nullable()
                defaultValue = -1L          // Keep the default value
                nullable = false            // LongType must be non-nullable
            })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId")
            AddEditTaskScreen(
                navController = navController,
                taskViewModel = taskViewModel,
                taskId = if (taskId !=null && taskId !=-1L ) taskId else null
            )
        }
        // Add other screens here as you create them

    }
}
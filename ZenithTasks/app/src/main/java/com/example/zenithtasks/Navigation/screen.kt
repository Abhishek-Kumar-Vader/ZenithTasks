package com.example.zenithtasks.Navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument // <-- NEW IMPORT


sealed class Screen(val route: String, val arguments: List<NamedNavArgument> = emptyList()) { // NEW: Add arguments parameter

        object TaskBoardScreen : Screen("task_board_screen") // No arguments for this screen

        object AddEditTaskScreen : Screen(
            route = "add_edit_task_screen?taskId={taskId}",
            arguments = listOf( // <--- NEW: Define arguments for this screen
                navArgument("taskId") { // Match the name "{taskId}" from the route
                    type = NavType.LongType     // Remove .nullable()
                    defaultValue = -1L          // Keep the default value
                    nullable = false // Allow taskId to be null (or the default -1L)
                    }
                )
        ) {
            // Function to create the route with or without a taskId
            fun createRoute(taskId: Long? = null): String {
                // Use the default value in the route if taskId is null or -1L, otherwise use the actual ID
                return if (taskId != null && taskId != -1L) {
                    "add_edit_task_screen?taskId=$taskId"
                } else {
                    // If taskId is null or -1L, navigate to the base route without the parameter.
                    // The NavArgument's defaultValue will then apply.
                    "add_edit_task_screen"
                }
            }
        }
}
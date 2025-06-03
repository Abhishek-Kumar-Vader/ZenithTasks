package com.example.zenithtasks.Navigation

sealed class Screen(val route: String, val title: String) { // Added title for bottom nav labels
    object TaskBoard : Screen("task_board_screen", "Board") // Keeping this for now, might be removed later
    object AddEditTask : Screen("add_edit_task_screen", "Add/Edit Task") {
        fun createRoute(taskId: Long? = null) = "add_edit_task_screen?taskId=$taskId"
    }

    // New screens for bottom navigation tabs
    object TodoTasks : Screen("todo_tasks_screen", "To Do")
    object InProgressTasks : Screen("in_progress_tasks_screen", "In Progress")
    object DoneTasks : Screen("done_tasks_screen", "Done")
    object CancelledTasks : Screen("cancelled_tasks_screen", "Cancelled")

    // Helper object for easy access to routes, if you prefer
    companion object {
        const val TODO_TASKS_ROUTE = "todo_tasks_screen"
        const val IN_PROGRESS_TASKS_ROUTE = "in_progress_tasks_screen"
        const val DONE_TASKS_ROUTE = "done_tasks_screen"
        const val CANCELLED_TASKS_ROUTE = "cancelled_tasks_screen"
        const val ADD_EDIT_TASK_ROUTE = "add_edit_task_screen"
    }
}

// If you were using a top-level object named Screens, you would update it like this:
object Screens {
//    val TaskBoard = "task_board_screen" // Keep for now
//    val AddEditTask = object { // Nested object for route creation
//        val route = "add_edit_task_screen"
//        fun createRoute(taskId: Long? = null) = "add_edit_task_screen?taskId=$taskId"
//    }

    // New routes for tabbed screens
    val TodoTasks = "todo_tasks_screen"
    val InProgressTasks = "in_progress_tasks_screen"
    val DoneTasks = "done_tasks_screen"
    val CancelledTasks = "cancelled_tasks_screen"
}
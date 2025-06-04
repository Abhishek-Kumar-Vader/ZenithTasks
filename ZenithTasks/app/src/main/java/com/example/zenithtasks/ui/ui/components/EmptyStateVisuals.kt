package com.example.zenithtasks.ui.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.zenithtasks.R
import com.example.zenithtasks.data.TaskStatus // Import TaskStatus
import com.example.zenithtasks.ui.ui.themes.ZenithTasksTheme

@Composable
fun EmptyStateVisuals(
    status: TaskStatus?, // Nullable to handle cases like search or general empty state
    modifier: Modifier = Modifier,
    message: String? = null // NEW: Added optional message parameter
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // You can use different images based on status if you have them,
        // or a generic one if not specified.
        val imageRes = when (status) {
            TaskStatus.TODO -> R.drawable.baseline_add_24 // Assuming you have these drawables
            TaskStatus.IN_PROGRESS -> R.drawable.baseline_add_24
            TaskStatus.DONE -> R.drawable.baseline_add_24
            TaskStatus.CANCELLED -> R.drawable.baseline_add_24
            TaskStatus.ARCHIVED -> R.drawable.baseline_add_24 // Assuming you have an archive icon
            null -> R.drawable.baseline_add_24 // Generic empty state icon
        }

        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null, // Content description can be dynamic if needed
            modifier = Modifier.size(120.dp),
            colorFilter = ColorFilter.tint(Color.Gray)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message ?: when (status) { // Use provided message or default
                TaskStatus.TODO -> "No tasks to do! Time to relax or add a new one."
                TaskStatus.IN_PROGRESS -> "No tasks in progress. Keep up the good work!"
                TaskStatus.DONE -> "You haven't completed any tasks yet. Let's get started!"
                TaskStatus.CANCELLED -> "No cancelled tasks here."
                TaskStatus.ARCHIVED -> "No archived tasks found."
                null -> "Nothing to show here yet."
            },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyStateVisualsPreview() {
    ZenithTasksTheme {
        Column {
            EmptyStateVisuals(status = TaskStatus.TODO)
            Spacer(Modifier.height(16.dp))
            EmptyStateVisuals(status = TaskStatus.ARCHIVED, message = "Custom archived message!")
            Spacer(Modifier.height(16.dp))
            EmptyStateVisuals(status = null, message = "No search results.")
        }
    }
}
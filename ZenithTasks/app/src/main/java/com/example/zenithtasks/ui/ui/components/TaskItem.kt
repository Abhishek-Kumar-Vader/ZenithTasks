package com.example.zenithtasks.ui.ui.components

import android.content.res.Configuration // Added for Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.zenithtasks.data.Priority
import com.example.zenithtasks.data.Task
import com.example.zenithtasks.data.TaskStatus
import com.example.zenithtasks.ui.ui.themes.ZenithTasksTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale // Added for SimpleDateFormat

@Composable
fun TaskItem(
    task: Task,
    modifier: Modifier = Modifier,
    onTaskClick: (Long) -> Unit,
    onDeleteClick: (Task) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onTaskClick(task.id) }, // Make the entire row clickable
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (task.description?.isNotBlank() == true) {
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(
                text = "Status: ${task.status.name.replace("_", " ")}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
            task.dueDate?.let {
                // CORRECTED LINE HERE: Pass 'it' directly to format()
                Text(
                    text = "Due: ${SimpleDateFormat("MMM dd,yyyy", Locale.getDefault()).format(it)}", // Added Locale.getDefault() for robustness
                    style = MaterialTheme.typography.bodySmall
                )
            }
            // You might want to add Priority display here as well, similar to TaskItemCard
            Text(
                text = "Priority: ${task.priority.name}",
                style = MaterialTheme.typography.bodySmall,
                color = when (task.priority) {
                    Priority.URGENT -> MaterialTheme.colorScheme.error
                    Priority.HIGH -> MaterialTheme.colorScheme.primary
                    Priority.MEDIUM -> MaterialTheme.colorScheme.secondary
                    Priority.LOW -> MaterialTheme.colorScheme.tertiary
                }
            )
        }

        IconButton(
            onClick = { onDeleteClick(task) },
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Task",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun TaskItemPreview() {
    ZenithTasksTheme {
        Column { // Wrap in a Column to display multiple items
            TaskItem(
                task = Task(
                    id = 1,
                    title = "Sample To Do Task",
                    description = "This is a detailed description for the task.",
                    isCompleted = false,
                    dueDate = Date(), // Use current date for preview
                    status = TaskStatus.TODO,
                    priority = Priority.HIGH
                ),
                onTaskClick = {},
                onDeleteClick = {}
            )
            Spacer(Modifier.height(8.dp)) // Add space between preview items
            TaskItem(
                task = Task(
                    id = 2,
                    title = "Another Task",
                    description = null, // Null description
                    isCompleted = true,
                    dueDate = Date(System.currentTimeMillis() + 86400000), // Tomorrow
                    status = TaskStatus.DONE,
                    priority = Priority.LOW
                ),
                onTaskClick = {},
                onDeleteClick = {}
            )
        }
    }
}
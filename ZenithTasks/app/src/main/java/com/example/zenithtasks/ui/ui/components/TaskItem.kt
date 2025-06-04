// In TaskItem.kt
package com.example.zenithtasks.ui.ui.components

import android.content.res.Configuration
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
import androidx.compose.material.icons.filled.Archive // Import Archive icon
import androidx.compose.material.icons.filled.Restore // NEW IMPORT for Restore icon
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Checkbox
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import java.util.Locale

@Composable
fun TaskItem(
    task: Task,
    onTaskClick: (Long) -> Unit,
    onDeleteClick: (Task) -> Unit,
    onToggleCompletion: (Task, Boolean) -> Unit,
    onArchiveClick: (Task) -> Unit,
    onRestoreClick: (Task) -> Unit // NEW: Add onRestoreClick parameter
) {
    val showDeleteDialog = remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTaskClick(task.id) }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { isChecked ->
                    onToggleCompletion(task, isChecked)
                }
            )
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(
                    text = task.title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                task.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                task.dueDate?.let {
                    val sdf = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                    Text(
                        text = "Due: ${sdf.format(it)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "Priority: ${task.priority.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Status: ${task.status.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Row {
            // Conditionally display Archive or Restore button
            if (task.status == TaskStatus.ARCHIVED) {
                IconButton(onClick = { onRestoreClick(task) }) { // NEW: Restore action
                    Icon(
                        imageVector = Icons.Default.Restore,
                        contentDescription = "Restore Task",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                IconButton(onClick = { onArchiveClick(task) }) { // Archive action (for non-archived tasks)
                    Icon(
                        imageVector = Icons.Default.Archive,
                        contentDescription = "Archive Task",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = { showDeleteDialog.value = true }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Task",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = { Text("Confirm Delete") },
            text = { Text("Are you sure you want to delete this task?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteClick(task)
                        showDeleteDialog.value = false
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDeleteDialog.value = false // Just dismiss the dialog
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun TaskItemPreview() {
    ZenithTasksTheme {
        Column {
            TaskItem(
                task = Task(
                    id = 1,
                    title = "Buy Groceries",
                    description = "Milk, Eggs, Bread",
                    isCompleted = false,
                    dueDate = Date(),
                    status = TaskStatus.TODO,
                    priority = Priority.HIGH
                ),
                onTaskClick = {},
                onDeleteClick = {},
                onToggleCompletion = { _, _ -> },
                onArchiveClick = {}, // Provide empty lambda for preview
                onRestoreClick = {} // Provide empty lambda for preview
            )
            Spacer(Modifier.height(8.dp))
            TaskItem(
                task = Task(
                    id = 2,
                    title = "Finished Archived Report",
                    description = "Draft for Q2 earnings",
                    isCompleted = true,
                    dueDate = Date(),
                    status = TaskStatus.ARCHIVED, // Preview archived task
                    priority = Priority.URGENT
                ),
                onTaskClick = {},
                onDeleteClick = {},
                onToggleCompletion = { _, _ -> },
                onArchiveClick = {}, // Provide empty lambda for preview
                onRestoreClick = {} // Provide empty lambda for preview
            )
        }
    }
}
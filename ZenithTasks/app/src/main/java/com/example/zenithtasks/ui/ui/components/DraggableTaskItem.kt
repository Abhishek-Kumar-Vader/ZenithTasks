package com.example.zenithtasks.ui.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.zenithtasks.data.Task
import com.example.zenithtasks.data.TaskStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.clickable // Ensure clickable is here for onClick
import androidx.compose.ui.tooling.preview.Preview // <--- ADD THIS IMPORT
import com.example.zenithtasks.ui.ui.themes.ZenithTasksTheme
import java.util.Calendar

@Composable
fun DraggableTaskItem(
    task: Task,
    onTaskClick: (Long) -> Unit,
    onDeleteClick: (Task) -> Unit,
    onDragStart: (Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
    currentOffset: Offset = Offset.Zero,
    isDragging: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .zIndex(if (isDragging) 1f else 0f)
            .graphicsLayer {
                translationX = currentOffset.x
                translationY = currentOffset.y
                shadowElevation = if (isDragging) 8.dp.toPx() else 2.dp.toPx()
                alpha = if (isDragging) 0.8f else 1f
            }
            .pointerInput(task.id) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { offset ->
                        onDragStart(offset)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount)
                    },
                    onDragEnd = {
                        onDragEnd()
                    },
                    onDragCancel = {
                        onDragEnd() // TEMPORARY DEBUG HACK: Call onDragEnd even on cancel
                        onDragCancel() // Still call original onDragCancel if it does anything else
                    }
                )
            },
        onClick = {
            if (!isDragging) {
                onTaskClick(task.id)
            }
        },
        colors = CardDefaults.cardColors(
            containerColor = when {
                isDragging -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
                task.isCompleted -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                else -> MaterialTheme.colorScheme.surface
            }
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header row with drag handle, title, and delete button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Drag handle
                Icon(
                    imageVector = Icons.Default.DragHandle,
                    contentDescription = "Drag Handle",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Task title
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                // Delete button (only show if not dragging)
                if (!isDragging) {
                    IconButton(
                        onClick = {
                            onDeleteClick(task)
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Task",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Task description (if available)
            if (!task.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.description!!, // Use !! if you're sure it's not null here or add a default
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // --- ADDED: Creation Date ---
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Created: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(task.creationDate))}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            // --- END ADDED Creation Date ---

            // Due date (if available) - THIS WAS ALREADY HERE, JUST CONFIRMING PLACEMENT
            task.dueDate?.let { dueDate ->
                Spacer(modifier = Modifier.height(8.dp)) // Adjust spacing as needed
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(
                                color = if (dueDate < System.currentTimeMillis()) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.primary
                                },
                                shape = CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Due: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(dueDate))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (dueDate < System.currentTimeMillis()) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        }
                    )
                }
            }

            // Status indicator (only show on floating item during drag)
            if (isDragging) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .background(
                            color = getStatusColor(task.status),
                            shape = MaterialTheme.shapes.extraSmall
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = task.status.name.replace("_", " "),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun getStatusColor(status: TaskStatus): Color {
    return when (status) {
        TaskStatus.TODO -> MaterialTheme.colorScheme.secondary
        TaskStatus.IN_PROGRESS -> MaterialTheme.colorScheme.tertiary
        TaskStatus.DONE -> MaterialTheme.colorScheme.primary
    }
}

@Preview(showBackground = true, widthDp = 300, showSystemUi = false) // You can adjust widthDp as needed
@Composable
fun DraggableTaskItemPreview() {
    ZenithTasksTheme { // Wrap your preview in your app's theme
        val sampleTask1 = Task(
            id = 1L,
            title = "Design new app icon and splash screen",
            description = "Explore different visual styles and color palettes. Focus on a modern, clean look.",
            isCompleted = false,
            dueDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 3) }.timeInMillis, // 3 days from now
            status = TaskStatus.TODO,
            creationDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -5) }.timeInMillis // 5 days ago
        )

        val sampleTask2 = Task(
            id = 2L,
            title = "Implement Dagger Hilt for DI",
            description = "Refactor existing manual dependency injection setup to use Hilt modules.",
            isCompleted = false,
            dueDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }.timeInMillis, // Yesterday (overdue)
            status = TaskStatus.IN_PROGRESS,
            creationDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -10) }.timeInMillis // 10 days ago
        )

        val sampleTask3 = Task(
            id = 3L,
            title = "Write unit tests for ViewModel logic",
            description = null, // No description
            isCompleted = true,
            dueDate = null, // No due date
            status = TaskStatus.DONE,
            creationDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -2) }.timeInMillis // 2 days ago
        )

        Column(modifier = Modifier.padding(16.dp)) { // Add a column to stack multiple previews
            DraggableTaskItem(
                task = sampleTask1,
                onTaskClick = { /* Preview click */ },
                onDeleteClick = { /* Preview delete */ },
                onDragStart = { /* Preview drag */ },
                onDrag = { /* Preview drag */ },
                onDragEnd = { /* Preview drag */ },
                onDragCancel = { /* Preview drag */ },
                isDragging = false // Not dragging in preview
            )
            Spacer(modifier = Modifier.height(16.dp))
            DraggableTaskItem(
                task = sampleTask2,
                onTaskClick = { /* Preview click */ },
                onDeleteClick = { /* Preview delete */ },
                onDragStart = { /* Preview drag */ },
                onDrag = { /* Preview drag */ },
                onDragEnd = { /* Preview drag */ },
                onDragCancel = { /* Preview drag */ },
                isDragging = false // Not dragging in preview
            )
            Spacer(modifier = Modifier.height(16.dp))
            DraggableTaskItem(
                task = sampleTask3,
                onTaskClick = { /* Preview click */ },
                onDeleteClick = { /* Preview delete */ },
                onDragStart = { /* Preview drag */ },
                onDrag = { /* Preview drag */ },
                onDragEnd = { /* Preview drag */ },
                onDragCancel = { /* Preview drag */ },
                isDragging = false // Not dragging in preview
            )
            Spacer(modifier = Modifier.height(16.dp))
            // You can also preview the dragging state
            DraggableTaskItem(
                task = sampleTask1.copy(status = TaskStatus.IN_PROGRESS), // Show it as if it's being dragged to another status
                onTaskClick = { /* Preview click */ },
                onDeleteClick = { /* Preview delete */ },
                onDragStart = { /* Preview drag */ },
                onDrag = { /* Preview drag */ },
                onDragEnd = { /* Preview drag */ },
                onDragCancel = { /* Preview drag */ },
                isDragging = true // Show in dragging state
            )
        }
    }
}
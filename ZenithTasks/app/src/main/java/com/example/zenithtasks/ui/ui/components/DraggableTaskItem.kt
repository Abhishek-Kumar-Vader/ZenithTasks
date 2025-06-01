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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
// Check this import carefully! Make sure it's the Compose UI Color.
import androidx.compose.ui.graphics.Color // <--- ENSURE THIS IS THE ONLY Color IMPORT
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
import androidx.compose.foundation.clickable
import androidx.compose.ui.tooling.preview.Preview
import java.util.Calendar

// Imports for Task Priority and Colors
import com.example.zenithtasks.data.TaskPriority
import com.example.zenithtasks.ui.ui.themes.PriorityHigh
import com.example.zenithtasks.ui.ui.themes.PriorityLow
import com.example.zenithtasks.ui.ui.themes.PriorityMedium
import com.example.zenithtasks.ui.ui.themes.PriorityUrgent
import com.example.zenithtasks.ui.ui.themes.ZenithTasksTheme


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
                    text = task.description!!,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(4.dp)) // Spacer before dates/priority

            // --- Priority and Dates Row ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp), // Space between elements
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Priority Indicator
                Box(
                    modifier = Modifier
                        .size(10.dp) // Slightly larger dot for priority
                        .background(
                            color = getPriorityColor(task.priority), // <--- Use priority color
                            shape = CircleShape
                        )
                )
                Text(
                    text = task.priority.name.replace("_", " "), // Display priority name
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                // Creation Date
                Text(
                    text = "Created: ${SimpleDateFormat("MMM dd,yyyy", Locale.getDefault()).format(Date(task.creationDate))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                // Due date (if available)
                task.dueDate?.let { dueDate ->
                    Spacer(modifier = Modifier.width(8.dp)) // Spacer between creation and due date
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
                        text = "Due: ${SimpleDateFormat("MMM dd,yyyy", Locale.getDefault()).format(Date(dueDate))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (dueDate < System.currentTimeMillis()) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        }
                    )
                }
            }
            // --- End Priority and Dates Row ---


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

@Composable
private fun getPriorityColor(priority: TaskPriority): Color {
    return when (priority) {
        TaskPriority.LOW -> PriorityLow
        TaskPriority.MEDIUM -> PriorityMedium
        TaskPriority.HIGH -> PriorityHigh
        TaskPriority.URGENT -> PriorityUrgent
    }
}


@Preview(showBackground = true, widthDp = 300, showSystemUi = false)
@Composable
fun DraggableTaskItemPreview() {
    ZenithTasksTheme {
        val sampleTask1 = Task(
            id = 1L,
            title = "Design new app icon and splash screen",
            description = "Explore different visual styles and color palettes. Focus on a modern, clean look.",
            isCompleted = false,
            dueDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 3) }.timeInMillis,
            status = TaskStatus.TODO,
            creationDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -5) }.timeInMillis,
            priority = TaskPriority.HIGH
        )

        val sampleTask2 = Task(
            id = 2L,
            title = "Implement Dagger Hilt for DI",
            description = "Refactor existing manual dependency injection setup to use Hilt modules.",
            isCompleted = false,
            dueDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }.timeInMillis,
            status = TaskStatus.IN_PROGRESS,
            creationDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -10) }.timeInMillis,
            priority = TaskPriority.URGENT
        )

        val sampleTask3 = Task(
            id = 3L,
            title = "Write unit tests for ViewModel logic",
            description = null,
            isCompleted = true,
            dueDate = null,
            status = TaskStatus.DONE,
            creationDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -2) }.timeInMillis,
            priority = TaskPriority.LOW
        )

        val sampleTask4 = sampleTask1.copy(
            id = 4L,
            title = "Review Q3 Marketing Strategy",
            description = "Consolidate feedback from sales and product teams.",
            status = TaskStatus.TODO,
            priority = TaskPriority.MEDIUM,
            dueDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 10) }.timeInMillis
        )


        Column(modifier = Modifier.padding(16.dp)) {
            DraggableTaskItem(
                task = sampleTask1,
                onTaskClick = { /* Preview click */ },
                onDeleteClick = { /* Preview delete */ },
                onDragStart = { /* Preview drag */ },
                onDrag = { /* Preview drag */ },
                onDragEnd = { /* Preview drag */ },
                onDragCancel = { /* Preview drag */ },
                isDragging = false
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
                isDragging = false
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
                isDragging = false
            )
            Spacer(modifier = Modifier.height(16.dp))
            DraggableTaskItem(
                task = sampleTask4,
                onTaskClick = { /* Preview click */ },
                onDeleteClick = { /* Preview delete */ },
                onDragStart = { /* Preview drag */ },
                onDrag = { /* Preview drag */ },
                onDragEnd = { /* Preview drag */ },
                onDragCancel = { /* Preview drag */ },
                isDragging = false
            )
            Spacer(modifier = Modifier.height(16.dp))
            DraggableTaskItem(
                task = sampleTask1.copy(status = TaskStatus.IN_PROGRESS, priority = TaskPriority.HIGH),
                onTaskClick = { /* Preview click */ },
                onDeleteClick = { /* Preview delete */ },
                onDragStart = { /* Preview drag */ },
                onDrag = { /* Preview drag */ },
                onDragEnd = { /* Preview drag */ },
                onDragCancel = { /* Preview drag */ },
                currentOffset = Offset(20f, 20f),
                isDragging = true
            )
        }
    }
}
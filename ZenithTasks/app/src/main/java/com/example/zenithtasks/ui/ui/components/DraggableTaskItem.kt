package com.example.zenithtasks.ui.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress // Changed from detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.zenithtasks.data.Task
import java.text.SimpleDateFormat // Added for date formatting if not present

// --- NEW IMPORTS FOR VISUAL DRAG ---
import androidx.compose.ui.graphics.graphicsLayer // For translationX/Y
import androidx.compose.ui.zIndex // To make dragged item appear on top
// --- END NEW IMPORTS ---

@Composable
fun DraggableTaskItem(
    task: Task,
    onTaskClick: (Long) -> Unit,
    onDeleteClick: (Task) -> Unit,
    onDragStart: (Offset) -> Unit, // Pass initial touch point to parent
    onDrag: (Offset) -> Unit, // Pass delta movement to parent
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
    isDragging: Boolean = false,
    // --- NEW: Parameter for parent-controlled visual offset ---
    currentOffset: Offset = Offset.Zero, // This offset will be applied by the parent
    modifier: Modifier = Modifier
) {
    // This localDragOffset tracks the drag within THIS composable's pointerInput
    var localDragOffset by remember { mutableStateOf(Offset.Zero) }

    val density = LocalDensity.current // Keep this if used for other density calculations
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp) // Add padding here for spacing between items
            .zIndex(if (isDragging) 1f else 0f) // Bring dragged item to front
            .graphicsLayer { // This is where the visual movement is applied
                // Combine the parent's controlled offset (currentOffset) with the local drag offset
                translationX = currentOffset.x + localDragOffset.x
                translationY = currentOffset.y + localDragOffset.y
                shadowElevation = if (isDragging) 8.dp.toPx() else 2.dp.toPx() // Apply elevation
            }
            .pointerInput(Unit) { // Detect drag gestures
                detectDragGesturesAfterLongPress( // Changed to AfterLongPress
                    onDragStart = { offset ->
                        localDragOffset = Offset.Zero // Reset local offset for new drag
                        onDragStart(offset) // Notify parent of drag start
                    },
                    onDrag = { change, dragAmount ->
                        change.consume() // Consume the event to prevent other gestures
                        localDragOffset += dragAmount // Accumulate local drag movement
                        onDrag(dragAmount) // Notify parent about the change in drag amount
                    },
                    onDragEnd = {
                        localDragOffset = Offset.Zero // Reset local offset after drag
                        onDragEnd() // Notify parent that drag ended
                    },
                    onDragCancel = {
                        localDragOffset = Offset.Zero // Reset local offset on cancel
                        onDragCancel() // Notify parent that drag was cancelled
                    }
                )
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isDragging) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDragging) 8.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { if (!isDragging) onTaskClick(task.id) } // Only clickable if not dragging
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Drag handle (Optional, but good for UX)
            Icon(
                imageVector = Icons.Default.DragHandle,
                contentDescription = "Drag Handle",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 8.dp)
            )

            // Task content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isDragging) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )

                Spacer(modifier = Modifier.height(4.dp))

                if (task.description?.isNotBlank() == true) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isDragging) {
                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Text(
                    text = "Status: ${task.status.name.replace("_", " ")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDragging) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )

                task.dueDate?.let {
                    Text(
                        text = "Due: ${SimpleDateFormat("MMM dd, yyyy").format(java.util.Date(it))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDragging) {
                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        }
                    )
                }
            }

            // Delete button (only show when not dragging)
            if (!isDragging) {
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
    }
}
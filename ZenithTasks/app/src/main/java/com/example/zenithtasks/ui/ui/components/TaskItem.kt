package com.example.zenithtasks.ui.ui.components

import androidx.compose.foundation.clickable // <-- NEW IMPORT
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.zenithtasks.data.Task
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Arrangement

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
            .padding(16.dp), // Apply padding to the Row instead of the inner Column
        verticalAlignment = Alignment.CenterVertically, // Vertically align items in the row
        horizontalArrangement = Arrangement.SpaceBetween // Push the delete icon to the end
    ) {
        Column(
            // Remove the padding from here as it's now on the Row
            modifier = Modifier.weight(1f) // Makes this Column take up available space, pushing IconButton to end
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (task.description?.isNotBlank() == true) {
                Text(
                    text = task.description, // No need for .toString() if description is already String?
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(
                text = "Status: ${task.status.name.replace("_", " ")}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
            // Add due date if you implement it later
            task.dueDate?.let {
                // You might want to format the Long timestamp to a readable date string here
                Text(
                    text = "Due: ${java.text.SimpleDateFormat("MMM dd, yyyy").format(java.util.Date(it))}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // --- ADD THE DELETE BUTTON HERE ---
        IconButton(
            onClick = { onDeleteClick(task) }, // Call the onDeleteClick lambda
            modifier = Modifier.align(Alignment.CenterVertically) // Ensure it's centered vertically
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Task",
                tint = MaterialTheme.colorScheme.error // Make the icon red for danger
            )
        }
    }
}
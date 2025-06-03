package com.example.zenithtasks.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

enum class TaskStatus {
    TODO,
    IN_PROGRESS,
    DONE,
    CANCELLED
}

// <--- ADD THIS Priority ENUM DEFINITION HERE IN Task.kt --- >
enum class Priority {
    URGENT,
    HIGH,
    MEDIUM,
    LOW;

    // Helper to get a list in desired sorting order
    companion object {
        fun getOrderedPriorities(): List<Priority> {
            return listOf(URGENT, HIGH, MEDIUM, LOW)
        }
    }
}
// <--- END ADDITION --- >

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val description: String? = null,
    val isCompleted: Boolean = false,
    val dueDate: Date? = null,
    val status: TaskStatus = TaskStatus.TODO,
    val priority: Priority = Priority.MEDIUM,
    val creationDate: Date = Date(System.currentTimeMillis())
)
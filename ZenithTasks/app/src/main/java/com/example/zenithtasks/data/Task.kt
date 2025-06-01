package com.example.zenithtasks.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

enum class TaskStatus {
    TODO,
    IN_PROGRESS,
    DONE
}

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L, // CHANGED FROM Int to Long
    val title: String,
    val description: String?,
    val isCompleted: Boolean = false,
    val dueDate: Long? = null,
    val status: TaskStatus = TaskStatus.TODO,
    val orderIndex: Int = 0,
    val creationDate: Long = System.currentTimeMillis(),
    val priority: TaskPriority = TaskPriority.MEDIUM
)
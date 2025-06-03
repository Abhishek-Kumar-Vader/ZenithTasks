package com.example.zenithtasks.data

import androidx.room.TypeConverter
import java.util.Date
import com.example.zenithtasks.data.TaskStatus // <--- ADD THIS IMPORT
import com.example.zenithtasks.data.Priority   // <--- ADD THIS IMPORT (now that Priority is in Task.kt)

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromTaskStatus(status: TaskStatus?): String? {
        return status?.name
    }

    @TypeConverter
    fun toTaskStatus(statusString: String?): TaskStatus? {
        return statusString?.let { TaskStatus.valueOf(it) }
    }

    // Renamed from TaskPriority to Priority for consistency (matching your Task.kt)
    @TypeConverter
    fun fromPriority(priority: Priority?): String? { // <--- Changed from TaskPriority to Priority
        return priority?.name
    }

    @TypeConverter
    fun toPriority(priorityString: String?): Priority? { // <--- Changed from TaskPriority to Priority
        return priorityString?.let { Priority.valueOf(it) }
    }
}
package com.example.zenithtasks.data

import androidx.room.TypeConverter
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
    @TypeConverter // <--- NEW TYPE CONVERTER FOR TASKPRIORITY
    fun fromTaskPriority(priority: TaskPriority?): String? {
        return priority?.name
    }
    @TypeConverter // <--- NEW TYPE CONVERTER FOR TASKPRIORITY
    fun toTaskPriority(priorityString: String?): TaskPriority? {
        return priorityString?.let { TaskPriority.valueOf(it) }
    }
}
package com.example.zenithtasks.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.zenithtasks.data.Converters
import com.example.zenithtasks.data.Task
import com.example.zenithtasks.data.TaskDao

// Define your entities and database version here.
// Make sure to increment version every time you change the schema.
@Database(entities = [Task::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "task_database" // Your database file name
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
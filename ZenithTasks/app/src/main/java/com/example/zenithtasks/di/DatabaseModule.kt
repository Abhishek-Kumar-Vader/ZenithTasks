package com.example.zenithtasks.di

import android.content.Context
import androidx.room.Room
import com.example.zenithtasks.data.TaskDao
import com.example.zenithtasks.data.AppDatabase
import com.example.zenithtasks.notifications.AlarmScheduler
import com.example.zenithtasks.notifications.AlarmSchedulerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // This module's bindings are available for the entire application lifecycle
object DatabaseModule {

    @Provides
    @Singleton // Ensure only one instance of the database throughout the app
    fun provideDatabase(
        @ApplicationContext context: Context // Hilt provides Application context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "task_db" // Your database name
        ).fallbackToDestructiveMigration() // Allows database schema changes by recreating DB (for development)
            .build()
    }

    @Provides
    @Singleton // TaskDao should also be a singleton if the database is
    fun provideTaskDao(database: AppDatabase): TaskDao {
        return database.taskDao()
    }

    // NEW: Provide AlarmScheduler
    @Provides
    @Singleton
    fun provideAlarmScheduler(@ApplicationContext context: Context): AlarmScheduler {
        return AlarmSchedulerImpl(context)
    }
}
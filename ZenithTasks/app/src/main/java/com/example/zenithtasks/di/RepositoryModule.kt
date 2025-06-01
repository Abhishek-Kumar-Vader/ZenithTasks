package com.example.zenithtasks.di

import com.example.zenithtasks.data.TaskDao
import com.example.zenithtasks.data.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // This module's bindings are available for the entire application lifecycle
object RepositoryModule {

    @Provides
    @Singleton // Ensure only one instance of the repository
    fun provideTaskRepository(taskDao: TaskDao): TaskRepository {
        return TaskRepository(taskDao)
    }
}
package com.example.zenithtasks.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Upsert // Handles both insert and update
    suspend fun upsert(task: Task): Long

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskById(taskId: Long): Flow<Task?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task): Int

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM tasks WHERE status = :status")
    fun getTasksByStatus(status: TaskStatus): Flow<List<Task>>

    @Query("SELECT * FROM tasks ORDER BY priority DESC, dueDate ASC")
    fun getAllTasks(): Flow<List<Task>>

    // NEW: Retrieves all archived tasks.
    @Query("SELECT * FROM tasks WHERE status = :status ORDER BY priority DESC, dueDate ASC")
    fun getArchivedTasks(status: TaskStatus = TaskStatus.ARCHIVED): Flow<List<Task>>
}
package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY dueDate ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE projectId = :projectId ORDER BY dueDate ASC")
    fun getTasksByProject(projectId: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE status = :status ORDER BY dueDate ASC")
    fun getTasksByStatus(status: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE priority = :priority ORDER BY dueDate ASC")
    fun getTasksByPriority(priority: String): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)
}

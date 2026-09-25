package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.ProjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY startDate DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
    fun getProjectById(id: Long): Flow<ProjectEntity?>

    @Query("SELECT * FROM projects WHERE customerId = :customerId ORDER BY startDate DESC")
    fun getProjectsByCustomer(customerId: Long): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE status = :status ORDER BY startDate DESC")
    fun getProjectsByStatus(status: String): Flow<List<ProjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity): Long

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Delete
    suspend fun deleteProject(project: ProjectEntity)
}

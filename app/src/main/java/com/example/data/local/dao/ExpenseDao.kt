package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entities.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE projectId = :projectId ORDER BY date DESC")
    fun getExpensesByProject(projectId: Long): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE category = :category ORDER BY date DESC")
    fun getExpensesByCategory(category: String): Flow<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity): Long

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)
}

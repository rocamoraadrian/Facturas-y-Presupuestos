package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.QuoteEntity
import com.example.data.local.entities.QuoteItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuoteDao {
    @Query("SELECT * FROM quotes ORDER BY date DESC")
    fun getAllQuotes(): Flow<List<QuoteEntity>>

    @Query("SELECT * FROM quotes WHERE id = :id LIMIT 1")
    fun getQuoteById(id: Long): Flow<QuoteEntity?>

    @Query("SELECT * FROM quotes WHERE projectId = :projectId ORDER BY date DESC")
    fun getQuotesByProject(projectId: Long): Flow<List<QuoteEntity>>

    @Query("SELECT * FROM quotes WHERE customerId = :customerId ORDER BY date DESC")
    fun getQuotesByCustomer(customerId: Long): Flow<List<QuoteEntity>>

    @Query("SELECT * FROM quotes WHERE status = :status ORDER BY date DESC")
    fun getQuotesByStatus(status: String): Flow<List<QuoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuote(quote: QuoteEntity): Long

    @Update
    suspend fun updateQuote(quote: QuoteEntity)

    @Delete
    suspend fun deleteQuote(quote: QuoteEntity)

    // Items
    @Query("SELECT * FROM quote_items WHERE quoteId = :quoteId")
    fun getItemsByQuoteId(quoteId: Long): Flow<List<QuoteItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuoteItems(items: List<QuoteItemEntity>)

    @Query("DELETE FROM quote_items WHERE quoteId = :quoteId")
    suspend fun deleteItemsByQuoteId(quoteId: Long)
}

package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.InvoiceEntity
import com.example.data.local.entities.InvoiceItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InvoiceDao {
    @Query("SELECT * FROM invoices ORDER BY date DESC")
    fun getAllInvoices(): Flow<List<InvoiceEntity>>

    @Query("SELECT * FROM invoices WHERE id = :id LIMIT 1")
    fun getInvoiceById(id: Long): Flow<InvoiceEntity?>

    @Query("SELECT * FROM invoices WHERE projectId = :projectId ORDER BY date DESC")
    fun getInvoicesByProject(projectId: Long): Flow<List<InvoiceEntity>>

    @Query("SELECT * FROM invoices WHERE customerId = :customerId ORDER BY date DESC")
    fun getInvoicesByCustomer(customerId: Long): Flow<List<InvoiceEntity>>

    @Query("SELECT * FROM invoices WHERE status = :status ORDER BY date DESC")
    fun getInvoicesByStatus(status: String): Flow<List<InvoiceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: InvoiceEntity): Long

    @Update
    suspend fun updateInvoice(invoice: InvoiceEntity)

    @Delete
    suspend fun deleteInvoice(invoice: InvoiceEntity)

    // Items
    @Query("SELECT * FROM invoice_items WHERE invoiceId = :invoiceId")
    fun getItemsByInvoiceId(invoiceId: Long): Flow<List<InvoiceItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoiceItems(items: List<InvoiceItemEntity>)
}

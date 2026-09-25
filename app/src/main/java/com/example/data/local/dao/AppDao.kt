package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.CompanySettingsEntity
import com.example.data.local.entities.CustomerEntity
import com.example.data.local.entities.DemoSessionEntity
import com.example.data.local.entities.ExpenseEntity
import com.example.data.local.entities.InvoiceEntity
import com.example.data.local.entities.PaymentEntity
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.ProjectEntity
import com.example.data.local.entities.ProjectPhaseEntity
import com.example.data.local.entities.ProjectPhotoEntity
import com.example.data.local.entities.QuoteEntity
import com.example.data.local.entities.QuoteItemEntity
import com.example.data.local.entities.TaskEntity
import com.example.data.local.entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao : UserDao, CustomerDao, ProjectDao, QuoteDao, InvoiceDao, ExpenseDao, TaskDao {

    // PAYMENTS
    @Query("SELECT * FROM payments ORDER BY date DESC")
    fun getAllPayments(): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE invoiceId = :invoiceId ORDER BY date DESC")
    fun getPaymentsByInvoice(invoiceId: Long): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE projectId = :projectId ORDER BY date DESC")
    fun getPaymentsByProject(projectId: Long): Flow<List<PaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity): Long

    // PRODUCTS / PRICE LIST
    @Query("SELECT * FROM products ORDER BY category ASC, name ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE category = :category ORDER BY name ASC")
    fun getProductsByCategory(category: String): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    // PROJECT PHASES
    @Query("SELECT * FROM project_phases WHERE projectId = :projectId ORDER BY sortOrder ASC")
    fun getPhasesByProject(projectId: Long): Flow<List<ProjectPhaseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhases(phases: List<ProjectPhaseEntity>)

    @Update
    suspend fun updatePhase(phase: ProjectPhaseEntity)

    @Query("DELETE FROM project_phases WHERE projectId = :projectId")
    suspend fun deletePhasesByProject(projectId: Long)

    // PROJECT PHOTOS
    @Query("SELECT * FROM project_photos WHERE projectId = :projectId ORDER BY createdAt DESC")
    fun getPhotosByProject(projectId: Long): Flow<List<ProjectPhotoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: ProjectPhotoEntity): Long

    @Delete
    suspend fun deletePhoto(photo: ProjectPhotoEntity)

    // COMPANY SETTINGS
    @Query("SELECT * FROM company_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<CompanySettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: CompanySettingsEntity)

    // DEMO SESSIONS
    @Query("SELECT * FROM demo_sessions ORDER BY createdAt DESC")
    fun getAllDemoSessions(): Flow<List<DemoSessionEntity>>

    @Query("SELECT * FROM demo_sessions WHERE accessCode = :code LIMIT 1")
    suspend fun findDemoSessionByCode(code: String): DemoSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDemoSession(session: DemoSessionEntity): Long

    @Update
    suspend fun updateDemoSession(session: DemoSessionEntity)
}

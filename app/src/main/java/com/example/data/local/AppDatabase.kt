package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.local.converters.Converters
import com.example.data.local.dao.AppDao
import com.example.data.local.dao.CustomerDao
import com.example.data.local.dao.ExpenseDao
import com.example.data.local.dao.InvoiceDao
import com.example.data.local.dao.ProjectDao
import com.example.data.local.dao.QuoteDao
import com.example.data.local.dao.TaskDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entities.CompanySettingsEntity
import com.example.data.local.entities.CustomerEntity
import com.example.data.local.entities.DemoSessionEntity
import com.example.data.local.entities.ExpenseEntity
import com.example.data.local.entities.InvoiceEntity
import com.example.data.local.entities.InvoiceItemEntity
import com.example.data.local.entities.PaymentEntity
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.ProjectEntity
import com.example.data.local.entities.ProjectPhaseEntity
import com.example.data.local.entities.ProjectPhotoEntity
import com.example.data.local.entities.QuoteEntity
import com.example.data.local.entities.QuoteItemEntity
import com.example.data.local.entities.TaskEntity
import com.example.data.local.entities.UserEntity

@Database(
    entities = [
        UserEntity::class,
        CustomerEntity::class,
        ProjectEntity::class,
        QuoteEntity::class,
        QuoteItemEntity::class,
        InvoiceEntity::class,
        InvoiceItemEntity::class,
        ExpenseEntity::class,
        PaymentEntity::class,
        ProductEntity::class,
        TaskEntity::class,
        ProjectPhaseEntity::class,
        ProjectPhotoEntity::class,
        CompanySettingsEntity::class,
        DemoSessionEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    // Unified & Modular DAOs
    abstract fun appDao(): AppDao
    abstract fun userDao(): UserDao
    abstract fun customerDao(): CustomerDao
    abstract fun projectDao(): ProjectDao
    abstract fun quoteDao(): QuoteDao
    abstract fun invoiceDao(): InvoiceDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "reformas_pro.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

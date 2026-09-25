package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad de Usuario / Empleado
 */
@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val email: String,
    val phone: String,
    val role: String, // "ADMIN" or "EMPLOYEE"
    val canCreateQuotes: Boolean = true,
    val canAddExpenses: Boolean = true,
    val canUploadPhotos: Boolean = true,
    val canManageUsers: Boolean = false,
    val canManageSettings: Boolean = false,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Entidad de Cliente
 */
@Entity(
    tableName = "customers",
    indices = [Index(value = ["phone"])]
)
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val company: String = "",
    val phone: String,
    val whatsapp: String = "",
    val email: String = "",
    val address: String = "",
    val siteAddress: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Entidad de Proyecto / Obra
 * Relación: Customer (1) -> Projects (N)
 */
@Entity(
    tableName = "projects",
    foreignKeys = [
        ForeignKey(
            entity = CustomerEntity::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["customerId"])]
)
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerId: Long,
    val name: String,
    val address: String,
    val projectType: String, // "Cocina", "Baño", "Reforma integral", etc.
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000),
    val status: String = "En curso", // "Planificado", "En preparación", "En curso", "Pausado", "Finalizado", "Cancelado"
    val budgetAmount: Double = 0.0,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Entidad de Presupuesto
 * Relación: Customer (1) -> Quotes (N), Project (1) -> Quotes (N) [Opcional]
 */
@Entity(
    tableName = "quotes",
    foreignKeys = [
        ForeignKey(
            entity = CustomerEntity::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["quoteNumber"], unique = true),
        Index(value = ["customerId"]),
        Index(value = ["projectId"])
    ]
)
data class QuoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val quoteNumber: String, // e.g. "PRE-2026-001"
    val customerId: Long,
    val projectId: Long? = null,
    val projectType: String = "Reforma integral",
    val date: Long = System.currentTimeMillis(),
    val validityDays: Int = 30,
    val subtotal: Double = 0.0,
    val discountPercent: Double = 0.0,
    val discountAmount: Double = 0.0,
    val taxableBase: Double = 0.0,
    val vatRate: Double = 21.0, // 21, 10, 4, 0
    val totalAmount: Double = 0.0,
    val status: String = "Borrador", // "Borrador", "Enviado", "Visto", "Aceptado", "Rechazado", "Caducado"
    val notes: String = "",
    val conditions: String = "Forma de pago: 40% al inicio de obra, 40% a mitad de obra, 20% al finalizar. Garantía de 2 años en instalaciones.",
    val signatureBase64: String = "",
    val signatureDate: Long = 0L,
    val clientSignedName: String = "",
    val clientIpInfo: String = "",
    val isConvertedToInvoice: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Entidad de Partida Presupuestaria
 * Relación: Quote (1) -> QuoteItems (N)
 */
@Entity(
    tableName = "quote_items",
    foreignKeys = [
        ForeignKey(
            entity = QuoteEntity::class,
            parentColumns = ["id"],
            childColumns = ["quoteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["quoteId"])]
)
data class QuoteItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val quoteId: Long,
    val description: String,
    val category: String, // "Materiales", "Mano de obra", "Servicios"
    val quantity: Double,
    val unit: String, // "m²", "ml", "ud", "h", "kg", "l", "día"
    val unitPrice: Double,
    val discount: Double = 0.0,
    val total: Double
)

/**
 * Entidad de Factura
 * Relación: Customer (1) -> Invoices (N), Project (1) -> Invoices (N) [Opcional], Quote (1) -> Invoice (1/N) [Opcional]
 */
@Entity(
    tableName = "invoices",
    foreignKeys = [
        ForeignKey(
            entity = CustomerEntity::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = QuoteEntity::class,
            parentColumns = ["id"],
            childColumns = ["quoteId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["invoiceNumber"], unique = true),
        Index(value = ["customerId"]),
        Index(value = ["projectId"]),
        Index(value = ["quoteId"])
    ]
)
data class InvoiceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val invoiceNumber: String, // e.g. "FAC-2026-001"
    val quoteId: Long? = null,
    val customerId: Long,
    val projectId: Long? = null,
    val date: Long = System.currentTimeMillis(),
    val dueDate: Long = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000),
    val subtotal: Double = 0.0,
    val vatRate: Double = 21.0,
    val vatAmount: Double = 0.0,
    val totalAmount: Double = 0.0,
    val paidAmount: Double = 0.0,
    val status: String = "Pendiente", // "Pendiente", "Parcial", "Pagada", "Vencida"
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Entidad de Línea de Factura
 * Relación: Invoice (1) -> InvoiceItems (N)
 */
@Entity(
    tableName = "invoice_items",
    foreignKeys = [
        ForeignKey(
            entity = InvoiceEntity::class,
            parentColumns = ["id"],
            childColumns = ["invoiceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["invoiceId"])]
)
data class InvoiceItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val invoiceId: Long,
    val description: String,
    val category: String = "Materiales",
    val quantity: Double,
    val unit: String,
    val unitPrice: Double,
    val discount: Double = 0.0,
    val total: Double
)

/**
 * Entidad de Gasto / Ticket de compra
 * Relación: Project (1) -> Expenses (N) [Opcional], Quote (1) -> Expenses (N) [Opcional], User (1) -> Expenses (N) [Opcional]
 */
@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = QuoteEntity::class,
            parentColumns = ["id"],
            childColumns = ["quoteId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["createdByUserId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["projectId"]),
        Index(value = ["quoteId"]),
        Index(value = ["createdByUserId"])
    ]
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long? = null,
    val quoteId: Long? = null,
    val description: String,
    val category: String = "Material", // "Material", "Mano de obra", "Transporte", "Combustible", "Herramientas", "Subcontratas", "Otros"
    val amount: Double,
    val supplier: String = "",
    val invoiceNumber: String = "",
    val date: Long = System.currentTimeMillis(),
    val receiptPhotoUri: String = "",
    val isOcrDetected: Boolean = false,
    val createdByUserId: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Entidad de Pago / Cobro
 * Relación: Invoice (1) -> Payments (N) [Opcional], Project (1) -> Payments (N) [Opcional], Customer (1) -> Payments (N) [Opcional]
 */
@Entity(
    tableName = "payments",
    foreignKeys = [
        ForeignKey(
            entity = InvoiceEntity::class,
            parentColumns = ["id"],
            childColumns = ["invoiceId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = CustomerEntity::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["invoiceId"]),
        Index(value = ["projectId"]),
        Index(value = ["customerId"])
    ]
)
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val invoiceId: Long? = null,
    val projectId: Long? = null,
    val customerId: Long? = null,
    val amount: Double,
    val date: Long = System.currentTimeMillis(),
    val paymentMethod: String = "Transferencia", // "Efectivo", "Transferencia", "Tarjeta", "Bizum", "Otro"
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Entidad de Catálogo de Precios
 */
@Entity(
    tableName = "products",
    indices = [Index(value = ["category"])]
)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String, // "Materiales", "Mano de obra", "Servicios"
    val unit: String,
    val price: Double,
    val supplier: String = "",
    val notes: String = ""
)

/**
 * Entidad de Tarea de Obra
 * Relación: Project (1) -> Tasks (N) [Opcional]
 */
@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["projectId"])]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long? = null,
    val title: String,
    val assigneeName: String = "",
    val dueDate: Long = System.currentTimeMillis() + (7L * 24 * 60 * 60 * 1000),
    val priority: String = "Media", // "Baja", "Media", "Alta", "Urgente"
    val status: String = "Pendiente", // "Pendiente", "En curso", "Completada"
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Entidad de Fases de Obra
 * Relación: Project (1) -> ProjectPhases (N)
 */
@Entity(
    tableName = "project_phases",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["projectId"])]
)
data class ProjectPhaseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val phaseName: String,
    val sortOrder: Int,
    val status: String = "Pendiente", // "Pendiente", "En curso", "Finalizada"
    val notes: String = ""
)

/**
 * Entidad de Galería de Fotos de Obra
 * Relación: Project (1) -> ProjectPhotos (N), User (1) -> ProjectPhotos (N) [Opcional]
 */
@Entity(
    tableName = "project_photos",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["takenByUserId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["projectId"]),
        Index(value = ["takenByUserId"])
    ]
)
data class ProjectPhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val stage: String, // "Antes", "Durante", "Después"
    val photoUri: String,
    val description: String = "",
    val takenByUserId: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Entidad de Configuración de Empresa
 */
@Entity(tableName = "company_settings")
data class CompanySettingsEntity(
    @PrimaryKey val id: Long = 1,
    val commercialName: String = "ReformasPro Construcciones",
    val legalName: String = "Reformas y Obras Pro S.L.",
    val taxId: String = "B-12345678",
    val address: String = "Calle Gran Vía 45, Planta 2, Madrid",
    val phone: String = "+34 600 123 456",
    val email: String = "contacto@reformaspro.es",
    val website: String = "www.reformaspro.es",
    val iban: String = "ES91 2100 0418 4502 0005 1332",
    val logoUri: String = "",
    val defaultVatRate: Double = 21.0,
    val paymentTerms: String = "40% al aceptar presupuesto, 40% a mitad de obra, 20% al finalizar.",
    val defaultValidityDays: Int = 30,
    val alertThreshold80: Boolean = true,
    val alertThreshold90: Boolean = true,
    val alertThreshold100: Boolean = true
)

/**
 * Entidad de Sesiones Demo
 */
@Entity(
    tableName = "demo_sessions",
    indices = [Index(value = ["accessCode"], unique = true)]
)
data class DemoSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val accessCode: String,
    val targetContact: String,
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + (30L * 60 * 1000), // 30 mins
    val isExpired: Boolean = false,
    val isUsed: Boolean = false
)

// Typealiases to allow referring to entities as User, Customer, Project, Quote, Invoice, Expense, Task
typealias User = UserEntity
typealias Customer = CustomerEntity
typealias Project = ProjectEntity
typealias Quote = QuoteEntity
typealias QuoteItem = QuoteItemEntity
typealias Invoice = InvoiceEntity
typealias InvoiceItem = InvoiceItemEntity
typealias Expense = ExpenseEntity
typealias Task = TaskEntity
typealias Payment = PaymentEntity
typealias Product = ProductEntity
typealias ProjectPhase = ProjectPhaseEntity
typealias ProjectPhoto = ProjectPhotoEntity
typealias CompanySettings = CompanySettingsEntity
typealias DemoSession = DemoSessionEntity


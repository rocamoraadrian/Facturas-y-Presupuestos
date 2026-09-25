package com.example.data.repository

import com.example.data.local.dao.AppDao
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReformasRepository(private val appDao: AppDao) {

    init {
        CoroutineScope(Dispatchers.IO).launch {
            checkAndSeedInitialData()
        }
    }

    // Customers
    val allCustomers: Flow<List<CustomerEntity>> = appDao.getAllCustomers()
    fun getCustomerById(id: Long) = appDao.getCustomerById(id)
    suspend fun insertCustomer(customer: CustomerEntity) = withContext(Dispatchers.IO) { appDao.insertCustomer(customer) }
    suspend fun updateCustomer(customer: CustomerEntity) = withContext(Dispatchers.IO) { appDao.updateCustomer(customer) }
    suspend fun deleteCustomer(customer: CustomerEntity) = withContext(Dispatchers.IO) { appDao.deleteCustomer(customer) }

    // Projects
    val allProjects: Flow<List<ProjectEntity>> = appDao.getAllProjects()
    fun getProjectById(id: Long) = appDao.getProjectById(id)
    fun getProjectsByCustomer(customerId: Long) = appDao.getProjectsByCustomer(customerId)
    suspend fun insertProject(project: ProjectEntity) = withContext(Dispatchers.IO) { appDao.insertProject(project) }
    suspend fun updateProject(project: ProjectEntity) = withContext(Dispatchers.IO) { appDao.updateProject(project) }
    suspend fun deleteProject(project: ProjectEntity) = withContext(Dispatchers.IO) { appDao.deleteProject(project) }

    // Quotes
    val allQuotes: Flow<List<QuoteEntity>> = appDao.getAllQuotes()
    fun getQuoteById(id: Long) = appDao.getQuoteById(id)
    fun getQuotesByProject(projectId: Long) = appDao.getQuotesByProject(projectId)
    fun getQuotesByCustomer(customerId: Long) = appDao.getQuotesByCustomer(customerId)
    suspend fun insertQuote(quote: QuoteEntity) = withContext(Dispatchers.IO) { appDao.insertQuote(quote) }
    suspend fun updateQuote(quote: QuoteEntity) = withContext(Dispatchers.IO) { appDao.updateQuote(quote) }
    suspend fun deleteQuote(quote: QuoteEntity) = withContext(Dispatchers.IO) { appDao.deleteQuote(quote) }

    // Quote Items
    fun getItemsByQuoteId(quoteId: Long) = appDao.getItemsByQuoteId(quoteId)
    suspend fun insertQuoteItems(items: List<QuoteItemEntity>) = withContext(Dispatchers.IO) { appDao.insertQuoteItems(items) }
    suspend fun deleteItemsByQuoteId(quoteId: Long) = withContext(Dispatchers.IO) { appDao.deleteItemsByQuoteId(quoteId) }

    // Invoices
    val allInvoices: Flow<List<InvoiceEntity>> = appDao.getAllInvoices()
    fun getInvoiceById(id: Long) = appDao.getInvoiceById(id)
    fun getInvoicesByProject(projectId: Long) = appDao.getInvoicesByProject(projectId)
    fun getInvoicesByCustomer(customerId: Long) = appDao.getInvoicesByCustomer(customerId)
    suspend fun insertInvoice(invoice: InvoiceEntity) = withContext(Dispatchers.IO) { appDao.insertInvoice(invoice) }
    suspend fun updateInvoice(invoice: InvoiceEntity) = withContext(Dispatchers.IO) { appDao.updateInvoice(invoice) }
    suspend fun deleteInvoice(invoice: InvoiceEntity) = withContext(Dispatchers.IO) { appDao.deleteInvoice(invoice) }

    // Expenses
    val allExpenses: Flow<List<ExpenseEntity>> = appDao.getAllExpenses()
    fun getExpensesByProject(projectId: Long) = appDao.getExpensesByProject(projectId)
    suspend fun insertExpense(expense: ExpenseEntity) = withContext(Dispatchers.IO) { appDao.insertExpense(expense) }
    suspend fun deleteExpense(expense: ExpenseEntity) = withContext(Dispatchers.IO) { appDao.deleteExpense(expense) }

    // Payments
    val allPayments: Flow<List<PaymentEntity>> = appDao.getAllPayments()
    fun getPaymentsByInvoice(invoiceId: Long) = appDao.getPaymentsByInvoice(invoiceId)
    fun getPaymentsByProject(projectId: Long) = appDao.getPaymentsByProject(projectId)
    suspend fun insertPayment(payment: PaymentEntity) = withContext(Dispatchers.IO) { appDao.insertPayment(payment) }

    // Products / Price Catalog
    val allProducts: Flow<List<ProductEntity>> = appDao.getAllProducts()
    fun getProductsByCategory(category: String) = appDao.getProductsByCategory(category)
    suspend fun insertProduct(product: ProductEntity) = withContext(Dispatchers.IO) { appDao.insertProduct(product) }
    suspend fun deleteProduct(product: ProductEntity) = withContext(Dispatchers.IO) { appDao.deleteProduct(product) }

    // Tasks
    val allTasks: Flow<List<TaskEntity>> = appDao.getAllTasks()
    fun getTasksByProject(projectId: Long) = appDao.getTasksByProject(projectId)
    suspend fun insertTask(task: TaskEntity) = withContext(Dispatchers.IO) { appDao.insertTask(task) }
    suspend fun updateTask(task: TaskEntity) = withContext(Dispatchers.IO) { appDao.updateTask(task) }
    suspend fun deleteTask(task: TaskEntity) = withContext(Dispatchers.IO) { appDao.deleteTask(task) }

    // Project Phases
    fun getPhasesByProject(projectId: Long) = appDao.getPhasesByProject(projectId)
    suspend fun insertPhases(phases: List<ProjectPhaseEntity>) = withContext(Dispatchers.IO) { appDao.insertPhases(phases) }
    suspend fun updatePhase(phase: ProjectPhaseEntity) = withContext(Dispatchers.IO) { appDao.updatePhase(phase) }

    // Project Photos
    fun getPhotosByProject(projectId: Long) = appDao.getPhotosByProject(projectId)
    suspend fun insertPhoto(photo: ProjectPhotoEntity) = withContext(Dispatchers.IO) { appDao.insertPhoto(photo) }
    suspend fun deletePhoto(photo: ProjectPhotoEntity) = withContext(Dispatchers.IO) { appDao.deletePhoto(photo) }

    // Users
    val allUsers: Flow<List<UserEntity>> = appDao.getAllUsers()
    suspend fun insertUser(user: UserEntity) = withContext(Dispatchers.IO) { appDao.insertUser(user) }
    suspend fun updateUser(user: UserEntity) = withContext(Dispatchers.IO) { appDao.updateUser(user) }
    suspend fun deleteUser(user: UserEntity) = withContext(Dispatchers.IO) { appDao.deleteUser(user) }

    // Settings
    val settings: Flow<CompanySettingsEntity?> = appDao.getSettings()
    suspend fun updateSettings(settings: CompanySettingsEntity) = withContext(Dispatchers.IO) { appDao.insertSettings(settings) }

    // Demo Sessions
    val allDemoSessions: Flow<List<DemoSessionEntity>> = appDao.getAllDemoSessions()
    suspend fun createDemoSession(contact: String, code: String): Long = withContext(Dispatchers.IO) {
        val session = DemoSessionEntity(
            accessCode = code.uppercase().trim(),
            targetContact = contact,
            createdAt = System.currentTimeMillis(),
            expiresAt = System.currentTimeMillis() + (30L * 60 * 1000)
        )
        appDao.insertDemoSession(session)
    }

    suspend fun validateDemoCode(code: String): DemoSessionEntity? = withContext(Dispatchers.IO) {
        val trimmed = code.uppercase().trim()
        if (trimmed == "DEMO2026") {
            return@withContext DemoSessionEntity(
                id = 9999,
                accessCode = "DEMO2026",
                targetContact = "demo@reformaspro.es",
                createdAt = System.currentTimeMillis(),
                expiresAt = System.currentTimeMillis() + (30L * 60 * 1000),
                isExpired = false,
                isUsed = false
            )
        }
        val session = appDao.findDemoSessionByCode(trimmed)
        if (session != null && !session.isExpired && !session.isUsed && System.currentTimeMillis() < session.expiresAt) {
            session
        } else {
            null
        }
    }

    // Seed Data
    private suspend fun checkAndSeedInitialData() = withContext(Dispatchers.IO) {
        val existingUsers = appDao.getAllUsers().firstOrNull()
        if (!existingUsers.isNullOrEmpty()) return@withContext

        // Default Company Settings
        appDao.insertSettings(
            CompanySettingsEntity(
                id = 1,
                commercialName = "ReformasPro Construcciones",
                legalName = "Reformas y Soluciones Profesionales S.L.",
                taxId = "B-98765432",
                address = "Calle Gran Vía 48, 2º Izq, 28013 Madrid",
                phone = "+34 622 889 900",
                email = "info@reformaspro.es",
                website = "www.reformaspro.es",
                iban = "ES66 2100 0418 4502 0005 1332",
                defaultVatRate = 21.0,
                paymentTerms = "40% al inicio y acopio de materiales, 40% a certificación intermedia, 20% a finalización y entrega de llaves."
            )
        )

        // Seed Users
        val adminId = appDao.insertUser(
            UserEntity(
                name = "Carlos Mendoza (Director)",
                email = "carlos@reformaspro.es",
                phone = "+34 600 111 222",
                role = "ADMIN",
                canCreateQuotes = true,
                canAddExpenses = true,
                canUploadPhotos = true,
                canManageUsers = true,
                canManageSettings = true
            )
        )
        appDao.insertUser(
            UserEntity(
                name = "Alejandro Gómez (Jefe de Obra)",
                email = "alejandro@reformaspro.es",
                phone = "+34 600 333 444",
                role = "EMPLOYEE",
                canCreateQuotes = true,
                canAddExpenses = true,
                canUploadPhotos = true,
                canManageUsers = false,
                canManageSettings = false
            )
        )

        // Seed Price Catalog (Materials, Labor, Services)
        val defaultProducts = listOf(
            ProductEntity(name = "Azulejo porcelánico rectificado 60x60", category = "Materiales", unit = "m²", price = 24.50, supplier = "Cerámicas Porcelanosa"),
            ProductEntity(name = "Cemento Portland CEM II 32.5 R (saco 25kg)", category = "Materiales", unit = "ud", price = 4.80, supplier = "Materiales del Sur"),
            ProductEntity(name = "Mortero autonivelante de alta resistencia (25kg)", category = "Materiales", unit = "ud", price = 8.50, supplier = "Weber"),
            ProductEntity(name = "Pladur Standard N 13mm (2.5x1.2m)", category = "Materiales", unit = "ud", price = 11.90, supplier = "Pladur Gypsum"),
            ProductEntity(name = "Pintura plástica lavable mate blanco (15L)", category = "Materiales", unit = "ud", price = 44.00, supplier = "Pinturas Titan"),
            ProductEntity(name = "Tubería multicapa PEX-Al-PEX 20mm", category = "Materiales", unit = "ml", price = 2.90, supplier = "Uponor"),
            ProductEntity(name = "Cable unipolar H07V-K 2.5mm² (rollo 100m)", category = "Materiales", unit = "ud", price = 48.00, supplier = "General Cable"),
            ProductEntity(name = "Inodoro adosado a pared salida dual con tapa amortiguada", category = "Materiales", unit = "ud", price = 240.00, supplier = "Roca"),
            ProductEntity(name = "Plato de ducha resina antideslizante 120x80cm", category = "Materiales", unit = "ud", price = 185.00, supplier = "MineralStone"),
            // Labor
            ProductEntity(name = "Oficial 1ª Albañil / Alicatador", category = "Mano de obra", unit = "h", price = 28.00),
            ProductEntity(name = "Peón especialista de obra", category = "Mano de obra", unit = "h", price = 20.00),
            ProductEntity(name = "Electricista instalador autorizado", category = "Mano de obra", unit = "h", price = 34.00),
            ProductEntity(name = "Fontanero instalador profesional", category = "Mano de obra", unit = "h", price = 32.00),
            ProductEntity(name = "Pintor profesional de acabados", category = "Mano de obra", unit = "h", price = 26.00),
            ProductEntity(name = "Instalador oficial de climatización", category = "Mano de obra", unit = "h", price = 35.00),
            // Services
            ProductEntity(name = "Desplazamiento técnico y transporte de herramientas", category = "Servicios", unit = "ud", price = 35.00),
            ProductEntity(name = "Contenedor de residuos y escombros (5m³)", category = "Servicios", unit = "ud", price = 180.00, supplier = "EcoContenedores"),
            ProductEntity(name = "Alquiler de andamio homologado con montaje", category = "Servicios", unit = "día", price = 50.00)
        )
        appDao.insertProducts(defaultProducts)

        // Seed Customers
        val cust1Id = appDao.insertCustomer(
            CustomerEntity(
                name = "Elena Martínez Ruiz",
                company = "Particular",
                phone = "+34 612 345 678",
                whatsapp = "+34 612 345 678",
                email = "elena.martinez@email.com",
                address = "Calle Velázquez 74, 3ºB, 28001 Madrid",
                siteAddress = "Calle Velázquez 74, 3ºB, 28001 Madrid",
                notes = "Preferencia de contacto por WhatsApp por las mañanas. Quiere acabados en blanco mate y grifería negra."
            )
        )
        val cust2Id = appDao.insertCustomer(
            CustomerEntity(
                name = "Javier Serrano (Hostelería)",
                company = "Restaurante La Taberna del Puerto",
                phone = "+34 654 987 321",
                whatsapp = "+34 654 987 321",
                email = "javier@tabernadelpuerto.es",
                address = "Paseo Marítimo 14, 46011 Valencia",
                siteAddress = "Paseo Marítimo 14, 46011 Valencia",
                notes = "Obras solo en horario de 8:00 a 16:00 antes de las aperturas del fin de semana."
            )
        )
        val cust3Id = appDao.insertCustomer(
            CustomerEntity(
                name = "Marcos Navarro Soto",
                company = "Particular",
                phone = "+34 689 112 233",
                whatsapp = "+34 689 112 233",
                email = "marcos.navarro@email.com",
                address = "Av. Diagonal 420, Barcelona",
                siteAddress = "Av. Diagonal 420, Barcelona",
                notes = "Chalet unifamiliar. Sustitución completa de suelos y pintura."
            )
        )

        // Seed Projects
        val proj1Id = appDao.insertProject(
            ProjectEntity(
                customerId = cust1Id,
                name = "Reforma Integral Piso Velázquez",
                address = "Calle Velázquez 74, 3ºB, Madrid",
                projectType = "Reforma integral",
                budgetAmount = 24500.0,
                status = "En curso",
                notes = "Piso de 90m². Incluye cocina, 2 baños, tabiquería y suelos porcelánicos."
            )
        )
        val proj2Id = appDao.insertProject(
            ProjectEntity(
                customerId = cust2Id,
                name = "Renovación Cocina Industrial y Aseos",
                address = "Paseo Marítimo 14, Valencia",
                projectType = "Cocina",
                budgetAmount = 14800.0,
                status = "En preparación",
                notes = "Alicatado antibacteriano y fontanería de alto caudal."
            )
        )
        val proj3Id = appDao.insertProject(
            ProjectEntity(
                customerId = cust3Id,
                name = "Tarima Flotante y Pintura Plástica",
                address = "Av. Diagonal 420, Barcelona",
                projectType = "Suelos",
                budgetAmount = 6200.0,
                status = "Finalizado",
                notes = "Obra completada con éxito y satisfacción del cliente."
            )
        )

        // Seed Phases for Project 1
        val phases = listOf(
            ProjectPhaseEntity(projectId = proj1Id, phaseName = "Demolición y desescombro", sortOrder = 1, status = "Finalizada"),
            ProjectPhaseEntity(projectId = proj1Id, phaseName = "Albañilería y tabiquería", sortOrder = 2, status = "Finalizada"),
            ProjectPhaseEntity(projectId = proj1Id, phaseName = "Instalaciones fontanería y saneamiento", sortOrder = 3, status = "Finalizada"),
            ProjectPhaseEntity(projectId = proj1Id, phaseName = "Instalación eléctrica y cuadro de mando", sortOrder = 4, status = "En curso"),
            ProjectPhaseEntity(projectId = proj1Id, phaseName = "Falsos techos pladur y aislamiento", sortOrder = 5, status = "En curso"),
            ProjectPhaseEntity(projectId = proj1Id, phaseName = "Alicatados y suelos porcelánicos", sortOrder = 6, status = "Pendiente"),
            ProjectPhaseEntity(projectId = proj1Id, phaseName = "Pintura lisa y repasos", sortOrder = 7, status = "Pendiente"),
            ProjectPhaseEntity(projectId = proj1Id, phaseName = "Carpintería interior y sanitarios", sortOrder = 8, status = "Pendiente"),
            ProjectPhaseEntity(projectId = proj1Id, phaseName = "Limpieza fin de obra y entrega de llaves", sortOrder = 9, status = "Pendiente")
        )
        appDao.insertPhases(phases)

        // Seed Quotes
        val quote1Id = appDao.insertQuote(
            QuoteEntity(
                quoteNumber = "PRE-2026-001",
                customerId = cust1Id,
                projectId = proj1Id,
                projectType = "Reforma integral",
                subtotal = 20247.93,
                discountPercent = 0.0,
                taxableBase = 20247.93,
                vatRate = 21.0,
                totalAmount = 24500.0,
                status = "Aceptado",
                notes = "Presupuesto aprobado presencialmente por el cliente con firma digital.",
                signatureBase64 = "FIRMADO_CLIENTE_DEMO",
                signatureDate = System.currentTimeMillis() - (5L * 24 * 60 * 60 * 1000),
                clientSignedName = "Elena Martínez",
                isConvertedToInvoice = true
            )
        )
        val quote2Id = appDao.insertQuote(
            QuoteEntity(
                quoteNumber = "PRE-2026-002",
                customerId = cust2Id,
                projectId = proj2Id,
                projectType = "Cocina",
                subtotal = 12231.40,
                discountPercent = 0.0,
                taxableBase = 12231.40,
                vatRate = 21.0,
                totalAmount = 14800.0,
                status = "Enviado",
                notes = "Enviado por WhatsApp al gerente para su revisión."
            )
        )
        val quote3Id = appDao.insertQuote(
            QuoteEntity(
                quoteNumber = "PRE-2026-003",
                customerId = cust3Id,
                projectId = proj3Id,
                projectType = "Suelos",
                subtotal = 5123.97,
                discountPercent = 0.0,
                taxableBase = 5123.97,
                vatRate = 21.0,
                totalAmount = 6200.0,
                status = "Aceptado",
                signatureBase64 = "FIRMADO_MARCOS",
                signatureDate = System.currentTimeMillis() - (20L * 24 * 60 * 60 * 1000),
                clientSignedName = "Marcos Navarro",
                isConvertedToInvoice = true
            )
        )

        // Seed Items for Quote 1
        val quote1Items = listOf(
            QuoteItemEntity(quoteId = quote1Id, description = "Demolición de tabiques, alicatados y retirada de escombros a vertedero", category = "Mano de obra", quantity = 1.0, unit = "ud", unitPrice = 1450.0, total = 1450.0),
            QuoteItemEntity(quoteId = quote1Id, description = "Suministro y colocación de suelo porcelánico rectificado 60x60", category = "Materiales", quantity = 75.0, unit = "m²", unitPrice = 45.0, total = 3375.0),
            QuoteItemEntity(quoteId = quote1Id, description = "Renovación completa instalación fontanería multicapa y desagües", category = "Mano de obra", quantity = 1.0, unit = "ud", unitPrice = 3200.0, total = 3200.0),
            QuoteItemEntity(quoteId = quote1Id, description = "Instalación eléctrica completa con nuevo cuadro y 45 puntos", category = "Mano de obra", quantity = 1.0, unit = "ud", unitPrice = 3900.0, total = 3900.0),
            QuoteItemEntity(quoteId = quote1Id, description = "Falso techo continuo de pladur con lana de roca acústica", category = "Materiales", quantity = 70.0, unit = "m²", unitPrice = 38.0, total = 2660.0),
            QuoteItemEntity(quoteId = quote1Id, description = "Alicatado cerámico de paredes de baños hasta techo", category = "Mano de obra", quantity = 48.0, unit = "m²", unitPrice = 42.0, total = 2016.0),
            QuoteItemEntity(quoteId = quote1Id, description = "Pintura plástica lisa blanca mate dos manos en paredes y techos", category = "Mano de obra", quantity = 180.0, unit = "m²", unitPrice = 12.0, total = 2160.0),
            QuoteItemEntity(quoteId = quote1Id, description = "Instalación de 2 platos de ducha de resina e inodoros suspendidos", category = "Materiales", quantity = 2.0, unit = "ud", unitPrice = 743.46, total = 1486.93)
        )
        appDao.insertQuoteItems(quote1Items)

        // Seed Invoices
        val inv1Id = appDao.insertInvoice(
            InvoiceEntity(
                invoiceNumber = "FAC-2026-001",
                quoteId = quote1Id,
                customerId = cust1Id,
                projectId = proj1Id,
                subtotal = 8099.17,
                vatRate = 21.0,
                vatAmount = 1700.83,
                totalAmount = 9800.0,
                paidAmount = 9800.0,
                status = "Pagada",
                notes = "Anticipo del 40% pactado al inicio de la obra."
            )
        )
        val inv2Id = appDao.insertInvoice(
            InvoiceEntity(
                invoiceNumber = "FAC-2026-002",
                quoteId = quote1Id,
                customerId = cust1Id,
                projectId = proj1Id,
                subtotal = 8099.17,
                vatRate = 21.0,
                vatAmount = 1700.83,
                totalAmount = 9800.0,
                paidAmount = 0.0,
                status = "Pendiente",
                notes = "Segunda certificación de obra 40% al finalizar instalaciones y pladur."
            )
        )
        val inv3Id = appDao.insertInvoice(
            InvoiceEntity(
                invoiceNumber = "FAC-2026-003",
                quoteId = quote3Id,
                customerId = cust3Id,
                projectId = proj3Id,
                subtotal = 5123.97,
                vatRate = 21.0,
                vatAmount = 1076.03,
                totalAmount = 6200.0,
                paidAmount = 6200.0,
                status = "Pagada",
                notes = "Factura final de liquidación de trabajos de suelos."
            )
        )

        // Seed Payments
        appDao.insertPayment(
            PaymentEntity(
                invoiceId = inv1Id,
                projectId = proj1Id,
                customerId = cust1Id,
                amount = 9800.0,
                paymentMethod = "Transferencia",
                notes = "Recibido en cuenta Santander justificante bancario."
            )
        )
        appDao.insertPayment(
            PaymentEntity(
                invoiceId = inv3Id,
                projectId = proj3Id,
                customerId = cust3Id,
                amount = 6200.0,
                paymentMethod = "Transferencia",
                notes = "Liquidación recibida."
            )
        )

        // Seed Expenses (Real site costs)
        appDao.insertExpense(
            ExpenseEntity(
                projectId = proj1Id,
                description = "Azulejos y pegamento porcelánico C2TE",
                category = "Material",
                amount = 1890.50,
                supplier = "Materiales García S.L.",
                invoiceNumber = "MG-2026-441",
                isOcrDetected = true
            )
        )
        appDao.insertExpense(
            ExpenseEntity(
                projectId = proj1Id,
                description = "2 Contenedores de 5m³ y gestión de residuos",
                category = "Transporte",
                amount = 360.0,
                supplier = "EcoContenedores Madrid",
                invoiceNumber = "EC-8891",
                isOcrDetected = false
            )
        )
        appDao.insertExpense(
            ExpenseEntity(
                projectId = proj1Id,
                description = "Cuadro eléctrico y cable libre de halógenos",
                category = "Material",
                amount = 945.20,
                supplier = "Suministros Eléctricos Norte",
                invoiceNumber = "SEN-1029",
                isOcrDetected = true
            )
        )
        appDao.insertExpense(
            ExpenseEntity(
                projectId = proj1Id,
                description = "Pladur y perfiles galvanizados 70mm",
                category = "Material",
                amount = 1120.0,
                supplier = "Almacenes La Yesera",
                invoiceNumber = "AY-3301",
                isOcrDetected = false
            )
        )

        // Seed Tasks
        appDao.insertTask(
            TaskEntity(
                projectId = proj1Id,
                title = "Verificar nivelación y planeidad antes del porcelánico",
                assigneeName = "Alejandro Gómez",
                priority = "Alta",
                status = "En curso"
            )
        )
        appDao.insertTask(
            TaskEntity(
                projectId = proj1Id,
                title = "Recepción de sanitarios Roca y platos de ducha",
                assigneeName = "Carlos Mendoza",
                priority = "Urgente",
                status = "Pendiente"
            )
        )
        appDao.insertTask(
            TaskEntity(
                projectId = proj1Id,
                title = "Prueba de presión y estanqueidad fontanería",
                assigneeName = "Alejandro Gómez",
                priority = "Alta",
                status = "Completada"
            )
        )
        appDao.insertTask(
            TaskEntity(
                projectId = proj2Id,
                title = "Medición técnica exacta salida de humos de cocina",
                assigneeName = "Carlos Mendoza",
                priority = "Media",
                status = "Pendiente"
            )
        )
    }
}

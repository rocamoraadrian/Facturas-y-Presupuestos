package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
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
import com.example.data.repository.ReformasRepository
import com.example.ui.strings.AppLanguage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class AppScreen {
    object Dashboard : AppScreen()
    object Quotes : AppScreen()
    object Customers : AppScreen()
    object Projects : AppScreen()
    object MoreMenu : AppScreen()
    
    // Sub-screens
    data class CustomerDetail(val customerId: Long) : AppScreen()
    data class ProjectDetail(val projectId: Long, val initialTab: Int = 0) : AppScreen()
    data class QuoteDetail(val quoteId: Long) : AppScreen()
    object CreateQuote : AppScreen()
    object Invoices : AppScreen()
    object Expenses : AppScreen()
    object Payments : AppScreen()
    object Tasks : AppScreen()
    object Templates : AppScreen()
    object PriceCatalog : AppScreen()
    object MaterialCalculator : AppScreen()
    object CompanySettings : AppScreen()
    object UserManagement : AppScreen()
    object DemoAccess : AppScreen()
    object Login : AppScreen()
}

data class AuthState(
    val isLoggedIn: Boolean = true,
    val isDemoMode: Boolean = false,
    val currentUserName: String = "Carlos Mendoza (Admin)",
    val currentUserRole: String = "ADMIN", // "ADMIN" or "EMPLOYEE"
    val demoRemainingSeconds: Int = 1800, // 30 minutes
    val demoExpiredMessage: String? = null
)

class ReformasViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ReformasRepository

    init {
        val database = AppDatabase.getInstance(application)
        repository = ReformasRepository(database.appDao())
    }

    // Language state
    private val _currentLanguage = MutableStateFlow(AppLanguage.SPANISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    fun setLanguage(lang: AppLanguage) {
        _currentLanguage.value = lang
    }

    // Navigation state
    private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.Dashboard)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val screenBackStack = mutableListOf<AppScreen>()

    fun navigateTo(screen: AppScreen) {
        screenBackStack.add(_currentScreen.value)
        _currentScreen.value = screen
    }

    fun navigateBack(): Boolean {
        return if (screenBackStack.isNotEmpty()) {
            _currentScreen.value = screenBackStack.removeAt(screenBackStack.lastIndex)
            true
        } else {
            if (_currentScreen.value != AppScreen.Dashboard) {
                _currentScreen.value = AppScreen.Dashboard
                true
            } else {
                false
            }
        }
    }

    // Auth & Demo State
    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private var demoTimerJob: Job? = null

    fun startDemoSession(targetContact: String, code: String) {
        viewModelScope.launch {
            val valid = repository.validateDemoCode(code)
            if (valid != null) {
                _authState.value = AuthState(
                    isLoggedIn = true,
                    isDemoMode = true,
                    currentUserName = "Usuario Demo (${targetContact.ifEmpty { "Invitado" }})",
                    currentUserRole = "ADMIN",
                    demoRemainingSeconds = 1800,
                    demoExpiredMessage = null
                )
                startDemoTimer()
                _currentScreen.value = AppScreen.Dashboard
            }
        }
    }

    private fun startDemoTimer() {
        demoTimerJob?.cancel()
        demoTimerJob = viewModelScope.launch {
            while (_authState.value.demoRemainingSeconds > 0) {
                delay(1000)
                val newSec = _authState.value.demoRemainingSeconds - 1
                _authState.value = _authState.value.copy(demoRemainingSeconds = newSec)
                if (newSec <= 0) {
                    // Session ended
                    _authState.value = AuthState(
                        isLoggedIn = false,
                        isDemoMode = false,
                        demoExpiredMessage = "La sesión de demostración ha finalizado."
                    )
                    _currentScreen.value = AppScreen.Login
                    break
                }
            }
        }
    }

    fun logout() {
        demoTimerJob?.cancel()
        _authState.value = AuthState(isLoggedIn = false, demoExpiredMessage = null)
        _currentScreen.value = AppScreen.Login
    }

    fun loginAsAdmin() {
        demoTimerJob?.cancel()
        _authState.value = AuthState(
            isLoggedIn = true,
            isDemoMode = false,
            currentUserName = "Carlos Mendoza (Admin)",
            currentUserRole = "ADMIN"
        )
        _currentScreen.value = AppScreen.Dashboard
    }

    fun loginAsEmployee() {
        demoTimerJob?.cancel()
        _authState.value = AuthState(
            isLoggedIn = true,
            isDemoMode = false,
            currentUserName = "Alejandro Gómez (Operario)",
            currentUserRole = "EMPLOYEE"
        )
        _currentScreen.value = AppScreen.Dashboard
    }

    // Data sources from Repository
    val customers: StateFlow<List<CustomerEntity>> = repository.allCustomers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val projects: StateFlow<List<ProjectEntity>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val quotes: StateFlow<List<QuoteEntity>> = repository.allQuotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val invoices: StateFlow<List<InvoiceEntity>> = repository.allInvoices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenses: StateFlow<List<ExpenseEntity>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val payments: StateFlow<List<PaymentEntity>> = repository.allPayments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val products: StateFlow<List<ProductEntity>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasks: StateFlow<List<TaskEntity>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val users: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val companySettings: StateFlow<CompanySettingsEntity?> = repository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Sub-queries
    fun getCustomerById(id: Long) = repository.getCustomerById(id)
    fun getProjectById(id: Long) = repository.getProjectById(id)
    fun getQuoteById(id: Long) = repository.getQuoteById(id)
    fun getQuoteItems(quoteId: Long) = repository.getItemsByQuoteId(quoteId)
    fun getProjectExpenses(projectId: Long) = repository.getExpensesByProject(projectId)
    fun getProjectPhases(projectId: Long) = repository.getPhasesByProject(projectId)
    fun getProjectPhotos(projectId: Long) = repository.getPhotosByProject(projectId)
    fun getProjectTasks(projectId: Long) = repository.getTasksByProject(projectId)
    fun getProjectInvoices(projectId: Long) = repository.getInvoicesByProject(projectId)
    fun getProjectPayments(projectId: Long) = repository.getPaymentsByProject(projectId)

    // Database Actions
    fun createCustomer(customer: CustomerEntity, onCreated: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val id = repository.insertCustomer(customer)
            onCreated(id)
        }
    }

    fun updateCustomer(customer: CustomerEntity) {
        viewModelScope.launch {
            repository.updateCustomer(customer)
        }
    }

    fun deleteCustomer(customer: CustomerEntity) {
        viewModelScope.launch {
            repository.deleteCustomer(customer)
        }
    }

    fun createProject(project: ProjectEntity, onCreated: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val id = repository.insertProject(project)
            // Initialize default phases for new project
            val defaultPhases = listOf(
                ProjectPhaseEntity(projectId = id, phaseName = "Demolición y limpieza inicial", sortOrder = 1, status = "Pendiente"),
                ProjectPhaseEntity(projectId = id, phaseName = "Albañilería y replanteo", sortOrder = 2, status = "Pendiente"),
                ProjectPhaseEntity(projectId = id, phaseName = "Instalaciones (Fontanería / Electricidad)", sortOrder = 3, status = "Pendiente"),
                ProjectPhaseEntity(projectId = id, phaseName = "Revestimientos y acabados", sortOrder = 4, status = "Pendiente"),
                ProjectPhaseEntity(projectId = id, phaseName = "Entrega y fin de obra", sortOrder = 5, status = "Pendiente")
            )
            repository.insertPhases(defaultPhases)
            onCreated(id)
        }
    }

    fun updateProject(project: ProjectEntity) {
        viewModelScope.launch {
            repository.updateProject(project)
        }
    }

    fun deleteProject(project: ProjectEntity) {
        viewModelScope.launch {
            repository.deleteProject(project)
        }
    }

    fun createQuoteWithItems(
        quote: QuoteEntity,
        items: List<QuoteItemEntity>,
        onCreated: (Long) -> Unit = {}
    ) {
        viewModelScope.launch {
            val quoteId = repository.insertQuote(quote)
            val mappedItems = items.map { it.copy(quoteId = quoteId) }
            repository.insertQuoteItems(mappedItems)
            onCreated(quoteId)
        }
    }

    fun updateQuoteStatus(quote: QuoteEntity, newStatus: String) {
        viewModelScope.launch {
            repository.updateQuote(quote.copy(status = newStatus))
        }
    }

    fun signQuote(quote: QuoteEntity, signatureBase64: String, signedName: String) {
        viewModelScope.launch {
            val updated = quote.copy(
                status = "Aceptado",
                signatureBase64 = signatureBase64,
                signatureDate = System.currentTimeMillis(),
                clientSignedName = signedName,
                clientIpInfo = "Dispositivo Móvil / Firma Biométrica en Pantalla"
            )
            repository.updateQuote(updated)
        }
    }

    fun convertQuoteToInvoice(quote: QuoteEntity, onInvoiceCreated: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val invCount = (invoices.value.size + 1)
            val invNum = "FAC-2026-%03d".format(invCount)
            val vatAmt = quote.totalAmount - quote.taxableBase
            val invoice = InvoiceEntity(
                invoiceNumber = invNum,
                quoteId = quote.id,
                customerId = quote.customerId,
                projectId = quote.projectId,
                date = System.currentTimeMillis(),
                subtotal = quote.taxableBase,
                vatRate = quote.vatRate,
                vatAmount = vatAmt,
                totalAmount = quote.totalAmount,
                paidAmount = 0.0,
                status = "Pendiente",
                notes = "Factura generada a partir del Presupuesto ${quote.quoteNumber}."
            )
            val invoiceId = repository.insertInvoice(invoice)
            repository.updateQuote(quote.copy(isConvertedToInvoice = true))
            onInvoiceCreated(invoiceId)
        }
    }

    fun addPayment(payment: PaymentEntity) {
        viewModelScope.launch {
            repository.insertPayment(payment)
            // Update invoice paid amount
            val inv = invoices.value.find { it.id == payment.invoiceId }
            if (inv != null) {
                val newPaid = inv.paidAmount + payment.amount
                val newStatus = when {
                    newPaid >= inv.totalAmount -> "Pagada"
                    newPaid > 0 -> "Parcial"
                    else -> "Pendiente"
                }
                repository.updateInvoice(inv.copy(paidAmount = newPaid, status = newStatus))
            }
        }
    }

    fun addExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.insertExpense(expense)
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    fun addTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.insertTask(task)
        }
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun updatePhaseStatus(phase: ProjectPhaseEntity, newStatus: String) {
        viewModelScope.launch {
            repository.updatePhase(phase.copy(status = newStatus))
        }
    }

    fun addProjectPhoto(photo: ProjectPhotoEntity) {
        viewModelScope.launch {
            repository.insertPhoto(photo)
        }
    }

    fun deleteProjectPhoto(photo: ProjectPhotoEntity) {
        viewModelScope.launch {
            repository.deletePhoto(photo)
        }
    }

    fun addProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.insertProduct(product)
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    fun updateCompanySettings(settings: CompanySettingsEntity) {
        viewModelScope.launch {
            repository.updateSettings(settings)
        }
    }

    fun addUser(user: UserEntity) {
        viewModelScope.launch {
            repository.insertUser(user)
        }
    }

    fun updateUser(user: UserEntity) {
        viewModelScope.launch {
            repository.updateUser(user)
        }
    }

    fun deleteUser(user: UserEntity) {
        viewModelScope.launch {
            repository.deleteUser(user)
        }
    }

    fun generateNewDemoCode(contact: String, onGenerated: (String) -> Unit) {
        viewModelScope.launch {
            val randomNum = (1000..9999).random()
            val code = "DEMO-$randomNum"
            repository.createDemoSession(contact, code)
            onGenerated(code)
        }
    }
}

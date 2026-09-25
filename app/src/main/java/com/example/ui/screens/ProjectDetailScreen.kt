package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.local.entities.ExpenseEntity
import com.example.data.local.entities.PaymentEntity
import com.example.data.local.entities.ProjectPhaseEntity
import com.example.data.local.entities.ProjectPhotoEntity
import com.example.data.local.entities.TaskEntity
import com.example.ui.components.PriorityBadge
import com.example.ui.components.StatusBadge
import com.example.ui.strings.LocalStrings
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CrimsonError
import com.example.ui.theme.ForestGreenLight
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.ReformasViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    projectId: Long,
    initialTab: Int = 0,
    viewModel: ReformasViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val context = LocalContext.current
    val projectFlow = remember(projectId) { viewModel.getProjectById(projectId) }
    val project by projectFlow.collectAsState(initial = null)
    val customers by viewModel.customers.collectAsState()
    val quotes by viewModel.quotes.collectAsState()
    val invoices by viewModel.invoices.collectAsState()
    val expenses by viewModel.expenses.collectAsState()
    val payments by viewModel.payments.collectAsState()
    val tasks by viewModel.tasks.collectAsState()

    val phasesFlow = remember(projectId) { viewModel.getProjectPhases(projectId) }
    val phases by phasesFlow.collectAsState(initial = emptyList())
    val photosFlow = remember(projectId) { viewModel.getProjectPhotos(projectId) }
    val photos by photosFlow.collectAsState(initial = emptyList())

    var selectedTab by remember { mutableIntStateOf(initialTab) }
    val tabs = listOf("Resumen", "Presupuestos", "Gastos", "Facturas", "Pagos", "Fotos", "Tareas", "Fases")

    val curProject = project
    if (curProject == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Cargando proyecto...")
        }
        return
    }

    val customer = customers.find { it.id == curProject.customerId }
    val projectQuotes = quotes.filter { it.projectId == projectId }
    val projectExpenses = expenses.filter { it.projectId == projectId }
    val projectInvoices = invoices.filter { it.projectId == projectId }
    val projectPayments = payments.filter { it.projectId == projectId }
    val projectTasks = tasks.filter { it.projectId == projectId }

    val totalBudget = if (curProject.budgetAmount > 0) curProject.budgetAmount else projectQuotes.sumOf { it.totalAmount }
    val totalSpent = projectExpenses.sumOf { it.amount }
    val totalInvoiced = projectInvoices.sumOf { it.totalAmount }
    val totalPaid = projectPayments.sumOf { it.amount }
    val estimatedProfit = (totalBudget - totalSpent).coerceAtLeast(0.0)

    val expenseRatio = if (totalBudget > 0) (totalSpent / totalBudget) else 0.0
    val completedPhases = phases.count { it.status == "Finalizada" }
    val phaseProgress = if (phases.isNotEmpty()) (completedPhases.toFloat() / phases.size.toFloat()) else 0f

    // Photo Dialog state
    var showAddPhotoDialog by remember { mutableStateOf(false) }
    var showAddTaskDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(curProject.name, fontWeight = FontWeight.Bold, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    StatusBadge(status = curProject.status, modifier = Modifier.padding(end = 12.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Tab Header
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 12.dp,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                tabs.forEachIndexed { index, tabTitle ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(tabTitle, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                    )
                }
            }

            // Tab Content
            when (selectedTab) {
                0 -> {
                    // RESUMEN DEL PROYECTO
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            // Budget Alert if > 80%, 90%, 100%
                            if (expenseRatio >= 0.80) {
                                val alertColor = if (expenseRatio >= 1.0) CrimsonError else AmberWarning
                                val alertText = when {
                                    expenseRatio >= 1.0 -> "¡ALERTA CRÍTICA! Los gastos han superado el 100% del presupuesto de la obra."
                                    expenseRatio >= 0.90 -> "Aviso de presupuesto: Se ha consumido más del 90% del margen presupuestado."
                                    else -> "Atención: Los gastos reales han alcanzado el 80% del presupuesto."
                                }
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = alertColor.copy(alpha = 0.15f)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Warning, contentDescription = null, tint = alertColor)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = alertText, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = alertColor)
                                    }
                                }
                            }
                        }

                        item {
                            // Project Header Card
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(text = "Cliente: ${customer?.name ?: "Sin asignar"}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text(text = "Tipo: ${curProject.projectType} • 📍 ${curProject.address}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Phase progress
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Progreso de fases:", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                        Text("$completedPhases / ${phases.size} fases (${(phaseProgress * 100).toInt()}%)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    LinearProgressIndicator(
                                        progress = { phaseProgress },
                                        modifier = Modifier.fillMaxWidth().height(8.dp),
                                        color = ForestGreenPrimary,
                                        trackColor = MaterialTheme.colorScheme.surface
                                    )
                                }
                            }
                        }

                        item {
                            // Comparison Budget vs Real Costs
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("COMPARATIVA PRESUPUESTO VS GASTO REAL", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Presupuesto pactado:", fontSize = 13.sp)
                                        Text(String.format(Locale.GERMANY, "%.2f €", totalBudget), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Gasto real acumulado:", fontSize = 13.sp)
                                        Text(
                                            String.format(Locale.GERMANY, "%.2f €", totalSpent),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (expenseRatio > 0.85) AmberWarning else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Beneficio estimado:", fontSize = 13.sp)
                                        Text(
                                            String.format(Locale.GERMANY, "%.2f €", estimatedProfit),
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 14.sp,
                                            color = ForestGreenLight
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    LinearProgressIndicator(
                                        progress = { expenseRatio.toFloat().coerceIn(0f, 1f) },
                                        modifier = Modifier.fillMaxWidth().height(8.dp),
                                        color = if (expenseRatio > 0.9) AmberWarning else ForestGreenLight,
                                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = String.format(Locale.US, "%.1f %% del presupuesto utilizado", expenseRatio * 100),
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        item {
                            // Invoicing & Collections summary
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("FACTURACIÓN Y COBROS", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Total Facturado:", fontSize = 13.sp)
                                        Text(String.format(Locale.GERMANY, "%.2f €", totalInvoiced), fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Total Cobrado:", fontSize = 13.sp)
                                        Text(String.format(Locale.GERMANY, "%.2f €", totalPaid), fontWeight = FontWeight.Bold, color = ForestGreenLight)
                                    }
                                    val pendingInvoice = (totalInvoiced - totalPaid).coerceAtLeast(0.0)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Pendiente de cobro:", fontSize = 13.sp)
                                        Text(String.format(Locale.GERMANY, "%.2f €", pendingInvoice), fontWeight = FontWeight.Bold, color = if (pendingInvoice > 0) AmberWarning else ForestGreenLight)
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // PRESUPUESTOS DE LA OBRA
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(projectQuotes) { q ->
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth().clickable { viewModel.navigateTo(AppScreen.QuoteDetail(q.id)) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(q.quoteNumber, fontWeight = FontWeight.Bold)
                                        Text(String.format(Locale.GERMANY, "%.2f €", q.totalAmount), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    }
                                    StatusBadge(status = q.status)
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // GASTOS DE LA OBRA
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Text("Total Gastos: ${String.format(Locale.GERMANY, "%.2f €", totalSpent)}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        items(projectExpenses) { exp ->
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(exp.description, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("${exp.supplier} • ${exp.category}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        if (exp.isOcrDetected) {
                                            Text("📷 Escaneado con OCR", fontSize = 10.sp, color = ForestGreenLight, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                    Text(
                                        String.format(Locale.GERMANY, "%.2f €", exp.amount),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // FACTURAS
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(projectInvoices) { inv ->
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(inv.invoiceNumber, fontWeight = FontWeight.Bold)
                                        Text(String.format(Locale.GERMANY, "Total: %.2f €", inv.totalAmount), fontSize = 13.sp)
                                    }
                                    StatusBadge(status = inv.status)
                                }
                            }
                        }
                    }
                }

                4 -> {
                    // PAGOS
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(projectPayments) { pay ->
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Cobro por ${pay.paymentMethod}", fontWeight = FontWeight.Bold)
                                        if (pay.notes.isNotEmpty()) {
                                            Text(pay.notes, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                    Text(
                                        String.format(Locale.GERMANY, "%.2f €", pay.amount),
                                        fontWeight = FontWeight.Bold,
                                        color = ForestGreenLight,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }

                5 -> {
                    // FOTOS: Antes, Durante, Después
                    var selectedPhotoStage by remember { mutableStateOf("Antes") }
                    val filteredPhotos = photos.filter { it.stage.equals(selectedPhotoStage, ignoreCase = true) }

                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("Antes", "Durante", "Después").forEach { stage ->
                                    FilterChip(
                                        selected = selectedPhotoStage == stage,
                                        onClick = { selectedPhotoStage = stage },
                                        label = { Text(stage) }
                                    )
                                }
                            }
                            IconButton(onClick = { showAddPhotoDialog = true }) {
                                Icon(Icons.Default.AddAPhoto, contentDescription = "Subir foto", tint = MaterialTheme.colorScheme.primary)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (filteredPhotos.isEmpty()) {
                            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Text("No hay fotos en fase '$selectedPhotoStage'", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        } else {
                            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(filteredPhotos) { photo ->
                                    Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                                        Column {
                                            if (photo.photoUri.isNotEmpty()) {
                                                AsyncImage(
                                                    model = photo.photoUri,
                                                    contentDescription = photo.description,
                                                    modifier = Modifier.fillMaxWidth().height(180.dp)
                                                )
                                            } else {
                                                Box(
                                                    modifier = Modifier.fillMaxWidth().height(140.dp).background(MaterialTheme.colorScheme.surfaceVariant),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                                }
                                            }
                                            Row(
                                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(photo.description.ifEmpty { "Foto $selectedPhotoStage" }, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                                IconButton(onClick = { viewModel.deleteProjectPhoto(photo) }) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = CrimsonError, modifier = Modifier.size(18.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                6 -> {
                    // TAREAS
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Tareas de Obra (${projectTasks.size})", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Button(
                                onClick = { showAddTaskDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Añadir", fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(projectTasks) { task ->
                                Card(
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = task.status == "Completada",
                                            onCheckedChange = { checked ->
                                                val newSt = if (checked) "Completada" else "En curso"
                                                viewModel.updateTask(task.copy(status = newSt))
                                            },
                                            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                                        )
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = task.title,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 13.sp,
                                                color = if (task.status == "Completada") Color.Gray else MaterialTheme.colorScheme.onSurface
                                            )
                                            if (task.assigneeName.isNotEmpty()) {
                                                Text("Responsable: ${task.assigneeName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                        PriorityBadge(priority = task.priority)
                                    }
                                }
                            }
                        }
                    }
                }

                7 -> {
                    // FASES DE LA OBRA
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        Text("Fases de Ejecución de la Obra", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(10.dp))

                        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(phases) { phase ->
                                Card(
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(phase.phaseName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                        }
                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            val nextStatus = when (phase.status) {
                                                "Pendiente" -> "En curso"
                                                "En curso" -> "Finalizada"
                                                else -> "Pendiente"
                                            }
                                            Surface(
                                                modifier = Modifier.clickable {
                                                    viewModel.updatePhaseStatus(phase, nextStatus)
                                                }
                                            ) {
                                                StatusBadge(status = phase.status)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Photo Dialog
    if (showAddPhotoDialog) {
        var stage by remember { mutableStateOf("Durante") }
        var photoDesc by remember { mutableStateOf("") }
        var photoUriStr by remember { mutableStateOf("") }

        val galleryPicker = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->
            if (uri != null) {
                photoUriStr = uri.toString()
            }
        }

        Dialog(onDismissRequest = { showAddPhotoDialog = false }) {
            Surface(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Subir Fotografía de Obra", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Antes", "Durante", "Después").forEach { st ->
                            FilterChip(
                                selected = stage == st,
                                onClick = { stage = st },
                                label = { Text(st) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { galleryPicker.launch("image/*") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (photoUriStr.isNotEmpty()) "Foto seleccionada ✓" else "Seleccionar de Galería / Cámara")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = photoDesc,
                        onValueChange = { photoDesc = it },
                        label = { Text("Descripción (ej: Estado inicial baño)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { showAddPhotoDialog = false }, modifier = Modifier.weight(1f)) {
                            Text("Cancelar")
                        }
                        Button(
                            onClick = {
                                val photo = ProjectPhotoEntity(
                                    projectId = projectId,
                                    stage = stage,
                                    photoUri = photoUriStr,
                                    description = photoDesc
                                )
                                viewModel.addProjectPhoto(photo)
                                showAddPhotoDialog = false
                                Toast.makeText(context, "Fotografía guardada", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Guardar")
                        }
                    }
                }
            }
        }
    }

    // Add Task Dialog
    if (showAddTaskDialog) {
        var taskTitle by remember { mutableStateOf("") }
        var assignee by remember { mutableStateOf("") }
        var priority by remember { mutableStateOf("Media") }

        Dialog(onDismissRequest = { showAddTaskDialog = false }) {
            Surface(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Añadir Tarea de Obra", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = taskTitle,
                        onValueChange = { taskTitle = it },
                        label = { Text("Nombre de la tarea *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = assignee,
                        onValueChange = { assignee = it },
                        label = { Text("Responsable") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Baja", "Media", "Alta", "Urgente").forEach { pr ->
                            FilterChip(
                                selected = priority == pr,
                                onClick = { priority = pr },
                                label = { Text(pr, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { showAddTaskDialog = false }, modifier = Modifier.weight(1f)) {
                            Text("Cancelar")
                        }
                        Button(
                            onClick = {
                                if (taskTitle.isNotBlank()) {
                                    val task = TaskEntity(
                                        projectId = projectId,
                                        title = taskTitle,
                                        assigneeName = assignee,
                                        priority = priority,
                                        status = "Pendiente"
                                    )
                                    viewModel.addTask(task)
                                    showAddTaskDialog = false
                                    Toast.makeText(context, "Tarea añadida", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Añadir")
                        }
                    }
                }
            }
        }
    }
}

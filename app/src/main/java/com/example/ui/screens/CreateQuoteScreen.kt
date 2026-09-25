package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entities.CustomerEntity
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.ProjectEntity
import com.example.data.local.entities.QuoteEntity
import com.example.data.local.entities.QuoteItemEntity
import com.example.ui.strings.LocalStrings
import com.example.ui.theme.CrimsonError
import com.example.ui.theme.ForestGreenLight
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.ReformasViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateQuoteScreen(
    viewModel: ReformasViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val context = LocalContext.current
    val s = LocalStrings.current
    val customers by viewModel.customers.collectAsState()
    val projects by viewModel.projects.collectAsState()
    val products by viewModel.products.collectAsState()
    val quotes by viewModel.quotes.collectAsState()

    var currentStep by remember { mutableIntStateOf(1) } // 1: Cliente, 2: Tipo de Obra, 3: Partidas, 4: Resumen

    // Step 1: Customer
    var selectedCustomerId by remember { mutableLongStateOf(customers.firstOrNull()?.id ?: 0L) }
    var isNewCustomerMode by remember { mutableStateOf(false) }
    var newCustomerName by remember { mutableStateOf("") }
    var newCustomerPhone by remember { mutableStateOf("") }
    var newCustomerEmail by remember { mutableStateOf("") }
    var newCustomerAddress by remember { mutableStateOf("") }

    // Step 2: Work type & Project
    val workTypes = listOf("Cocina", "Baño", "Reforma integral", "Fachada", "Piscina", "Construcción", "Electricidad", "Fontanería", "Pintura", "Suelos", "Otro")
    var selectedWorkType by remember { mutableStateOf("Reforma integral") }
    var selectedProjectId by remember { mutableLongStateOf(0L) }

    // Step 3: Items
    val quoteItems = remember { mutableStateListOf<QuoteItemEntity>() }
    var showAddItemDialog by remember { mutableStateOf(false) }
    var showPriceCatalogPicker by remember { mutableStateOf(false) }

    // Step 4: Discounts & VAT
    var discountPercent by remember { mutableDoubleStateOf(0.0) }
    var vatRate by remember { mutableDoubleStateOf(21.0) }
    var conditionsText by remember { mutableStateOf("Forma de pago: 40% al inicio de obra, 40% a mitad de obra, 20% al finalizar. Garantía de 2 años en instalaciones.") }

    // Calculation derived values
    val subtotal = quoteItems.sumOf { it.total }
    val discountAmount = subtotal * (discountPercent / 100.0)
    val taxableBase = subtotal - discountAmount
    val vatAmount = taxableBase * (vatRate / 100.0)
    val totalAmount = taxableBase + vatAmount

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Nuevo Presupuesto", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(
                            text = when (currentStep) {
                                1 -> s.createQuoteStep1
                                2 -> s.createQuoteStep2
                                3 -> s.createQuoteStep3
                                else -> s.createQuoteStep4
                            },
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (currentStep > 1) currentStep-- else viewModel.navigateBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Step Indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(1, 2, 3, 4).forEach { stepNum ->
                    val isActive = currentStep == stepNum
                    val isPassed = currentStep > stepNum
                    Surface(
                        color = when {
                            isActive -> MaterialTheme.colorScheme.primary
                            isPassed -> ForestGreenLight
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                    ) {
                        Text(
                            text = "Paso $stepNum",
                            color = if (isActive || isPassed) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 6.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            when (currentStep) {
                1 -> {
                    // PASO 1 - CLIENTE
                    Text("Paso 1: Seleccionar o Crear Cliente", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        FilterChip(
                            selected = !isNewCustomerMode,
                            onClick = { isNewCustomerMode = false },
                            label = { Text("Cliente Existente") }
                        )
                        FilterChip(
                            selected = isNewCustomerMode,
                            onClick = { isNewCustomerMode = true },
                            label = { Text("+ Nuevo Cliente") }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (!isNewCustomerMode) {
                        if (customers.isEmpty()) {
                            Text("No hay clientes guardados. Cambia a 'Nuevo Cliente'.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            Text("Selecciona el cliente para este presupuesto:", fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            customers.forEach { cust ->
                                val isSelected = selectedCustomerId == cust.id
                                Card(
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable { selectedCustomerId = cust.id }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(cust.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text("${cust.phone} • ${cust.address.ifEmpty { "Sin dirección" }}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        if (isSelected) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // Form for new customer
                        OutlinedTextField(
                            value = newCustomerName,
                            onValueChange = { newCustomerName = it },
                            label = { Text("Nombre y Apellidos *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newCustomerPhone,
                            onValueChange = { newCustomerPhone = it },
                            label = { Text("Teléfono / WhatsApp *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newCustomerEmail,
                            onValueChange = { newCustomerEmail = it },
                            label = { Text("Correo Electrónico") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newCustomerAddress,
                            onValueChange = { newCustomerAddress = it },
                            label = { Text("Dirección de la obra") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (isNewCustomerMode) {
                                if (newCustomerName.isBlank() || newCustomerPhone.isBlank()) {
                                    Toast.makeText(context, "Indica nombre y teléfono del cliente", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                val newCust = CustomerEntity(
                                    name = newCustomerName,
                                    phone = newCustomerPhone,
                                    whatsapp = newCustomerPhone,
                                    email = newCustomerEmail,
                                    address = newCustomerAddress,
                                    siteAddress = newCustomerAddress
                                )
                                viewModel.createCustomer(newCust) { newId ->
                                    selectedCustomerId = newId
                                    currentStep = 2
                                }
                            } else {
                                if (selectedCustomerId == 0L && customers.isNotEmpty()) {
                                    selectedCustomerId = customers.first().id
                                }
                                currentStep = 2
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text("Continuar a Tipo de Obra")
                    }
                }

                2 -> {
                    // PASO 2 - TIPO DE OBRA
                    Text("Paso 2: Tipo de Obra", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Selecciona la categoría principal de la reforma:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(14.dp))

                    workTypes.forEach { type ->
                        val isSelected = selectedWorkType == type
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { selectedWorkType = type }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(type, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, fontSize = 14.sp)
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { currentStep = 3 },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text("Continuar a Partidas")
                    }
                }

                3 -> {
                    // PASO 3 - PARTIDAS
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Paso 3: Partidas Presupuestarias", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("${quoteItems.size} partidas añadidas", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(
                            text = String.format(Locale.GERMANY, "%.2f €", subtotal),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { showAddItemDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Añadir Partida", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { showPriceCatalogPicker = true },
                            modifier = Modifier.weight(1.2f).height(48.dp)
                        ) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Catálogo Precios", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (quoteItems.isEmpty()) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("No hay partidas aún.", fontWeight = FontWeight.SemiBold)
                                Text("Añade partidas manuales o elige productos del catálogo de precios.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    } else {
                        quoteItems.forEachIndexed { index, item ->
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.description, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                        Text(
                                            text = "${item.quantity} ${item.unit} x ${String.format(Locale.GERMANY, "%.2f €", item.unitPrice)} (${item.category})",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = String.format(Locale.GERMANY, "Total: %.2f €", item.total),
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontSize = 13.sp
                                        )
                                    }
                                    IconButton(onClick = { quoteItems.removeAt(index) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = CrimsonError)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (quoteItems.isEmpty()) {
                                Toast.makeText(context, "Añade al menos una partida", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            currentStep = 4
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text("Continuar a Resumen y Totales")
                    }
                }

                4 -> {
                    // PASO 4 - RESUMEN & TOTALES
                    Text("Paso 4: Resumen, IVA y Condiciones", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Discount & VAT inputs
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Ajustes de IVA y Descuento", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf(21.0, 10.0, 4.0, 0.0).forEach { rate ->
                                    val label = if (rate == 0.0) "Exento" else "${rate.toInt()}%"
                                    FilterChip(
                                        selected = vatRate == rate,
                                        onClick = { vatRate = rate },
                                        label = { Text(label, fontSize = 11.sp) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = if (discountPercent > 0) discountPercent.toString() else "",
                                onValueChange = { discountPercent = it.toDoubleOrNull() ?: 0.0 },
                                label = { Text("Descuento aplicable (%)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Final totals card
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Subtotal:", fontSize = 13.sp)
                                Text(String.format(Locale.GERMANY, "%.2f €", subtotal), fontSize = 13.sp)
                            }
                            if (discountPercent > 0) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Descuento ($discountPercent%):", fontSize = 13.sp)
                                    Text(String.format(Locale.GERMANY, "-%.2f €", discountAmount), fontSize = 13.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Base Imponible:", fontSize = 13.sp)
                                Text(String.format(Locale.GERMANY, "%.2f €", taxableBase), fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("I.V.A. (${vatRate.toInt()}%):", fontSize = 13.sp)
                                Text(String.format(Locale.GERMANY, "%.2f €", vatAmount), fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Divider()
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("TOTAL PRESUPUESTO:", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(
                                    String.format(Locale.GERMANY, "%.2f €", totalAmount),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = conditionsText,
                        onValueChange = { conditionsText = it },
                        label = { Text("Condiciones de pago y entrega") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Final Save Button
                    Button(
                        onClick = {
                            val count = quotes.size + 1
                            val quoteNum = "PRE-2026-%03d".format(count)
                            val newQuote = QuoteEntity(
                                quoteNumber = quoteNum,
                                customerId = selectedCustomerId,
                                projectId = if (selectedProjectId > 0L) selectedProjectId else null,
                                projectType = selectedWorkType,
                                subtotal = subtotal,
                                discountPercent = discountPercent,
                                discountAmount = discountAmount,
                                taxableBase = taxableBase,
                                vatRate = vatRate,
                                totalAmount = totalAmount,
                                status = "Borrador",
                                conditions = conditionsText
                            )
                            viewModel.createQuoteWithItems(newQuote, quoteItems) { savedId ->
                                Toast.makeText(context, "¡Presupuesto $quoteNum creado!", Toast.LENGTH_SHORT).show()
                                viewModel.navigateTo(AppScreen.QuoteDetail(savedId))
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth().height(52.dp).testTag("save_final_quote_button")
                    ) {
                        Text("Guardar y Finalizar Presupuesto", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // Add Item Dialog
    if (showAddItemDialog) {
        var desc by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Mano de obra") }
        var qtyStr by remember { mutableStateOf("1") }
        var unit by remember { mutableStateOf("ud") }
        var priceStr by remember { mutableStateOf("0") }

        Dialog(onDismissRequest = { showAddItemDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Añadir Partida", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Descripción de la partida") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = qtyStr,
                            onValueChange = { qtyStr = it },
                            label = { Text("Cantidad") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = unit,
                            onValueChange = { unit = it },
                            label = { Text("Unidad (m², ud...)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = priceStr,
                        onValueChange = { priceStr = it },
                        label = { Text("Precio Unitario (€)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { showAddItemDialog = false }, modifier = Modifier.weight(1f)) {
                            Text("Cancelar")
                        }
                        Button(
                            onClick = {
                                val qty = qtyStr.toDoubleOrNull() ?: 1.0
                                val price = priceStr.toDoubleOrNull() ?: 0.0
                                if (desc.isNotBlank()) {
                                    quoteItems.add(
                                        QuoteItemEntity(
                                            quoteId = 0L,
                                            description = desc,
                                            category = category,
                                            quantity = qty,
                                            unit = unit,
                                            unitPrice = price,
                                            total = qty * price
                                        )
                                    )
                                    showAddItemDialog = false
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

    // Price Catalog Picker Dialog
    if (showPriceCatalogPicker) {
        Dialog(onDismissRequest = { showPriceCatalogPicker = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth().height(480.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Seleccionar de la Biblioteca de Precios", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    androidx.compose.foundation.lazy.LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(products.size) { idx ->
                            val prod = products[idx]
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                modifier = Modifier.fillMaxWidth().clickable {
                                    quoteItems.add(
                                        QuoteItemEntity(
                                            quoteId = 0L,
                                            description = prod.name,
                                            category = prod.category,
                                            quantity = 1.0,
                                            unit = prod.unit,
                                            unitPrice = prod.price,
                                            total = prod.price
                                        )
                                    )
                                    showPriceCatalogPicker = false
                                    Toast.makeText(context, "Partida añadida: ${prod.name}", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(prod.name, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                        Text("${prod.category} • ${prod.unit}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Text(
                                        String.format(Locale.GERMANY, "%.2f €", prod.price),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(onClick = { showPriceCatalogPicker = false }, modifier = Modifier.fillMaxWidth()) {
                        Text("Cerrar")
                    }
                }
            }
        }
    }
}

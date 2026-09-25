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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import com.example.data.local.entities.InvoiceEntity
import com.example.data.local.entities.PaymentEntity
import com.example.ui.components.StatusBadge
import com.example.ui.theme.ForestGreenLight
import com.example.ui.viewmodel.ReformasViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoicesScreen(
    viewModel: ReformasViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val context = LocalContext.current
    val invoices by viewModel.invoices.collectAsState()
    val customers by viewModel.customers.collectAsState()
    val projects by viewModel.projects.collectAsState()

    var selectedInvoiceForPayment by remember { mutableStateOf<InvoiceEntity?>(null) }

    val totalInvoiced = invoices.sumOf { it.totalAmount }
    val totalPaid = invoices.sumOf { it.paidAmount }
    val pendingAmount = (totalInvoiced - totalPaid).coerceAtLeast(0.0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Facturación y Cobros", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
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
            // Summary Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Total Emitido", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(String.format(Locale.GERMANY, "%.2f €", totalInvoiced), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Column {
                        Text("Cobrado", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(String.format(Locale.GERMANY, "%.2f €", totalPaid), fontWeight = FontWeight.Bold, color = ForestGreenLight, fontSize = 15.sp)
                    }
                    Column {
                        Text("Pendiente", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(String.format(Locale.GERMANY, "%.2f €", pendingAmount), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 15.sp)
                    }
                }
            }

            if (invoices.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay facturas emitidas todavía.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(invoices, key = { it.id }) { invoice ->
                        val customer = customers.find { it.id == invoice.customerId }
                        val project = projects.find { it.id == invoice.projectId }

                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(invoice.invoiceNumber, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                                    StatusBadge(status = invoice.status)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(customer?.name ?: "Cliente #${invoice.customerId}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                if (project != null) {
                                    Text("Obra: ${project.name}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Total: ${String.format(Locale.GERMANY, "%.2f €", invoice.totalAmount)}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("Cobrado: ${String.format(Locale.GERMANY, "%.2f €", invoice.paidAmount)}", fontSize = 11.sp, color = ForestGreenLight)
                                    }

                                    if (invoice.status != "Pagada") {
                                        Button(
                                            onClick = { selectedInvoiceForPayment = invoice },
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                            modifier = Modifier.height(36.dp)
                                        ) {
                                            Icon(Icons.Default.AttachMoney, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Registrar Cobro", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }
    }

    // Record Payment Dialog
    if (selectedInvoiceForPayment != null) {
        val invoice = selectedInvoiceForPayment!!
        var payAmountStr by remember { mutableStateOf(String.format(Locale.US, "%.2f", (invoice.totalAmount - invoice.paidAmount))) }
        var payMethod by remember { mutableStateOf("Transferencia") }
        var notes by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { selectedInvoiceForPayment = null }) {
            Surface(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Registrar Cobro - ${invoice.invoiceNumber}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = payAmountStr,
                        onValueChange = { payAmountStr = it },
                        label = { Text("Importe recibido (€)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Método de pago:", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Transferencia", "Bizum", "Efectivo", "Tarjeta").forEach { m ->
                            FilterChip(
                                selected = payMethod == m,
                                onClick = { payMethod = m },
                                label = { Text(m, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notas / Justificante") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { selectedInvoiceForPayment = null }, modifier = Modifier.weight(1f)) {
                            Text("Cancelar")
                        }
                        Button(
                            onClick = {
                                val amount = payAmountStr.toDoubleOrNull() ?: 0.0
                                if (amount > 0) {
                                    val payment = PaymentEntity(
                                        invoiceId = invoice.id,
                                        projectId = invoice.projectId,
                                        customerId = invoice.customerId,
                                        amount = amount,
                                        paymentMethod = payMethod,
                                        notes = notes
                                    )
                                    viewModel.addPayment(payment)
                                    selectedInvoiceForPayment = null
                                    Toast.makeText(context, "Cobro registrado", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Guardar Cobro")
                        }
                    }
                }
            }
        }
    }
}

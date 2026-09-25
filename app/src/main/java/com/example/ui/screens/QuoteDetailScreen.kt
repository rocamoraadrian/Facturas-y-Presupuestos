package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.CustomerEntity
import com.example.data.local.entities.QuoteEntity
import com.example.data.local.entities.QuoteItemEntity
import com.example.services.PdfGenerator
import com.example.ui.components.DigitalSignatureDialog
import com.example.ui.components.StatusBadge
import com.example.ui.strings.LocalStrings
import com.example.ui.theme.ForestGreenLight
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.ReformasViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuoteDetailScreen(
    quoteId: Long,
    viewModel: ReformasViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val context = LocalContext.current
    val s = LocalStrings.current
    val quoteFlow = remember(quoteId) { viewModel.getQuoteById(quoteId) }
    val quote by quoteFlow.collectAsState(initial = null)
    val itemsFlow = remember(quoteId) { viewModel.getQuoteItems(quoteId) }
    val items by itemsFlow.collectAsState(initial = emptyList())
    val customers by viewModel.customers.collectAsState()
    val companySettings by viewModel.companySettings.collectAsState()

    var showSignatureDialog by remember { mutableStateOf(false) }

    val currentQuote = quote
    val customer = customers.find { it.id == currentQuote?.customerId }

    if (currentQuote == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Cargando presupuesto...", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val dateStr = dateFormat.format(Date(currentQuote.date))

    if (showSignatureDialog) {
        DigitalSignatureDialog(
            quote = currentQuote,
            items = items,
            onDismiss = { showSignatureDialog = false },
            onConfirmSignature = { sigData, clientName ->
                viewModel.signQuote(currentQuote, sigData, clientName)
                showSignatureDialog = false
                Toast.makeText(context, "Presupuesto firmado y aceptado correctamente", Toast.LENGTH_SHORT).show()
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentQuote.quoteNumber, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("quote_detail_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    StatusBadge(status = currentQuote.status, modifier = Modifier.padding(end = 12.dp))
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
            // Customer & Project Info Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "INFORMACIÓN DEL CLIENTE Y OBRA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = customer?.name ?: "Cliente #${currentQuote.customerId}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (!customer?.phone.isNullOrEmpty()) {
                        Text(
                            text = "Teléfono: ${customer?.phone}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (!customer?.address.isNullOrEmpty()) {
                        Text(
                            text = "Dirección: ${customer?.address}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tipo de obra: ${currentQuote.projectType} • Fecha: $dateStr",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons Bar: PDF, WhatsApp, Signature, Convert
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        val settings = companySettings ?: com.example.data.local.entities.CompanySettingsEntity()
                        val cust = customer ?: CustomerEntity(name = "Cliente", phone = "")
                        val pdf = PdfGenerator.generateQuotePdf(context, currentQuote, cust, items, settings)
                        if (pdf != null) {
                            PdfGenerator.sharePdf(context, pdf, shareToWhatsApp = false, messageText = "Presupuesto ${currentQuote.quoteNumber}")
                        } else {
                            Toast.makeText(context, "Error generando PDF", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("export_pdf_button")
                ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("PDF", fontSize = 13.sp)
                }

                Button(
                    onClick = {
                        val settings = companySettings ?: com.example.data.local.entities.CompanySettingsEntity()
                        val cust = customer ?: CustomerEntity(name = "Cliente", phone = "")
                        val pdf = PdfGenerator.generateQuotePdf(context, currentQuote, cust, items, settings)
                        val msg = "Hola ${cust.name}, te enviamos el presupuesto correspondiente a la obra de ${currentQuote.projectType} (${currentQuote.quoteNumber}) por importe de ${String.format(Locale.GERMANY, "%.2f €", currentQuote.totalAmount)}. Puedes consultarlo en el PDF adjunto."
                        if (pdf != null) {
                            PdfGenerator.sharePdf(context, pdf, shareToWhatsApp = true, messageText = msg)
                        } else {
                            Toast.makeText(context, "Error al preparar envío", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                    modifier = Modifier
                        .weight(1.3f)
                        .height(48.dp)
                        .testTag("whatsapp_button")
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("WhatsApp", fontSize = 13.sp, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Signature & Invoice Conversion Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { showSignatureDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("open_signature_dialog_button")
                ) {
                    Icon(Icons.Default.Draw, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (currentQuote.signatureBase64.isNotEmpty()) "Ver Firma" else "Firmar Digitalmente", fontSize = 12.sp)
                }

                if (currentQuote.status == "Aceptado" && !currentQuote.isConvertedToInvoice) {
                    Button(
                        onClick = {
                            viewModel.convertQuoteToInvoice(currentQuote) { newInvId ->
                                Toast.makeText(context, "¡Factura generada con éxito!", Toast.LENGTH_SHORT).show()
                                viewModel.navigateTo(AppScreen.Invoices)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("convert_to_invoice_button")
                    ) {
                        Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Crear Factura", fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Line items header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Partidas Presupuestarias (${items.size})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Items table cards
            items.forEachIndexed { index, item ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${index + 1}. ${item.description}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = String.format(Locale.GERMANY, "%.2f €", item.total),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Categoría: ${item.category}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${item.quantity} ${item.unit} x ${String.format(Locale.GERMANY, "%.2f €", item.unitPrice)}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Financial Summary Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "DESGLOSE ECONÓMICO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Subtotal partidas", fontSize = 13.sp)
                        Text(text = String.format(Locale.GERMANY, "%.2f €", currentQuote.subtotal), fontSize = 13.sp)
                    }

                    if (currentQuote.discountPercent > 0) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Descuento (${currentQuote.discountPercent}%)", fontSize = 13.sp)
                            Text(text = String.format(Locale.GERMANY, "-%.2f €", currentQuote.discountAmount), fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Base imponible", fontSize = 13.sp)
                        Text(text = String.format(Locale.GERMANY, "%.2f €", currentQuote.taxableBase), fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "I.V.A. (${currentQuote.vatRate.toInt()}%)", fontSize = 13.sp)
                        val vatVal = currentQuote.totalAmount - currentQuote.taxableBase
                        Text(text = String.format(Locale.GERMANY, "%.2f €", vatVal), fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = MaterialTheme.colorScheme.outline)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "TOTAL PRESUPUESTO", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                        Text(
                            text = String.format(Locale.GERMANY, "%.2f €", currentQuote.totalAmount),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Digital signature status
            if (currentQuote.signatureBase64.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFD1FAE5)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF065F46),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Presupuesto Aceptado con Firma Digital",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF065F46)
                            )
                            val sigDateStr = if (currentQuote.signatureDate > 0) dateFormat.format(Date(currentQuote.signatureDate)) else dateStr
                            Text(
                                text = "Firmado por: ${currentQuote.clientSignedName} el $sigDateStr",
                                fontSize = 11.sp,
                                color = Color(0xFF047857)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

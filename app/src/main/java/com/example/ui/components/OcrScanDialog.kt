package com.example.ui.components

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.local.entities.ExpenseEntity
import com.example.data.local.entities.ProjectEntity
import com.example.services.OcrService
import com.example.ui.strings.LocalStrings
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OcrScanDialog(
    projects: List<ProjectEntity>,
    preselectedProjectId: Long = 0L,
    onDismiss: () -> Unit,
    onExpenseSaved: (ExpenseEntity, Boolean) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val s = LocalStrings.current

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isScanning by remember { mutableStateOf(false) }
    var ocrDone by remember { mutableStateOf(false) }

    // Detected / Editable fields
    var supplier by remember { mutableStateOf("") }
    var invoiceNumber by remember { mutableStateOf("") }
    var totalAmount by remember { mutableStateOf("") }
    var vatRate by remember { mutableStateOf("21") }
    var taxableBase by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Material") }
    var selectedProjectId by remember { mutableStateOf(preselectedProjectId) }
    var askAddToProject by remember { mutableStateOf(false) }
    var projectDropdownExpanded by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            selectedBitmap = bitmap
            isScanning = true
            scope.launch {
                val result = OcrService.analyzeReceiptPhoto(context, null)
                supplier = result.supplier
                invoiceNumber = result.invoiceNumber
                totalAmount = String.format(Locale.US, "%.2f", result.totalAmount)
                vatRate = result.vatRate.toInt().toString()
                taxableBase = String.format(Locale.US, "%.2f", result.taxableBase)
                description = "Compra en ${result.supplier} (Ticket ${result.invoiceNumber})"
                isScanning = false
                ocrDone = true
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            isScanning = true
            scope.launch {
                val result = OcrService.analyzeReceiptPhoto(context, uri)
                supplier = result.supplier
                invoiceNumber = result.invoiceNumber
                totalAmount = String.format(Locale.US, "%.2f", result.totalAmount)
                vatRate = result.vatRate.toInt().toString()
                taxableBase = String.format(Locale.US, "%.2f", result.taxableBase)
                description = "Gasto factura ${result.supplier}"
                isScanning = false
                ocrDone = true
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.DocumentScanner,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = s.ocrTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = s.cancel)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Image Selection Buttons
                if (!ocrDone && !isScanning) {
                    Text(
                        text = "Selecciona cómo capturar el ticket o factura:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { cameraLauncher.launch(null) },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .testTag("ocr_camera_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cámara")
                        }

                        OutlinedButton(
                            onClick = { galleryLauncher.launch("image/*") },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .testTag("ocr_gallery_button")
                        ) {
                            Icon(Icons.Default.Image, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Galería")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Quick simulation trigger for testing
                    OutlinedButton(
                        onClick = {
                            isScanning = true
                            scope.launch {
                                val result = OcrService.analyzeReceiptPhoto(context, null)
                                supplier = result.supplier
                                invoiceNumber = result.invoiceNumber
                                totalAmount = String.format(Locale.US, "%.2f", result.totalAmount)
                                vatRate = result.vatRate.toInt().toString()
                                taxableBase = String.format(Locale.US, "%.2f", result.taxableBase)
                                description = "Compra de materiales en ${result.supplier}"
                                isScanning = false
                                ocrDone = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Escanear Ticket de Prueba (Simulador)")
                    }
                }

                if (isScanning) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Procesando imagen con OCR...",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                // OCR Result & Editing fields
                if (ocrDone) {
                    // Preview thumbnail if available
                    if (selectedBitmap != null) {
                        Image(
                            bitmap = selectedBitmap!!.asImageBitmap(),
                            contentDescription = "Ticket fotografiado",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    } else if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Ticket escaneado",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Datos detectados por OCR. Puedes modificarlos:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = supplier,
                        onValueChange = { supplier = it },
                        label = { Text(s.ocrSupplier) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = totalAmount,
                            onValueChange = { totalAmount = it },
                            label = { Text("Importe Total (€)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1.2f),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = vatRate,
                            onValueChange = { vatRate = it },
                            label = { Text("IVA (%)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(0.8f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = invoiceNumber,
                        onValueChange = { invoiceNumber = it },
                        label = { Text(s.ocrInvoiceNumber) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Concepto del gasto") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Project selection
                    if (projects.isNotEmpty()) {
                        ExposedDropdownMenuBox(
                            expanded = projectDropdownExpanded,
                            onExpandedChange = { projectDropdownExpanded = !projectDropdownExpanded }
                        ) {
                            val curProj = projects.find { it.id == selectedProjectId }
                            OutlinedTextField(
                                value = curProj?.name ?: "Sin proyecto asignado",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Asociar a Obra / Proyecto") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = projectDropdownExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = projectDropdownExpanded,
                                onDismissRequest = { projectDropdownExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Sin proyecto") },
                                    onClick = {
                                        selectedProjectId = 0L
                                        projectDropdownExpanded = false
                                    }
                                )
                                projects.forEach { p ->
                                    DropdownMenuItem(
                                        text = { Text(p.name) },
                                        onClick = {
                                            selectedProjectId = p.id
                                            projectDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Ask confirmation & Save
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                ocrDone = false
                                selectedBitmap = null
                                selectedImageUri = null
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Volver")
                        }

                        Button(
                            onClick = {
                                val amountVal = totalAmount.toDoubleOrNull() ?: 0.0
                                val vatVal = vatRate.toDoubleOrNull() ?: 21.0
                                val expense = ExpenseEntity(
                                    projectId = if (selectedProjectId > 0L) selectedProjectId else null,
                                    description = description.ifEmpty { "Factura $supplier" },
                                    category = category,
                                    amount = amountVal,
                                    supplier = supplier,
                                    invoiceNumber = invoiceNumber,
                                    receiptPhotoUri = selectedImageUri?.toString() ?: "",
                                    isOcrDetected = true
                                )
                                onExpenseSaved(expense, selectedProjectId > 0L)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier
                                .weight(1.5f)
                                .testTag("save_ocr_expense_button")
                        ) {
                            Text(s.ocrSaveInvoice)
                        }
                    }
                }
            }
        }
    }
}

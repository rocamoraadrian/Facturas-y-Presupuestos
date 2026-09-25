package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.entities.QuoteEntity
import com.example.data.local.entities.QuoteItemEntity
import com.example.ui.strings.LocalStrings
import com.example.ui.theme.ForestGreenLight
import java.util.Locale

@Composable
fun DigitalSignatureDialog(
    quote: QuoteEntity,
    items: List<QuoteItemEntity>,
    onDismiss: () -> Unit,
    onConfirmSignature: (signatureData: String, clientName: String) -> Unit
) {
    val s = LocalStrings.current
    var clientName by remember { mutableStateOf(quote.clientSignedName.ifEmpty { "Cliente" }) }
    val paths = remember { mutableStateListOf<List<Offset>>() }
    var currentPath = remember { mutableStateListOf<Offset>() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 20.dp),
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
                            imageVector = Icons.Default.Draw,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = s.digitalSignature,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = s.cancel)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Summary Card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Presupuesto: ${quote.quoteNumber}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = String.format(Locale.GERMANY, "%.2f €", quote.totalAmount),
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 15.sp
                            )
                        }
                        Text(
                            text = "Obra: ${quote.projectType}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Partidas presupuestadas: ${items.size}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Conditions note
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "La firma electrónica en pantalla manifiesta la aceptación plena de las partidas y condiciones de pago.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = clientName,
                    onValueChange = { clientName = it },
                    label = { Text("Nombre y Apellidos del Firmante") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("signature_name_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Dibuja tu firma con el dedo en el recuadro:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Signature Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .background(Color(0xFFFCFDFD), RoundedCornerShape(8.dp))
                        .border(1.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                ) {
                    Canvas(
                        modifier = Modifier
                            .matchParentSize()
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        currentPath = mutableStateListOf(offset)
                                        paths.add(currentPath)
                                    },
                                    onDrag = { change, _ ->
                                        change.consume()
                                        currentPath.add(change.position)
                                    }
                                )
                            }
                    ) {
                        paths.forEach { pathPoints ->
                            if (pathPoints.size > 1) {
                                val path = Path().apply {
                                    moveTo(pathPoints[0].x, pathPoints[0].y)
                                    for (i in 1 until pathPoints.size) {
                                        lineTo(pathPoints[i].x, pathPoints[i].y)
                                    }
                                }
                                drawPath(
                                    path = path,
                                    color = Color(0xFF0F172A),
                                    style = Stroke(
                                        width = 4f,
                                        cap = StrokeCap.Round,
                                        join = StrokeJoin.Round
                                    )
                                )
                            }
                        }
                    }

                    // Clear button
                    IconButton(
                        onClick = { paths.clear() },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Limpiar firma",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    if (paths.isEmpty()) {
                        Text(
                            text = "Firma aquí...",
                            color = Color.LightGray,
                            fontSize = 13.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(s.cancel)
                    }
                    Button(
                        onClick = {
                            val sigCode = "FIRMA_VALIDADA_${System.currentTimeMillis()}"
                            onConfirmSignature(sigCode, clientName)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("confirm_signature_button")
                    ) {
                        Text("Aceptar Presupuesto")
                    }
                }
            }
        }
    }
}

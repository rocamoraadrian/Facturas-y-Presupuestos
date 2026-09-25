package com.example.ui.components

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
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.strings.LocalStrings
import java.util.Locale

@Composable
fun MaterialCalculatorDialog(
    onDismiss: () -> Unit
) {
    val s = LocalStrings.current
    var selectedTab by remember { mutableIntStateOf(0) }

    // Tiles state
    var tileArea by remember { mutableStateOf("35") }
    var tileWastePercent by remember { mutableStateOf("10") }
    var tileBoxM2 by remember { mutableStateOf("1.44") }

    // Paint state
    var paintArea by remember { mutableStateOf("120") }
    var paintYieldPerLiter by remember { mutableStateOf("10") }
    var paintCoats by remember { mutableStateOf("2") }

    // Mortar state
    var mortarArea by remember { mutableStateOf("40") }
    var mortarThicknessCm by remember { mutableStateOf("4") }
    var mortarBagKg by remember { mutableStateOf("25") }

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
                            Icons.Default.Calculate,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Calculadora de Materiales",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = s.cancel)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Azulejos", fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.GridOn, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Pintura", fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.FormatPaint, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Mortero", fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.Layers, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (selectedTab) {
                    0 -> {
                        // Azulejos
                        OutlinedTextField(
                            value = tileArea,
                            onValueChange = { tileArea = it },
                            label = { Text("Superficie a alicatar / solar (m²)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = tileWastePercent,
                                onValueChange = { tileWastePercent = it },
                                label = { Text("% Desperdicio (cortes)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = tileBoxM2,
                                onValueChange = { tileBoxM2 = it },
                                label = { Text("m² por caja") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        val areaNum = tileArea.toDoubleOrNull() ?: 0.0
                        val wasteNum = tileWastePercent.toDoubleOrNull() ?: 10.0
                        val boxM2Num = (tileBoxM2.toDoubleOrNull() ?: 1.44).coerceAtLeast(0.1)

                        val totalM2Needed = areaNum * (1.0 + (wasteNum / 100.0))
                        val boxesNeeded = Math.ceil(totalM2Needed / boxM2Num).toInt()

                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "RESULTADO ESTIMADO",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = String.format(Locale.GERMANY, "Total m² necesarios: %.2f m²", totalM2Needed),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Cajas recomendadas: $boxesNeeded cajas",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    1 -> {
                        // Pintura
                        OutlinedTextField(
                            value = paintArea,
                            onValueChange = { paintArea = it },
                            label = { Text("Superficie de paredes y techos (m²)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = paintYieldPerLiter,
                                onValueChange = { paintYieldPerLiter = it },
                                label = { Text("Rendimiento (m²/L)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = paintCoats,
                                onValueChange = { paintCoats = it },
                                label = { Text("Nº de manos") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        val areaP = paintArea.toDoubleOrNull() ?: 0.0
                        val yieldL = (paintYieldPerLiter.toDoubleOrNull() ?: 10.0).coerceAtLeast(1.0)
                        val coats = (paintCoats.toDoubleOrNull() ?: 2.0).coerceAtLeast(1.0)

                        val litersNeeded = (areaP / yieldL) * coats
                        val buckets15L = Math.ceil(litersNeeded / 15.0).toInt()

                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "RESULTADO ESTIMADO",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = String.format(Locale.GERMANY, "Litros necesarios: %.1f L", litersNeeded),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Equivalente en botes de 15L: $buckets15L botes",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    2 -> {
                        // Cemento / Mortero
                        OutlinedTextField(
                            value = mortarArea,
                            onValueChange = { mortarArea = it },
                            label = { Text("Superficie de solera o recrecido (m²)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = mortarThicknessCm,
                                onValueChange = { mortarThicknessCm = it },
                                label = { Text("Espesor solera (cm)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = mortarBagKg,
                                onValueChange = { mortarBagKg = it },
                                label = { Text("Kg por saco") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        val areaM = mortarArea.toDoubleOrNull() ?: 0.0
                        val thickCm = mortarThicknessCm.toDoubleOrNull() ?: 4.0
                        val bagKg = (mortarBagKg.toDoubleOrNull() ?: 25.0).coerceAtLeast(5.0)

                        // 1 m³ of mortar ≈ 1800 kg. Volume = area * (thickCm/100)
                        val volumeM3 = areaM * (thickCm / 100.0)
                        val totalKg = volumeM3 * 1800.0
                        val bagsNeeded = Math.ceil(totalKg / bagKg).toInt()

                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "RESULTADO ESTIMADO",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = String.format(Locale.GERMANY, "Volumen: %.2f m³ (aprox. %.0f kg)", volumeM3, totalKg),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Sacos necesarios ($bagKg kg): $bagsNeeded sacos",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

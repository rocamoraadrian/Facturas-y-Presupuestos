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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Countertops
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Foundation
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.QuoteEntity
import com.example.data.local.entities.QuoteItemEntity
import com.example.ui.theme.ForestGreenLight
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.ReformasViewModel
import java.util.Locale

data class QuoteTemplate(
    val title: String,
    val projectType: String,
    val icon: ImageVector,
    val description: String,
    val items: List<QuoteItemEntity>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplatesScreen(
    viewModel: ReformasViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val context = LocalContext.current
    val customers by viewModel.customers.collectAsState()
    val quotes by viewModel.quotes.collectAsState()

    val templates = listOf(
        QuoteTemplate(
            title = "Plantilla: Reforma de Baño Completo",
            projectType = "Baño",
            icon = Icons.Default.Bathtub,
            description = "Demolición, fontanería multicapa, plato de ducha pizarra, inodoro y alicatado.",
            items = listOf(
                QuoteItemEntity(quoteId = 0, description = "Demolición de alicatados, sanitarios y desescombro", category = "Mano de obra", quantity = 1.0, unit = "ud", unitPrice = 450.0, total = 450.0),
                QuoteItemEntity(quoteId = 0, description = "Instalación de fontanería y desagües para ducha, lavabo e inodoro", category = "Mano de obra", quantity = 1.0, unit = "ud", unitPrice = 750.0, total = 750.0),
                QuoteItemEntity(quoteId = 0, description = "Alicatado y solado porcelánico con mortero cola C2TE", category = "Mano de obra", quantity = 25.0, unit = "m²", unitPrice = 38.0, total = 950.0),
                QuoteItemEntity(quoteId = 0, description = "Suministro e instalación plato de ducha carga mineral 120x80", category = "Materiales", quantity = 1.0, unit = "ud", unitPrice = 280.0, total = 280.0),
                QuoteItemEntity(quoteId = 0, description = "Inodoro adosado a pared salida dual con tapa amortiguada", category = "Materiales", quantity = 1.0, unit = "ud", unitPrice = 240.0, total = 240.0),
                QuoteItemEntity(quoteId = 0, description = "Mampara frontal de cristal templado 8mm con tratamiento antical", category = "Materiales", quantity = 1.0, unit = "ud", unitPrice = 320.0, total = 320.0)
            )
        ),
        QuoteTemplate(
            title = "Plantilla: Reforma de Cocina",
            projectType = "Cocina",
            icon = Icons.Default.Countertops,
            description = "Puntos de fontanería, electricidad para electrodomésticos, alicatado frontal y solado.",
            items = listOf(
                QuoteItemEntity(quoteId = 0, description = "Desmontaje mobiliario antiguo y desescombro", category = "Mano de obra", quantity = 1.0, unit = "ud", unitPrice = 400.0, total = 400.0),
                QuoteItemEntity(quoteId = 0, description = "Puntos de agua y desagüe para fregadero, lavavajillas y lavadora", category = "Mano de obra", quantity = 1.0, unit = "ud", unitPrice = 650.0, total = 650.0),
                QuoteItemEntity(quoteId = 0, description = "Nueva instalación eléctrica cocina con enchufes de fuerza", category = "Mano de obra", quantity = 1.0, unit = "ud", unitPrice = 850.0, total = 850.0),
                QuoteItemEntity(quoteId = 0, description = "Suelo porcelánico rectificado antideslizante", category = "Materiales", quantity = 18.0, unit = "m²", unitPrice = 42.0, total = 756.0),
                QuoteItemEntity(quoteId = 0, description = "Falso techo continuo de pladur con 4 focos LED empotrados", category = "Materiales", quantity = 1.0, unit = "ud", unitPrice = 480.0, total = 480.0)
            )
        ),
        QuoteTemplate(
            title = "Plantilla: Pintura Plástica Integral",
            projectType = "Pintura",
            icon = Icons.Default.Brush,
            description = "Lijado, emplastecido de grietas, imprimación fijadora y 2 manos de pintura lavable.",
            items = listOf(
                QuoteItemEntity(quoteId = 0, description = "Protección de suelos, carpinterías y mobiliario con plástico y cinta", category = "Servicios", quantity = 1.0, unit = "ud", unitPrice = 180.0, total = 180.0),
                QuoteItemEntity(quoteId = 0, description = "Emplastecido y lijado de faltas y grietas en paredes y techos", category = "Mano de obra", quantity = 1.0, unit = "ud", unitPrice = 350.0, total = 350.0),
                QuoteItemEntity(quoteId = 0, description = "Pintura plástica lisa lavable blanco mate dos manos", category = "Mano de obra", quantity = 180.0, unit = "m²", unitPrice = 11.50, total = 2070.0)
            )
        ),
        QuoteTemplate(
            title = "Plantilla: Suelos Porcelánicos / Tarima",
            projectType = "Suelos",
            icon = Icons.Default.GridOn,
            description = "Nivelación, colocación de pavimento y rodapié lacado blanco.",
            items = listOf(
                QuoteItemEntity(quoteId = 0, description = "Retirada de suelo y rodapié existente con transporte a vertedero", category = "Mano de obra", quantity = 65.0, unit = "m²", unitPrice = 9.0, total = 585.0),
                QuoteItemEntity(quoteId = 0, description = "Suministro e instalación suelo porcelánico imitación madera", category = "Materiales", quantity = 65.0, unit = "m²", unitPrice = 44.0, total = 2860.0),
                QuoteItemEntity(quoteId = 0, description = "Instalación de rodapié lacado en blanco 9cm con corte a inglete", category = "Mano de obra", quantity = 55.0, unit = "ml", unitPrice = 9.50, total = 522.50)
            )
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Plantillas de Presupuestos", fontWeight = FontWeight.Bold) },
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
                .padding(16.dp)
        ) {
            Text(
                text = "Crea un presupuesto en 1 clic a partir de una plantilla estándar. Podrás modificar o añadir cualquier partida posteriormente.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(templates) { template ->
                    val templateSubtotal = template.items.sumOf { it.total }
                    val vatVal = templateSubtotal * 0.21
                    val templateTotal = templateSubtotal + vatVal

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(template.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(template.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text(template.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Incluye ${template.items.size} partidas • Base: ${String.format(Locale.GERMANY, "%.2f €", templateSubtotal)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = String.format(Locale.GERMANY, "Total estimado: %.2f €", templateTotal),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Button(
                                    onClick = {
                                        val custId = customers.firstOrNull()?.id ?: 1L
                                        val count = quotes.size + 1
                                        val quoteNum = "PRE-2026-%03d".format(count)
                                        val newQuote = QuoteEntity(
                                            quoteNumber = quoteNum,
                                            customerId = custId,
                                            projectType = template.projectType,
                                            subtotal = templateSubtotal,
                                            taxableBase = templateSubtotal,
                                            vatRate = 21.0,
                                            totalAmount = templateTotal,
                                            status = "Borrador",
                                            notes = "Presupuesto generado con ${template.title}"
                                        )
                                        viewModel.createQuoteWithItems(newQuote, template.items) { quoteId ->
                                            Toast.makeText(context, "Presupuesto generado", Toast.LENGTH_SHORT).show()
                                            viewModel.navigateTo(AppScreen.QuoteDetail(quoteId))
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Usar Plantilla", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

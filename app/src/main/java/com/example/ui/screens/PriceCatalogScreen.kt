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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entities.ProductEntity
import com.example.ui.theme.CrimsonError
import com.example.ui.viewmodel.ReformasViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceCatalogScreen(
    viewModel: ReformasViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val context = LocalContext.current
    val products by viewModel.products.collectAsState()
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    val categories = listOf("Todos", "Materiales", "Mano de obra", "Servicios")
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredProducts = products.filter { p ->
        val matchesCat = if (selectedCategoryIndex == 0) true else p.category.equals(categories[selectedCategoryIndex], ignoreCase = true)
        val matchesSearch = p.name.contains(searchQuery, ignoreCase = true) || p.supplier.contains(searchQuery, ignoreCase = true)
        matchesCat && matchesSearch
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Biblioteca de Precios", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Box(modifier = modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Search field
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Buscar en biblioteca de precios...", fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Category Tabs
                TabRow(selectedTabIndex = selectedCategoryIndex) {
                    categories.forEachIndexed { index, cat ->
                        Tab(
                            selected = selectedCategoryIndex == index,
                            onClick = { selectedCategoryIndex = index },
                            text = { Text(cat, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                        )
                    }
                }

                if (filteredProducts.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No hay artículos en esta categoría", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredProducts, key = { it.id }) { product ->
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(product.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                        Text(
                                            text = "${product.category} • Unidad: ${product.unit} ${if (product.supplier.isNotEmpty()) "• ${product.supplier}" else ""}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = String.format(Locale.GERMANY, "%.2f €", product.price),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        IconButton(onClick = { viewModel.deleteProduct(product) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = CrimsonError, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(70.dp))
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir a la biblioteca")
            }
        }
    }

    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Materiales") }
        var unit by remember { mutableStateOf("ud") }
        var priceStr by remember { mutableStateOf("") }
        var supplier by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showAddDialog = false }) {
            Surface(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Añadir a Biblioteca de Precios", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre del artículo o servicio *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Materiales", "Mano de obra", "Servicios").forEach { cat ->
                            FilterChip(
                                selected = category == cat,
                                onClick = { category = cat },
                                label = { Text(cat, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = priceStr,
                            onValueChange = { priceStr = it },
                            label = { Text("Precio base (€) *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = unit,
                            onValueChange = { unit = it },
                            label = { Text("Unidad (m², h, ud...)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = supplier,
                        onValueChange = { supplier = it },
                        label = { Text("Proveedor habitual (opcional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { showAddDialog = false }, modifier = Modifier.weight(1f)) {
                            Text("Cancelar")
                        }
                        Button(
                            onClick = {
                                val p = priceStr.toDoubleOrNull() ?: 0.0
                                if (name.isNotBlank() && p > 0) {
                                    val prod = ProductEntity(
                                        name = name,
                                        category = category,
                                        unit = unit,
                                        price = p,
                                        supplier = supplier
                                    )
                                    viewModel.addProduct(prod)
                                    showAddDialog = false
                                    Toast.makeText(context, "Producto guardado en la biblioteca", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Indica nombre y precio", Toast.LENGTH_SHORT).show()
                                }
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
}

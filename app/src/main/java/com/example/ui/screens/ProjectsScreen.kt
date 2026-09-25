package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
import com.example.data.local.entities.ProjectEntity
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.ForestGreenLight
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.ReformasViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(
    viewModel: ReformasViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val projects by viewModel.projects.collectAsState()
    val customers by viewModel.customers.collectAsState()
    val expenses by viewModel.expenses.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf("Todos") }
    var showCreateDialog by remember { mutableStateOf(false) }

    val statusTabs = listOf("Todos", "En curso", "En preparación", "Finalizado", "Planificado")

    val filteredProjects = projects.filter { p ->
        val customer = customers.find { it.id == p.customerId }
        val matchesSearch = p.name.contains(searchQuery, ignoreCase = true) ||
                p.projectType.contains(searchQuery, ignoreCase = true) ||
                (customer?.name?.contains(searchQuery, ignoreCase = true) ?: false)
        val matchesStatus = if (selectedStatusFilter == "Todos") true else p.status.equals(selectedStatusFilter, ignoreCase = true)
        matchesSearch && matchesStatus
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header & Search
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Proyectos y Obras",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${projects.size} obras registradas",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar obra, cliente o dirección...", fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("projects_search_input")
                )
            }

            // Filter Tabs
            ScrollableTabRow(
                selectedTabIndex = statusTabs.indexOf(selectedStatusFilter).coerceAtLeast(0),
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                statusTabs.forEach { tab ->
                    Tab(
                        selected = selectedStatusFilter == tab,
                        onClick = { selectedStatusFilter = tab },
                        text = { Text(tab, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                    )
                }
            }

            if (filteredProjects.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se encontraron obras",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredProjects, key = { it.id }) { project ->
                        val customer = customers.find { it.id == project.customerId }
                        val projectExpenses = expenses.filter { it.projectId == project.id }
                        val spentTotal = projectExpenses.sumOf { it.amount }
                        val budget = project.budgetAmount
                        val ratio = if (budget > 0) (spentTotal / budget).coerceIn(0.0, 1.0) else 0.0

                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(2.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.navigateTo(AppScreen.ProjectDetail(project.id)) }
                                .testTag("project_card_${project.id}")
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = project.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    StatusBadge(status = project.status)
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Cliente: ${customer?.name ?: "Sin asignar"} • ${project.projectType}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Text(
                                    text = "📍 ${project.address.ifEmpty { "Dirección no especificada" }}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Budget vs Spent Progress
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = String.format(Locale.GERMANY, "Gastado: %.0f €", spentTotal),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (ratio > 0.85) AmberWarning else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = String.format(Locale.GERMANY, "Presupuesto: %.0f €", budget),
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                LinearProgressIndicator(
                                    progress = { ratio.toFloat() },
                                    modifier = Modifier.fillMaxWidth().height(6.dp),
                                    color = if (ratio > 0.9) AmberWarning else ForestGreenLight,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { showCreateDialog = true },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("floating_new_project_button")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Nueva Obra", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }

    if (showCreateDialog) {
        var name by remember { mutableStateOf("") }
        var address by remember { mutableStateOf("") }
        var selectedCustId by remember { mutableLongStateOf(customers.firstOrNull()?.id ?: 0L) }
        var projectType by remember { mutableStateOf("Reforma integral") }
        var budgetStr by remember { mutableStateOf("") }
        var notes by remember { mutableStateOf("") }
        var custDropdownExpanded by remember { mutableStateOf(false) }

        Dialog(onDismissRequest = { showCreateDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Crear Nueva Obra / Proyecto", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre de la obra *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    if (customers.isNotEmpty()) {
                        ExposedDropdownMenuBox(
                            expanded = custDropdownExpanded,
                            onExpandedChange = { custDropdownExpanded = !custDropdownExpanded }
                        ) {
                            val curCust = customers.find { it.id == selectedCustId }
                            OutlinedTextField(
                                value = curCust?.name ?: "Seleccionar cliente",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Cliente asociado *") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = custDropdownExpanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = custDropdownExpanded,
                                onDismissRequest = { custDropdownExpanded = false }
                            ) {
                                customers.forEach { c ->
                                    DropdownMenuItem(
                                        text = { Text(c.name) },
                                        onClick = {
                                            selectedCustId = c.id
                                            custDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Dirección de la obra") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = budgetStr,
                        onValueChange = { budgetStr = it },
                        label = { Text("Presupuesto estimado (€)") },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(onClick = { showCreateDialog = false }, modifier = Modifier.weight(1f)) {
                            Text("Cancelar")
                        }
                        Button(
                            onClick = {
                                if (name.isNotBlank()) {
                                    val bVal = budgetStr.toDoubleOrNull() ?: 0.0
                                    val project = ProjectEntity(
                                        customerId = selectedCustId,
                                        name = name,
                                        address = address,
                                        projectType = projectType,
                                        budgetAmount = bVal,
                                        notes = notes
                                    )
                                    viewModel.createProject(project) {
                                        showCreateDialog = false
                                        Toast.makeText(context, "Obra creada con éxito", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, "Indica el nombre de la obra", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Crear Obra")
                        }
                    }
                }
            }
        }
    }
}

package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.StatusBadge
import com.example.ui.strings.LocalStrings
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.ReformasViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun QuotesScreen(
    viewModel: ReformasViewModel,
    modifier: Modifier = Modifier
) {
    val s = LocalStrings.current
    val quotes by viewModel.quotes.collectAsState()
    val customers by viewModel.customers.collectAsState()
    val projects by viewModel.projects.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf("Todos") }

    val statusTabs = listOf("Todos", "Borrador", "Enviado", "Aceptado", "Rechazado")

    val filteredQuotes = quotes.filter { q ->
        val customer = customers.find { it.id == q.customerId }
        val matchesSearch = q.quoteNumber.contains(searchQuery, ignoreCase = true) ||
                q.projectType.contains(searchQuery, ignoreCase = true) ||
                (customer?.name?.contains(searchQuery, ignoreCase = true) ?: false)
        val matchesStatus = if (selectedStatusFilter == "Todos") true else q.status.equals(selectedStatusFilter, ignoreCase = true)
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
                    text = "Presupuestos",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${quotes.size} presupuestos registrados",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(s.searchPlaceholder, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quotes_search_input")
                )
            }

            // Status filter tabs
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

            // Quotes list
            if (filteredQuotes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Description,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No se encontraron presupuestos",
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredQuotes, key = { it.id }) { quote ->
                        val customer = customers.find { it.id == quote.customerId }
                        val project = projects.find { it.id == quote.projectId }
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val dateStr = dateFormat.format(Date(quote.date))

                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.navigateTo(AppScreen.QuoteDetail(quote.id)) }
                                .testTag("quote_card_${quote.id}")
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = quote.quoteNumber,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    StatusBadge(status = quote.status)
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = customer?.name ?: "Cliente #${quote.customerId}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Text(
                                    text = "${quote.projectType} • ${project?.name ?: "Sin obra asignada"}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = dateStr,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = String.format(Locale.GERMANY, "%.2f €", quote.totalAmount),
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }

        // Floating Action Button for New Quote
        FloatingActionButton(
            onClick = { viewModel.navigateTo(AppScreen.CreateQuote) },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("floating_new_quote_button")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Nuevo Presupuesto", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

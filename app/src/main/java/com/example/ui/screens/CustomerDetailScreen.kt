package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.StatusBadge
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.ReformasViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailScreen(
    customerId: Long,
    viewModel: ReformasViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val context = LocalContext.current
    val customerFlow = remember(customerId) { viewModel.getCustomerById(customerId) }
    val customer by customerFlow.collectAsState(initial = null)
    val quotes by viewModel.quotes.collectAsState()
    val projects by viewModel.projects.collectAsState()
    val invoices by viewModel.invoices.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Presupuestos, 1: Obras, 2: Facturas

    val currentCustomer = customer
    if (currentCustomer == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Cargando cliente...")
        }
        return
    }

    val customerQuotes = quotes.filter { it.customerId == customerId }
    val customerProjects = projects.filter { it.customerId == customerId }
    val customerInvoices = invoices.filter { it.customerId == customerId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentCustomer.name, fontWeight = FontWeight.Bold) },
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
            // Customer Header Info Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(currentCustomer.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    if (currentCustomer.company.isNotEmpty()) {
                        Text(currentCustomer.company, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Teléfono: ${currentCustomer.phone}", fontSize = 13.sp)
                    if (currentCustomer.email.isNotEmpty()) {
                        Text("Email: ${currentCustomer.email}", fontSize = 13.sp)
                    }
                    if (currentCustomer.address.isNotEmpty()) {
                        Text("Dirección: ${currentCustomer.address}", fontSize = 13.sp)
                    }
                    if (currentCustomer.notes.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Notas: ${currentCustomer.notes}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        IconButton(onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${currentCustomer.phone}"))
                            context.startActivity(intent)
                        }) {
                            Icon(Icons.Default.Call, contentDescription = "Llamar", tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = {
                            try {
                                val cleanNum = currentCustomer.whatsapp.ifEmpty { currentCustomer.phone }.replace(Regex("[^0-9]"), "")
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=$cleanNum"))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "No se pudo abrir WhatsApp", Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "WhatsApp", tint = Color(0xFF25D366))
                        }
                        if (currentCustomer.email.isNotEmpty()) {
                            IconButton(onClick = {
                                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${currentCustomer.email}"))
                                context.startActivity(intent)
                            }) {
                                Icon(Icons.Default.Email, contentDescription = "Email", tint = MaterialTheme.colorScheme.secondary)
                            }
                        }
                    }
                }
            }

            // Tabs
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Presupuestos (${customerQuotes.size})", fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Obras (${customerProjects.size})", fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Facturas (${customerInvoices.size})", fontSize = 12.sp) }
                )
            }

            // Tab Content
            when (selectedTab) {
                0 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(customerQuotes) { q ->
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.navigateTo(AppScreen.QuoteDetail(q.id)) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(q.quoteNumber, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(q.projectType, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(
                                            String.format(Locale.GERMANY, "%.2f €", q.totalAmount),
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontSize = 14.sp
                                        )
                                    }
                                    StatusBadge(status = q.status)
                                }
                            }
                        }
                    }
                }
                1 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(customerProjects) { p ->
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.navigateTo(AppScreen.ProjectDetail(p.id)) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(p.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(p.projectType, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    StatusBadge(status = p.status)
                                }
                            }
                        }
                    }
                }
                2 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(customerInvoices) { inv ->
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(inv.invoiceNumber, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(
                                            String.format(Locale.GERMANY, "Total: %.2f €", inv.totalAmount),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    StatusBadge(status = inv.status)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entities.CustomerEntity
import com.example.ui.strings.LocalStrings
import com.example.ui.theme.ForestGreenLight
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.ReformasViewModel

@Composable
fun CustomersScreen(
    viewModel: ReformasViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val s = LocalStrings.current
    val customers by viewModel.customers.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showCreateDialog by remember { mutableStateOf(false) }

    val filteredCustomers = customers.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.company.contains(searchQuery, ignoreCase = true) ||
                it.phone.contains(searchQuery, ignoreCase = true) ||
                it.address.contains(searchQuery, ignoreCase = true)
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
                    text = "Clientes",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${customers.size} clientes en cartera",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar por nombre, empresa o teléfono...", fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("customers_search_input")
                )
            }

            if (filteredCustomers.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay clientes que coincidan",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredCustomers, key = { it.id }) { customer ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(2.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.navigateTo(AppScreen.CustomerDetail(customer.id)) }
                                .testTag("customer_card_${customer.id}")
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = customer.name.take(2).uppercase(),
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            fontSize = 15.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = customer.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                        if (customer.company.isNotEmpty()) {
                                            Text(
                                                text = customer.company,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        Text(
                                            text = customer.address.ifEmpty { "Dirección no especificada" },
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Fast Contact Action Buttons: Call, WhatsApp, Email
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    if (customer.phone.isNotEmpty()) {
                                        IconButton(
                                            onClick = {
                                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${customer.phone}"))
                                                context.startActivity(intent)
                                            }
                                        ) {
                                            Icon(Icons.Default.Call, contentDescription = "Llamar", tint = MaterialTheme.colorScheme.primary)
                                        }

                                        IconButton(
                                            onClick = {
                                                try {
                                                    val cleanNum = customer.whatsapp.ifEmpty { customer.phone }.replace(Regex("[^0-9]"), "")
                                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=$cleanNum"))
                                                    context.startActivity(intent)
                                                } catch (e: Exception) {
                                                    Toast.makeText(context, "No se pudo abrir WhatsApp", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        ) {
                                            Icon(Icons.Default.Share, contentDescription = "WhatsApp", tint = Color(0xFF25D366))
                                        }
                                    }

                                    if (customer.email.isNotEmpty()) {
                                        IconButton(
                                            onClick = {
                                                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${customer.email}"))
                                                context.startActivity(intent)
                                            }
                                        ) {
                                            Icon(Icons.Default.Email, contentDescription = "Email", tint = MaterialTheme.colorScheme.secondary)
                                        }
                                    }
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

        FloatingActionButton(
            onClick = { showCreateDialog = true },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("floating_new_customer_button")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Nuevo Cliente", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }

    if (showCreateDialog) {
        var name by remember { mutableStateOf("") }
        var company by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var address by remember { mutableStateOf("") }
        var notes by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showCreateDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Crear Nuevo Cliente", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre completo *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = company,
                        onValueChange = { company = it },
                        label = { Text("Empresa (opcional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Teléfono / WhatsApp *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Dirección") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(onClick = { showCreateDialog = false }, modifier = Modifier.weight(1f)) {
                            Text("Cancelar")
                        }
                        androidx.compose.material3.Button(
                            onClick = {
                                if (name.isNotBlank() && phone.isNotBlank()) {
                                    val customer = CustomerEntity(
                                        name = name,
                                        company = company,
                                        phone = phone,
                                        whatsapp = phone,
                                        email = email,
                                        address = address,
                                        siteAddress = address,
                                        notes = notes
                                    )
                                    viewModel.createCustomer(customer)
                                    showCreateDialog = false
                                    Toast.makeText(context, "Cliente guardado", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Indica nombre y teléfono", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
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

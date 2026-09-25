package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.CompanySettingsEntity
import com.example.ui.viewmodel.ReformasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanySettingsScreen(
    viewModel: ReformasViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val context = LocalContext.current
    val currentSettings by viewModel.companySettings.collectAsState()

    var commercialName by remember(currentSettings) { mutableStateOf(currentSettings?.commercialName ?: "ReformasPro") }
    var legalName by remember(currentSettings) { mutableStateOf(currentSettings?.legalName ?: "") }
    var taxId by remember(currentSettings) { mutableStateOf(currentSettings?.taxId ?: "") }
    var address by remember(currentSettings) { mutableStateOf(currentSettings?.address ?: "") }
    var phone by remember(currentSettings) { mutableStateOf(currentSettings?.phone ?: "") }
    var email by remember(currentSettings) { mutableStateOf(currentSettings?.email ?: "") }
    var website by remember(currentSettings) { mutableStateOf(currentSettings?.website ?: "") }
    var iban by remember(currentSettings) { mutableStateOf(currentSettings?.iban ?: "") }
    var paymentTerms by remember(currentSettings) { mutableStateOf(currentSettings?.paymentTerms ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración de Empresa", fontWeight = FontWeight.Bold) },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("DATOS FISCALES PARA PDF Y FACTURAS", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                    Text("Esta información aparecerá automáticamente en las cabeceras de tus presupuestos y facturas oficiales.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = commercialName,
                onValueChange = { commercialName = it },
                label = { Text("Nombre Comercial") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = legalName,
                onValueChange = { legalName = it },
                label = { Text("Razón Social") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = taxId,
                onValueChange = { taxId = it },
                label = { Text("CIF / NIF") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Domicilio Fiscal") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Teléfono de Contacto") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = website,
                onValueChange = { website = it },
                label = { Text("Página Web") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = iban,
                onValueChange = { iban = it },
                label = { Text("Cuenta Bancaria (IBAN)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = paymentTerms,
                onValueChange = { paymentTerms = it },
                label = { Text("Condiciones de pago predeterminadas") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val updated = (currentSettings ?: CompanySettingsEntity()).copy(
                        commercialName = commercialName,
                        legalName = legalName,
                        taxId = taxId,
                        address = address,
                        phone = phone,
                        email = email,
                        website = website,
                        iban = iban,
                        paymentTerms = paymentTerms
                    )
                    viewModel.updateCompanySettings(updated)
                    Toast.makeText(context, "Configuración guardada", Toast.LENGTH_SHORT).show()
                    viewModel.navigateBack()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Guardar Cambios", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

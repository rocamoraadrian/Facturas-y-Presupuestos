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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.SnippetFolder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.strings.AppLanguage
import com.example.ui.strings.LocalLanguage
import com.example.ui.strings.LocalStrings
import com.example.ui.theme.CrimsonError
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.ReformasViewModel

@Composable
fun MoreMenuScreen(
    viewModel: ReformasViewModel,
    onOpenCalculator: () -> Unit,
    modifier: Modifier = Modifier
) {
    val s = LocalStrings.current
    val currentLang by viewModel.currentLanguage.collectAsState()
    val authState by viewModel.authState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // User Profile Summary Header
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = authState.currentUserName.take(1).uppercase(),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = authState.currentUserName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(
                        text = "Rol: ${authState.currentUserRole} ${if (authState.isDemoMode) "• Sesión Demo" else ""}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(text = "GESTIÓN ECONÓMICA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(6.dp))

        Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column {
                MoreMenuItem(
                    title = s.menuInvoices,
                    subtitle = "Control de cobros y estados de facturas",
                    icon = Icons.Default.Receipt,
                    onClick = { viewModel.navigateTo(AppScreen.Invoices) },
                    testTag = "menu_invoices"
                )
                Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                MoreMenuItem(
                    title = s.menuExpenses,
                    subtitle = "Tickets, gastos reales y OCR",
                    icon = Icons.Default.ReceiptLong,
                    onClick = { viewModel.navigateTo(AppScreen.Expenses) },
                    testTag = "menu_expenses"
                )
                Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                MoreMenuItem(
                    title = s.menuPayments,
                    subtitle = "Registro de ingresos y métodos de pago",
                    icon = Icons.Default.Payments,
                    onClick = { viewModel.navigateTo(AppScreen.Payments) },
                    testTag = "menu_payments"
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(text = "HERRAMIENTAS DE OBRA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(6.dp))

        Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column {
                MoreMenuItem(
                    title = s.menuTasks,
                    subtitle = "Plazos, prioridades y responsables",
                    icon = Icons.Default.Assignment,
                    onClick = { viewModel.navigateTo(AppScreen.Tasks) },
                    testTag = "menu_tasks"
                )
                Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                MoreMenuItem(
                    title = s.menuTemplates,
                    subtitle = "Plantillas rápidas para baños, cocinas y pintura",
                    icon = Icons.Default.SnippetFolder,
                    onClick = { viewModel.navigateTo(AppScreen.Templates) },
                    testTag = "menu_templates"
                )
                Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                MoreMenuItem(
                    title = s.menuPriceList,
                    subtitle = "Catálogo de materiales, mano de obra y servicios",
                    icon = Icons.Default.MenuBook,
                    onClick = { viewModel.navigateTo(AppScreen.PriceCatalog) },
                    testTag = "menu_price_list"
                )
                Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                MoreMenuItem(
                    title = s.menuCalculator,
                    subtitle = "Cálculo de azulejos, pintura y mortero",
                    icon = Icons.Default.Calculate,
                    onClick = onOpenCalculator,
                    testTag = "menu_calculator"
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(text = "EMPRESA Y CONFIGURACIÓN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(6.dp))

        Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column {
                MoreMenuItem(
                    title = s.menuSettings,
                    subtitle = "Datos fiscales, CIF, logotipo e IBAN",
                    icon = Icons.Default.CorporateFare,
                    onClick = { viewModel.navigateTo(AppScreen.CompanySettings) },
                    testTag = "menu_settings"
                )
                Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                MoreMenuItem(
                    title = s.menuUsers,
                    subtitle = "Gestión de empleados y permisos",
                    icon = Icons.Default.ManageAccounts,
                    onClick = { viewModel.navigateTo(AppScreen.UserManagement) },
                    testTag = "menu_users"
                )
                Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                MoreMenuItem(
                    title = s.menuDemoCode,
                    subtitle = "Generador de código temporal de 30 min",
                    icon = Icons.Default.HourglassTop,
                    onClick = { viewModel.navigateTo(AppScreen.DemoAccess) },
                    testTag = "menu_demo_code"
                )
                Divider(color = MaterialTheme.colorScheme.surfaceVariant)

                // Language Selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val nextLang = if (currentLang == AppLanguage.SPANISH) AppLanguage.ENGLISH else AppLanguage.SPANISH
                            viewModel.setLanguage(nextLang)
                        }
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Language, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = s.menuLanguage, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text(text = "Cambiar a ${if (currentLang == AppLanguage.SPANISH) "English 🇬🇧" else "Español 🇪🇸"}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${currentLang.flag} ${currentLang.displayName}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Logout Button
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.logout() }
                .testTag("menu_logout")
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Logout, contentDescription = null, tint = CrimsonError, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = s.logout, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CrimsonError)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun MoreMenuItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(14.dp)
            .testTag(testTag),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(14.dp)
        )
    }
}

package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.PriorityBadge
import com.example.ui.components.StatusBadge
import com.example.ui.strings.LocalStrings
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.BlueInfo
import com.example.ui.theme.ForestGreenLight
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.ReformasViewModel
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    viewModel: ReformasViewModel,
    onOpenOcr: () -> Unit,
    modifier: Modifier = Modifier
) {
    val s = LocalStrings.current
    val quotes by viewModel.quotes.collectAsState()
    val projects by viewModel.projects.collectAsState()
    val invoices by viewModel.invoices.collectAsState()
    val expenses by viewModel.expenses.collectAsState()
    val payments by viewModel.payments.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val authState by viewModel.authState.collectAsState()

    var selectedPeriod by remember { mutableStateOf(s.periodMonth) }

    // Computations
    val totalBudgeted = quotes.filter { it.status == "Aceptado" }.sumOf { it.totalAmount }
    val totalSpent = expenses.sumOf { it.amount }
    val totalInvoiced = invoices.sumOf { it.totalAmount }
    val totalCollected = payments.sumOf { it.amount }
    val pendingToCollect = (totalInvoiced - totalCollected).coerceAtLeast(0.0)
    val estimatedProfit = (totalBudgeted - totalSpent).coerceAtLeast(0.0)

    val activeProjectsCount = projects.count { it.status == "En curso" || it.status == "En preparación" }
    val finishedProjectsCount = projects.count { it.status == "Finalizado" }
    val pendingTasksCount = tasks.count { it.status != "Completada" }

    val quotesAcceptedCount = quotes.count { it.status == "Aceptado" }
    val quotesSentCount = quotes.count { it.status == "Enviado" }
    val quotesPendingCount = quotes.count { it.status == "Borrador" || it.status == "Enviado" }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Top Welcome Card
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "ReformasPro",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Hola, ${authState.currentUserName}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(4.dp)
            ) {
                Text(
                    text = authState.currentUserRole,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Action Buttons
        Text(
            text = "Acciones Rápidas",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(10.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickActionButton(
                title = "📷 Foto Factura",
                color = ForestGreenPrimary,
                textColor = Color.White,
                onClick = onOpenOcr,
                testTag = "dashboard_quick_scan"
            )
            QuickActionButton(
                title = "+ Presupuesto",
                color = MaterialTheme.colorScheme.primaryContainer,
                textColor = MaterialTheme.colorScheme.onPrimaryContainer,
                onClick = { viewModel.navigateTo(AppScreen.CreateQuote) },
                testTag = "dashboard_quick_quote"
            )
            QuickActionButton(
                title = "+ Cliente",
                color = MaterialTheme.colorScheme.surfaceVariant,
                textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                onClick = { viewModel.navigateTo(AppScreen.Customers) },
                testTag = "dashboard_quick_customer"
            )
            QuickActionButton(
                title = "+ Obra",
                color = MaterialTheme.colorScheme.surfaceVariant,
                textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                onClick = { viewModel.navigateTo(AppScreen.Projects) },
                testTag = "dashboard_quick_project"
            )
            QuickActionButton(
                title = "+ Tarea",
                color = MaterialTheme.colorScheme.surfaceVariant,
                textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                onClick = { viewModel.navigateTo(AppScreen.Tasks) },
                testTag = "dashboard_quick_task"
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Period Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(s.periodToday, s.periodWeek, s.periodMonth, s.periodYear, s.periodAll).forEach { period ->
                val isSelected = selectedPeriod == period
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedPeriod = period },
                    label = { Text(period, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Key Financial Metrics Grid
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            DashboardMetricCard(
                title = s.monthlyExpenses,
                value = String.format(Locale.GERMANY, "%.0f €", totalSpent),
                icon = Icons.Default.ReceiptLong,
                iconTint = AmberWarning,
                modifier = Modifier.weight(1f)
            )
            DashboardMetricCard(
                title = s.estimatedProfit,
                value = String.format(Locale.GERMANY, "%.0f €", estimatedProfit),
                icon = Icons.Default.TrendingUp,
                iconTint = ForestGreenLight,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            DashboardMetricCard(
                title = s.pendingInvoices,
                value = String.format(Locale.GERMANY, "%.0f €", pendingToCollect),
                icon = Icons.Default.Payment,
                iconTint = BlueInfo,
                modifier = Modifier.weight(1f)
            )
            DashboardMetricCard(
                title = s.paymentsReceived,
                value = String.format(Locale.GERMANY, "%.0f €", totalCollected),
                icon = Icons.Default.CheckCircle,
                iconTint = ForestGreenLight,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Budgeted vs Actual Spent Bar
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = s.budgetedVsSpent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    val ratio = if (totalBudgeted > 0) (totalSpent / totalBudgeted).coerceIn(0.0, 1.0) else 0.0
                    Text(
                        text = String.format(Locale.US, "%.1f %% gastado", ratio * 100),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = if (ratio > 0.9) AmberWarning else ForestGreenLight
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                val progress = if (totalBudgeted > 0) (totalSpent / totalBudgeted).toFloat().coerceIn(0f, 1f) else 0f
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp),
                    color = if (progress > 0.85f) AmberWarning else ForestGreenPrimary,
                    trackColor = MaterialTheme.colorScheme.surface
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = String.format(Locale.GERMANY, "Presupuestado: %.0f €", totalBudgeted),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = String.format(Locale.GERMANY, "Gastado real: %.0f €", totalSpent),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Project and Task Counters
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MiniCounterCard(
                title = s.activeProjects,
                count = activeProjectsCount.toString(),
                subtitle = "$finishedProjectsCount terminadas",
                icon = Icons.Default.Construction,
                modifier = Modifier
                    .weight(1f)
                    .clickable { viewModel.navigateTo(AppScreen.Projects) }
            )
            MiniCounterCard(
                title = s.pendingTasks,
                count = pendingTasksCount.toString(),
                subtitle = "Próximos plazos",
                icon = Icons.Default.Assignment,
                modifier = Modifier
                    .weight(1f)
                    .clickable { viewModel.navigateTo(AppScreen.Tasks) }
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Quotes Status Summary
        Text(
            text = "Estado de Presupuestos",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            QuoteStatusPill("Aceptados ($quotesAcceptedCount)", "Aceptado")
            QuoteStatusPill("Enviados ($quotesSentCount)", "Enviado")
            QuoteStatusPill("Pendientes ($quotesPendingCount)", "Pendiente")
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Urgent / Pending Tasks preview
        if (tasks.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tareas Próximas de Obra",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Ver todas",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { viewModel.navigateTo(AppScreen.Tasks) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            tasks.take(3).forEach { task ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (task.assigneeName.isNotEmpty()) {
                                Text(
                                    text = "Asignado a: ${task.assigneeName}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        PriorityBadge(priority = task.priority)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun QuickActionButton(
    title: String,
    color: Color,
    textColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        color = color,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Text(
            text = title,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
        )
    }
}

@Composable
fun DashboardMetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(iconTint.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun MiniCounterCard(
    title: String,
    count: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = count, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun QuoteStatusPill(label: String, status: String) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.padding(horizontal = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatusBadge(status = status)
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

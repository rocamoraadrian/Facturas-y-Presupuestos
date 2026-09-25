package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.BottomNavBar
import com.example.ui.components.DemoBanner
import com.example.ui.components.MaterialCalculatorDialog
import com.example.ui.components.OcrScanDialog
import com.example.ui.screens.AuthLoginScreen
import com.example.ui.screens.CompanySettingsScreen
import com.example.ui.screens.CustomerDetailScreen
import com.example.ui.screens.CustomersScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DemoAccessScreen
import com.example.ui.screens.ExpensesScreen
import com.example.ui.screens.InvoicesScreen
import com.example.ui.screens.MoreMenuScreen
import com.example.ui.screens.PriceCatalogScreen
import com.example.ui.screens.ProjectDetailScreen
import com.example.ui.screens.ProjectsScreen
import com.example.ui.screens.QuoteDetailScreen
import com.example.ui.screens.QuotesScreen
import com.example.ui.screens.CreateQuoteScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.screens.TemplatesScreen
import com.example.ui.screens.UsersScreen
import com.example.ui.strings.AppLanguage
import com.example.ui.strings.EnglishStrings
import com.example.ui.strings.LocalLanguage
import com.example.ui.strings.LocalStrings
import com.example.ui.strings.SpanishStrings
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.ReformasViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val reformasViewModel: ReformasViewModel = viewModel()
            val currentLanguage by reformasViewModel.currentLanguage.collectAsState()
            val strings = if (currentLanguage == AppLanguage.SPANISH) SpanishStrings else EnglishStrings

            CompositionLocalProvider(
                LocalStrings provides strings,
                LocalLanguage provides currentLanguage
            ) {
                MyApplicationTheme {
                    ReformasApp(viewModel = reformasViewModel)
                }
            }
        }
    }
}

@Composable
fun ReformasApp(viewModel: ReformasViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val authState by viewModel.authState.collectAsState()
    val projects by viewModel.projects.collectAsState()

    var showOcrDialog by remember { mutableStateOf(false) }
    var showCalculatorDialog by remember { mutableStateOf(false) }

    // Dialogs
    if (showOcrDialog) {
        OcrScanDialog(
            projects = projects,
            onDismiss = { showOcrDialog = false },
            onExpenseSaved = { expense, wasAttachedToProject ->
                viewModel.addExpense(expense)
                showOcrDialog = false
            }
        )
    }

    if (showCalculatorDialog) {
        MaterialCalculatorDialog(
            onDismiss = { showCalculatorDialog = false }
        )
    }

    if (!authState.isLoggedIn) {
        AuthLoginScreen(viewModel = viewModel)
        return
    }

    val isMainTab = when (currentScreen) {
        is AppScreen.Dashboard,
        is AppScreen.Quotes,
        is AppScreen.Customers,
        is AppScreen.Projects,
        is AppScreen.MoreMenu -> true
        else -> false
    }

    Scaffold(
        bottomBar = {
            if (isMainTab) {
                BottomNavBar(
                    currentScreen = currentScreen,
                    onNavigate = { screen -> viewModel.navigateTo(screen) }
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Real-time Demo countdown banner
            if (authState.isDemoMode) {
                DemoBanner(
                    remainingSeconds = authState.demoRemainingSeconds,
                    onExitDemo = { viewModel.logout() }
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                when (val screen = currentScreen) {
                    is AppScreen.Dashboard -> DashboardScreen(
                        viewModel = viewModel,
                        onOpenOcr = { showOcrDialog = true }
                    )
                    is AppScreen.Quotes -> QuotesScreen(viewModel = viewModel)
                    is AppScreen.Customers -> CustomersScreen(viewModel = viewModel)
                    is AppScreen.Projects -> ProjectsScreen(viewModel = viewModel)
                    is AppScreen.MoreMenu -> MoreMenuScreen(
                        viewModel = viewModel,
                        onOpenCalculator = { showCalculatorDialog = true }
                    )
                    is AppScreen.QuoteDetail -> QuoteDetailScreen(
                        quoteId = screen.quoteId,
                        viewModel = viewModel
                    )
                    is AppScreen.CustomerDetail -> CustomerDetailScreen(
                        customerId = screen.customerId,
                        viewModel = viewModel
                    )
                    is AppScreen.ProjectDetail -> ProjectDetailScreen(
                        projectId = screen.projectId,
                        initialTab = screen.initialTab,
                        viewModel = viewModel
                    )
                    is AppScreen.CreateQuote -> CreateQuoteScreen(viewModel = viewModel)
                    is AppScreen.Invoices -> InvoicesScreen(viewModel = viewModel)
                    is AppScreen.Expenses -> ExpensesScreen(
                        viewModel = viewModel,
                        onOpenOcr = { showOcrDialog = true }
                    )
                    is AppScreen.Payments -> InvoicesScreen(viewModel = viewModel)
                    is AppScreen.Tasks -> TasksScreen(viewModel = viewModel)
                    is AppScreen.Templates -> TemplatesScreen(viewModel = viewModel)
                    is AppScreen.PriceCatalog -> PriceCatalogScreen(viewModel = viewModel)
                    is AppScreen.MaterialCalculator -> {
                        showCalculatorDialog = true
                        DashboardScreen(
                            viewModel = viewModel,
                            onOpenOcr = { showOcrDialog = true }
                        )
                    }
                    is AppScreen.CompanySettings -> CompanySettingsScreen(viewModel = viewModel)
                    is AppScreen.UserManagement -> UsersScreen(viewModel = viewModel)
                    is AppScreen.DemoAccess -> DemoAccessScreen(viewModel = viewModel)
                    is AppScreen.Login -> AuthLoginScreen(viewModel = viewModel)
                }
            }
        }
    }
}

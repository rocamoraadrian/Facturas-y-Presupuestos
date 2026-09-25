package com.example.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.strings.LocalStrings
import com.example.ui.theme.ForestGreenLight
import com.example.ui.viewmodel.AppScreen

data class NavItem(
    val screen: AppScreen,
    val title: String,
    val icon: ImageVector,
    val testTag: String
)

@Composable
fun BottomNavBar(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val s = LocalStrings.current

    val items = listOf(
        NavItem(AppScreen.Dashboard, s.navDashboard, Icons.Default.Dashboard, "nav_dashboard"),
        NavItem(AppScreen.Quotes, s.navQuotes, Icons.Default.Description, "nav_quotes"),
        NavItem(AppScreen.Customers, s.navCustomers, Icons.Default.People, "nav_customers"),
        NavItem(AppScreen.Projects, s.navProjects, Icons.Default.Construction, "nav_projects"),
        NavItem(AppScreen.MoreMenu, s.navMore, Icons.Default.MoreHoriz, "nav_more")
    )

    NavigationBar(
        modifier = modifier.height(68.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = when (item.screen) {
                is AppScreen.Dashboard -> currentScreen is AppScreen.Dashboard
                is AppScreen.Quotes -> currentScreen is AppScreen.Quotes || currentScreen is AppScreen.QuoteDetail || currentScreen is AppScreen.CreateQuote
                is AppScreen.Customers -> currentScreen is AppScreen.Customers || currentScreen is AppScreen.CustomerDetail
                is AppScreen.Projects -> currentScreen is AppScreen.Projects || currentScreen is AppScreen.ProjectDetail
                is AppScreen.MoreMenu -> currentScreen is AppScreen.MoreMenu || currentScreen is AppScreen.Invoices || currentScreen is AppScreen.Expenses || currentScreen is AppScreen.Payments || currentScreen is AppScreen.Tasks || currentScreen is AppScreen.Templates || currentScreen is AppScreen.PriceCatalog || currentScreen is AppScreen.MaterialCalculator || currentScreen is AppScreen.CompanySettings || currentScreen is AppScreen.UserManagement || currentScreen is AppScreen.DemoAccess
                else -> false
            }

            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.screen) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag(item.testTag)
            )
        }
    }
}

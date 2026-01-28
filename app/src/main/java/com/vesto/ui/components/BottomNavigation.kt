package com.vesto.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.vesto.navigation.Screen

/**
 * Bottom Navigation Bar for main app screens
 */
@Composable
fun VestoBottomNavigation(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
            label = { Text("Dashboard") },
            selected = currentRoute == Screen.Dashboard.route,
            onClick = { onNavigate(Screen.Dashboard.route) }
        )
        
        NavigationBarItem(
            icon = { Icon(Icons.Default.List, contentDescription = "Expenses") },
            label = { Text("Expenses") },
            selected = currentRoute == Screen.Expenses.route,
            onClick = { onNavigate(Screen.Expenses.route) }
        )
        
        NavigationBarItem(
            icon = { Icon(Icons.Default.Insights, contentDescription = "Insights") },
            label = { Text("Insights") },
            selected = currentRoute == Screen.Insights.route,
            onClick = { onNavigate(Screen.Insights.route) }
        )
        
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
            selected = currentRoute == Screen.Settings.route,
            onClick = { onNavigate(Screen.Settings.route) }
        )
    }
}

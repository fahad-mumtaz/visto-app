package com.vesto.ui.insights

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vesto.data.local.entity.CategoryExpenseSummary
import com.vesto.viewmodel.DashboardViewModel
import java.text.NumberFormat
import java.util.*

/**
 * Insights Screen
 * Shows spending analytics, trends, and smart tips
 */
@Composable
fun InsightsScreen(
    dashboardViewModel: DashboardViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by dashboardViewModel.dashboardUiState.collectAsState()
    val monthlySummary by dashboardViewModel.monthlyCategorySummary.collectAsState()
    val totalExpenses by dashboardViewModel.monthlyTotal.collectAsState()
    
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }
        
        item {
            Text(
                text = "Insights",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Spending Summary
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Monthly Spending",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                formatCurrency(totalExpenses, uiState.currencySymbol),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Icon(
                            Icons.Default.TrendingUp,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
        
        // Top Categories
        item {
            Text(
                "Top Spending Categories",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        if (monthlySummary.isNotEmpty()) {
            items(monthlySummary.take(5).size) { index ->
                val category = monthlySummary[index]
                TopCategoryCard(category, uiState.currencySymbol, totalExpenses)
            }
        } else {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No expenses to analyze yet",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        
        // Smart Tips
        item {
            Text(
                "Smart Tips",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        item {
            SmartTipCard(
                icon = Icons.Default.Lightbulb,
                title = "Budget Progress",
                description = if (uiState.isOverBudget) {
                    "You've exceeded your budget. Consider reviewing your expenses."
                } else {
                    val percentage = (uiState.budgetProgress * 100).toInt()
                    "You've used $percentage% of your monthly budget. Keep it up!"
                }
            )
        }
        
        if (monthlySummary.isNotEmpty()) {
            val topCategory = monthlySummary.firstOrNull()
            if (topCategory != null) {
                item {
                    SmartTipCard(
                        icon = Icons.Default.Star,
                        title = "Top Category",
                        description = "Most of your spending is on ${topCategory.categoryName}. " +
                                "Consider setting a limit for this category."
                    )
                }
            }
        }
        
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun TopCategoryCard(
    category: CategoryExpenseSummary,
    currencySymbol: String,
    total: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val emoji = when (category.categoryIcon.lowercase()) {
                    "restaurant" -> "🍔"
                    "directions_car" -> "🚗"
                    "shopping_bag" -> "🛍️"
                    "movie" -> "🎬"
                    "receipt" -> "📄"
                    "health_and_safety" -> "🏥"
                    "school" -> "📚"
                    else -> "📦"
                }
                
                Text(emoji, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        category.categoryName,
                        fontWeight = FontWeight.Bold
                    )
                    val percentage = if (total > 0) ((category.totalAmount / total) * 100).toInt() else 0
                    Text(
                        "$percentage% of total",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Text(
                formatCurrency(category.totalAmount, currencySymbol),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SmartTipCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

private fun formatCurrency(amount: Double, symbol: String): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 0
        maximumFractionDigits = 0
    }
    return "$symbol ${formatter.format(amount)}"
}

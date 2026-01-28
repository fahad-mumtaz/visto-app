package com.vesto.ui.expense

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vesto.data.local.entity.ExpenseWithCategory
import com.vesto.viewmodel.ExpenseViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Expenses List Screen
 * Shows all expenses grouped by date with swipe-to-delete
 */
@Composable
fun ExpensesListScreen(
    viewModel: ExpenseViewModel,
    userPreferences: com.vesto.data.local.entity.UserPreferencesEntity?,
    modifier: Modifier = Modifier
) {
    val expenses by viewModel.expenses.collectAsState()
    val groupedExpenses = expenses.groupBy { getDateGroup(it.date) }
    var deletedExpenseMessage by remember { mutableStateOf<String?>(null) }
    
    Scaffold(
        snackbarHost = {
            if (deletedExpenseMessage != null) {
                Snackbar(
                    action = {
                        TextButton(onClick = {
                            // Undo would go here
                            deletedExpenseMessage = null
                        }) {
                            Text("UNDO")
                        }
                    }
                ) {
                    Text(deletedExpenseMessage ?: "")
                }
            }
        },
        modifier = modifier
    ) { padding ->
        if (expenses.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "📊",
                        style = MaterialTheme.typography.displayLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No expenses yet",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Add your first expense to start tracking",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }
                
                item {
                    Text(
                        text = "Expenses",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                groupedExpenses.forEach { (dateGroup, expensesInGroup) ->
                    item {
                        Text(
                            text = dateGroup,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    
                    items(expensesInGroup, key = { it.id }) { expense ->
                        ExpenseItem(
                            expense = expense,
                            currencySymbol = userPreferences?.currencySymbol ?: "₨",
                            onDelete = {
                                viewModel.deleteExpense(expense.id)
                                deletedExpenseMessage = "Expense deleted"
                            }
                        )
                    }
                }
                
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

/**
 * Individual Expense Item with swipe-to-delete
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExpenseItem(
    expense: ExpenseWithCategory,
    currencySymbol: String,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberDismissState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == DismissValue.DismissedToStart || dismissValue == DismissValue.DismissedToEnd) {
                onDelete()
                true
            } else {
                false
            }
        }
    )
    
    SwipeToDismiss(
        state = dismissState,
        background = {
            val color = if (dismissState.dismissDirection == DismissDirection.StartToEnd ||
                dismissState.dismissDirection == DismissDirection.EndToStart) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                Color.Transparent
            }
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = if (dismissState.dismissDirection == DismissDirection.StartToEnd) {
                    Alignment.CenterStart
                } else {
                    Alignment.CenterEnd
                }
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        },
        dismissContent = {
            Card(
                modifier = modifier.fillMaxWidth(),
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Category icon/color
                        val categoryColor = try {
                            Color(android.graphics.Color.parseColor(expense.categoryColor ?: "#E0E0E0"))
                        } catch (e: Exception) {
                            MaterialTheme.colorScheme.primaryContainer
                        }
                        
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(categoryColor, shape = MaterialTheme.shapes.medium),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = getCategoryEmoji(expense.categoryIcon ?: "category"),
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column {
                            Text(
                                text = expense.categoryName ?: "Uncategorized",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            if (expense.notes != null) {
                                Text(
                                    text = expense.notes,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    
                    Text(
                        text = formatCurrency(expense.amount, currencySymbol),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    )
}

/**
 * Group date helper
 */
private fun getDateGroup(timestamp: Long): String {
    val calendar = Calendar.getInstance()
    val today = calendar.clone() as Calendar
    today.set(Calendar.HOUR_OF_DAY, 0)
    today.set(Calendar.MINUTE, 0)
    today.set(Calendar.SECOND, 0)
    today.set(Calendar.MILLISECOND, 0)
    
    val yesterday = today.clone() as Calendar
    yesterday.add(Calendar.DAY_OF_YEAR, -1)
    
    calendar.timeInMillis = timestamp
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    
    return when {
        calendar.timeInMillis >= today.timeInMillis -> "Today"
        calendar.timeInMillis >= yesterday.timeInMillis -> "Yesterday"
        else -> {
            val formatter = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
            formatter.format(Date(timestamp))
        }
    }
}

/**
 * Get emoji for category
 */
private fun getCategoryEmoji(iconName: String): String {
    return when (iconName.lowercase()) {
        "restaurant", "food" -> "🍔"
        "directions_car", "transport" -> "🚗"
        "shopping_bag", "shopping" -> "🛍️"
        "movie", "entertainment" -> "🎬"
        "receipt", "bills" -> "📄"
        "health_and_safety", "health" -> "🏥"
        "school", "education" -> "📚"
        else -> "📦"
    }
}

/**
 * Format currency
 */
private fun formatCurrency(amount: Double, symbol: String): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 0
        maximumFractionDigits = 0
    }
    return "$symbol ${formatter.format(amount)}"
}

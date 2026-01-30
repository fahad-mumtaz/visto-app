package com.vesto.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vesto.data.local.entity.CategoryExpenseSummary
import com.vesto.data.repository.ExpenseRepository
import com.vesto.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for Dashboard screen
 * Manages budget, expenses, and chart data
 */
class DashboardViewModel(
    private val expenseRepository: ExpenseRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    
    // User preferences (budget, currency)
    val userPreferences = userPreferencesRepository.getUserPreferences()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    
    // Monthly total expenses
    val monthlyTotal = expenseRepository.getMonthlyTotal()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    
    // Weekly total expenses
    val weeklyTotal = expenseRepository.getWeeklyTotal()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    
    // Monthly category summary for pie chart
    val monthlyCategorySummary = expenseRepository.getMonthlyCategorySummary()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    // Weekly category summary for pie chart
    val weeklyCategorySummary = expenseRepository.getWeeklyCategorySummary()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    // Derived UI state
    val dashboardUiState: StateFlow<DashboardUiState> = combine(
        userPreferences,
        monthlyTotal,
        weeklyTotal
    ) { prefs, monthly, weekly ->
        val budget = prefs?.monthlyBudget ?: 0.0
        val moneyLeft = budget - monthly
        val budgetProgress = if (budget > 0) (monthly / budget).toFloat() else 0f
        
        DashboardUiState(
            monthlyBudget = budget,
            currencySymbol = prefs?.currencySymbol ?: "₨",
            monthlySpent = monthly,
            weeklySpent = weekly,
            moneyLeft = moneyLeft,
            budgetProgress = budgetProgress,
            isOverBudget = monthly > budget && budget > 0
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )
}

/**
 * ViewModel Factory for DashboardViewModel
 */
class DashboardViewModelFactory(
    private val expenseRepository: ExpenseRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            return DashboardViewModel(expenseRepository, userPreferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

/**
 * Dashboard UI State
 */
data class DashboardUiState(
    val monthlyBudget: Double = 0.0,
    val currencySymbol: String = "₨",
    val monthlySpent: Double = 0.0,
    val weeklySpent: Double = 0.0,
    val moneyLeft: Double = 0.0,
    val budgetProgress: Float = 0f,
    val isOverBudget: Boolean = false
)

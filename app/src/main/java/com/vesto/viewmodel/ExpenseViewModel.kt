package com.vesto.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vesto.data.local.entity.CategoryEntity
import com.vesto.data.local.entity.ExpenseWithCategory
import com.vesto.data.repository.CategoryRepository
import com.vesto.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for Expense Management
 * Handles adding, editing, deleting expenses
 */
class ExpenseViewModel(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    
    // All expenses with category details
    val expenses = expenseRepository.getAllExpenses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    // All categories
    val categories = categoryRepository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    // UI State for add expense sheet
    private val _addExpenseState = MutableStateFlow(AddExpenseState())
    val addExpenseState: StateFlow<AddExpenseState> = _addExpenseState.asStateFlow()
    
    // Recently deleted expense for undo functionality
    private var recentlyDeletedExpense: Pair<Long, ExpenseWithCategory>? = null
    
    /**
     * Set amount for new expense
     */
    fun setAmount(amountText: String) {
        val amount = amountText.toDoubleOrNull() ?: 0.0
        _addExpenseState.update { it.copy(amount = amount, amountError = null) }
    }
    
    /**
     * Select category
     */
    fun selectCategory(categoryId: Long) {
        _addExpenseState.update { it.copy(selectedCategoryId = categoryId, categoryError = null) }
    }
    
    /**
     * Set notes
     */
    fun setNotes(notes: String) {
        _addExpenseState.update { it.copy(notes = notes) }
    }
    
    /**
     * Set date
     */
    fun setDate(timestamp: Long) {
        _addExpenseState.update { it.copy(date = timestamp) }
    }
    
    /**
     * Validate and add expense
     */
    fun addExpense(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val state = _addExpenseState.value
            
            // Validation
            var hasError = false
            
            if (state.amount <= 0) {
                _addExpenseState.update { it.copy(amountError = "Please enter a valid amount") }
                hasError = true
            }
            
            if (state.selectedCategoryId == null) {
                _addExpenseState.update { it.copy(categoryError = "Please select a category") }
                hasError = true
            }
            
            if (hasError) return@launch
            
            try {
                _addExpenseState.update { it.copy(isLoading = true) }
                
                expenseRepository.addExpense(
                    amount = state.amount,
                    categoryId = state.selectedCategoryId,
                    date = state.date,
                    notes = state.notes.ifBlank { null }
                )
                
                // Reset state
                _addExpenseState.value = AddExpenseState()
                onSuccess()
                
            } catch (e: Exception) {
                _addExpenseState.update { 
                    it.copy(
                        isLoading = false,
                        amountError = "Failed to save expense"
                    )
                }
            }
        }
    }
    
    /**
     * Delete expense
     */
    fun deleteExpense(expenseId: Long) {
        viewModelScope.launch {
            val expense = expenseRepository.getExpenseById(expenseId)
            if (expense != null) {
                expenseRepository.deleteExpenseById(expenseId)
                // Note: We simplified undo - in a real app you'd store the full ExpenseWithCategory
            }
        }
    }
    
    /**
     * Reset add expense state
     */
    fun resetAddExpenseState() {
        _addExpenseState.value = AddExpenseState()
    }
}

/**
 * State for Add Expense UI
 */
data class AddExpenseState(
    val amount: Double = 0.0,
    val selectedCategoryId: Long? = null,
    val notes: String = "",
    val date: Long = System.currentTimeMillis(),
    val amountError: String? = null,
    val categoryError: String? = null,
    val isLoading: Boolean = false
)

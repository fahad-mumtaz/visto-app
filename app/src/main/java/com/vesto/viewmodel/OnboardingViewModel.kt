package com.vesto.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vesto.data.local.entity.Currency
import com.vesto.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for Onboarding flow
 * Manages budget setup and currency selection state
 */
class OnboardingViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    
    // UI State
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()
    
    /**
     * Set monthly budget
     */
    fun setBudget(budgetText: String) {
        val budget = budgetText.toDoubleOrNull() ?: 0.0
        _uiState.update { it.copy(monthlyBudget = budget) }
    }
    
    /**
     * Select currency
     */
    fun selectCurrency(currency: Currency) {
        _uiState.update { it.copy(selectedCurrency = currency) }
    }
    
    /**
     * Validate and move to next step
     */
    fun validateBudget(): Boolean {
        val isValid = _uiState.value.monthlyBudget > 0
        if (!isValid) {
            _uiState.update { it.copy(budgetError = "Please enter a valid budget") }
        } else {
            _uiState.update { it.copy(budgetError = null) }
        }
        return isValid
    }
    
    /**
     * Complete onboarding
     * Saves budget and currency to database
     */
    fun completeOnboarding(onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                
                userPreferencesRepository.completeOnboarding(
                    budget = _uiState.value.monthlyBudget,
                    currency = _uiState.value.selectedCurrency
                )
                
                _uiState.update { it.copy(isLoading = false) }
                onComplete()
                
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        budgetError = "Failed to save. Please try again."
                    )
                }
            }
        }
    }
}

/**
 * ViewModel Factory for OnboardingViewModel
 */
class OnboardingViewModelFactory(
    private val userPreferencesRepository: UserPreferencesRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OnboardingViewModel::class.java)) {
            return OnboardingViewModel(userPreferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

/**
 * UI State for onboarding
 */
data class OnboardingUiState(
    val monthlyBudget: Double = 0.0,
    val selectedCurrency: Currency = Currency.PKR,
    val budgetError: String? = null,
    val isLoading: Boolean = false
)

package com.vesto.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vesto.data.local.entity.Currency
import com.vesto.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for Settings screen
 */
class SettingsViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    
    val userPreferences = userPreferencesRepository.getUserPreferences()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    
    fun updateBudget(budget: Double) {
        viewModelScope.launch {
            userPreferencesRepository.updateMonthlyBudget(budget)
        }
    }
    
    fun updateCurrency(currency: Currency) {
        viewModelScope.launch {
            userPreferencesRepository.updateCurrency(currency)
        }
    }
    
    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setDarkMode(enabled)
        }
    }
    
    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setNotifications(enabled)
        }
    }
}

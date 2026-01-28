package com.vesto.data.repository

import com.vesto.data.local.dao.UserPreferencesDao
import com.vesto.data.local.entity.Currency
import com.vesto.data.local.entity.UserPreferencesEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository for user preferences
 * Provides a clean API for accessing and modifying user settings
 */
class UserPreferencesRepository(
    private val userPreferencesDao: UserPreferencesDao
) {
    
    /**
     * Observe user preferences changes
     */
    fun getUserPreferences(): Flow<UserPreferencesEntity?> {
        return userPreferencesDao.getUserPreferences()
    }
    
    /**
     * Get user preferences once (suspend)
     */
    suspend fun getUserPreferencesOnce(): UserPreferencesEntity? {
        return userPreferencesDao.getUserPreferencesOnce()
    }
    
    /**
     * Update monthly budget
     */
    suspend fun updateMonthlyBudget(budget: Double) {
        userPreferencesDao.updateBudget(budget)
    }
    
    /**
     * Update currency
     */
    suspend fun updateCurrency(currency: Currency) {
        userPreferencesDao.updateCurrency(currency.code, currency.symbol)
    }
    
    /**
     * Toggle dark mode
     */
    suspend fun setDarkMode(enabled: Boolean) {
        userPreferencesDao.updateDarkMode(enabled)
    }
    
    /**
     * Toggle notifications
     */
    suspend fun setNotifications(enabled: Boolean) {
        userPreferencesDao.updateNotifications(enabled)
    }
    
    /**
     * Complete onboarding
     * Saves budget and currency, marks onboarding as done
     */
    suspend fun completeOnboarding(budget: Double, currency: Currency) {
        val preferences = UserPreferencesEntity(
            id = 1,
            monthlyBudget = budget,
            currency = currency.code,
            currencySymbol = currency.symbol,
            isOnboardingCompleted = true
        )
        userPreferencesDao.insertOrUpdate(preferences)
    }
    
    /**
     * Check if onboarding is completed
     */
    suspend fun isOnboardingCompleted(): Boolean {
        return userPreferencesDao.isOnboardingCompleted() ?: false
    }
    
    /**
     * Save initial preferences (used during onboarding)
     */
    suspend fun savePreferences(preferences: UserPreferencesEntity) {
        userPreferencesDao.insertOrUpdate(preferences)
    }
}

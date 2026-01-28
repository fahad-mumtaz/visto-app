package com.vesto.data.local.dao

import androidx.room.*
import com.vesto.data.local.entity.UserPreferencesEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for user preferences
 * Provides reactive queries using Flow for observing preference changes
 */
@Dao
interface UserPreferencesDao {
    
    /**
     * Get user preferences as a Flow
     * Emits new values whenever preferences are updated
     */
    @Query("SELECT * FROM user_preferences WHERE id = 1")
    fun getUserPreferences(): Flow<UserPreferencesEntity?>
    
    /**
     * Get user preferences synchronously
     * Used for one-time reads
     */
    @Query("SELECT * FROM user_preferences WHERE id = 1")
    suspend fun getUserPreferencesOnce(): UserPreferencesEntity?
    
    /**
     * Insert or update user preferences
     * Uses REPLACE strategy to update if exists
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(preferences: UserPreferencesEntity)
    
    /**
     * Update monthly budget
     */
    @Query("UPDATE user_preferences SET monthlyBudget = :budget, updatedAt = :timestamp WHERE id = 1")
    suspend fun updateBudget(budget: Double, timestamp: Long = System.currentTimeMillis())
    
    /**
     * Update currency
     */
    @Query("UPDATE user_preferences SET currency = :currency, currencySymbol = :symbol, updatedAt = :timestamp WHERE id = 1")
    suspend fun updateCurrency(
        currency: String,
        symbol: String,
        timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * Update dark mode preference
     */
    @Query("UPDATE user_preferences SET darkModeEnabled = :enabled, updatedAt = :timestamp WHERE id = 1")
    suspend fun updateDarkMode(enabled: Boolean, timestamp: Long = System.currentTimeMillis())
    
    /**
     * Update notifications preference
     */
    @Query("UPDATE user_preferences SET notificationsEnabled = :enabled, updatedAt = :timestamp WHERE id = 1")
    suspend fun updateNotifications(enabled: Boolean, timestamp: Long = System.currentTimeMillis())
    
    /**
     * Mark onboarding as completed
     */
    @Query("UPDATE user_preferences SET isOnboardingCompleted = :completed, updatedAt = :timestamp WHERE id = 1")
    suspend fun setOnboardingCompleted(
        completed: Boolean = true,
        timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * Check if onboarding is completed
     */
    @Query("SELECT isOnboardingCompleted FROM user_preferences WHERE id = 1")
    suspend fun isOnboardingCompleted(): Boolean?
}

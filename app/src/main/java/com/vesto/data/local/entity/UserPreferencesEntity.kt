package com.vesto.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for storing user preferences
 * Stores app settings like budget, currency, and theme preferences
 */
@Entity(tableName = "user_preferences")
data class UserPreferencesEntity(
    @PrimaryKey
    val id: Int = 1, // Single row for user preferences
    
    val monthlyBudget: Double = 0.0,
    val currency: String = "PKR", // Default to Pakistani Rupee
    val currencySymbol: String = "₨",
    
    val darkModeEnabled: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val budgetAlertThreshold: Int = 80, // Alert when 80% of budget is used
    
    val isOnboardingCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Supported currencies with their symbols
 */
enum class Currency(val code: String, val symbol: String, val name: String) {
    PKR("PKR", "₨", "Pakistani Rupee"),
    USD("USD", "$", "US Dollar"),
    EUR("EUR", "€", "Euro"),
    GBP("GBP", "£", "British Pound"),
    INR("INR", "₹", "Indian Rupee"),
    AED("AED", "د.إ", "UAE Dirham"),
    SAR("SAR", "ر.س", "Saudi Riyal")
}

package com.vesto.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for individual expenses
 * Tracks each expense with amount, category, date, and optional notes
 */
@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("categoryId"), Index("date")]
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val amount: Double,
    val categoryId: Long?, // Nullable in case category is deleted
    val date: Long, // Timestamp in milliseconds
    val notes: String? = null,
    
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Data class for expense with category details
 * Used for UI display with joined data from expense and category tables
 */
data class ExpenseWithCategory(
    val id: Long,
    val amount: Double,
    val date: Long,
    val notes: String?,
    val categoryId: Long?,
    val categoryName: String?,
    val categoryIcon: String?,
    val categoryColor: String?
)

/**
 * Data class for expense summary by category
 * Used for pie charts and category-wise spending analysis
 */
data class CategoryExpenseSummary(
    val categoryId: Long,
    val categoryName: String,
    val categoryIcon: String,
    val categoryColor: String,
    val totalAmount: Double,
    val expenseCount: Int
)

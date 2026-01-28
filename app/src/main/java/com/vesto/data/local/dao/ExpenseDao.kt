package com.vesto.data.local.dao

import androidx.room.*
import com.vesto.data.local.entity.CategoryExpenseSummary
import com.vesto.data.local.entity.ExpenseEntity
import com.vesto.data.local.entity.ExpenseWithCategory
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for expenses
 * Provides comprehensive queries for expense management and analytics
 */
@Dao
interface ExpenseDao {
    
    /**
     * Get all expenses with category details as Flow
     * Sorted by date descending (newest first)
     */
    @Query("""
        SELECT 
            e.id,
            e.amount,
            e.date,
            e.notes,
            e.categoryId,
            c.name as categoryName,
            c.icon as categoryIcon,
            c.colorHex as categoryColor
        FROM expenses e
        LEFT JOIN categories c ON e.categoryId = c.id
        ORDER BY e.date DESC
    """)
    fun getAllExpensesWithCategory(): Flow<List<ExpenseWithCategory>>
    
    /**
     * Get expense by ID
     */
    @Query("SELECT * FROM expenses WHERE id = :expenseId")
    suspend fun getExpenseById(expenseId: Long): ExpenseEntity?
    
    /**
     * Insert new expense
     * Returns the ID of the inserted expense
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: ExpenseEntity): Long
    
    /**
     * Update expense
     */
    @Update
    suspend fun update(expense: ExpenseEntity)
    
    /**
     * Delete expense
     */
    @Delete
    suspend fun delete(expense: ExpenseEntity)
    
    /**
     * Delete expense by ID
     */
    @Query("DELETE FROM expenses WHERE id = :expenseId")
    suspend fun deleteById(expenseId: Long)
    
    /**
     * Get expenses for a specific date range
     */
    @Query("""
        SELECT 
            e.id,
            e.amount,
            e.date,
            e.notes,
            e.categoryId,
            c.name as categoryName,
            c.icon as categoryIcon,
            c.colorHex as categoryColor
        FROM expenses e
        LEFT JOIN categories c ON e.categoryId = c.id
        WHERE e.date >= :startDate AND e.date <= :endDate
        ORDER BY e.date DESC
    """)
    fun getExpensesInRange(startDate: Long, endDate: Long): Flow<List<ExpenseWithCategory>>
    
    /**
     * Get total expenses for current month
     */
    @Query("""
        SELECT COALESCE(SUM(amount), 0.0)
        FROM expenses
        WHERE date >= :monthStartTimestamp AND date <= :monthEndTimestamp
    """)
    fun getMonthlyTotal(monthStartTimestamp: Long, monthEndTimestamp: Long): Flow<Double>
    
    /**
     * Get total expenses for current week
     */
    @Query("""
        SELECT COALESCE(SUM(amount), 0.0)
        FROM expenses
        WHERE date >= :weekStartTimestamp AND date <= :weekEndTimestamp
    """)
    fun getWeeklyTotal(weekStartTimestamp: Long, weekEndTimestamp: Long): Flow<Double>
    
    /**
     * Get category-wise expense summary for a date range
     * Used for pie charts and category breakdowns
     */
    @Query("""
        SELECT 
            c.id as categoryId,
            c.name as categoryName,
            c.icon as categoryIcon,
            c.colorHex as categoryColor,
            COALESCE(SUM(e.amount), 0.0) as totalAmount,
            COUNT(e.id) as expenseCount
        FROM categories c
        LEFT JOIN expenses e ON c.id = e.categoryId 
            AND e.date >= :startDate 
            AND e.date <= :endDate
        WHERE c.isActive = 1
        GROUP BY c.id, c.name, c.icon, c.colorHex
        HAVING totalAmount > 0
        ORDER BY totalAmount DESC
    """)
    fun getCategoryExpenseSummary(startDate: Long, endDate: Long): Flow<List<CategoryExpenseSummary>>
    
    /**
     * Get expenses for a specific category
     */
    @Query("""
        SELECT 
            e.id,
            e.amount,
            e.date,
            e.notes,
            e.categoryId,
            c.name as categoryName,
            c.icon as categoryIcon,
            c.colorHex as categoryColor
        FROM expenses e
        LEFT JOIN categories c ON e.categoryId = c.id
        WHERE e.categoryId = :categoryId
        ORDER BY e.date DESC
    """)
    fun getExpensesByCategory(categoryId: Long): Flow<List<ExpenseWithCategory>>
    
    /**
     * Get total number of expenses
     */
    @Query("SELECT COUNT(*) FROM expenses")
    fun getTotalExpenseCount(): Flow<Int>
    
    /**
     * Delete all expenses (for testing/reset)
     */
    @Query("DELETE FROM expenses")
    suspend fun deleteAll()
}

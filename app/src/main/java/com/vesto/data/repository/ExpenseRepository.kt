package com.vesto.data.repository

import com.vesto.data.local.dao.ExpenseDao
import com.vesto.data.local.entity.CategoryExpenseSummary
import com.vesto.data.local.entity.ExpenseEntity
import com.vesto.data.local.entity.ExpenseWithCategory
import kotlinx.coroutines.flow.Flow
import java.util.*

/**
 * Repository for expenses
 * Provides business logic for expense management and analytics
 */
class ExpenseRepository(
    private val expenseDao: ExpenseDao
) {
    
    /**
     * Observe all expenses with category details
     */
    fun getAllExpenses(): Flow<List<ExpenseWithCategory>> {
        return expenseDao.getAllExpensesWithCategory()
    }
    
    /**
     * Get expense by ID
     */
    suspend fun getExpenseById(expenseId: Long): ExpenseEntity? {
        return expenseDao.getExpenseById(expenseId)
    }
    
    /**
     * Add new expense
     */
    suspend fun addExpense(
        amount: Double,
        categoryId: Long?,
        date: Long = System.currentTimeMillis(),
        notes: String? = null
    ): Long {
        val expense = ExpenseEntity(
            amount = amount,
            categoryId = categoryId,
            date = date,
            notes = notes
        )
        return expenseDao.insert(expense)
    }
    
    /**
     * Update expense
     */
    suspend fun updateExpense(expense: ExpenseEntity) {
        expenseDao.update(expense)
    }
    
    /**
     * Delete expense
     */
    suspend fun deleteExpense(expense: ExpenseEntity) {
        expenseDao.delete(expense)
    }
    
    /**
     * Delete expense by ID
     */
    suspend fun deleteExpenseById(expenseId: Long) {
        expenseDao.deleteById(expenseId)
    }
    
    /**
     * Get expenses in date range
     */
    fun getExpensesInRange(startDate: Long, endDate: Long): Flow<List<ExpenseWithCategory>> {
        return expenseDao.getExpensesInRange(startDate, endDate)
    }
    
    /**
     * Get current month's total expenses
     */
    fun getMonthlyTotal(): Flow<Double> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val monthStart = calendar.timeInMillis
        
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val monthEnd = calendar.timeInMillis
        
        return expenseDao.getMonthlyTotal(monthStart, monthEnd)
    }
    
    /**
     * Get current week's total expenses
     */
    fun getWeeklyTotal(): Flow<Double> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val weekStart = calendar.timeInMillis
        
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val weekEnd = calendar.timeInMillis
        
        return expenseDao.getWeeklyTotal(weekStart, weekEnd)
    }
    
    /**
     * Get monthly category expense summary (for pie chart)
     */
    fun getMonthlyCategorySummary(): Flow<List<CategoryExpenseSummary>> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val monthStart = calendar.timeInMillis
        
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val monthEnd = calendar.timeInMillis
        
        return expenseDao.getCategoryExpenseSummary(monthStart, monthEnd)
    }
    
    /**
     * Get weekly category expense summary (for pie chart)
     */
    fun getWeeklyCategorySummary(): Flow<List<CategoryExpenseSummary>> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val weekStart = calendar.timeInMillis
        
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val weekEnd = calendar.timeInMillis
        
        return expenseDao.getCategoryExpenseSummary(weekStart, weekEnd)
    }
    
    /**
     * Get expenses for specific category
     */
    fun getExpensesByCategory(categoryId: Long): Flow<List<ExpenseWithCategory>> {
        return expenseDao.getExpensesByCategory(categoryId)
    }
    
    /**
     * Get total expense count
     */
    fun getTotalExpenseCount(): Flow<Int> {
        return expenseDao.getTotalExpenseCount()
    }
}

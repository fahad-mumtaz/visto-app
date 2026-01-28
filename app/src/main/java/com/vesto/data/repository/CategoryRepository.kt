package com.vesto.data.repository

import com.vesto.data.local.dao.CategoryDao
import com.vesto.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository for categories
 * Handles category CRUD operations and provides clean API for UI
 */
class CategoryRepository(
    private val categoryDao: CategoryDao
) {
    
    /**
     * Observe all active categories
     */
    fun getAllCategories(): Flow<List<CategoryEntity>> {
        return categoryDao.getAllCategories()
    }
    
    /**
     * Get all categories (one-time)
     */
    suspend fun getAllCategoriesOnce(): List<CategoryEntity> {
        return categoryDao.getAllCategoriesOnce()
    }
    
    /**
     * Get category by ID
     */
    suspend fun getCategoryById(categoryId: Long): CategoryEntity? {
        return categoryDao.getCategoryById(categoryId)
    }
    
    /**
     * Observe category by ID
     */
    fun getCategoryByIdFlow(categoryId: Long): Flow<CategoryEntity?> {
        return categoryDao.getCategoryByIdFlow(categoryId)
    }
    
    /**
     * Add a new custom category
     */
    suspend fun addCategory(
        name: String,
        icon: String,
        colorHex: String
    ): Long {
        val category = CategoryEntity(
            name = name,
            icon = icon,
            colorHex = colorHex,
            isCustom = true,
            isActive = true
        )
        return categoryDao.insert(category)
    }
    
    /**
     * Update existing category
     */
    suspend fun updateCategory(category: CategoryEntity) {
        categoryDao.update(category)
    }
    
    /**
     * Delete category (soft delete)
     */
    suspend fun deleteCategory(categoryId: Long) {
        categoryDao.softDelete(categoryId)
    }
    
    /**
     * Get only custom categories
     */
    fun getCustomCategories(): Flow<List<CategoryEntity>> {
        return categoryDao.getCustomCategories()
    }
}

package com.vesto.data.local.dao

import androidx.room.*
import com.vesto.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for categories
 * Handles CRUD operations for expense categories
 */
@Dao
interface CategoryDao {
    
    /**
     * Get all active categories as Flow
     * Automatically updates when categories change
     */
    @Query("SELECT * FROM categories WHERE isActive = 1 ORDER BY isCustom ASC, name ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>
    
    /**
     * Get all active categories (one-time)
     */
    @Query("SELECT * FROM categories WHERE isActive = 1 ORDER BY isCustom ASC, name ASC")
    suspend fun getAllCategoriesOnce(): List<CategoryEntity>
    
    /**
     * Get category by ID
     */
    @Query("SELECT * FROM categories WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId: Long): CategoryEntity?
    
    /**
     * Get category by ID as Flow
     */
    @Query("SELECT * FROM categories WHERE id = :categoryId")
    fun getCategoryByIdFlow(categoryId: Long): Flow<CategoryEntity?>
    
    /**
     * Insert a new category
     * Returns the ID of the inserted category
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoryEntity): Long
    
    /**
     * Insert multiple categories
     * Used for initializing default categories
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>)
    
    /**
     * Update category
     */
    @Update
    suspend fun update(category: CategoryEntity)
    
    /**
     * Soft delete category (mark as inactive)
     */
    @Query("UPDATE categories SET isActive = 0 WHERE id = :categoryId")
    suspend fun softDelete(categoryId: Long)
    
    /**
     * Hard delete category
     */
    @Delete
    suspend fun delete(category: CategoryEntity)
    
    /**
     * Get custom categories only
     */
    @Query("SELECT * FROM categories WHERE isCustom = 1 AND isActive = 1 ORDER BY name ASC")
    fun getCustomCategories(): Flow<List<CategoryEntity>>
    
    /**
     * Check if default categories exist
     */
    @Query("SELECT COUNT(*) FROM categories WHERE isCustom = 0")
    suspend fun getDefaultCategoriesCount(): Int
}

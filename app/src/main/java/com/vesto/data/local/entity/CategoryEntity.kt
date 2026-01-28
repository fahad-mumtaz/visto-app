package com.vesto.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for expense categories
 * Stores both predefined and user-custom categories
 */
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val name: String,
    val icon: String, // Icon identifier (e.g., "food", "transport")
    val colorHex: String, // Hex color code for the category
    
    val isCustom: Boolean = false, // True if user-created, false if predefined
    val isActive: Boolean = true, // Soft delete flag
    
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Predefined expense categories with icons and colors
 */
object DefaultCategories {
    val categories = listOf(
        CategoryEntity(
            id = 1,
            name = "Food",
            icon = "restaurant",
            colorHex = "#FFB3BA",
            isCustom = false
        ),
        CategoryEntity(
            id = 2,
            name = "Transport",
            icon = "directions_car",
            colorHex = "#BAE1FF",
            isCustom = false
        ),
        CategoryEntity(
            id = 3,
            name = "Shopping",
            icon = "shopping_bag",
            colorHex = "#FFDFBA",
            isCustom = false
        ),
        CategoryEntity(
            id = 4,
            name = "Entertainment",
            icon = "movie",
            colorHex = "#FFFACD",
            isCustom = false
        ),
        CategoryEntity(
            id = 5,
            name = "Bills",
            icon = "receipt",
            colorHex = "#B5EAD7",
            isCustom = false
        ),
        CategoryEntity(
            id = 6,
            name = "Health",
            icon = "health_and_safety",
            colorHex = "#E0BBE4",
            isCustom = false
        ),
        CategoryEntity(
            id = 7,
            name = "Education",
            icon = "school",
            colorHex = "#FFD1DC",
            isCustom = false
        ),
        CategoryEntity(
            id = 8,
            name = "Other",
            icon = "category",
            colorHex = "#C7CEEA",
            isCustom = false
        )
    )
}

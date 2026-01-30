package com.vesto.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.vesto.data.local.dao.CategoryDao
import com.vesto.data.local.dao.ExpenseDao
import com.vesto.data.local.dao.UserPreferencesDao
import com.vesto.data.local.entity.CategoryEntity
import com.vesto.data.local.entity.DefaultCategories
import com.vesto.data.local.entity.ExpenseEntity
import com.vesto.data.local.entity.UserPreferencesEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Vesto Room Database
 * 
 * Entities:
 * - UserPreferencesEntity: App settings and budget
 * - CategoryEntity: Expense categories (predefined + custom)
 * - ExpenseEntity: Individual expense records
 * 
 * Version: 1
 */
@Database(
    entities = [
        UserPreferencesEntity::class,
        CategoryEntity::class,
        ExpenseEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class VestoDatabase : RoomDatabase() {
    
    abstract fun userPreferencesDao(): UserPreferencesDao
    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao(): ExpenseDao
    
    companion object {
        @Volatile
        private var INSTANCE: VestoDatabase? = null
        
        /**
         * Get database instance (singleton pattern)
         * Initializes default categories on first creation
         */
        fun getDatabase(context: Context): VestoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VestoDatabase::class.java,
                    "vesto_database"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration() // For development - remove in production
                    .build()
                
                INSTANCE = instance
                instance
            }
        }
    }
    
    /**
     * Database callback to initialize default data
     */
    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            
            // Initialize default categories when database is created
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDefaultData(database)
                }
            }
        }
        
        /**
         * Populate default categories and initial user preferences
         */
        private suspend fun populateDefaultData(database: VestoDatabase) {
            val categoryDao = database.categoryDao()
            val preferencesDao = database.userPreferencesDao()
            
            // Check if default categories already exist
            if (categoryDao.getDefaultCategoriesCount() == 0) {
                // Insert default categories
                categoryDao.insertAll(DefaultCategories.categories)
            }
            
            // Initialize user preferences if not exists
            if (preferencesDao.getUserPreferencesOnce() == null) {
                preferencesDao.insertOrUpdate(
                    UserPreferencesEntity(
                        id = 1,
                        monthlyBudget = 0.0,
                        currency = "PKR",
                        currencySymbol = "₨",
                        isOnboardingCompleted = false
                    )
                )
            }
        }
    }
}

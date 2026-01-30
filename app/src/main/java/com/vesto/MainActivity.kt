package com.vesto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.vesto.data.local.database.VestoDatabase
import com.vesto.data.repository.CategoryRepository
import com.vesto.data.repository.ExpenseRepository
import com.vesto.data.repository.UserPreferencesRepository
import com.vesto.navigation.Screen
import com.vesto.ui.components.VestoBottomNavigation
import com.vesto.ui.dashboard.DashboardScreen
import com.vesto.ui.expense.AddExpenseBottomSheet
import com.vesto.ui.expense.ExpensesListScreen
import com.vesto.ui.insights.InsightsScreen
import com.vesto.ui.onboarding.OnboardingNavHost
import com.vesto.ui.settings.SettingsScreen
import com.vesto.ui.theme.VestoTheme
import com.vesto.viewmodel.*
import kotlinx.coroutines.launch

/**
 * Main Activity for Vesto App
 * 
 * Entry point that:
 * 1. Checks onboarding status
 * 2. Navigates to onboarding or main app
 * 3. Sets up dependency injection (manual for now)
 */
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize database and repositories
        val database = VestoDatabase.getDatabase(applicationContext)
        val userPreferencesRepo = UserPreferencesRepository(database.userPreferencesDao())
        val categoryRepo = CategoryRepository(database.categoryDao())
        val expenseRepo = ExpenseRepository(database.expenseDao())
        
        setContent {
            VestoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    VestoApp(
                        userPreferencesRepo = userPreferencesRepo,
                        categoryRepo = categoryRepo,
                        expenseRepo = expenseRepo
                    )
                }
            }
        }
    }
}

/**
 * Main app composable
 * Handles navigation between onboarding and main app
 */
@Composable
fun VestoApp(
    userPreferencesRepo: UserPreferencesRepository,
    categoryRepo: CategoryRepository,
    expenseRepo: ExpenseRepository
) {
    val coroutineScope = rememberCoroutineScope()
    var isOnboardingCompleted by remember { mutableStateOf<Boolean?>(null) }
    
    // Check onboarding status
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            isOnboardingCompleted = userPreferencesRepo.isOnboardingCompleted()
        }
    }
    
    // Show loading or navigate
    when (isOnboardingCompleted) {
        null -> {
            // Loading state - could show splash screen
        }
        false -> {
            // Show onboarding
            val onboardingViewModel: OnboardingViewModel = viewModel(
                factory = OnboardingViewModelFactory(userPreferencesRepo)
            )
            OnboardingNavHost(
                viewModel = onboardingViewModel,
                onOnboardingComplete = {
                    isOnboardingCompleted = true
                }
            )
        }
        true -> {
            // Show main app
            MainNavHost(
                userPreferencesRepo = userPreferencesRepo,
                categoryRepo = categoryRepo,
                expenseRepo = expenseRepo
            )
        }
    }
}

/**
 * Main app navigation host with bottom navigation
 * Navigates between Dashboard, Expenses, Insights, Settings
 */
@Composable
fun MainNavHost(
    userPreferencesRepo: UserPreferencesRepository,
    categoryRepo: CategoryRepository,
    expenseRepo: ExpenseRepository
) {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    var showAddExpenseSheet by remember { mutableStateOf(false) }
    
    // Shared ExpenseViewModel
    val expenseViewModel: ExpenseViewModel = viewModel(
        factory = ExpenseViewModelFactory(expenseRepo, categoryRepo)
    )
    
    // User preferences for currency
    val userPreferences by userPreferencesRepo.getUserPreferences().collectAsState(initial = null)
    
    Scaffold(
        bottomBar = {
            VestoBottomNavigation(
                currentRoute = currentRoute ?: Screen.Dashboard.route,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Dashboard.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                val dashboardViewModel: DashboardViewModel = viewModel(
                    factory = DashboardViewModelFactory(expenseRepo, userPreferencesRepo)
                )
                DashboardScreen(
                    viewModel = dashboardViewModel,
                    onAddExpenseClick = { showAddExpenseSheet = true }
                )
            }
            
            composable(Screen.Expenses.route) {
                ExpensesListScreen(
                    viewModel = expenseViewModel,
                    userPreferences = userPreferences
                )
            }
            
            composable(Screen.Insights.route) {
                val dashboardViewModel: DashboardViewModel = viewModel(
                    factory = DashboardViewModelFactory(expenseRepo, userPreferencesRepo)
                )
                InsightsScreen(dashboardViewModel = dashboardViewModel)
            }
            
            composable(Screen.Settings.route) {
                val settingsViewModel: SettingsViewModel = viewModel(
                    factory = SettingsViewModelFactory(userPreferencesRepo)
                )
                SettingsScreen(viewModel = settingsViewModel)
            }
        }
    }
    
    // Add Expense Bottom Sheet
    if (showAddExpenseSheet) {
        val state by expenseViewModel.addExpenseState.collectAsState()
        val categories by expenseViewModel.categories.collectAsState()
        
        AddExpenseBottomSheet(
            state = state,
            categories = categories,
            currencySymbol = userPreferences?.currencySymbol ?: "₨",
            onAmountChange = expenseViewModel::setAmount,
            onCategorySelect = expenseViewModel::selectCategory,
            onNotesChange = expenseViewModel::setNotes,
            onSave = {
                expenseViewModel.addExpense {
                    showAddExpenseSheet = false
                }
            },
            onDismiss = {
                expenseViewModel.resetAddExpenseState()
                showAddExpenseSheet = false
            }
        )
    }
}

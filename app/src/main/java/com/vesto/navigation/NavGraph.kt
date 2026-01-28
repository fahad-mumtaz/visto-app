package com.vesto.navigation

/**
 * Navigation routes for Vesto app
 */
sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Dashboard : Screen("dashboard")
    object Expenses : Screen("expenses")
    object Insights : Screen("insights")
    object Settings : Screen("settings")
}

/**
 * Bottom navigation destinations
 */
val bottomNavItems = listOf(
    Screen.Dashboard,
    Screen.Expenses,
    Screen.Insights,
    Screen.Settings
)

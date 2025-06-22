package com.finshare.android.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.finshare.android.presentation.screens.dashboard.DashboardScreen
import com.finshare.android.presentation.screens.groups.GroupsScreen
import com.finshare.android.presentation.screens.expenses.ExpensesScreen
import com.finshare.android.presentation.screens.profile.ProfileScreen

/**
 * Main navigation component for FinShare Android application
 * Implements bottom navigation with Clean Architecture pattern
 */
@Composable
fun FinShareNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController = navController)
        }
        composable(Screen.Groups.route) {
            GroupsScreen(navController = navController)
        }
        composable(Screen.Expenses.route) {
            ExpensesScreen(navController = navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
    }
}

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Groups : Screen("groups")
    object Expenses : Screen("expenses")
    object Profile : Screen("profile")
}
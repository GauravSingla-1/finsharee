package com.finshare.android.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.finshare.android.presentation.screens.auth.AuthScreen
import com.finshare.android.presentation.screens.dashboard.DashboardScreen
import com.finshare.android.presentation.screens.groups.GroupsScreen
import com.finshare.android.presentation.screens.expenses.ExpensesScreen
import com.finshare.android.presentation.screens.profile.ProfileScreen
import com.finshare.android.presentation.screens.ai.AiCoPilotScreen
import com.finshare.android.presentation.screens.receipt.ReceiptScanScreen

@Composable
fun FinShareNavigation(
    navController: NavHostController,
    startDestination: String = FinShareDestinations.AUTH
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(FinShareDestinations.AUTH) {
            AuthScreen(navController = navController)
        }
        
        composable(FinShareDestinations.DASHBOARD) {
            DashboardScreen(navController = navController)
        }
        
        composable(FinShareDestinations.GROUPS) {
            GroupsScreen(navController = navController)
        }
        
        composable(FinShareDestinations.EXPENSES) {
            ExpensesScreen(navController = navController)
        }
        
        composable(FinShareDestinations.PROFILE) {
            ProfileScreen(navController = navController)
        }
        
        composable(FinShareDestinations.AI_COPILOT) {
            AiCoPilotScreen(navController = navController)
        }
        
        composable(FinShareDestinations.RECEIPT_SCAN) {
            ReceiptScanScreen(navController = navController)
        }
    }
}

object FinShareDestinations {
    const val AUTH = "auth"
    const val DASHBOARD = "dashboard"
    const val GROUPS = "groups"
    const val EXPENSES = "expenses"
    const val PROFILE = "profile"
    const val AI_COPILOT = "ai_copilot"
    const val RECEIPT_SCAN = "receipt_scan"
}
package com.finshare.android.data.network

object ApiConstants {
    const val BASE_URL = "http://10.0.2.2:5000/" // For Android Emulator
    // For physical device, use: "http://YOUR_COMPUTER_IP:5000/"
    
    object Endpoints {
        const val GROUPS = "api/groups"
        const val EXPENSES = "api/expenses"
        const val AI_CATEGORIZE = "api/ai/categorize"
        const val AI_TRIP_BUDGET = "api/ai/copilot/trip-budget"
        const val AI_CHAT = "api/ai/copilot/chat"
        const val ANALYTICS_BUDGETS = "api/analytics/budgets"
        const val ANALYTICS_INSIGHTS = "api/analytics/insights"
        const val SETTLEMENTS = "api/settlements/transactions"
        const val NOTIFICATIONS = "api/notifications"
    }
    
    object Headers {
        const val CONTENT_TYPE = "Content-Type"
        const val APPLICATION_JSON = "application/json"
        const val USER_ID = "X-Authenticated-User-ID"
    }
}
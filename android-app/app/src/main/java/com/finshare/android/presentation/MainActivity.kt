package com.finshare.android.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.finshare.android.presentation.navigation.FinShareNavigation
import com.finshare.android.presentation.ui.theme.FinShareTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity implementing Clean Architecture with MVVM
 * Entry point for the FinShare Android application
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinShareTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    FinShareNavigation(navController = navController)
                }
            }
        }
    }
}
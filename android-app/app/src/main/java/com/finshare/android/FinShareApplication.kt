package com.finshare.android

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * FinShare Application class with Hilt integration
 * Entry point for dependency injection throughout the app
 */
@HiltAndroidApp
class FinShareApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
    }
}
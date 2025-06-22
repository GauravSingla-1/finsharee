package com.finshare.android.di

import android.app.NotificationManager
import android.content.Context
import androidx.work.WorkManager
import com.finshare.android.data.security.BiometricAuthManager
import com.finshare.android.data.security.SecureStorageManager
import com.finshare.android.data.camera.ReceiptScannerManager
import com.finshare.android.data.local.sms.SmsParser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {

    @Provides
    @Singleton
    fun provideBiometricAuthManager(): BiometricAuthManager = BiometricAuthManager()

    @Provides
    @Singleton
    fun provideSecureStorageManager(@ApplicationContext context: Context): SecureStorageManager {
        return SecureStorageManager(context)
    }

    @Provides
    @Singleton
    fun provideReceiptScannerManager(@ApplicationContext context: Context): ReceiptScannerManager {
        return ReceiptScannerManager(context)
    }

    @Provides
    @Singleton
    fun provideSmsParser(): SmsParser = SmsParser()

    @Provides
    @Singleton
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }
}
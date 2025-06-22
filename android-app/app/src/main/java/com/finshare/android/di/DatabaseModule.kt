package com.finshare.android.di

import android.content.Context
import androidx.room.Room
import com.finshare.android.data.local.database.FinShareDatabase
import com.finshare.android.data.local.database.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideFinShareDatabase(@ApplicationContext context: Context): FinShareDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            FinShareDatabase::class.java,
            "finshare_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideUserDao(database: FinShareDatabase): UserDao = database.userDao()

    @Provides
    fun provideGroupDao(database: FinShareDatabase): GroupDao = database.groupDao()

    @Provides
    fun provideExpenseDao(database: FinShareDatabase): ExpenseDao = database.expenseDao()

    @Provides
    fun provideBudgetDao(database: FinShareDatabase): BudgetDao = database.budgetDao()

    @Provides
    fun provideSettlementDao(database: FinShareDatabase): SettlementDao = database.settlementDao()

    @Provides
    fun providePendingTransactionDao(database: FinShareDatabase): PendingTransactionDao = 
        database.pendingTransactionDao()
}
package com.finshare.android.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.finshare.android.data.local.database.dao.*
import com.finshare.android.data.local.database.entities.*

@Database(
    entities = [
        UserEntity::class,
        GroupEntity::class,
        ExpenseEntity::class,
        BudgetEntity::class,
        SettlementEntity::class,
        PendingTransactionEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FinShareDatabase : RoomDatabase() {
    
    abstract fun userDao(): UserDao
    abstract fun groupDao(): GroupDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun budgetDao(): BudgetDao
    abstract fun settlementDao(): SettlementDao
    abstract fun pendingTransactionDao(): PendingTransactionDao

    companion object {
        @Volatile
        private var INSTANCE: FinShareDatabase? = null

        fun getDatabase(context: Context): FinShareDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FinShareDatabase::class.java,
                    "finshare_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
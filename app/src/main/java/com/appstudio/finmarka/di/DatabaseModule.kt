
package com.appstudio.finmarka.di

import android.content.Context
import androidx.room.Room
import com.appstudio.finmarka.data.local.AppDatabase
import com.appstudio.finmarka.data.local.dao.BudgetDao
import com.appstudio.finmarka.data.local.dao.CategoryDao
import com.appstudio.finmarka.data.local.dao.ExpenseSplitDao
import com.appstudio.finmarka.data.local.dao.FriendDao
import com.appstudio.finmarka.data.local.dao.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGson(): com.google.gson.Gson = com.google.gson.Gson()
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "finmarka_db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideTransactionDao(database: AppDatabase): TransactionDao {
        return database.transactionDao()
    }

    @Provides
    @Singleton
    fun provideCategoryDao(database: AppDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    @Singleton
    fun provideBudgetDao(database: AppDatabase): BudgetDao {
        return database.budgetDao()
    }

    @Provides
    @Singleton
    fun provideFriendDao(database: AppDatabase): FriendDao {
        return database.friendDao()
    }

    @Provides
    @Singleton
    fun provideExpenseSplitDao(database: AppDatabase): ExpenseSplitDao {
        return database.expenseSplitDao()
    }
}

package com.appstudio.finmarka.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.appstudio.finmarka.data.local.dao.BudgetDao
import com.appstudio.finmarka.data.local.dao.CategoryDao
import com.appstudio.finmarka.data.local.dao.ExchangeRateDao
import com.appstudio.finmarka.data.local.dao.ExpenseSplitDao
import com.appstudio.finmarka.data.local.dao.FinanceDao
import com.appstudio.finmarka.data.local.dao.FriendDao
import com.appstudio.finmarka.data.local.dao.TagDao
import com.appstudio.finmarka.data.local.dao.TodoDao
import com.appstudio.finmarka.data.local.dao.TransactionDao
import com.appstudio.finmarka.data.local.dao.WarrantyDao
import com.appstudio.finmarka.data.local.entity.AccountEntity
import com.appstudio.finmarka.data.local.entity.BudgetEntity
import com.appstudio.finmarka.data.local.entity.CategoryEntity
import com.appstudio.finmarka.data.local.entity.ExchangeRateEntity
import com.appstudio.finmarka.data.local.entity.ExpenseSplitEntity
import com.appstudio.finmarka.data.local.entity.FriendEntity
import com.appstudio.finmarka.data.local.entity.TagEntity
import com.appstudio.finmarka.data.local.entity.TodoEntity
import com.appstudio.finmarka.data.local.entity.TransactionEntity
import com.appstudio.finmarka.data.local.entity.TransactionTagCrossRef
import com.appstudio.finmarka.data.local.entity.WarrantyEntity

@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        BudgetEntity::class,
        FriendEntity::class,
        ExpenseSplitEntity::class,
        TagEntity::class,
        TransactionTagCrossRef::class,
        TodoEntity::class,
        WarrantyEntity::class,
        ExchangeRateEntity::class,
        AccountEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao
    abstract fun friendDao(): FriendDao
    abstract fun expenseSplitDao(): ExpenseSplitDao
    abstract fun tagDao(): TagDao
    abstract fun todoDao(): TodoDao
    abstract fun warrantyDao(): WarrantyDao
    abstract fun exchangeRateDao(): ExchangeRateDao
    abstract fun financeDao(): FinanceDao
}

package com.appstudio.finmarka.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.appstudio.finmarka.data.local.dao.BudgetDao
import com.appstudio.finmarka.data.local.dao.CategoryDao
import com.appstudio.finmarka.data.local.dao.ExpenseSplitDao
import com.appstudio.finmarka.data.local.dao.FriendDao
import com.appstudio.finmarka.data.local.dao.TransactionDao
import com.appstudio.finmarka.data.local.entity.BudgetEntity
import com.appstudio.finmarka.data.local.entity.CategoryEntity
import com.appstudio.finmarka.data.local.entity.ExpenseSplitEntity
import com.appstudio.finmarka.data.local.entity.FriendEntity
import com.appstudio.finmarka.data.local.entity.TransactionEntity

@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        BudgetEntity::class,
        FriendEntity::class,
        ExpenseSplitEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao
    abstract fun friendDao(): FriendDao
    abstract fun expenseSplitDao(): ExpenseSplitDao
}

package com.appstudio.finmarka.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.appstudio.finmarka.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity): Long

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM transactions ORDER BY dateTime DESC, createdTimestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY dateTime DESC, createdTimestamp DESC LIMIT :limit")
    fun getRecentTransactions(limit: Int = 10): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Int): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE type = :type AND dateTime >= :startDate AND dateTime <= :endDate")
    fun getTransactionsByTypeAndDateRange(
        type: String,
        startDate: Long,
        endDate: Long
    ): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE dateTime >= :startDate AND dateTime <= :endDate ORDER BY dateTime DESC")
    fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE categoryId = :categoryId AND type = 'EXPENSE' AND dateTime >= :startDate AND dateTime <= :endDate")
    fun getExpensesByCategoryAndDateRange(
        categoryId: Int,
        startDate: Long,
        endDate: Long
    ): Flow<List<TransactionEntity>>

    @Query("SELECT COALESCE(SUM(convertedAmount), 0) FROM transactions WHERE categoryId = :categoryId AND type = 'EXPENSE' AND dateTime >= :startDate AND dateTime <= :endDate")
    suspend fun getExpenseSumByCategory(categoryId: Int, startDate: Long, endDate: Long): Double

    @Query("SELECT COALESCE(SUM(convertedAmount), 0) FROM transactions WHERE type = 'INCOME' AND dateTime >= :startDate AND dateTime <= :endDate")
    suspend fun getTotalIncome(startDate: Long, endDate: Long): Double

    @Query("SELECT COALESCE(SUM(convertedAmount), 0) FROM transactions WHERE type = 'EXPENSE' AND dateTime >= :startDate AND dateTime <= :endDate")
    suspend fun getTotalExpense(startDate: Long, endDate: Long): Double

    @Query("SELECT * FROM transactions WHERE (note LIKE '%' || :query || '%' OR amount = :amountQuery) AND dateTime >= :startDate AND dateTime <= :endDate ORDER BY dateTime DESC")
    fun searchTransactions(
        query: String,
        amountQuery: Double,
        startDate: Long,
        endDate: Long
    ): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE dateTime >= :startDate AND dateTime <= :endDate AND (:categoryId = -1 OR categoryId = :categoryId) AND (:type IS NULL OR type = :type) ORDER BY dateTime DESC")
    fun filterTransactions(
        startDate: Long,
        endDate: Long,
        categoryId: Int,
        type: String?
    ): Flow<List<TransactionEntity>>

    @Query("SELECT categoryId, SUM(convertedAmount) as total FROM transactions WHERE type = 'EXPENSE' AND dateTime >= :startDate AND dateTime <= :endDate GROUP BY categoryId")
    fun getExpenseSumsByCategory(startDate: Long, endDate: Long): Flow<List<CategoryExpenseSum>>
}

data class CategoryExpenseSum(val categoryId: Int, val total: Double)

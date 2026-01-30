package com.appstudio.finmarka.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.appstudio.finmarka.data.local.entity.ExpenseSplitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseSplitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(split: ExpenseSplitEntity): Long

    @Update
    suspend fun update(split: ExpenseSplitEntity)

    @Delete
    suspend fun delete(split: ExpenseSplitEntity)

    @Query("DELETE FROM expense_splits WHERE transactionId = :transactionId")
    suspend fun deleteByTransactionId(transactionId: Int)

    @Query("SELECT * FROM expense_splits WHERE id = :id")
    suspend fun getSplitById(id: Int): ExpenseSplitEntity?

    @Query("SELECT * FROM expense_splits WHERE transactionId = :transactionId")
    fun getSplitsForTransaction(transactionId: Int): Flow<List<ExpenseSplitEntity>>

    @Query("SELECT * FROM expense_splits WHERE friendId = :friendId")
    fun getSplitsForFriend(friendId: Int): Flow<List<ExpenseSplitEntity>>

    @Query("SELECT * FROM expense_splits WHERE friendId = :friendId AND settled = 0")
    fun getUnsettledSplitsForFriend(friendId: Int): Flow<List<ExpenseSplitEntity>>

    @Query("SELECT * FROM expense_splits")
    fun getAllSplits(): Flow<List<ExpenseSplitEntity>>
}

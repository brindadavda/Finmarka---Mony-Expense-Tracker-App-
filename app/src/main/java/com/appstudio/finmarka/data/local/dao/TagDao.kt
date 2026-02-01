package com.appstudio.finmarka.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.appstudio.finmarka.data.local.entity.TagEntity
import com.appstudio.finmarka.data.local.entity.TransactionTagCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: TagEntity): Long

    @Update
    suspend fun updateTag(tag: TagEntity)

    @Delete
    suspend fun deleteTag(tag: TagEntity)

    @Query("SELECT * FROM tags ORDER BY name ASC")
    fun getAllTags(): Flow<List<TagEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTransactionTagCrossRef(crossRef: TransactionTagCrossRef)

    @Query("DELETE FROM transaction_tag_cross_ref WHERE transactionId = :transactionId AND tagId = :tagId")
    suspend fun deleteTransactionTagCrossRef(transactionId: Int, tagId: Int)

    @Query(
        "SELECT tags.* FROM tags " +
            "INNER JOIN transaction_tag_cross_ref ON tags.id = transaction_tag_cross_ref.tagId " +
            "WHERE transaction_tag_cross_ref.transactionId = :transactionId " +
            "ORDER BY tags.name ASC"
    )
    fun getTagsForTransaction(transactionId: Int): Flow<List<TagEntity>>

    @Transaction
    @Query("DELETE FROM transaction_tag_cross_ref WHERE transactionId = :transactionId")
    suspend fun clearTagsForTransaction(transactionId: Int)
}

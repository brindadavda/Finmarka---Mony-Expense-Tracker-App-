package com.appstudio.finmarka.data.repository

import com.appstudio.finmarka.data.local.dao.ExpenseSplitDao
import com.appstudio.finmarka.data.local.dao.FriendDao
import com.appstudio.finmarka.data.local.entity.ExpenseSplitEntity
import com.appstudio.finmarka.domain.model.ExpenseSplit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseSplitRepository @Inject constructor(
    private val expenseSplitDao: ExpenseSplitDao,
    private val friendDao: FriendDao
) {

    fun getSplitsForTransaction(transactionId: Int): Flow<List<ExpenseSplit>> {
        return expenseSplitDao.getSplitsForTransaction(transactionId)
            .combine(friendDao.getAllFriends()) { splits, friends ->
                splits.map { entity ->
                    val friendName = friends.find { it.id == entity.friendId }?.name ?: ""
                    ExpenseSplit(
                        id = entity.id,
                        transactionId = entity.transactionId,
                        friendId = entity.friendId,
                        friendName = friendName,
                        shareAmount = entity.shareAmount,
                        currency = entity.currency,
                        settled = entity.settled,
                        settledAt = entity.settledAt
                    )
                }
            }
    }

    fun getUnsettledSplitsForFriend(friendId: Int): Flow<List<ExpenseSplit>> {
        return expenseSplitDao.getUnsettledSplitsForFriend(friendId)
            .combine(friendDao.getAllFriends()) { splits, friends ->
                splits.map { entity ->
                    val friendName = friends.find { it.id == entity.friendId }?.name ?: ""
                    ExpenseSplit(
                        id = entity.id,
                        transactionId = entity.transactionId,
                        friendId = entity.friendId,
                        friendName = friendName,
                        shareAmount = entity.shareAmount,
                        currency = entity.currency,
                        settled = entity.settled,
                        settledAt = entity.settledAt
                    )
                }
            }
    }

    suspend fun addSplit(transactionId: Int, friendId: Int, shareAmount: Double, currency: String): Long {
        return expenseSplitDao.insert(
            ExpenseSplitEntity(
                transactionId = transactionId,
                friendId = friendId,
                shareAmount = shareAmount,
                currency = currency
            )
        )
    }

    suspend fun markSettled(splitId: Int) {
        expenseSplitDao.getSplitsForTransaction(0).map { } // get by id would need a getById in DAO
        // For now we need getSplitById in DAO - add it
    }

    suspend fun deleteSplitsForTransaction(transactionId: Int) {
        expenseSplitDao.deleteByTransactionId(transactionId)
    }
}
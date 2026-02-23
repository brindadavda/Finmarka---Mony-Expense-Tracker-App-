package com.appstudio.finmarka.data.repository

import com.appstudio.finmarka.data.local.dao.ExpenseSplitDao
import com.appstudio.finmarka.data.local.dao.FriendDao
import com.appstudio.finmarka.data.local.entity.ExpenseSplitEntity
import com.appstudio.finmarka.data.local.entity.FriendEntity
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ExpenseSplitRepositoryTest {
    private val expenseSplitDao: ExpenseSplitDao = mock()
    private val friendDao: FriendDao = mock()
    private val repository = ExpenseSplitRepository(expenseSplitDao, friendDao)

    @Test
    fun `getSplitsForTransaction maps friend names`() = runTest {
        whenever(expenseSplitDao.getSplitsForTransaction(5)).thenReturn(
            flowOf(listOf(ExpenseSplitEntity(id = 1, transactionId = 5, friendId = 10, shareAmount = 12.0, currency = "USD")))
        )
        whenever(friendDao.getAllFriends()).thenReturn(flowOf(listOf(FriendEntity(id = 10, name = "Bob"))))

        val result = repository.getSplitsForTransaction(5).first()

        assertEquals("Bob", result.first().friendName)
    }

    @Test
    fun `getUnsettledSplitsForFriend maps missing friend to empty name`() = runTest {
        whenever(expenseSplitDao.getUnsettledSplitsForFriend(9)).thenReturn(
            flowOf(listOf(ExpenseSplitEntity(id = 1, transactionId = 5, friendId = 9, shareAmount = 12.0, currency = "USD")))
        )
        whenever(friendDao.getAllFriends()).thenReturn(flowOf(emptyList()))

        val result = repository.getUnsettledSplitsForFriend(9).first()

        assertEquals("", result.first().friendName)
    }

    @Test
    fun `deleteSplitsForTransaction delegates to dao`() = runTest {
        repository.deleteSplitsForTransaction(3)
        verify(expenseSplitDao).deleteByTransactionId(3)
    }
}

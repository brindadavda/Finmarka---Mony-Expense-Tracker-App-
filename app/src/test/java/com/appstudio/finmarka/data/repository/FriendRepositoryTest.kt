package com.appstudio.finmarka.data.repository

import com.appstudio.finmarka.data.local.dao.FriendDao
import com.appstudio.finmarka.data.local.entity.FriendEntity
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class FriendRepositoryTest {
    private val friendDao: FriendDao = mock()
    private val repository = FriendRepository(friendDao)

    @Test
    fun `getAllFriends maps entities to domain`() = runTest {
        whenever(friendDao.getAllFriends()).thenReturn(flowOf(listOf(FriendEntity(id = 1, name = "Ana", email = "a@x.com"))))

        val result = repository.getAllFriends().first()

        assertEquals(1, result.size)
        assertEquals("Ana", result.first().name)
    }

    @Test
    fun `updateFriend no-op when not found`() = runTest {
        whenever(friendDao.getFriendById(2)).thenReturn(null)

        repository.updateFriend(2, "New")

        verify(friendDao, never()).update(any())
    }

    @Test
    fun `updateFriend updates entity when found`() = runTest {
        val entity = FriendEntity(id = 2, name = "Old", email = "old@x.com")
        whenever(friendDao.getFriendById(2)).thenReturn(entity)

        repository.updateFriend(2, "New", "new@x.com")

        verify(friendDao).update(entity.copy(name = "New", email = "new@x.com"))
    }
}

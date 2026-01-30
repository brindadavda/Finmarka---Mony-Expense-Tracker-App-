package com.appstudio.finmarka.data.repository

import com.appstudio.finmarka.data.local.dao.FriendDao
import com.appstudio.finmarka.data.local.entity.FriendEntity
import com.appstudio.finmarka.domain.model.Friend
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FriendRepository @Inject constructor(
    private val friendDao: FriendDao
) {

    fun getAllFriends(): Flow<List<Friend>> {
        return friendDao.getAllFriends().map { list ->
            list.map { it.toFriend() }
        }
    }

    suspend fun getFriendById(id: Int): FriendEntity? {
        return friendDao.getFriendById(id)
    }

    suspend fun addFriend(name: String, email: String? = null): Long {
        return friendDao.insert(FriendEntity(name = name, email = email))
    }

    suspend fun updateFriend(id: Int, name: String, email: String? = null) {
        val entity = friendDao.getFriendById(id) ?: return
        friendDao.update(entity.copy(name = name, email = email))
    }

    suspend fun deleteFriend(id: Int) {
        friendDao.deleteById(id)
    }

    private fun FriendEntity.toFriend(): Friend {
        return Friend(
            id = id,
            name = name,
            email = email,
            createdTimestamp = createdTimestamp
        )
    }
}

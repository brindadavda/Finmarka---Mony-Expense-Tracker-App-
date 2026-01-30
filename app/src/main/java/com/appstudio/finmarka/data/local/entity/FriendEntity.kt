package com.appstudio.finmarka.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friends")
data class FriendEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String? = null,
    val createdTimestamp: Long = System.currentTimeMillis()
)

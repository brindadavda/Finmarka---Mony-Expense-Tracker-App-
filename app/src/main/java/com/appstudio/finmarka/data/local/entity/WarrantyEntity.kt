package com.appstudio.finmarka.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "warranties")
data class WarrantyEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productName: String,
    val purchaseDate: Long,
    val expiryDate: Long,
    val attachmentUri: String? = null,
    val reminderDaysBefore: Int = 7,
    val createdTimestamp: Long = System.currentTimeMillis()
)

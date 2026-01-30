package com.appstudio.finmarka.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,
    val currency: String,
    val convertedAmount: Double,
    val type: String, // INCOME / EXPENSE
    val categoryId: Int,
    val dateTime: Long,
    val note: String? = null,
    val paymentMode: String,
    val createdTimestamp: Long = System.currentTimeMillis()
)

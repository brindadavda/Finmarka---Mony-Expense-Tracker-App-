package com.appstudio.finmarka.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,
    val currency: String,
    val convertedAmount: Double,
    val type: String, // INCOME / EXPENSE / TRANSFER
    val categoryId: Int,
    val accountId: Int? = null,
    val merchantName: String? = null,
    val dateTime: Long,
    val note: String? = null,
    val paymentMode: String,
    val status: String = "COMPLETED",
    val isRecurring: Boolean = false,
    val isReimbursement: Boolean = false,
    val isTemplate: Boolean = false,
    val isExcluded: Boolean = false,
    val attachmentUris: String? = null,
    val createdTimestamp: Long = System.currentTimeMillis()
)

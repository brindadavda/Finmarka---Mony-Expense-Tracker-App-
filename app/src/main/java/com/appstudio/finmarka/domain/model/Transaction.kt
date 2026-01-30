package com.appstudio.finmarka.domain.model

import com.appstudio.finmarka.data.model.PaymentMode
import com.appstudio.finmarka.data.model.TransactionType

data class Transaction(
    val id: Int,
    val amount: Double,
    val currency: String,
    val convertedAmount: Double,
    val type: TransactionType,
    val categoryId: Int,
    val categoryName: String = "",
    val dateTime: Long,
    val note: String?,
    val paymentMode: PaymentMode,
    val createdTimestamp: Long
)

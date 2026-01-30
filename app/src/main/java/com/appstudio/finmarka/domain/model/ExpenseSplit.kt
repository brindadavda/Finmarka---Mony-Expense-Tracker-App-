package com.appstudio.finmarka.domain.model

data class ExpenseSplit(
    val id: Int,
    val transactionId: Int,
    val friendId: Int,
    val friendName: String = "",
    val shareAmount: Double,
    val currency: String,
    val settled: Boolean,
    val settledAt: Long? = null
)

package com.appstudio.finmarka.domain.model

import com.appstudio.finmarka.data.model.TransactionType

data class Category(
    val id: Int,
    val name: String,
    val type: TransactionType,
    val icon: String = "",
    val color: String = "#6200EE"
)

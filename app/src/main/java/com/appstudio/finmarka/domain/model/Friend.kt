package com.appstudio.finmarka.domain.model

data class Friend(
    val id: Int,
    val name: String,
    val email: String? = null,
    val createdTimestamp: Long = 0L
)

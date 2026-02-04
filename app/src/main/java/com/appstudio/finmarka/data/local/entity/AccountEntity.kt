package com.appstudio.finmarka.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val balance: Double,
    val details: String? = null,
    val currency: String,
    val colorHex: String,
    val iconName: String,
    val pinned: Boolean = false,
    val excluded: Boolean = false,
    val isCreditCard: Boolean = false,
    val creditLimit: Double? = null,
    val billingDay: Int? = null,
    val gracePeriod: Int? = null,
    val billAlerts: Boolean = false
)

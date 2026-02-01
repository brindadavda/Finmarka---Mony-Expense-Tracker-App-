package com.appstudio.finmarka.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "exchange_rates",
    indices = [Index(value = ["baseCurrency", "targetCurrency", "effectiveDate"])]
)
data class ExchangeRateEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val baseCurrency: String,
    val targetCurrency: String,
    val rate: Double,
    val effectiveDate: Long,
    val createdTimestamp: Long = System.currentTimeMillis()
)

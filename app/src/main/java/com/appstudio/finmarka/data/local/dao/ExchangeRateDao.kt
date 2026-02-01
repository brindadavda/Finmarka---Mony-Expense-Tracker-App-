package com.appstudio.finmarka.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.appstudio.finmarka.data.local.entity.ExchangeRateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExchangeRateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rate: ExchangeRateEntity): Long

    @Update
    suspend fun update(rate: ExchangeRateEntity)

    @Delete
    suspend fun delete(rate: ExchangeRateEntity)

    @Query(
        "SELECT * FROM exchange_rates WHERE baseCurrency = :baseCurrency AND targetCurrency = :targetCurrency " +
            "ORDER BY effectiveDate DESC"
    )
    fun getRatesForPair(baseCurrency: String, targetCurrency: String): Flow<List<ExchangeRateEntity>>

    @Query(
        "SELECT * FROM exchange_rates WHERE baseCurrency = :baseCurrency AND targetCurrency = :targetCurrency " +
            "ORDER BY effectiveDate DESC LIMIT 1"
    )
    suspend fun getLatestRate(baseCurrency: String, targetCurrency: String): ExchangeRateEntity?

    @Query(
        "SELECT * FROM exchange_rates WHERE effectiveDate BETWEEN :startDate AND :endDate " +
            "ORDER BY effectiveDate DESC"
    )
    fun getRatesInRange(startDate: Long, endDate: Long): Flow<List<ExchangeRateEntity>>
}

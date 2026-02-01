package com.appstudio.finmarka.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.appstudio.finmarka.data.local.entity.WarrantyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WarrantyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(warranty: WarrantyEntity): Long

    @Update
    suspend fun update(warranty: WarrantyEntity)

    @Delete
    suspend fun delete(warranty: WarrantyEntity)

    @Query("SELECT * FROM warranties ORDER BY expiryDate ASC")
    fun getAllWarranties(): Flow<List<WarrantyEntity>>

    @Query("SELECT * FROM warranties WHERE expiryDate BETWEEN :startDate AND :endDate ORDER BY expiryDate ASC")
    fun getWarrantiesExpiringBetween(startDate: Long, endDate: Long): Flow<List<WarrantyEntity>>

    @Query("SELECT * FROM warranties WHERE id = :id")
    suspend fun getWarrantyById(id: Int): WarrantyEntity?
}

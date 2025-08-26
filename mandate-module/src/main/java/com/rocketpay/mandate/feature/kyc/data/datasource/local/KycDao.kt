package com.rocketpay.mandate.feature.kyc.data.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rocketpay.mandate.feature.kyc.data.entities.KycEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface KycDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(kycEntities: List<KycEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOne(kycEntity: KycEntity)

    @Query("SELECT * FROM kyc")
    fun getAll(): Flow<List<KycEntity>>

    @Query("SELECT * FROM kyc")
    fun getAllNonLive(): List<KycEntity>
}

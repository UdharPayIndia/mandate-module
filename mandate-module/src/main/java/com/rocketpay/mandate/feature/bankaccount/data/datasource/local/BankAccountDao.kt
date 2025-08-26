package com.rocketpay.mandate.feature.bankaccount.data.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rocketpay.mandate.feature.bankaccount.data.entities.BankAccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface BankAccountDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(bankAccountEntities: List<BankAccountEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOne(bankAccountEntity: BankAccountEntity)

    @Query("SELECT * FROM bank_account WHERE is_deleted = 0")
    fun getAll(): Flow<List<BankAccountEntity>>

    @Query("SELECT * FROM bank_account")
    fun getAllNonLive(): List<BankAccountEntity>
}

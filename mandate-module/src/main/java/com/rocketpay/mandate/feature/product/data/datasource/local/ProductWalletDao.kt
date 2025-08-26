package com.rocketpay.mandate.feature.product.data.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.rocketpay.mandate.feature.product.data.entities.ProductWalletEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface ProductWalletDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOne(entity: ProductWalletEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(entities: List<ProductWalletEntity>)

    @Query("SELECT * FROM productWallet WHERE productType = :productType")
    fun getOne(productType: String): Flow<ProductWalletEntity?>

    @Query("SELECT * FROM productWallet WHERE productType = :productType")
    fun getOneNonLive(productType: String): ProductWalletEntity?

    @Update
    fun updateAll(superKeyPlanHistories: List<ProductWalletEntity>)

    @Transaction
    fun upsert(
        list: List<ProductWalletEntity>,
        onInsert: (ProductWalletEntity) -> Unit,
        onUpdate: (ProductWalletEntity, ProductWalletEntity) -> Unit,
    ) {
        val entities = getUpdatedWallets(list, onInsert, onUpdate)
        insertAll(entities.first)
        updateAll(entities.second)
    }

    private fun getUpdatedWallets(
        list: List<ProductWalletEntity>,
        onInsert: (ProductWalletEntity) -> Unit,
        onUpdate: (ProductWalletEntity, ProductWalletEntity) -> Unit,
    ): Pair<List<ProductWalletEntity>, List<ProductWalletEntity>> {
        val updateList = mutableListOf<ProductWalletEntity>()
        val insertList = mutableListOf<ProductWalletEntity>()

        list.toMutableList().forEach {
            val entity = getOneNonLive(it.id)
            if (entity == null) {
                insertList.add(it)
                onInsert(it)
            } else {
                updateList.add(it)
                onUpdate(entity, it)
            }
        }

        return Pair(insertList, updateList)
    }

    @Query("SELECT COUNT(*) FROM productWallet")
    fun getCount(): Int

}

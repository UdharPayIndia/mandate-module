package com.rocketpay.mandate.feature.product.data.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.rocketpay.mandate.feature.product.data.entities.ProductOrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface ProductOrderDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOne(entity: ProductOrderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(entities: List<ProductOrderEntity>)

    @Query("SELECT * FROM productOrder WHERE id = :id")
    fun getOne(id: String): Flow<ProductOrderEntity?>

    @Query("SELECT * FROM productOrder WHERE id = :id")
    fun getOneNonLive(id: String): ProductOrderEntity?

    @Query("SELECT * FROM productOrder WHERE isDeleted = 0")
    fun getAll(): Flow<List<ProductOrderEntity>>

    @Query("SELECT * FROM productOrder WHERE isDeleted = 0")
    fun getAllNonLive(): List<ProductOrderEntity>

    @Query("SELECT * FROM productOrder WHERE state = :state AND isDeleted = 0")
    fun getAllByStateNonLive(state: String): List<ProductOrderEntity>

    @Query("SELECT * FROM productOrder WHERE productType = :productType AND isDeleted = 0 AND orderType != :orderType")
    fun getAllByProductType(productType: String, orderType: String): Flow<List<ProductOrderEntity>?>

    @Query("SELECT * FROM productOrder WHERE productType = :productType AND isDeleted = 0")
    fun getAllByProductType(productType: String): Flow<List<ProductOrderEntity>?>

    @Update
    fun updateAll(productOrders: List<ProductOrderEntity>)

    @Query("SELECT MAX(serverSequence) FROM productOrder")
    fun lastServerSequence(): Long

    @Transaction
    fun upsert(
        productOrders: List<ProductOrderEntity>,
        onInsert: (ProductOrderEntity) -> Unit,
        onUpdate: (ProductOrderEntity, ProductOrderEntity) -> Unit,
    ) {
        val productOrders = getUpdatedProductOrders(productOrders, onInsert, onUpdate)
        insertAll(productOrders.first)
        updateAll(productOrders.second)
    }

    private fun getUpdatedProductOrders(
        productOrders: List<ProductOrderEntity>,
        onInsert: (ProductOrderEntity) -> Unit,
        onUpdate: (ProductOrderEntity, ProductOrderEntity) -> Unit,
    ): Pair<List<ProductOrderEntity>, List<ProductOrderEntity>> {
        val updateList = mutableListOf<ProductOrderEntity>()
        val insertList = mutableListOf<ProductOrderEntity>()

        productOrders.toMutableList().forEach {
            val productOrder = getOneNonLive(it.id)
            if (productOrder == null) {
                insertList.add(it)
                onInsert(it)
            } else {
                updateList.add(it)
                onUpdate(productOrder, it)
            }
        }

        return Pair(insertList, updateList)
    }

    @Query("SELECT COUNT(*) FROM productOrder")
    fun getCount(): Int

}

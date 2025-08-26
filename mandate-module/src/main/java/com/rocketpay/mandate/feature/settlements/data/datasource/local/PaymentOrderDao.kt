package com.rocketpay.mandate.feature.settlements.data.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.rocketpay.mandate.feature.settlements.data.entities.PaymentOrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface PaymentOrderDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOne(entity: PaymentOrderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(entities: List<PaymentOrderEntity>)

    @Query("SELECT * FROM paymentOrder WHERE id = :id")
    fun getOne(id: String): Flow<PaymentOrderEntity?>

    @Query("SELECT * FROM paymentOrder WHERE id = :id")
    fun getOneNonLive(id: String): PaymentOrderEntity?

    @Query("SELECT * FROM paymentOrder WHERE is_deleted = 0")
    fun getAll(): Flow<List<PaymentOrderEntity>>

    @Query("SELECT * FROM paymentOrder WHERE is_deleted = 0")
    fun getAllNonLive(): List<PaymentOrderEntity>

    @Query("SELECT * FROM paymentOrder WHERE state = :state AND is_deleted = 0")
    fun getAllByStateNonLive(state: String): List<PaymentOrderEntity>

    @Query("UPDATE paymentOrder SET referenceString = :referenceString WHERE id = :paymentOrderId")
    fun updatePaymentOrderReference(paymentOrderId: String, referenceString: String)

    @Query("SELECT reference_id FROM paymentOrder WHERE reference_id in (SELECT reference_id FROM paymentOrder WHERE type = :paymentOrderType AND id in (:paymentOrderIds))")
    fun getPaymentOrdersCorrespondsToRefund(paymentOrderType: String, paymentOrderIds: List<String>): List<String>

    @RawQuery
    fun getPaginatedPaymentOrders(
        query: SupportSQLiteQuery
    ): List<PaymentOrderEntity>

    @Update
    fun updateAll(paymentOrders: List<PaymentOrderEntity>)

    @Query("SELECT MAX(server_sequence) FROM paymentOrder")
    fun lastServerSequence(): Long

    @Transaction
    fun upsert(
        paymentOrders: List<PaymentOrderEntity>,
        onInsert: (PaymentOrderEntity) -> Unit,
        onUpdate: (PaymentOrderEntity, PaymentOrderEntity) -> Unit,
    ) {
        val paymentOrders = getUpdatedPaymentOrders(paymentOrders, onInsert, onUpdate)
        insertAll(paymentOrders.first)
        updateAll(paymentOrders.second)
    }

    private fun getUpdatedPaymentOrders(
        paymentOrders: List<PaymentOrderEntity>,
        onInsert: (PaymentOrderEntity) -> Unit,
        onUpdate: (PaymentOrderEntity, PaymentOrderEntity) -> Unit,
    ): Pair<List<PaymentOrderEntity>, List<PaymentOrderEntity>> {
        val updateList = mutableListOf<PaymentOrderEntity>()
        val insertList = mutableListOf<PaymentOrderEntity>()

        paymentOrders.toMutableList().forEach {
            val paymentOrder = getOneNonLive(it.id)
            if (paymentOrder == null) {
                insertList.add(it)
                onInsert(it)
            } else {
                if(it.referenceString.isNullOrEmpty()){
                    it.referenceString = paymentOrder.referenceString
                }
                updateList.add(it)
                onUpdate(paymentOrder, it)
            }
        }

        return Pair(insertList, updateList)
    }

    @Query("SELECT COUNT(*) FROM paymentOrder")
    fun getCount(): Int

}

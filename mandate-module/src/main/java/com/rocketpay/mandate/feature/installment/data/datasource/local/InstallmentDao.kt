package com.rocketpay.mandate.feature.installment.data.datasource.local

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.main.statemachine.InstallmentAmountSummary
import kotlinx.coroutines.flow.Flow

@Dao
internal interface InstallmentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOne(installmentEntity: InstallmentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(installments: List<InstallmentEntity>)

    @Query("SELECT * FROM installment WHERE id = :installmentId")
    fun getOne(installmentId: String): Flow<InstallmentEntity?>

    @Query("SELECT * FROM installment WHERE id = :installmentId")
    fun getOneNonLive(installmentId: String): InstallmentEntity?

    @Query("SELECT * FROM installment WHERE mandate_id = :mandateId ORDER BY serial_number")
    fun getAllWithId(mandateId: String): Flow<List<InstallmentEntity>>

    @Query("SELECT * FROM installment WHERE mandate_id = :mandateId ORDER BY serial_number")
    fun getAllWithIdNonLive(mandateId: String): List<InstallmentEntity>

    @Query("SELECT * FROM installment")
    fun getAll(): List<InstallmentEntity>

    @Query("SELECT COUNT(*) as count, SUM(amount) as amount, SUM(amount_without_charges) as amountWithoutCharges FROM installment")
    fun getInstallmentSummary(): InstallmentSummary

    @Query("SELECT state, COUNT(*) as count, SUM(amount) as amount, SUM(amount_without_charges) as amountWithoutCharges FROM installment GROUP BY state")
    fun getInstallmentSummaryByState(): List<InstallmentSummaryByState>

    @Query("UPDATE installment SET amountUI = :amountUi WHERE id = :id")
    fun updateInstallmentAmountUI(id: String, amountUi: Double)

    @Update
    fun updateAll(installmentEntities: List<InstallmentEntity>)

    @Update
    fun updateOne(installmentEntity: InstallmentEntity)

    @Query("SELECT * FROM installment ORDER BY updated_at DESC, created_at DESC")
    fun lastTimeStamp(): InstallmentEntity

    @Query("SELECT installment.*, (SELECT name FROM mandate WHERE mandate.id = installment.mandate_id) as customerName  FROM installment WHERE installment.payment_order_id in (:paymentOrderIds)")
    fun getAllWithPaymentOrderIds(paymentOrderIds: List<String>): List<InstallmentWithCustomerEntity>?

    @Query(
        " SELECT " +
                "COUNT(CASE WHEN installment.due_date < :date AND (( installment.status not in ('skipped') AND (installment.state not in ('settlement_initiated', 'settlement_failed', 'settlement_success', 'collection_success') AND (installment.merchant_collected IS NULL OR installment.merchant_collected = 0) ) AND mandate.state not in ('pending', 'expired', 'terminated') AND mandate.method <> 'manual') OR ((installment.merchant_collected IS NULL OR installment.merchant_collected = 0) AND mandate.method = 'manual')) THEN installment.id END) as outstandingCount," +
                "SUM(CASE WHEN installment.due_date < :date AND (( installment.status not in ('skipped') AND (installment.state not in ('settlement_initiated', 'settlement_failed', 'settlement_success', 'collection_success') AND (installment.merchant_collected IS NULL OR installment.merchant_collected = 0) ) AND mandate.state not in ('pending', 'expired', 'terminated') AND mandate.method <> 'manual') OR ((installment.merchant_collected IS NULL OR installment.merchant_collected = 0) AND mandate.method = 'manual')) THEN installment.amountUI END) as outstandingAmount," +
                "COUNT(CASE WHEN installment.due_date >= :date AND (( installment.status not in ('skipped') AND (installment.state not in ('settlement_initiated', 'settlement_failed', 'settlement_success', 'collection_success') AND (installment.merchant_collected IS NULL OR installment.merchant_collected = 0) ) AND mandate.state not in ('pending', 'expired', 'terminated') AND mandate.method <> 'manual') OR ((installment.merchant_collected IS NULL OR installment.merchant_collected = 0) AND mandate.method = 'manual')) THEN installment.id END) as upcomingCount," +
                "SUM(CASE WHEN installment.due_date >= :date AND (( installment.status not in ('skipped') AND (installment.state not in ('settlement_initiated', 'settlement_failed', 'settlement_success', 'collection_success') AND (installment.merchant_collected IS NULL OR installment.merchant_collected = 0) ) AND mandate.state not in ('pending', 'expired', 'terminated') AND mandate.method <> 'manual') OR ((installment.merchant_collected IS NULL OR installment.merchant_collected = 0) AND mandate.method = 'manual')) THEN installment.amountUI END) as upcomingAmount," +
                "COUNT(CASE WHEN (( (installment.state in ('settlement_initiated', 'settlement_failed', 'settlement_success', 'collection_success') OR (installment.merchant_collected IS NOT NULL AND installment.merchant_collected = 1)) AND mandate.method <> 'manual') OR ((installment.merchant_collected IS NOT NULL AND installment.merchant_collected = 1) AND mandate.method = 'manual')) THEN installment.id END) as collectedCount," +
                "SUM(CASE WHEN (( (installment.state in ('settlement_initiated', 'settlement_failed', 'settlement_success', 'collection_success') OR (installment.merchant_collected IS NOT NULL AND installment.merchant_collected = 1)) AND mandate.method <> 'manual') OR ((installment.merchant_collected IS NOT NULL AND installment.merchant_collected = 1) AND mandate.method = 'manual'))  THEN installment.amountUI END) as collectedAmount" +
                " FROM installment LEFT JOIN mandate ON installment.mandate_id = mandate.id WHERE  mandate.is_deleted = 0 "
    )
    fun getTrackerAmountSummaryForAllMandate(date: Long): Flow<InstallmentAmountSummary>

    @Query(
        " SELECT " +
                "COUNT(CASE WHEN installment.due_date < :date AND (( installment.status not in ('skipped') AND (installment.state not in ('settlement_initiated', 'settlement_failed', 'settlement_success', 'collection_success') AND (installment.merchant_collected IS NULL OR installment.merchant_collected = 0) ) AND mandate.state not in ('pending', 'expired', 'terminated') AND mandate.method <> 'manual') OR ((installment.merchant_collected IS NULL OR installment.merchant_collected = 0) AND mandate.method = 'manual')) THEN installment.id END) as outstandingCount," +
                "SUM(CASE WHEN installment.due_date < :date AND (( installment.status not in ('skipped') AND (installment.state not in ('settlement_initiated', 'settlement_failed', 'settlement_success', 'collection_success') AND (installment.merchant_collected IS NULL OR installment.merchant_collected = 0) ) AND mandate.state not in ('pending', 'expired', 'terminated') AND mandate.method <> 'manual') OR ((installment.merchant_collected IS NULL OR installment.merchant_collected = 0) AND mandate.method = 'manual')) THEN installment.amountUI END) as outstandingAmount," +
                "COUNT(CASE WHEN installment.due_date >= :date AND (( installment.status not in ('skipped') AND (installment.state not in ('settlement_initiated', 'settlement_failed', 'settlement_success', 'collection_success') AND (installment.merchant_collected IS NULL OR installment.merchant_collected = 0) ) AND mandate.state not in ('pending', 'expired', 'terminated') AND mandate.method <> 'manual') OR ((installment.merchant_collected IS NULL OR installment.merchant_collected = 0) AND mandate.method = 'manual')) THEN installment.id END) as upcomingCount," +
                "SUM(CASE WHEN installment.due_date >= :date AND (( installment.status not in ('skipped') AND (installment.state not in ('settlement_initiated', 'settlement_failed', 'settlement_success', 'collection_success') AND (installment.merchant_collected IS NULL OR installment.merchant_collected = 0) ) AND mandate.state not in ('pending', 'expired', 'terminated') AND mandate.method <> 'manual') OR ((installment.merchant_collected IS NULL OR installment.merchant_collected = 0) AND mandate.method = 'manual')) THEN installment.amountUI END) as upcomingAmount," +
                "COUNT(CASE WHEN (( (installment.state in ('settlement_initiated', 'settlement_failed', 'settlement_success', 'collection_success') OR (installment.merchant_collected IS NOT NULL AND installment.merchant_collected = 1)) AND mandate.method <> 'manual') OR ((installment.merchant_collected IS NOT NULL AND installment.merchant_collected = 1) AND mandate.method = 'manual')) THEN installment.id END) as collectedCount," +
                "SUM(CASE WHEN (( (installment.state in ('settlement_initiated', 'settlement_failed', 'settlement_success', 'collection_success') OR (installment.merchant_collected IS NOT NULL AND installment.merchant_collected = 1)) AND mandate.method <> 'manual') OR ((installment.merchant_collected IS NOT NULL AND installment.merchant_collected = 1) AND mandate.method = 'manual'))  THEN installment.amountUI END) as collectedAmount" +
                " FROM installment LEFT JOIN mandate ON installment.mandate_id = mandate.id WHERE  mandate.is_deleted = 0 AND mandate.reference_id IS NOT NULL AND mandate.reference_id <> '' "
    )
    fun getTrackerAmountSummaryForSuperKeyMandate(date: Long): Flow<InstallmentAmountSummary>


    @Query(
        " SELECT " +
                "COUNT(CASE WHEN installment.due_date < :date AND installment.status not in ('skipped') AND (installment.state not in ('settlement_initiated', 'settlement_failed', 'settlement_success', 'collection_success') AND (installment.merchant_collected IS NULL OR installment.merchant_collected = 0) ) AND mandate.state not in ('pending', 'expired', 'terminated') THEN installment.id END) as outstandingCount," +
                "SUM(CASE WHEN installment.due_date < :date AND installment.status not in ('skipped') AND (installment.state not in ('settlement_initiated', 'settlement_failed', 'settlement_success', 'collection_success') AND (installment.merchant_collected IS NULL OR installment.merchant_collected = 0) ) AND mandate.state not in ('pending', 'expired', 'terminated') THEN installment.amountUI END) as outstandingAmount," +
                "COUNT(CASE WHEN installment.due_date >= :date AND installment.status not in ('skipped') AND (installment.state not in ('settlement_initiated', 'settlement_failed', 'settlement_success', 'collection_success') AND (installment.merchant_collected IS NULL OR installment.merchant_collected = 0) ) AND mandate.state not in ('pending', 'expired', 'terminated') THEN installment.id END) as upcomingCount," +
                "SUM(CASE WHEN installment.due_date >= :date AND installment.status not in ('skipped') AND (installment.state not in ('settlement_initiated', 'settlement_failed', 'settlement_success', 'collection_success') AND (installment.merchant_collected IS NULL OR installment.merchant_collected = 0) ) AND mandate.state not in ('pending', 'expired', 'terminated') THEN installment.amountUI END) as upcomingAmount," +
                "COUNT(CASE WHEN installment.state in ('settlement_initiated', 'settlement_failed', 'settlement_success', 'collection_success') OR (installment.merchant_collected IS NOT NULL AND installment.merchant_collected = 1) THEN installment.id END) as collectedCount," +
                "SUM(CASE WHEN installment.state in ('settlement_initiated', 'settlement_failed', 'settlement_success', 'collection_success') OR (installment.merchant_collected IS NOT NULL AND installment.merchant_collected = 1)  THEN installment.amountUI END) as collectedAmount" +
                " FROM installment LEFT JOIN mandate ON installment.mandate_id = mandate.id WHERE  mandate.is_deleted = 0 AND mandate.method <> 'manual' AND mandate.reference_id IS NOT NULL AND mandate.reference_id <> '' "
    )
    fun getAutomaticTrackerAmountSummary(date: Long): Flow<InstallmentAmountSummary>

    @Query(
        " SELECT " +
                "COUNT(CASE WHEN installment.due_date < :date AND (installment.merchant_collected IS NULL OR installment.merchant_collected = 0) THEN installment.id END) as outstandingCount,"  +
                "SUM(CASE WHEN installment.due_date < :date AND (installment.merchant_collected IS NULL OR installment.merchant_collected = 0) THEN installment.amountUI END) as outstandingAmount," +
                "COUNT(CASE WHEN installment.due_date >= :date AND (installment.merchant_collected IS NULL OR installment.merchant_collected = 0) THEN installment.id END) as upcomingCount,"  +
                "SUM(CASE WHEN installment.due_date >= :date AND (installment.merchant_collected IS NULL OR installment.merchant_collected = 0) THEN installment.amountUI END) as upcomingAmount," +
                "COUNT(CASE WHEN installment.merchant_collected IS NOT NULL AND installment.merchant_collected = 1 THEN installment.id END) as collectedCount,"  +
                "SUM(CASE WHEN installment.merchant_collected IS NOT NULL AND installment.merchant_collected = 1  THEN installment.amountUI END) as collectedAmount" +
                " FROM installment LEFT JOIN mandate ON installment.mandate_id = mandate.id WHERE  mandate.is_deleted = 0 AND mandate.method = 'manual' AND mandate.reference_id IS NOT NULL AND mandate.reference_id <> '' "
    )
    fun getManualTrackerAmountSummary(date: Long): Flow<InstallmentAmountSummary>

    @RawQuery
    fun getTrackedInstallments(query: SupportSQLiteQuery): List<InstallmentWithMandateEntity>

    @Transaction
    fun upsert(installmentEntities: List<InstallmentEntity>,
               onInsert: (InstallmentEntity) -> Unit,
               onUpdate: (InstallmentEntity, InstallmentEntity) -> Unit) {
        val installments = getUpdatedInstallments(installmentEntities, onInsert, onUpdate)
        insertAll(installments.first)
        updateAll(installments.second)
    }

    private fun getUpdatedInstallments(
        installmentEntities: List<InstallmentEntity>,
        onInsert: (InstallmentEntity) -> Unit,
        onUpdate: (InstallmentEntity, InstallmentEntity) -> Unit
    ): Pair<List<InstallmentEntity>, List<InstallmentEntity>> {
        val updateList = mutableListOf<InstallmentEntity>()
        val insertList = mutableListOf<InstallmentEntity>()

        installmentEntities.toMutableList().forEach {
            val installmentEntity = getOneNonLive(it.id)
            if (installmentEntity == null) {
                insertList.add(it)
                onInsert(it)
            } else {
                if(it.actions.isNullOrEmpty()){
                    it.actions = installmentEntity.actions
                }
                if(it.journey.isNullOrEmpty()){
                    it.journey = installmentEntity.journey
                }
                updateList.add(it)
                onUpdate(installmentEntity, it)
            }
        }

        return Pair(insertList, updateList)
    }
}

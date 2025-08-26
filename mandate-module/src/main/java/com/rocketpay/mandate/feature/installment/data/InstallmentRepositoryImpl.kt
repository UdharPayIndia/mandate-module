package com.rocketpay.mandate.feature.installment.data

import androidx.sqlite.db.SimpleSQLiteQuery
import com.rocketpay.mandate.feature.installment.data.datasource.local.InstallmentDao
import com.rocketpay.mandate.feature.installment.data.datasource.local.InstallmentDataStore
import com.rocketpay.mandate.feature.installment.data.datasource.local.InstallmentEntity
import com.rocketpay.mandate.feature.installment.data.datasource.local.InstallmentSummary
import com.rocketpay.mandate.feature.installment.data.datasource.local.InstallmentSummaryByState
import com.rocketpay.mandate.feature.installment.data.datasource.local.InstallmentWithMandateEntity
import com.rocketpay.mandate.feature.installment.data.datasource.remote.InstallmentService
import com.rocketpay.mandate.feature.installment.data.entities.CreateInstallmentRequest
import com.rocketpay.mandate.feature.installment.data.entities.InstallmentActionResponse
import com.rocketpay.mandate.feature.installment.data.entities.InstallmentDto
import com.rocketpay.mandate.feature.installment.data.entities.InstallmentMarkAsPaidRequest
import com.rocketpay.mandate.feature.installment.data.entities.InstallmentResponse
import com.rocketpay.mandate.feature.installment.data.mapper.InstallmentDtoToEntMapper
import com.rocketpay.mandate.feature.installment.data.mapper.InstallmentEntToDomMapper
import com.rocketpay.mandate.feature.installment.data.mapper.InstallmentPenaltyDtoToEntMapper
import com.rocketpay.mandate.feature.installment.domain.entities.Installment
import com.rocketpay.mandate.feature.installment.domain.entities.InstallmentPenalty
import com.rocketpay.mandate.feature.installment.domain.repositories.InstallmentRepository
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.main.statemachine.InstallmentAmountSummary
import com.rocketpay.mandate.feature.mandate.data.MandateSyncer
import com.udharpay.core.networkmanager.domain.entities.GenericSuccessResponse
import com.udharpay.core.networkmanager.domain.entities.Outcome
import com.rocketpay.mandate.common.syncmanager.client.SyncManager
import com.rocketpay.mandate.common.jsonconverter.JsonConverter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

internal class InstallmentRepositoryImpl(
    private val installmentService: InstallmentService,
    private val installmentDao: InstallmentDao,
    private val installmentDataStore: InstallmentDataStore,
    private val installmentDtoToEntMapper: InstallmentDtoToEntMapper,
    private val installmentEntToDomMapper: InstallmentEntToDomMapper,
    private val installmentPenaltyDtoToEntMapper: InstallmentPenaltyDtoToEntMapper
): InstallmentRepository {

    override fun getInstallments(mandateId: String): Flow<List<Installment>> {
        return installmentDao.getAllWithId(mandateId).transform {
            emit(installmentEntToDomMapper.mapList(it))
        }
    }

    override suspend fun getInstallmentsNonLive(mandateId: String): List<Installment>{
        val installments = installmentDao.getAllWithIdNonLive(mandateId)
        return if(!installments.isNullOrEmpty()){
            installmentEntToDomMapper.mapList(installments)
        }else{
            emptyList()
        }
    }

    override fun getInstallmentSummary(userId: String): InstallmentSummary {
        return installmentDao.getInstallmentSummary()
    }

    override fun getInstallmentSummaryByState(userId: String): List<InstallmentSummaryByState>{
        return installmentDao.getInstallmentSummaryByState()
    }

    override fun getInstallment(installmentId: String): Flow<Installment?> {
        return installmentDao.getOne(installmentId).transform {
            if (it == null) {
                emit(null)
            } else {
                emit(installmentEntToDomMapper.map(it))
            }
        }
    }

    override suspend fun fetchInstallments(createdAt: Long, maxUpdatedAt: Long): Outcome<InstallmentResponse> {
        return installmentService.getInstallments(createdAt, maxUpdatedAt)
    }

    override suspend fun refreshInstallment(installmentId: String): Outcome<Installment> {
        return when(val outcome = installmentService.refreshInstallment(installmentId)) {
            is Outcome.Error -> outcome
            is Outcome.Success -> {
                val installmentEntity = installmentDtoToEntMapper.map(outcome.data)
                installmentDao.upsert(listOf(installmentEntity), {} , {_,_ ->})
                installmentEntity.journey = JsonConverter.getInstance().toJson(outcome.data.journey)
                installmentDao.updateOne(installmentEntity)
                Outcome.Success(installmentEntToDomMapper.map(installmentEntity))
            }
        }
    }

    override suspend fun fetchInstallment(installmentId: String): Outcome<InstallmentDto> {
        return when(val outcome = installmentService.getInstallment(installmentId)){
            is Outcome.Error ->{
                outcome
            }
            is Outcome.Success -> {
                val installment = installmentDao.getOneNonLive(installmentId)
                installment?.let {
                    installment?.journey = JsonConverter.getInstance().toJson(outcome.data.journey)
                    installmentDao.updateOne(installment)
                }
                outcome
            }
        }
    }

    override suspend fun fetchInstallmentActions(installmentId: String): Outcome<InstallmentActionResponse>{
        return when(val outcome = installmentService.getInstallmentActions(installmentId)){
            is Outcome.Error ->{
                outcome
            }
            is Outcome.Success -> {
                val installment = installmentDao.getOneNonLive(installmentId)
                installment?.let {
                    installment.actions = outcome.data.actions.takeIf { !it.isNullOrEmpty() }?.let { JsonConverter.getInstance().toJson(outcome.data.actions) }
                    installmentDao.updateOne(installment)
                }
                outcome
            }
        }
    }

    override fun getInstallmentNonLive(installmentId: String): Installment? {
        val installmentEntity = installmentDao.getOneNonLive(installmentId)
        return if (installmentEntity == null) {
            null
        } else {
            installmentEntToDomMapper.map(installmentEntity)
        }
    }

    override fun getInstallmentsByPaymentOrderIds(paymentOrderIds: List<String>): List<Installment> {
        val installmentEntity = installmentDao.getAllWithPaymentOrderIds(paymentOrderIds)
        return if (installmentEntity == null) {
            emptyList()
        } else {
            installmentEntToDomMapper.mapInstallmentWithCustomerList(installmentEntity)
        }
    }

    override fun saveInstallments(data: List<InstallmentDto>,
                                  onInsert: (InstallmentEntity) -> Unit,
                                  onUpdate: (InstallmentEntity, InstallmentEntity) -> Unit) {
        val installmentEntities = installmentDtoToEntMapper.mapList(data)
        installmentDao.upsert(installmentEntities, onInsert, onUpdate)
    }


    override suspend fun retryInstallment(mandateId: String, installmentId: String, retryDate: String): Outcome<InstallmentDto> {
        return when(val outcome = installmentService.retryInstallment(mandateId, installmentId, retryDate)) {
            is Outcome.Error -> outcome
            is Outcome.Success -> {
                SyncManager.getInstance().enqueue(MandateSyncer.TYPE)
                fetchInstallment(installmentId)
                outcome
            }
        }
    }

    override suspend fun skipInstallment(mandateId: String, installmentId: String): Outcome<InstallmentDto> {
        return when(val outcome = installmentService.skipInstallment(mandateId, installmentId)) {
            is Outcome.Error -> outcome
            is Outcome.Success -> {
                SyncManager.getInstance().enqueue(MandateSyncer.TYPE)
                fetchInstallment(installmentId)
                outcome
            }
        }
    }

    override suspend fun markAsPaidInstallment(installmentId: String , request: InstallmentMarkAsPaidRequest): Outcome<InstallmentDto>{
        return when(val outcome = installmentService.markAsPaidInstallment(installmentId, request)) {
            is Outcome.Error -> outcome
            is Outcome.Success -> {
                SyncManager.getInstance().enqueue(MandateSyncer.TYPE)
                fetchInstallment(installmentId)
                outcome
            }
        }
    }

    override suspend fun requestOtp(
        amount: Double,
        dueDate: Long,
        mandateId: String
    ): Outcome<GenericSuccessResponse> {
        return installmentService.requestOtp(createInstallmentRequest(amount, dueDate, "", mandateId))
    }

    override suspend fun createInstallment(
        amount: Double,
        dueDate: Long,
        otp: String,
        mandateId: String
    ): Outcome<InstallmentDto> {
        return when(val outcome = installmentService.createInstallment(createInstallmentRequest(amount, dueDate, otp, mandateId))) {
            is Outcome.Error -> outcome
            is Outcome.Success -> {
                SyncManager.getInstance().enqueue(MandateSyncer.TYPE)
                outcome
            }
        }
    }

    private fun createInstallmentRequest(
        amount: Double,
        dueDate: Long,
        otp: String,
        mandateId: String
    ): CreateInstallmentRequest {
        return CreateInstallmentRequest(
            mandate_id = mandateId,
            amount = amount,
            due_date = dueDate,
            otp = otp
        )
    }

    override suspend fun fetchInstallmentPenalty(installmentId: String): Outcome<InstallmentPenalty>{
        return when(val outcome = installmentService.fetchInstallmentPenalty(installmentId)) {
            is Outcome.Error -> outcome
            is Outcome.Success -> {
                Outcome.Success(installmentPenaltyDtoToEntMapper.map(outcome.data))
            }
        }
    }

    override suspend fun chargePenalty(installmentId: String, installmentAmount: Double): Outcome<InstallmentPenalty>{
        return when(val outcome = installmentService.chargePenalty(installmentId, installmentAmount)) {
            is Outcome.Error -> outcome
            is Outcome.Success -> {
                SyncManager.getInstance().enqueue(MandateSyncer.TYPE)
                fetchInstallment(installmentId)
                Outcome.Success(installmentPenaltyDtoToEntMapper.map(outcome.data))
            }
        }
    }

    override fun getMaxTimeStamp(): InstallmentEntity{
        return installmentDao.lastTimeStamp()
    }

    override fun getTrackerAmountSummary(date: Long, isSuperKeyFlow: Boolean): Flow<InstallmentAmountSummary>{
        return if(isSuperKeyFlow){
            installmentDao.getTrackerAmountSummaryForSuperKeyMandate(date)
        }else{
            installmentDao.getTrackerAmountSummaryForAllMandate(date)
        }
    }

    override fun getAutomaticTrackerAmountSummary(date: Long): Flow<InstallmentAmountSummary>{
        return installmentDao.getAutomaticTrackerAmountSummary(date)
    }

    override fun getManualTrackerAmountSummary(date: Long): Flow<InstallmentAmountSummary>{
        return installmentDao.getManualTrackerAmountSummary(date)
    }

    override suspend fun updateInstallment(installmentId: String, installmentAmountUI: Double){
        return installmentDao.updateInstallmentAmountUI(installmentId, installmentAmountUI)
    }

    override suspend fun getTrackedInstallments(
        type: Int, lastFetched: Long,
        orderByDesc: Boolean,  limit: Int,
        isSuperKeyFlow: Boolean,
        skipManualMandate: Boolean): List<InstallmentWithMandateEntity> {
        return installmentDao.getTrackedInstallments(if(isSuperKeyFlow){
            getRawQueryForSuperKeyMandate(type, lastFetched, orderByDesc, limit)
        }else if(skipManualMandate) {
            getRawQueryForAllMandateWithoutManual(type, lastFetched, orderByDesc, limit)
        }else{
            getRawQueryForAllMandate(type, lastFetched, orderByDesc, limit)
        })

    }

    private fun getRawQueryForAllMandate(
        type: Int,
        lastFetched: Long,
        orderByDesc: Boolean,
        limit: Int,
    ): SimpleSQLiteQuery {
        val select = " SELECT installment.*,  mandate.name as customerName, mandate.installments as noOfInstallment, mandate.installments_paid as paidInstallment, mandate.state as mandateState, mandate.method as paymentMethod, mandate.reference_id as referenceId FROM installment LEFT JOIN mandate ON installment.mandate_id = mandate.id WHERE mandate.is_deleted = 0 "
        val typeBaseCondition = when (type) {
            0 -> " AND installment.due_date < $lastFetched AND ((mandate.method = 'manual' AND (installment.merchant_collected IS NULL OR installment.merchant_collected = 0)) OR (mandate.method <> 'manual' AND mandate.state not in ('pending', 'expired', 'terminated') AND installment.status not in ('skipped') AND (installment.state not in ('settlement_initiated', 'settlement_failed', 'settlement_success', 'collection_success') AND (installment.merchant_collected IS NULL OR installment.merchant_collected = 0) ))) "
            1 -> " AND installment.due_date > $lastFetched AND ((mandate.method = 'manual' AND (installment.merchant_collected IS NULL OR installment.merchant_collected = 0)) OR (mandate.method <> 'manual' AND mandate.state not in ('pending', 'expired', 'terminated') AND installment.status not in ('skipped') AND (installment.state not in ('settlement_initiated', 'settlement_failed', 'settlement_success', 'collection_success') AND (installment.merchant_collected IS NULL OR installment.merchant_collected = 0) ))) "
            2 ->
                if(orderByDesc){
                    " AND installment.due_date < $lastFetched AND ((mandate.method = 'manual' AND (installment.merchant_collected IS NOT NULL AND installment.merchant_collected = 1)) OR (mandate.method <> 'manual' AND (installment.state in ('settlement_initiated', 'settlement_failed', 'settlement_success', 'collection_success') OR (installment.merchant_collected IS NOT NULL AND installment.merchant_collected = 1)) )) "
                }else{
                    " AND installment.due_date > $lastFetched AND ((mandate.method = 'manual' AND (installment.merchant_collected IS NOT NULL AND installment.merchant_collected = 1)) OR (mandate.method <> 'manual' AND (installment.state in ('settlement_initiated', 'settlement_failed', 'settlement_success', 'collection_success') OR (installment.merchant_collected IS NOT NULL AND installment.merchant_collected = 1)) )) "
                }
            else -> " "
        }
        val orderByString = if(orderByDesc){
            " ORDER by due_date DESC LIMIT $limit "
        }else{
            " ORDER by due_date LIMIT $limit "
        }
        return SimpleSQLiteQuery(select + typeBaseCondition + orderByString)
    }

    private fun getRawQueryForAllMandateWithoutManual(
        type: Int,
        lastFetched: Long,
        orderByDesc: Boolean,
        limit: Int,
    ): SimpleSQLiteQuery {
        val select = " SELECT installment.*,  mandate.name as customerName, mandate.installments as noOfInstallment, mandate.installments_paid as paidInstallment, mandate.state as mandateState, mandate.method as paymentMethod, mandate.reference_id as referenceId FROM installment LEFT JOIN mandate ON installment.mandate_id = mandate.id LEFT JOIN paymentOrder ON installment.payment_order_id = paymentOrder.id " +
                " WHERE mandate.is_deleted = 0 AND paymentOrder.type in ('COLLECT','AUTH_MANDATE') AND paymentOrder.state = 'SUCCESS' "
        val typeBaseCondition = when (type) {
            0 -> " AND installment.due_date < $lastFetched AND ((mandate.method <> 'manual' AND mandate.state not in ('pending', 'expired', 'terminated') AND installment.status not in ('skipped') AND (installment.state not in ('settlement_initiated', 'settlement_failed', 'settlement_success', 'collection_success') ))) "
            1 -> " AND installment.due_date > $lastFetched AND ((mandate.method <> 'manual' AND mandate.state not in ('pending', 'expired', 'terminated') AND installment.status not in ('skipped') AND (installment.state not in ('settlement_initiated', 'settlement_failed', 'settlement_success', 'collection_success') ))) "
            2 ->
                if(orderByDesc){
                    " AND installment.due_date < $lastFetched AND ((mandate.method <> 'manual' AND (installment.state in ('settlement_initiated', 'settlement_failed', 'settlement_success', 'collection_success')) )) "
                }else{
                    " AND installment.due_date > $lastFetched AND ((mandate.method <> 'manual' AND (installment.state in ('settlement_initiated', 'settlement_failed', 'settlement_success', 'collection_success')) )) "
                }
            else -> " "
        }
        val orderByString = if(orderByDesc){
            " ORDER by due_date DESC LIMIT $limit "
        }else{
            " ORDER by due_date LIMIT $limit "
        }
        return SimpleSQLiteQuery(select + typeBaseCondition + orderByString)
    }

    private fun getRawQueryForSuperKeyMandate(
        type: Int,
        lastFetched: Long,
        orderByDesc: Boolean,
        limit: Int,
    ): SimpleSQLiteQuery {
        val select = " SELECT installment.*,  (SELECT superKey.customer_name FROM superKey WHERE superKey.superkey_id = mandate.reference_id ) as customerName, mandate.installments as noOfInstallment, mandate.installments_paid as paidInstallment, mandate.state as mandateState, mandate.method as paymentMethod, mandate.reference_id as referenceId FROM installment LEFT JOIN mandate ON installment.mandate_id = mandate.id WHERE mandate.is_deleted = 0 AND mandate.reference_id IS NOT NULL AND mandate.reference_id <> '' "
        val typeBaseCondition = when (type) {
            0 -> " AND installment.due_date < $lastFetched AND ((mandate.method = 'manual' AND (installment.merchant_collected IS NULL OR installment.merchant_collected = 0)) OR (mandate.method <> 'manual' AND mandate.state not in ('pending', 'expired', 'terminated') AND installment.status not in ('skipped') AND (installment.state not in ('settlement_initiated', 'settlement_failed', 'settlement_success', 'collection_success') AND (installment.merchant_collected IS NULL OR installment.merchant_collected = 0) ))) "
            1 -> " AND installment.due_date > $lastFetched AND ((mandate.method = 'manual' AND (installment.merchant_collected IS NULL OR installment.merchant_collected = 0)) OR (mandate.method <> 'manual' AND mandate.state not in ('pending', 'expired', 'terminated') AND installment.status not in ('skipped') AND (installment.state not in ('settlement_initiated', 'settlement_failed', 'settlement_success', 'collection_success') AND (installment.merchant_collected IS NULL OR installment.merchant_collected = 0) ))) "
            2 ->
                if(orderByDesc){
                    " AND installment.due_date < $lastFetched AND ((mandate.method = 'manual' AND (installment.merchant_collected IS NOT NULL AND installment.merchant_collected = 1)) OR (mandate.method <> 'manual' AND (installment.state in ('settlement_initiated', 'settlement_failed', 'settlement_success', 'collection_success') OR (installment.merchant_collected IS NOT NULL AND installment.merchant_collected = 1)) )) "
                }else{
                    " AND installment.due_date > $lastFetched AND ((mandate.method = 'manual' AND (installment.merchant_collected IS NOT NULL AND installment.merchant_collected = 1)) OR (mandate.method <> 'manual' AND (installment.state in ('settlement_initiated', 'settlement_failed', 'settlement_success', 'collection_success') OR (installment.merchant_collected IS NOT NULL AND installment.merchant_collected = 1)) )) "
                }
            else -> " "
        }
        val orderByString = if(orderByDesc){
            " ORDER by due_date DESC LIMIT $limit "
        }else{
            " ORDER by due_date LIMIT $limit "
        }
        return SimpleSQLiteQuery(select + typeBaseCondition + orderByString)
    }

}

package com.rocketpay.mandate.feature.installment.domain.repositories

import com.rocketpay.mandate.feature.installment.data.datasource.local.InstallmentEntity
import com.rocketpay.mandate.feature.installment.data.datasource.local.InstallmentSummary
import com.rocketpay.mandate.feature.installment.data.datasource.local.InstallmentSummaryByState
import com.rocketpay.mandate.feature.installment.data.datasource.local.InstallmentWithMandateEntity
import com.rocketpay.mandate.feature.installment.data.entities.InstallmentActionResponse
import com.rocketpay.mandate.feature.installment.data.entities.InstallmentDto
import com.rocketpay.mandate.feature.installment.data.entities.InstallmentMarkAsPaidRequest
import com.rocketpay.mandate.feature.installment.data.entities.InstallmentResponse
import com.rocketpay.mandate.feature.installment.domain.entities.Installment
import com.rocketpay.mandate.feature.installment.domain.entities.InstallmentPenalty
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.main.statemachine.InstallmentAmountSummary
import com.udharpay.core.networkmanager.domain.entities.GenericSuccessResponse
import com.udharpay.core.networkmanager.domain.entities.Outcome
import kotlinx.coroutines.flow.Flow

internal interface InstallmentRepository {
    fun getInstallments(mandateId: String): Flow<List<Installment>>
    suspend fun getInstallmentsNonLive(mandateId: String): List<Installment>

    fun getInstallment(installmentId: String): Flow<Installment?>
    suspend fun fetchInstallments(createdAt: Long, maxUpdatedAt: Long): Outcome<InstallmentResponse>
    suspend fun refreshInstallment(installmentId: String): Outcome<Installment>
    suspend fun fetchInstallment(installmentId: String): Outcome<InstallmentDto>
    suspend fun fetchInstallmentActions(installmentId: String): Outcome<InstallmentActionResponse>
    fun saveInstallments(
        data: List<InstallmentDto>,
        onInsert: (InstallmentEntity) -> Unit,
        onUpdate: (InstallmentEntity, InstallmentEntity) -> Unit
    )

    suspend fun retryInstallment(mandateId: String, installmentId: String, retryDate: String): Outcome<InstallmentDto>
    suspend fun skipInstallment(mandateId: String, installmentId: String): Outcome<InstallmentDto>
    suspend fun markAsPaidInstallment(installmentId: String , request: InstallmentMarkAsPaidRequest): Outcome<InstallmentDto>
    suspend fun requestOtp(
        amount: Double,
        dueDate: Long,
        mandateId: String
    ): Outcome<GenericSuccessResponse>

    suspend fun createInstallment(
        amount: Double,
        dueDate: Long,
        otp: String,
        mandateId: String
    ): Outcome<InstallmentDto>

    fun getInstallmentSummary(userId: String): InstallmentSummary
    fun getInstallmentSummaryByState(userId: String): List<InstallmentSummaryByState>
    fun getInstallmentNonLive(installmentId: String): Installment?
    fun getInstallmentsByPaymentOrderIds(paymentOrderIds: List<String>): List<Installment>
    suspend fun fetchInstallmentPenalty(installmentId: String): Outcome<InstallmentPenalty>
    suspend fun chargePenalty(installmentId: String, installmentAmount: Double): Outcome<InstallmentPenalty>
    fun getMaxTimeStamp(): InstallmentEntity?
    fun getTrackerAmountSummary(date: Long, isSuperKeyFlow: Boolean): Flow<InstallmentAmountSummary>
    fun getAutomaticTrackerAmountSummary(date: Long): Flow<InstallmentAmountSummary>
    fun getManualTrackerAmountSummary(date: Long): Flow<InstallmentAmountSummary>
    suspend fun getTrackedInstallments(
        type: Int, lastFetched: Long,
        orderByDesc: Boolean, limit: Int, isSuperKeyFlow: Boolean,
        skipManualMandate: Boolean
    ): List<InstallmentWithMandateEntity>
    suspend fun updateInstallment(installmentId: String, installmentAmountUI: Double)
}

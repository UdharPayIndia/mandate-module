package com.rocketpay.mandate.feature.installment.domain.usecase

import com.rocketpay.mandate.feature.installment.data.datasource.local.InstallmentSummary
import com.rocketpay.mandate.feature.installment.data.datasource.local.InstallmentSummaryByState
import com.rocketpay.mandate.feature.installment.data.datasource.local.InstallmentWithMandateEntity
import com.rocketpay.mandate.feature.installment.data.entities.InstallmentActionResponse
import com.rocketpay.mandate.feature.installment.data.entities.InstallmentDto
import com.rocketpay.mandate.feature.installment.data.entities.InstallmentMarkAsPaidRequest
import com.rocketpay.mandate.feature.installment.domain.entities.Installment
import com.rocketpay.mandate.feature.installment.domain.entities.InstallmentPenalty
import com.rocketpay.mandate.feature.installment.domain.repositories.InstallmentRepository
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.main.statemachine.InstallmentAmountSummary
import com.udharpay.core.networkmanager.domain.entities.GenericSuccessResponse
import com.udharpay.core.networkmanager.domain.entities.Outcome
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

internal class InstallmentUseCase internal constructor(
    private val installmentRepository: InstallmentRepository
) {

    suspend fun fetchInstallment(installmentId: String): Outcome<InstallmentDto>{
        return installmentRepository.fetchInstallment(installmentId)
    }

    internal suspend fun refreshInstallment(installmentId: String): Outcome<Installment> {
        return installmentRepository.refreshInstallment(installmentId)
    }

    internal fun getInstallments(mandateId: String): Flow<List<Installment>> {
        return installmentRepository.getInstallments(mandateId)
    }

    internal suspend fun getInstallmentsNonLive(mandateId: String): List<Installment> {
        return withContext(Dispatchers.IO){ installmentRepository.getInstallmentsNonLive(mandateId) }
    }

    internal fun getInstallment(installmentId: String): Flow<Installment?> {
        return installmentRepository.getInstallment(installmentId)
    }

    suspend fun fetchInstallmentActions(installmentId: String): Outcome<InstallmentActionResponse>{
        return installmentRepository.fetchInstallmentActions(installmentId)
    }

    internal fun getInstallmentNonLive(installmentId: String): Installment? {
        return installmentRepository.getInstallmentNonLive(installmentId)
    }

    internal fun getInstallmentsByPaymentOrderIds(paymentOrderIds: List<String>): List<Installment> {
        return installmentRepository.getInstallmentsByPaymentOrderIds(paymentOrderIds)
    }

    suspend fun retryInstallment(mandateId: String, installmentId: String, retryDate: String): Outcome<InstallmentDto> {
        return installmentRepository.retryInstallment(mandateId, installmentId, retryDate)
    }

    suspend fun skipInstallment(mandateId: String, installmentId: String): Outcome<InstallmentDto> {
        return installmentRepository.skipInstallment(mandateId, installmentId)
    }

    suspend fun markAsPaidInstallment(installmentId: String , request: InstallmentMarkAsPaidRequest): Outcome<InstallmentDto>{
        return installmentRepository.markAsPaidInstallment(installmentId, request)
    }

    suspend fun requestOtp(
        amount: Double,
        dueDate: Long,
        mandateId: String
    ): Outcome<GenericSuccessResponse> {
        return installmentRepository.requestOtp(amount, dueDate, mandateId)
    }

    suspend fun createInstallment(
        amount: Double,
        dueDate: Long,
        otp: String,
        mandateId: String
    ): Outcome<InstallmentDto> {
        return installmentRepository.createInstallment(amount, dueDate, otp, mandateId)
    }

    fun getInstallmentSummary(userId: String): InstallmentSummary {
        return installmentRepository.getInstallmentSummary(userId)
    }

    fun getInstallmentSummaryByState(userId: String): List<InstallmentSummaryByState>{
        return installmentRepository.getInstallmentSummaryByState(userId)
    }

    suspend fun fetchInstallmentPenalty(installmentId: String): Outcome<InstallmentPenalty>{
        return installmentRepository.fetchInstallmentPenalty(installmentId)
    }

    suspend fun chargePenalty(installmentId: String, installmentAmount: Double): Outcome<InstallmentPenalty>{
        return installmentRepository.chargePenalty(installmentId, installmentAmount)
    }

    fun getTrackerAmountSummary(date: Long, isSuperKeyFlow: Boolean): Flow<InstallmentAmountSummary>{
        return installmentRepository.getTrackerAmountSummary(date, isSuperKeyFlow)
    }

    internal suspend fun getTrackedInstallments(
        type: Int,
        lastFetched: Long,
        orderByDesc: Boolean,
        limit: Int,
        isSuperKeyFlow: Boolean,
        skipManualMandate: Boolean
    ): List<InstallmentWithMandateEntity>{
        return withContext(Dispatchers.IO){
            installmentRepository.getTrackedInstallments(
                type, lastFetched, orderByDesc, limit, isSuperKeyFlow, skipManualMandate)
        }
    }
}

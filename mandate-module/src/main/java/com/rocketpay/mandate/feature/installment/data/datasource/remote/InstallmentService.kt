package com.rocketpay.mandate.feature.installment.data.datasource.remote

import com.rocketpay.mandate.feature.installment.data.entities.CreateInstallmentRequest
import com.rocketpay.mandate.feature.installment.data.entities.InstallmentActionResponse
import com.rocketpay.mandate.feature.installment.data.entities.InstallmentDto
import com.rocketpay.mandate.feature.installment.data.entities.InstallmentMarkAsPaidRequest
import com.rocketpay.mandate.feature.installment.data.entities.InstallmentPenaltyDto
import com.rocketpay.mandate.feature.installment.data.entities.InstallmentPenaltyRequest
import com.rocketpay.mandate.feature.installment.data.entities.InstallmentResponse
import com.rocketpay.mandate.feature.installment.data.entities.InstallmentRetryRequest
import com.udharpay.core.networkmanager.data.NetworkRequestBuilder
import com.udharpay.core.networkmanager.data.get
import com.udharpay.core.networkmanager.data.post
import com.udharpay.core.networkmanager.data.put
import com.udharpay.core.networkmanager.domain.entities.GenericSuccessResponse
import com.udharpay.core.networkmanager.domain.entities.Outcome

internal class InstallmentService {

    suspend fun getInstallments(createdAt: Long, maxUpdatedAt: Long): Outcome<InstallmentResponse> {
        return NetworkRequestBuilder()
            .subUrl("/v2/merchant/installments/sync")
            .queryParameter("updated_at", maxUpdatedAt.toString())
            .queryParameter("created_at", createdAt.toString())
            .queryParameter("limit", "100")
            .build()
            .get()
    }

    suspend fun refreshInstallment(installmentId: String): Outcome<InstallmentDto> {
        return NetworkRequestBuilder()
            .subUrl("/v2/merchant/installments/{installment_id}/refresh")
            .pathParameter("installment_id", installmentId)
            .body(Any())
            .build()
            .post()
    }


    suspend fun getInstallment(installmentId: String): Outcome<InstallmentDto> {
        return NetworkRequestBuilder()
            .subUrl("/v2/merchant/installments/{installment_id}")
            .pathParameter("installment_id", installmentId)
            .build()
            .get()
    }

    suspend fun getInstallmentActions(installmentId: String): Outcome<InstallmentActionResponse> {
        return NetworkRequestBuilder()
            .subUrl("/v2/merchant/installments/{installment_id}/actions")
            .pathParameter("installment_id", installmentId)
            .build()
            .get()
    }

    suspend fun retryInstallment(mandateId: String, installmentId: String, retryDate: String): Outcome<InstallmentDto> {
        return NetworkRequestBuilder()
            .subUrl("/v2/merchant/installments/{installment_id}/retry")
            .pathParameter("installment_id", installmentId)
            .body(InstallmentRetryRequest(retryDate))
            .build()
            .post()
    }

    suspend fun skipInstallment(mandateId: String, installmentId: String): Outcome<InstallmentDto> {
        return NetworkRequestBuilder()
            .subUrl("/v1/merchant/installments/{installment_id}/skip")
            .pathParameter("installment_id", installmentId)
            .body(Any())
            .build()
            .post()
    }

    suspend fun markAsPaidInstallment(installmentId: String , request: InstallmentMarkAsPaidRequest): Outcome<InstallmentDto> {
        return NetworkRequestBuilder()
            .subUrl("/v1/merchant/installments/{installment_id}/medium")
            .pathParameter("installment_id", installmentId)
            .body(request)
            .build()
            .put()
    }

    suspend fun requestOtp(
        createInstallmentRequest: CreateInstallmentRequest
    ): Outcome<GenericSuccessResponse> {
        return NetworkRequestBuilder()
            .subUrl("/v1/merchant/installments/auth")
            .body(createInstallmentRequest)
            .build()
            .post()
    }

    suspend fun createInstallment(
        createInstallmentRequest: CreateInstallmentRequest
    ): Outcome<InstallmentDto> {
        return NetworkRequestBuilder()
            .subUrl("/v1/merchant/installments")
            .body(createInstallmentRequest)
            .build()
            .post()
    }

    suspend fun fetchInstallmentPenalty(installmentId: String): Outcome<InstallmentPenaltyDto> {
        return NetworkRequestBuilder()
            .subUrl("/v1/merchant/installments/{id}/charge-penalty")
            .pathParameter("id", installmentId)
            .build()
            .get()
    }

    suspend fun chargePenalty(installmentId: String, installmentAmount: Double): Outcome<InstallmentPenaltyDto> {
        return NetworkRequestBuilder()
            .subUrl("/v2/merchant/installments/{id}/charge-penalty")
            .pathParameter("id", installmentId)
            .body(InstallmentPenaltyRequest(installmentAmount))
            .build()
            .post()
    }
}

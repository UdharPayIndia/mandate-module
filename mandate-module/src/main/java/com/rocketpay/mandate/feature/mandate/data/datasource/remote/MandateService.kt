package com.rocketpay.mandate.feature.mandate.data.datasource.remote

import com.rocketpay.mandate.feature.mandate.data.entities.ChargeRequestDto
import com.rocketpay.mandate.feature.mandate.data.entities.ChargeResponseDto
import com.rocketpay.mandate.feature.mandate.data.entities.ConvertMandateDto
import com.rocketpay.mandate.feature.mandate.data.entities.CouponResponseDto
import com.rocketpay.mandate.feature.mandate.data.entities.CreateMandateDto
import com.rocketpay.mandate.feature.mandate.data.entities.MandateDto
import com.rocketpay.mandate.feature.mandate.data.entities.MandateListResponse
import com.rocketpay.mandate.feature.mandate.domain.entities.MandateStateDto
import com.udharpay.core.networkmanager.data.NetworkRequestBuilder
import com.udharpay.core.networkmanager.data.delete
import com.udharpay.core.networkmanager.data.get
import com.udharpay.core.networkmanager.data.post
import com.udharpay.core.networkmanager.data.put
import com.udharpay.core.networkmanager.domain.entities.GenericSuccessResponse
import com.udharpay.core.networkmanager.domain.entities.Outcome

internal class MandateService {

    suspend fun syncMandates(createdAt: Long, updatedAt: Long): Outcome<MandateListResponse> {
        return NetworkRequestBuilder()
            .subUrl("/v1/merchant/mandates/sync")
            .queryParameter("limit", "100")
            .queryParameter("updated_at", updatedAt.toString())
            .queryParameter("created_at", createdAt.toString())
            .build()
            .get()
    }

    suspend fun refreshMandate(mandateId: String): Outcome<MandateDto> {
        return NetworkRequestBuilder()
            .subUrl("/v2/merchant/mandates/{mandateId}/refresh")
            .pathParameter("mandateId", mandateId)
            .body(Any())
            .build()
            .post()
    }


    suspend fun computeCharges(chargeRequestDto: ChargeRequestDto): Outcome<ChargeResponseDto> {
        return NetworkRequestBuilder()
            .subUrl("/v2/merchant/mandates/compute-charges")
            .body(chargeRequestDto)
            .build()
            .post()
    }

    suspend fun getCouponList(chargeRequestDto: ChargeRequestDto): Outcome<List<CouponResponseDto>> {
        return NetworkRequestBuilder()
            .subUrl("/v2/merchant/mandate-coupons/list")
            .body(chargeRequestDto)
            .build()
            .post()
    }

    suspend fun createMandate(createMandateDto: CreateMandateDto): Outcome<MandateDto> {
        return NetworkRequestBuilder()
            .subUrl("/v2/merchant/mandates")
            .body(createMandateDto)
            .build()
            .post()
    }

    suspend fun deleteMandate(mandateId: String): Outcome<MandateDto> {
        return NetworkRequestBuilder()
            .subUrl("/v2/merchant/mandates/{id}")
            .pathParameter("id", mandateId)
            .build()
            .delete()
    }

    suspend fun cancelMandate(mandateId: String): Outcome<MandateDto> {
        return NetworkRequestBuilder()
            .subUrl("/v2/merchant/mandates/{id}/cancel")
            .pathParameter("id", mandateId)
            .body(Any())
            .build()
            .post()
    }

    suspend fun convertToManual(mandateId: String): Outcome<MandateDto> {
        return NetworkRequestBuilder()
            .subUrl("/v1/merchant/mandates/{mandateId}/payment-method")
            .pathParameter("mandateId", mandateId)
            .body(ConvertMandateDto())
            .build()
            .put()
    }

    suspend fun sendPaymentRequest(mandateId: String): Outcome<GenericSuccessResponse> {
        return NetworkRequestBuilder()
            .subUrl("/api/mandates/{id}/retry_auth")
            .pathParameter("id", mandateId)
            .build()
            .get()
    }

    suspend fun fetchMandateState(mandateId: String): Outcome<MandateStateDto> {
        return NetworkRequestBuilder()
            .subUrl("/api/mandates/{id}/status")
            .pathParameter("id", mandateId)
            .build()
            .get()
    }
}

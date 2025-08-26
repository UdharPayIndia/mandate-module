package com.rocketpay.mandate.feature.kyc.data.datasource.remote

import androidx.annotation.Keep
import com.google.gson.JsonObject
import com.rocketpay.mandate.feature.kyc.data.entities.KycDto
import com.rocketpay.mandate.feature.kyc.data.entities.KycTypeDto
import com.rocketpay.mandate.feature.kyc.domain.entities.KycWorkFlow
import com.udharpay.core.networkmanager.data.NetworkRequestBuilder
import com.udharpay.core.networkmanager.data.get
import com.udharpay.core.networkmanager.data.post
import com.udharpay.core.networkmanager.domain.entities.Outcome

internal open class KycService {

    open suspend fun fetchKycType(): Outcome<KycTypeDto> {
        return NetworkRequestBuilder()
            .subUrl("/v1/merchant/workflow/kyc-type")
            .build()
            .get()
    }

    open suspend fun fetchKyc(type: String): Outcome<KycDto> {
        return NetworkRequestBuilder()
            .subUrl("/v1/merchant/workflow")
            .queryParameter("kyc_type", type)
            .build()
            .get()
    }

    open suspend fun submitKyc(kycItem: KycWorkFlow, jsonObject: JsonObject): Outcome<KycDto> {
        jsonObject.addProperty("id", kycItem.id)
        return NetworkRequestBuilder()
            .subUrl("/v1/merchant/workflow/submit")
            .body(jsonObject)
            .build()
            .post()
    }

    open suspend fun initKyc(kycItem: KycWorkFlow): Outcome<KycDto> {
        val kycItemRequest = KycItemRequest(
            kycItem.id,
            kycItem.priority,
            kycItem.type,
            kycItem.status.value,
        )

        return NetworkRequestBuilder()
            .subUrl("/v1/merchant/workflow/init")
            .body(kycItemRequest)
            .build()
            .post()
    }
}

@Keep
data class KycItemRequest(
    val id: String,
    val priority: Int,
    val type: String,
    val state: String,
)

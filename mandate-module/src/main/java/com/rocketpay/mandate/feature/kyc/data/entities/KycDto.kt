package com.rocketpay.mandate.feature.kyc.data.entities

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class KycDto(
    val id: String?,
    val status: String?,
    val workflow: List<KycWorkFlowDto>?
)

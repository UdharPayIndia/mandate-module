package com.rocketpay.mandate.feature.kyc.data.entities

import androidx.annotation.Keep
import com.google.gson.JsonObject
import com.rocketpay.mandate.feature.kyc.domain.entities.KycItemErrorMeta
import com.rocketpay.mandate.feature.kyc.domain.entities.KycItemInputMeta

@Keep
internal class KycWorkFlowDto(
    val id: String,
    val name: String,
    val type: String,
    val priority: Int,
    val status: String?,
    val input: List<KycItemInputMeta>?,
    val output: JsonObject?,
    val error: KycItemErrorMeta?
)

package com.rocketpay.mandate.feature.mandate.data.entities

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal class ChargeRequestDto(
    val amount: Double,
    val frequency: String,
    val installments: Int,
    val bearer: String,
    val method: String,
    @SerializedName("coupon_id") val couponId: Int? = null,
    @SerializedName("reference_id") val referenceId: String? = null,
    @SerializedName("reference_type") val referenceType: String? = null
)
package com.rocketpay.mandate.feature.settlements.data.entities

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal class PaymentOrderReferenceDto(
    @SerializedName("id") val id: String,
    @SerializedName("tenant_id") val tenantId: String,
    @SerializedName("payin_order_id") val payInOrderId: String

)
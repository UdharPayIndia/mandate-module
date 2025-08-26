package com.rocketpay.mandate.feature.settlements.data.entities

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal class SettlementWalletDto(
    @SerializedName("id") val id: String,
    @SerializedName("product_type") val productType: String,
    @SerializedName("payin") val payin: String,
    @SerializedName("payout") val payout: String
)
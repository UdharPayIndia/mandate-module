package com.rocketpay.mandate.feature.settlements.data.entities

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal class SettlementBannerDto(
    @SerializedName("show_banner") val showBanner: Boolean,
    @SerializedName("message") val message: String
)
package com.rocketpay.mandate.feature.installment.data.entities

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal class InstallmentMarkAsPaidRequest(
    @SerializedName("manual_collected_details")
    val manualCollectedDetails: InstallmentManuallyCollectedDetailRequest,
    val medium: String = "MANUAL",
)

@Keep
internal class InstallmentManuallyCollectedDetailRequest(
    val mode: String,
    val comments: String?,
    @SerializedName("merchant_collected")
    val merchantCollected: Boolean = true,
)


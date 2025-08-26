package com.rocketpay.mandate.feature.installment.data.entities

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal class InstallmentRetryRequest(
    @SerializedName("schedule_date") val scheduleDate: String
)
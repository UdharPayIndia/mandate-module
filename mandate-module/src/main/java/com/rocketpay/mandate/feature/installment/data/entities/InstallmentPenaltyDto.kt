package com.rocketpay.mandate.feature.installment.data.entities

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal class InstallmentPenaltyDto(@SerializedName("amount") val amount: Double?,
                            @SerializedName("capture_at") val captureAt: Long?,
                            @SerializedName("status") val status: String?)
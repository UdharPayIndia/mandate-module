package com.rocketpay.mandate.feature.installment.data.entities

import androidx.annotation.Keep

@Keep
internal data class CreateInstallmentRequest(
    val mandate_id: String,
    val amount: Double,
    val due_date: Long,
    val otp: String
)

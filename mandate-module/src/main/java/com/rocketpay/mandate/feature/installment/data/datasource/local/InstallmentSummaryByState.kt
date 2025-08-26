package com.rocketpay.mandate.feature.installment.data.datasource.local

import androidx.annotation.Keep

@Keep
internal class InstallmentSummaryByState(
    val state: String,
    val count: Int,
    val amount: Double,
    val amountWithoutCharges: Double
)
package com.rocketpay.mandate.feature.mandate.data.entities

import androidx.annotation.Keep

@Keep
internal class MandateSummaryByState (
    val state: String,
    val count: Int
)
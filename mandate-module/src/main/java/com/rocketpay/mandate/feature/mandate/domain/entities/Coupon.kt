package com.rocketpay.mandate.feature.mandate.domain.entities

import androidx.annotation.Keep

@Keep
internal class Coupon(
    val id: Int,
    val name: String,
    val description: String,
)
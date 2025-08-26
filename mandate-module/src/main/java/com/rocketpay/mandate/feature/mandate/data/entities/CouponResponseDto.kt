package com.rocketpay.mandate.feature.mandate.data.entities

import androidx.annotation.Keep

@Keep
internal class CouponResponseDto(
    val id: Int,
    val name: String,
    val description: String,
    val type: String,
)
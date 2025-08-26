package com.rocketpay.mandate.feature.charge.domain.entities

internal data class Charge(
    val actualCharge: Double,
    val offerCharge: Double,
    val chargeType: ChargeType
)

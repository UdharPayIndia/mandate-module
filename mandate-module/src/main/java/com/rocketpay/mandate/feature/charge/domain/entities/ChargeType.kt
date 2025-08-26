package com.rocketpay.mandate.feature.charge.domain.entities


internal sealed class ChargeType(val value: String) {
    object Flat : ChargeType("FLAT")
    object Percentage : ChargeType("PERCENTAGE")

    companion object {
        val map by lazy {
            mapOf(
                "FLAT" to Flat,
                "PERCENTAGE" to Percentage
            )
        }
    }
}

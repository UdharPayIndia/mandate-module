package com.rocketpay.mandate.feature.mandate.domain.entities

internal sealed class PaymentMedium(val value: String) {
    object Manual : PaymentMedium("MANUAL")
    object Auto : PaymentMedium("AUTO")

    companion object {
        val map by lazy {
            mapOf(
                "AUTO" to Auto,
                "MANUAL" to Manual,
            )
        }

        fun get(mode: String?): PaymentMedium? {
            return map[mode]
        }
    }
}
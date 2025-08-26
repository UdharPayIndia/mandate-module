package com.rocketpay.mandate.feature.mandate.domain.entities

internal sealed class PaymentMethod(val value: String) {
    object Upi : PaymentMethod("upi")
    object Nach : PaymentMethod("emandate")
    object Manual : PaymentMethod("manual")

    companion object {
        val map by lazy {
            mapOf(
                "upi" to Upi,
                "emandate" to Nach,
                "manual" to Manual
            )
        }

        fun get(mode: String?): PaymentMethod {
            return map[mode] ?: Upi
        }
    }
}

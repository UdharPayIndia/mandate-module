package com.rocketpay.mandate.feature.mandate.domain.entities

internal sealed class MandateProduct(val value: String) {
    object Default : MandateProduct("subscription")
    object Khata : MandateProduct("khata")

    companion object {
        val map by lazy {
            mapOf(
                "subscription" to Default,
                "khata" to Khata
            )
        }

        fun get(value: String?): MandateProduct {
            return map[value] ?: Default
        }
    }
}

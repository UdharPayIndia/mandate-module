package com.rocketpay.mandate.feature.product.domain.entities

internal sealed class PaymentMethodEnum(val value: String) {
    object Upi: PaymentMethodEnum("UPI")
    object Card: PaymentMethodEnum("CC_DC")
    object NetBanking: PaymentMethodEnum("NET_BANKING")

    companion object {
        val map by lazy {
            mapOf(
                "UPI" to Upi,
                "CC_DC" to Card,
                "NET_BANKING" to NetBanking
            )
        }

        fun get(value: String): PaymentMethodEnum {
            return map[value] ?: Upi
        }
    }
}
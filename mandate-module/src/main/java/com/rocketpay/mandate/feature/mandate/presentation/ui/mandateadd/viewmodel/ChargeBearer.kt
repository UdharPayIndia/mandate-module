package com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.viewmodel

internal sealed class ChargeBearer(val value: String) {
    object Merchant : ChargeBearer("MERCHANT")
    object Customer : ChargeBearer("CUSTOMER")
    object Both : ChargeBearer("BOTH")

    companion object {
        val map by lazy {
            mapOf(
                "MERCHANT" to Merchant,
                "CUSTOMER" to Customer,
                "BOTH" to Both
            )
        }

        fun get(type: String?): ChargeBearer? {
            return map[type]
        }
    }
}

package com.rocketpay.mandate.feature.settlements.domain.entities

internal sealed class PaymentOrderStateEnum(val value: String) {
    object Success: PaymentOrderStateEnum("SUCCESS")
}
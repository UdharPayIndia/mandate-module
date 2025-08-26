package com.rocketpay.mandate.feature.settlements.domain.entities

internal sealed class PaymentOrderType(val value:String) {
    object Settle: PaymentOrderType("SETTLE")
    object Collect: PaymentOrderType("COLLECT")
    object Receive: PaymentOrderType("RECEIVE")
    object AuthMandate: PaymentOrderType("AUTH_MANDATE")
    object CollectRefund: PaymentOrderType("COLLECT_REFUND")

}
package com.rocketpay.mandate.feature.charge.presentation.ui.view


internal sealed class ChargeFlowType(val value: String) {
    object Business : ChargeFlowType("business")
    object Customer : ChargeFlowType("customer")
    object AutoPay : ChargeFlowType("autopay")


    companion object {
        val map by lazy {
            mapOf(
                "business" to Business,
                "customer" to Customer,
                "autopay" to AutoPay
            )
        }

        fun get(flowType: String?): ChargeFlowType? {
            return if (flowType == null) {
                null
            } else {
                map[flowType]
            }
        }
    }
}

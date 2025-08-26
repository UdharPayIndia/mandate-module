package com.rocketpay.mandate.feature.kyc.domain.entities

internal sealed class KycItemHyperVergeFlowType(val value: String) {
    object Web : KycItemHyperVergeFlowType("web")
    object Android : KycItemHyperVergeFlowType("android")

    companion object {
        val map by lazy {
            mapOf(
                "web" to Web,
                "android" to Android
            )
        }

        fun get(value: String?): KycItemHyperVergeFlowType {
            return map[value] ?: Android
        }
    }
}

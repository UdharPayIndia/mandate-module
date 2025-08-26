package com.rocketpay.mandate.feature.kyc.domain.entities

internal sealed class KycInputTypeEnum (val value: String) {
    object MultiChoice : KycInputTypeEnum("multiple_choice")
    object Document : KycInputTypeEnum("document")
    object Text : KycInputTypeEnum("text")

    companion object {
        val map by lazy {
            mapOf(
                "multiple_choice" to MultiChoice,
                "document" to Document,
                "text" to Text,
            )
        }

        fun get(value: String): KycInputTypeEnum {
            return map[value] ?: Text
        }
    }
}

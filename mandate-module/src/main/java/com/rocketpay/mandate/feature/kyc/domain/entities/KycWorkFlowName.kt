package com.rocketpay.mandate.feature.kyc.domain.entities

internal sealed class KycWorkFlowName(val value: String) {
    data object BusinessProof : KycWorkFlowName("businessProofVerification")
    data object PanDetailsVerification : KycWorkFlowName("panDetailsVerification")
    data object BankVerification : KycWorkFlowName("bankAccountVerification")
    data object SdkWorkFlow : KycWorkFlowName("sdkWorkflow")

    companion object {
        val map by lazy {
            mapOf(
                "businessProofVerification" to BusinessProof,
                "panDetailsVerification" to PanDetailsVerification,
                "bankAccountVerification" to BankVerification,
                "sdkWorkflow" to SdkWorkFlow,
                )
        }

        fun get(value: String?): KycWorkFlowName {
            return map[value] ?: PanDetailsVerification
        }
    }
}

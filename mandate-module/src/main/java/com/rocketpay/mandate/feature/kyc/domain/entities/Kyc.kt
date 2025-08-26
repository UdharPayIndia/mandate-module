package com.rocketpay.mandate.feature.kyc.domain.entities

internal data class Kyc(
    val id: String,
    val state: KycStateEnum,
    val workflow: List<KycWorkFlow>
)

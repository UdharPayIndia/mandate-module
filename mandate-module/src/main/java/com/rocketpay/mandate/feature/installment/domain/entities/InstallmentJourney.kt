package com.rocketpay.mandate.feature.installment.domain.entities

import com.rocketpay.mandate.feature.installment.data.mapper.TimeState

internal data class InstallmentJourney(
    val createdAt: Long,
    val status: InstallmentState,
    val state: InstallmentState,
    val statusDescription: String?,
    val timeState: TimeState
)

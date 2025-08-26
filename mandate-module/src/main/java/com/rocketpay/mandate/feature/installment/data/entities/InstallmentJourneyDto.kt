package com.rocketpay.mandate.feature.installment.data.entities

import androidx.annotation.Keep

@Keep
internal class InstallmentJourneyDto(
	val created_at: Long,
	val state: String,
	val status: String?,
	val status_description: String?,
	val time_state: String
)

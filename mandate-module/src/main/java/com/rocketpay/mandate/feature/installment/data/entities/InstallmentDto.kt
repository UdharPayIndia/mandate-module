package com.rocketpay.mandate.feature.installment.data.entities

import androidx.annotation.Keep
import com.rocketpay.mandate.feature.bankaccount.data.entities.BankAccountDto
import com.rocketpay.mandate.feature.mandate.data.entities.ChargeResponseDto

@Keep
internal class InstallmentDto(
	val id: String,
	val mandate_id: String,
	val serial_number: Int,
	val amount: Double,
	val state: String?,
	val status: String?,
	val due_date: Long,
	val created_at: Long,
	val updated_at: Long,
	val source: BankAccountDto?,
	val medium: String,
	val meta: InstallmentMeta?,
	val amount_without_charges: Double?,
	val utr: String?,
	val destination: BankAccountDto?,
	val installment_utr: String?,
	val charges: ChargeResponseDto?,
	val payment_order_id: String?,
	//Not in Sync Call
	val journey: List<InstallmentJourneyDto>?,
)

@Keep
internal class InstallmentAction(
	val id: String,
	val isEnabled: Boolean)

@Keep
internal class InstallmentMeta(
	val mode: String?,
	val comments: String?,
	val merchant_collected: Boolean?,
	val retry_schedule_date: String?
)

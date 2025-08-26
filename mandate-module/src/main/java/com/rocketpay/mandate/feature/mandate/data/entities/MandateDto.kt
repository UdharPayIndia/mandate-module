package com.rocketpay.mandate.feature.mandate.data.entities

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.rocketpay.mandate.feature.installment.data.entities.InstallmentAction

@Keep
internal class MandateDto(

	@SerializedName("id")
	val id: String,

	@SerializedName("amount")
	val amount: Double?,

	@SerializedName("amount_without_charges")
	val amountWithoutCharges: Double?,

	@SerializedName("bearer")
	val bearer: String?,

	@SerializedName("charge_id")
	val chargeId: String?,

	@SerializedName("discount_id")
	val discountId: String?,

	@SerializedName("installment_amount")
	val installmentAmount: Double?,

	@SerializedName("frequency")
	val frequency: String?,

	@SerializedName("installments")
	val installments: Int?,

	@SerializedName("product")
	val product: String?,

	@SerializedName("mandate_url")
	val mandateUrl: String?,

	@SerializedName("payment_details")
	val paymentDetails: PaymentDetailsDto,

	@SerializedName("description")
	val description: String?,

	@SerializedName("next_charge_at")
	val nextChargeAt: Long?,

	@SerializedName("gateway_mandate_id")
	val gatewayMandateId: String?,

	@SerializedName("start_at")
	val startAt: Long?,

	@SerializedName("end_at")
	val endAt: Long?,

	@SerializedName("created_at")
	val createdAt: Long?,

	@SerializedName("updated_at")
	val updatedAt: Long?,

	@SerializedName("is_deleted")
	val isDeleted: Boolean,

	@SerializedName("status")
	val status: String?,

	@SerializedName("original_amount")
	val originalAmount: Double?,

	@SerializedName("meta")
	val meta: MetaData?,

	@SerializedName("reference_id")
	val referenceId: String?,

	@SerializedName("reference_type")
	val referenceType: String?,

	@SerializedName("customer", alternate = ["customer_info"])
	val customer: CustomerDto?,

	@SerializedName("actions")
	val actions: List<InstallmentAction>?,
)

package com.rocketpay.mandate.feature.mandate.data.entities

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class CreateMandateDto(
	@SerializedName("amount")
	val amount: Double,

	@SerializedName("payment_details")
	val paymentDetails: PaymentDetailsDto,

	@SerializedName("installments")
	val installments: Int,

	@SerializedName("description")
	val description: String,

	@SerializedName("start_at")
	val startAt: Long,

	@SerializedName("customer")
	val customer: CustomerDto,

	@SerializedName("frequency")
	val frequency: String,

	@SerializedName("product")
	val product: String,

	@SerializedName("bearer")
	val bearer: String?,

	@SerializedName("charge_id")
	val chargeId: String?,

	@SerializedName("discount_id")
	val discountId: String?,

	@SerializedName("amount_without_charges")
	val amountWithoutCharges: Double?,

	@SerializedName("original_amount")
	val originalAmount: Double?,

	@SerializedName("reference_id")
	val referenceId: String?,

	@SerializedName("reference_type")
	val referenceType: String?,

	@SerializedName("meta")
	val meta: MetaData?
)

@Keep
internal class MetaData(
	@SerializedName("charges") val charges: ChargeResponseDto?,
	@SerializedName("self_checkout") val selfCheckoutDto: SelfCheckoutDto?
)

@Keep
internal class SelfCheckoutDto(@SerializedName("QR") val qr: QrData?)

@Keep
internal class QrData(@SerializedName("auth_data") val authData: AuthData?)

@Keep
internal class AuthData(@SerializedName("qr_data") val qrData: String?)
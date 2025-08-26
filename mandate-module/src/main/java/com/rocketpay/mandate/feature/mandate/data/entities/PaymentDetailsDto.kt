package com.rocketpay.mandate.feature.mandate.data.entities

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal class PaymentDetailsDto(
	@SerializedName("method")
	val method: String,

	@SerializedName("upi_id")
	val upiId: String?
)

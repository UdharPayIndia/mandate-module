package com.rocketpay.mandate.feature.mandate.data.entities

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal class CustomerDto(
	@SerializedName("name")
	val name: String?,

	@SerializedName("mobile_number")
	val mobileNumber: String?
)

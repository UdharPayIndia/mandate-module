package com.rocketpay.mandate.feature.property.data.entities

import androidx.annotation.Keep
import java.io.Serializable

@Keep
internal data class PropertyDto(
	val key: String,
	val value: String?
): Serializable

package com.rocketpay.mandate.feature.product.data.entities

import androidx.annotation.Keep

@Keep
internal data class PaymentMethodDto(val method: String, val priority: Int)

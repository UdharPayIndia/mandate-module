package com.rocketpay.mandate.feature.mandate.data.entities

import androidx.annotation.Keep

@Keep
internal class ConvertMandateDto (val payment_method: String = "manual")
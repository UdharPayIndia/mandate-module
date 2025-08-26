package com.rocketpay.mandate.feature.mandate.data.entities

import androidx.annotation.Keep

@Keep
internal class MandateListResponse(
    val items: List<MandateDto>
)
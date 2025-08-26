package com.rocketpay.mandate.feature.image.data.entities

import androidx.annotation.Keep

@Keep
internal class DeleteImageRequestDto(
    val documents: List<String>
)
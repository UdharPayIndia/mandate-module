package com.rocketpay.mandate.feature.mandate.data.datasource.local

import androidx.room.Embedded

internal class MandateWithSubtextEntity (
    @Embedded
    val mandateEntity: MandateEntity,

    @Embedded
    val mandateSubtextEntity: MandateSubtextEntity
)
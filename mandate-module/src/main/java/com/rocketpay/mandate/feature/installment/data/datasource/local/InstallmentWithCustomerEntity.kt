package com.rocketpay.mandate.feature.installment.data.datasource.local

import androidx.room.Embedded

internal class InstallmentWithCustomerEntity(
    @Embedded
    val installment: InstallmentEntity,
    val customerName: String?,
)
package com.rocketpay.mandate.feature.installment.data.datasource.local

import androidx.room.Embedded

internal class InstallmentWithMandateEntity(
    @Embedded
    val installment: InstallmentEntity,
    val customerName: String?,
    val noOfInstallment: String,
    val paidInstallment: String,
    val mandateState: String,
    val paymentMethod: String,
    val referenceId: String?,
    val isAutoReminderEnabled: Boolean
)
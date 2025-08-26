package com.rocketpay.mandate.feature.mandate.domain.entities

import com.rocketpay.mandate.feature.mandate.data.entities.MetaData

internal data class CreateMandate(
    val amount: Double,
    val paymentMethodDetail: PaymentMethodDetail,
    val installments: Int,
    val description: String,
    val startAt: Long,
    val customerDetail: CustomerDetail,
    val frequency: String,
    val product: String,
    val amountWithoutCharges: Double?,
    val bearer: String?,
    val chargeId: String?,
    val discountId: String?,
    val originalAmount: Double,
    val referenceId: String?,
    val referenceType: String?,
    val meta: MetaData?
)

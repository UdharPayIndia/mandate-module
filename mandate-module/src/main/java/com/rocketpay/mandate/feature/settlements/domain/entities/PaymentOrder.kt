package com.rocketpay.mandate.feature.settlements.domain.entities

import com.rocketpay.mandate.feature.settlements.data.entities.PaymentOrderMetaDto
import com.rocketpay.mandate.feature.settlements.data.entities.PaymentOrderReferenceDto
import com.rocketpay.mandate.feature.settlements.data.entities.PaymentOrderSummaryDto
import com.rocketpay.mandate.main.init.MandateManager
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.double
import com.rocketpay.mandate.feature.common.domain.CommonUseCase

internal class PaymentOrder(
    val id: String,
    val isDeleted: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    val state: String,
    val type: String,
    val serverSequence: Long,
    val referenceId: String,
    val payees: List<PaymentOrderSummaryDto>,
    val meta: PaymentOrderMetaDto?,
    val references: List<PaymentOrderReferenceDto>
){

    fun getSettlementAmount(): Double {
        var settledAmount = 0.0
        payees.forEach {
            if(it.accountId == CommonUseCase.getInstance().getAccountId()){
                settledAmount += it.amount.value.double()
            }
        }
        return settledAmount
    }
}
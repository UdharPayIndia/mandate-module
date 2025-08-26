package com.rocketpay.mandate.feature.settlements.presentation.ui.detail.statemachine

import com.rocketpay.mandate.feature.installment.domain.entities.Installment
import com.rocketpay.mandate.feature.settlements.data.entities.PaymentOrderReferenceDto
import com.rocketpay.mandate.feature.settlements.domain.entities.PaymentOrder
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseEvent
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseState
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.Screen
import com.rocketpay.mandate.common.mvistatemachine.contract.AsyncSideEffect
import com.rocketpay.mandate.common.mvistatemachine.contract.UiSideEffect

internal data class SettlementDetailState(
    val settlementId: String = "",
    val payInOrderId: String = "",
    internal val paymentOrder: PaymentOrder? = null,
    val installments: List<Installment> = emptyList(),
    val refundedInstallments: List<Installment> = emptyList(),
    val error: String = ""
) : BaseState(SettlementDetailScreen)

internal sealed class SettlementDetailEvent(name: String? = null) : BaseEvent(name) {
    data class Init(val settlementId: String, val payInOrderId: String): SettlementDetailEvent()
    data class LoadSettlement(
        val settlementId: String
    ): SettlementDetailEvent()
    data class LoadSettlementByPayInOrderId(
        val payInOrderId: String
    ): SettlementDetailEvent()
    data class SetSettlement(
        val paymentOrder: PaymentOrder?
    ): SettlementDetailEvent()
    data class UtrCopyClick(
        val message: String,
        val link: String
    ): SettlementDetailEvent()
    data class SetInstallments(
        val installments: List<Installment>,
        val refundedInstallments: List<Installment>
    ): SettlementDetailEvent()
    data class InstallmentClick(
        val installment: Installment
    ): SettlementDetailEvent("settled_installment_click")
    data object BackClick: SettlementDetailEvent()
    data class InstallmentDetailsFetchedError(val message: String): SettlementDetailEvent()
    data object InstallmentDetailsFetchedSuccess: SettlementDetailEvent()
    data object RetryClick: SettlementDetailEvent()
}

internal sealed class SettlementDetailASF : AsyncSideEffect {
    data class Init(val settlementId: String, val payInOrderId: String): SettlementDetailASF()
    data class LoadSettlement(
        val settlementId: String
    ) : SettlementDetailASF()
    data class LoadSettlementByPayInOrderId(
        val payInOrderId: String
    ): SettlementDetailASF()
    data class LoadSettlementInstallments(
        val references: List<PaymentOrderReferenceDto>
    ): SettlementDetailASF()
}

internal sealed class SettlementDetailUSF : UiSideEffect {
    data class Copy(val message: String, val link: String) : SettlementDetailUSF()
    data class SetInstallments(val installments: List<Installment>, val refundedInstallments: List<Installment>): SettlementDetailUSF()
    data class OpenMandate(val installment: Installment): SettlementDetailUSF()
    data object CloseScreen: SettlementDetailUSF()
}

internal object SettlementDetailScreen : Screen("settlement_detail")

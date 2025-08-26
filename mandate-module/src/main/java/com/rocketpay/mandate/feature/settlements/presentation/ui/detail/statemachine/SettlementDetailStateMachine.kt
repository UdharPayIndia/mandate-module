package com.rocketpay.mandate.feature.settlements.presentation.ui.detail.statemachine

import com.rocketpay.mandate.feature.installment.domain.usecase.InstallmentUseCase
import com.rocketpay.mandate.feature.settlements.data.PaymentOrderSyncer
import com.rocketpay.mandate.feature.settlements.domain.entities.PaymentOrderType
import com.rocketpay.mandate.feature.settlements.domain.usecase.PaymentOrderUseCase
import com.udharpay.core.networkmanager.domain.entities.Outcome
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseAnalyticsHandler
import com.rocketpay.mandate.common.mvistatemachine.contract.Next
import com.rocketpay.mandate.common.mvistatemachine.contract.collectIn
import com.rocketpay.mandate.common.mvistatemachine.viewmodel.simple.SimpleStateMachineImpl
import com.rocketpay.mandate.common.syncmanager.client.SyncManager
import kotlinx.coroutines.CoroutineScope

internal class SettlementDetailStateMachine (
    private val paymentOrderUseCase: PaymentOrderUseCase,
    private val installmentUseCase: InstallmentUseCase
) : SimpleStateMachineImpl<SettlementDetailEvent, SettlementDetailState, SettlementDetailASF, SettlementDetailUSF>(
    BaseAnalyticsHandler()
) {

    override fun startState(): SettlementDetailState {
        return SettlementDetailState()
    }

    override fun handleEvent(
        event: SettlementDetailEvent,
        state: SettlementDetailState
    ): Next<SettlementDetailState?, SettlementDetailASF?, SettlementDetailUSF?> {
        return when (event) {
            is SettlementDetailEvent.Init -> {
                next(state.copy(settlementId = event.settlementId, payInOrderId = event.payInOrderId),
                    SettlementDetailASF.Init(event.settlementId, event.payInOrderId))
            }
            is SettlementDetailEvent.LoadSettlement -> {
                next(state.copy(settlementId = event.settlementId),
                    SettlementDetailASF.LoadSettlement(event.settlementId))
            }
            is SettlementDetailEvent.LoadSettlementByPayInOrderId -> {
                if(!event.payInOrderId.isNullOrEmpty()){
                    next(state.copy(payInOrderId = event.payInOrderId),
                        SettlementDetailASF.LoadSettlementByPayInOrderId(event.payInOrderId))
                }else{
                    noChange()
                }
            }
            is SettlementDetailEvent.SetSettlement -> {
                next(state.copy(
                    paymentOrder = event.paymentOrder),
                    SettlementDetailASF.LoadSettlementInstallments(event.paymentOrder?.references ?: emptyList())
                )
            }
            is SettlementDetailEvent.UtrCopyClick -> {
                next(SettlementDetailUSF.Copy(event.message, event.link))
            }
            is SettlementDetailEvent.SetInstallments -> {
                next(state.copy(installments = event.installments,
                    refundedInstallments = event.refundedInstallments),
                    SettlementDetailUSF.SetInstallments(event.installments, event.refundedInstallments))
            }
            is SettlementDetailEvent.InstallmentClick ->{
                next(SettlementDetailUSF.OpenMandate(event.installment))
            }
            is SettlementDetailEvent.BackClick -> {
                next(SettlementDetailUSF.CloseScreen)
            }
            is SettlementDetailEvent.InstallmentDetailsFetchedError -> {
                next(state.copy(error = event.message))
            }
            is SettlementDetailEvent.InstallmentDetailsFetchedSuccess -> {
                next(state.copy(error = ""))
            }
            is SettlementDetailEvent.RetryClick -> {
                SyncManager.getInstance().enqueue(PaymentOrderSyncer.TYPE)
                next(SettlementDetailASF.Init(settlementId = state.settlementId, payInOrderId = state.payInOrderId))
            }
        }
    }


    override suspend fun handleAsyncSideEffect(
        sideEffect: SettlementDetailASF,
        dispatchEvent: (SettlementDetailEvent) -> Unit,
        viewModelScope: CoroutineScope
    ) {
        when (sideEffect) {
            is SettlementDetailASF.Init -> {
                if(!sideEffect.settlementId.isNullOrEmpty()){
                    dispatchEvent(SettlementDetailEvent.LoadSettlement(settlementId = sideEffect.settlementId))
                }else if(!sideEffect.payInOrderId.isNullOrEmpty()){
                    dispatchEvent(SettlementDetailEvent.LoadSettlementByPayInOrderId(sideEffect.payInOrderId))
                }else{
                    dispatchEvent(SettlementDetailEvent.BackClick)
                }
            }
            is SettlementDetailASF.LoadSettlement -> {
                when(val outcome = paymentOrderUseCase.fetchPaymentOrderDetails(sideEffect.settlementId)){
                    is Outcome.Success -> {
                        dispatchEvent.invoke(SettlementDetailEvent.InstallmentDetailsFetchedSuccess)
                    }
                    is Outcome.Error ->{
                        dispatchEvent.invoke(SettlementDetailEvent.InstallmentDetailsFetchedError(outcome.error.message.orEmpty()))

                    }
                }
                paymentOrderUseCase.getPaymentOrderByIdLive(sideEffect.settlementId).collectIn(viewModelScope){
                    dispatchEvent.invoke(SettlementDetailEvent.SetSettlement(it))
                }
            }
            is SettlementDetailASF.LoadSettlementByPayInOrderId -> {
                dispatchEvent.invoke(SettlementDetailEvent.LoadSettlement(
                    paymentOrderUseCase.getSettlementByPayInOrderId(sideEffect.payInOrderId).orEmpty()
                ))
            }
            is SettlementDetailASF.LoadSettlementInstallments -> {
                val paymentOrderIds = ArrayList(sideEffect.references.map { it.payInOrderId })
                val refundedPaymentOrderIds = ArrayList(
                    paymentOrderUseCase.getPaymentOrdersCorrespondsToRefund(PaymentOrderType.CollectRefund.value, paymentOrderIds)
                )

                val paymentOrderIterator = paymentOrderIds.iterator()
                while (paymentOrderIterator.hasNext()) {
                    val item = paymentOrderIterator.next()
                    val refundedPaymentOrderIterator = refundedPaymentOrderIds.iterator()
                    while(refundedPaymentOrderIterator.hasNext()){
                        val item2 = refundedPaymentOrderIterator.next()
                        if(item2 == item){
                            paymentOrderIterator.remove()
                            refundedPaymentOrderIterator.remove()
                        }
                    }
                }

                val installments = installmentUseCase.getInstallmentsByPaymentOrderIds(paymentOrderIds)
                val refundedInstallments = installmentUseCase.getInstallmentsByPaymentOrderIds(refundedPaymentOrderIds)

                dispatchEvent(SettlementDetailEvent.SetInstallments(installments, refundedInstallments))
            }
        }
    }

}

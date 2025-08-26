package com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.main.statemachine

import com.rocketpay.mandate.feature.installment.domain.usecase.InstallmentUseCase
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DateUtils
import com.rocketpay.mandate.common.mvistatemachine.contract.Next
import com.rocketpay.mandate.common.mvistatemachine.contract.collectIn
import com.rocketpay.mandate.common.mvistatemachine.viewmodel.simple.SimpleStateMachineImpl
import kotlinx.coroutines.CoroutineScope

internal class PaymentTrackerMainStateMachine (
    private val installmentUseCase: InstallmentUseCase,
) : SimpleStateMachineImpl<PaymentTrackerMainEvent, PaymentTrackerMainState, PaymentTrackerMainASF, PaymentTrackerMainUSF>(
    PaymentTrackerMainAnalyticsHandler()
) {

    override fun startState(): PaymentTrackerMainState {
        return PaymentTrackerMainState()
    }

    override fun handleEvent(
        event: PaymentTrackerMainEvent,
        state: PaymentTrackerMainState
    ): Next<PaymentTrackerMainState?, PaymentTrackerMainASF?, PaymentTrackerMainUSF?> {
        return when (event) {
            is PaymentTrackerMainEvent.Init -> {
                next(state.copy(
                    selectedIndex = event.selectedIndex,
                    isSuperKeyFlow = event.isSuperKeyFlow
                ),
                    PaymentTrackerMainASF.LoadInstallmentSummary(event.isSuperKeyFlow))
            }
            is PaymentTrackerMainEvent.InstallmentAmountSummaryLoaded -> {
                next(
                    state.copy(
                        installmentAmountSummary = event.summary,
                    ),
                    PaymentTrackerMainASF.CheckIsUpdated(
                        event.summary,
                        state.installmentAmountSummary
                    )
                )
            }
            is PaymentTrackerMainEvent.UpdateSelectedState ->{
                if(event.updateFragment){
                    next(state.copy(selectedIndex = event.selectedIndex),
                        PaymentTrackerMainUSF.UpdateFragment(event.selectedIndex))
                }else{
                    next(state.copy(selectedIndex = event.selectedIndex))
                }
            }
            is PaymentTrackerMainEvent.InstallmentSummaryUpdated -> {
                next(PaymentTrackerMainUSF.RefreshInstallments(
                    event.isOutstandingUpdated,
                    event.isUpcomingUpdated,
                    event.isCollectedUpdated
                ))
            }
            is PaymentTrackerMainEvent.ViewSettlementDashBoard -> {
                next(PaymentTrackerMainUSF.ViewSettlementDashBoard)
            }
        }
    }

    override suspend fun handleAsyncSideEffect(
        sideEffect: PaymentTrackerMainASF,
        dispatchEvent: (PaymentTrackerMainEvent) -> Unit,
        viewModelScope: CoroutineScope
    ) {
        when (sideEffect) {
            is PaymentTrackerMainASF.LoadInstallmentSummary -> {
                installmentUseCase.getTrackerAmountSummary(
                    DateUtils.getCurrentDateWithoutTimeInMillis(),
                    sideEffect.isSuperKeyFlow
                ).collectIn(viewModelScope) {
                    dispatchEvent(PaymentTrackerMainEvent.InstallmentAmountSummaryLoaded(it))
                }
            }
            is PaymentTrackerMainASF.CheckIsUpdated -> {
                if(sideEffect.updatedSummary != null && sideEffect.existingSummary != null){
                    val isOutstandingUpdated = sideEffect.updatedSummary.outstandingCount != sideEffect.existingSummary.outstandingCount
                            || sideEffect.updatedSummary.outstandingAmount != sideEffect.existingSummary.outstandingAmount
                    val isUpcomingUpdated = sideEffect.updatedSummary.upcomingCount != sideEffect.existingSummary.upcomingCount
                            || sideEffect.updatedSummary.upcomingAmount != sideEffect.existingSummary.upcomingAmount
                    val isCollectedUpdated = sideEffect.updatedSummary.collectedAmount != sideEffect.existingSummary.collectedAmount
                            || sideEffect.updatedSummary.collectedCount != sideEffect.existingSummary.collectedCount
                    if(isOutstandingUpdated || isUpcomingUpdated || isCollectedUpdated) {
                        dispatchEvent(PaymentTrackerMainEvent.InstallmentSummaryUpdated(
                            isOutstandingUpdated,
                            isUpcomingUpdated,
                            isCollectedUpdated
                        ))
                    }
                }
            }
        }
    }

}

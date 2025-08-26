package com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.list.statemachine

import com.rocketpay.mandate.feature.installment.domain.usecase.InstallmentUseCase
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.list.viewmodel.PaymentTrackerType
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseAnalyticsHandler
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DateUtils
import com.rocketpay.mandate.common.mvistatemachine.contract.Next
import com.rocketpay.mandate.common.mvistatemachine.viewmodel.simple.SimpleStateMachineImpl
import kotlinx.coroutines.CoroutineScope
import kotlin.collections.isNullOrEmpty

internal class PaymentTrackerListStateMachine(
    private val installmentUseCase: InstallmentUseCase,
) : SimpleStateMachineImpl<PaymentTrackerListEvent, PaymentTrackerListState, PaymentTrackerListASF, PaymentTrackerListUSF>(
    BaseAnalyticsHandler()
) {

    override fun startState(): PaymentTrackerListState {
        return PaymentTrackerListState()
    }

    override fun handleEvent(
        event: PaymentTrackerListEvent,
        state: PaymentTrackerListState
    ): Next<PaymentTrackerListState?, PaymentTrackerListASF?, PaymentTrackerListUSF?> {
        return when (event) {
            is PaymentTrackerListEvent.LoadInstallments -> {
                val type = PaymentTrackerType.getPaymentTrackerType(event.paymentTrackerType)
                val lastFetchedTimeStamp = getFetchedTime(type, event.orderByDesc)
                next(
                    state.copy(
                        paymentTrackerType = type,
                        orderByDesc = event.orderByDesc,
                        lastFetchedTimeStamp = lastFetchedTimeStamp,
                        isLoading = true,
                        isSuperKeyFlow = event.isSuperKeyFlow,
                        skipManualMandate = event.skipManualMandate
                    ),
                    PaymentTrackerListASF.LoadInstallments(
                        type,
                        lastFetchedTimeStamp,
                        state.limit,
                        event.orderByDesc,
                        event.isSuperKeyFlow,
                        event.skipManualMandate))
            }
            is PaymentTrackerListEvent.InstallmentsLoaded -> {
                val installments = state.installments
                if(!event.installments.isNullOrEmpty()){
                    state.installments.addAll(event.installments)
                    val isLastPage = event.installments.size < state.limit
                    next(
                        state.copy(
                            isLoading = false,
                            isLastPage = isLastPage,
                            lastFetchedTimeStamp = event.installments.last().installment.dueDate,
                            installments = installments,
                        ),
                        PaymentTrackerListUSF.UpdateInstallments(installments, isLastPage, state.skipManualMandate)

                    )
                }else{
                    next(state.copy(
                        isLoading = false,
                        isLastPage = true,
                    ),
                        PaymentTrackerListUSF.UpdateInstallments(installments,
                            true, state.skipManualMandate))
                }

            }
            is PaymentTrackerListEvent.InstallmentClick -> {
                if(state.skipManualMandate){
                    next(PaymentTrackerListUSF.OpenMandateDetails(
                        event.installment.installment.mandateId,
                        event.installment.installment.serialNumber
                    ))
                }else{
                    next(PaymentTrackerListUSF.OpenMandateDetails(
                        event.installment.installment.mandateId,
                        event.installment.installment.serialNumber
                    ))
                }
            }
            is PaymentTrackerListEvent.FetchNextInstallments -> {
                next(
                    state.copy(
                        isLoading = true,
                    ),
                    PaymentTrackerListASF.LoadInstallments(
                        state.paymentTrackerType,
                        state.lastFetchedTimeStamp,
                        state.limit,
                        state.orderByDesc,
                        state.isSuperKeyFlow,
                        state.skipManualMandate)
                )
            }
            is PaymentTrackerListEvent.RefreshInstallments -> {
                next(state.copy(
                    isLoading = false,
                    isLastPage = false,
                    lastFetchedTimeStamp = getFetchedTime(state.paymentTrackerType, state.orderByDesc),
                    installments = arrayListOf()
                ),
                    PaymentTrackerListASF.LoadInstallments(
                        state.paymentTrackerType,
                        getFetchedTime(state.paymentTrackerType, state.orderByDesc),
                        state.limit,
                        state.orderByDesc,
                        state.isSuperKeyFlow,
                        state.skipManualMandate),
                    PaymentTrackerListUSF.UpdateInstallments(emptyList(), false,
                        state.skipManualMandate)
                )
            }
        }
    }

    private fun getFetchedTime(type: PaymentTrackerType, orderByDesc: Boolean): Long{
        return when(type){
            PaymentTrackerType.Outstanding, PaymentTrackerType.Upcoming -> DateUtils.getCurrentDateWithoutTimeInMillis()
            else -> {
                if(orderByDesc){
                    Long.MAX_VALUE
                }else{
                    0L
                }
            }
        }
    }

    override suspend fun handleAsyncSideEffect(
        sideEffect: PaymentTrackerListASF,
        dispatchEvent: (PaymentTrackerListEvent) -> Unit,
        viewModelScope: CoroutineScope
    ) {
        when (sideEffect) {
            is PaymentTrackerListASF.LoadInstallments -> {
                val installments = installmentUseCase.getTrackedInstallments(
                        sideEffect.paymentTrackerType.index,
                        sideEffect.lastFetchedTimeStamp,
                        sideEffect.orderByDesc,
                        sideEffect.limit,
                    sideEffect.isSuperKeyFlow,
                    sideEffect.skipManualMandate
                )
                dispatchEvent(PaymentTrackerListEvent.InstallmentsLoaded(ArrayList(installments)))

            }
        }
    }

}

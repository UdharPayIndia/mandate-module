package com.rocketpay.mandate.feature.installment.presentation.ui.installmentupdate.statemachine

import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.installment.data.entities.InstallmentManuallyCollectedDetailRequest
import com.rocketpay.mandate.feature.installment.data.entities.InstallmentMarkAsPaidRequest
import com.rocketpay.mandate.feature.installment.domain.usecase.InstallmentUseCase
import com.rocketpay.mandate.feature.mandate.domain.entities.PaymentMode
import com.udharpay.core.networkmanager.domain.entities.Outcome
import com.rocketpay.mandate.common.mvistatemachine.contract.Next
import com.rocketpay.mandate.common.mvistatemachine.viewmodel.simple.SimpleStateMachineImpl
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import kotlinx.coroutines.CoroutineScope

internal class InstallmentUpdateStateMachine (
    private val installmentUseCase: InstallmentUseCase
): SimpleStateMachineImpl<InstallmentUpdateEvent, InstallmentUpdateState, InstallmentUpdateASF, InstallmentUpdateUSF>(
    InstallmentUpdateAnalyticsHandler()
) {

    override fun startState(): InstallmentUpdateState {
        return InstallmentUpdateState()
    }

    override fun handleEvent(
        event: InstallmentUpdateEvent,
        state: InstallmentUpdateState
    ): Next<InstallmentUpdateState?, InstallmentUpdateASF?, InstallmentUpdateUSF?> {
        return when (event) {
            is InstallmentUpdateEvent.InitData -> {
                if(!event.installmentId.isNullOrEmpty()){
                    next(state.copy(installmentId = event.installmentId,
                        mode = PaymentMode.get(event.mode),
                        reason = event.reason))
                }else{
                    next(InstallmentUpdateUSF.CloseScreen)
                }
            }
            is InstallmentUpdateEvent.UpdatePaymentMode -> {
                next(state.copy(mode = event.paymentMode))
            }
            is InstallmentUpdateEvent.SaveClick -> {
                if(state.mode != null) {
                    next(
                        InstallmentUpdateASF.MarkAsPaid(
                            state.installmentId!!,
                            state.mode!!,
                            state.reason,
                        ), InstallmentUpdateUSF.ShowProgressDialog(
                            ResourceManager.getInstance().getString(
                                R.string.rp_marking_it_as_paid
                            ), ResourceManager.getInstance().getString(
                                R.string.rp_please_wait
                            )
                        )
                    )
                }else{
                    val error = ResourceManager.getInstance().getString(R.string.rp_please_select_the_payment_mode)
                    next(InstallmentUpdateUSF.ShowToast(error))
                }
            }
            is InstallmentUpdateEvent.CloseProgressDialog -> {
                next(InstallmentUpdateUSF.CancelProgressDialog)
            }
            is InstallmentUpdateEvent.SaveFailed -> {
                next(InstallmentUpdateUSF.ShowToast(event.message))
            }
            is InstallmentUpdateEvent.SaveSuccess -> {
                next(InstallmentUpdateUSF.CloseScreen)
            }
            is InstallmentUpdateEvent.CloseClick -> {
                next(InstallmentUpdateUSF.CloseScreen)
            }
            is InstallmentUpdateEvent.ReasonChanged -> {
                next(state.copy(reason = event.comment))
            }
        }
    }

    override suspend fun handleAsyncSideEffect(
        sideEffect: InstallmentUpdateASF,
        dispatchEvent: (InstallmentUpdateEvent) -> Unit,
        viewModelScope: CoroutineScope
    ) {
        when(sideEffect){
            is InstallmentUpdateASF.MarkAsPaid -> {
                val model = InstallmentManuallyCollectedDetailRequest(
                    mode = sideEffect.mode.value,
                    comments = sideEffect.comment?.trim()
                )
                val request = InstallmentMarkAsPaidRequest(manualCollectedDetails = model)
                when(val outcome = installmentUseCase.markAsPaidInstallment(sideEffect.installmentId, request)) {
                    is Outcome.Success -> {
                        installmentUseCase.fetchInstallmentActions(sideEffect.installmentId)
                        dispatchEvent(InstallmentUpdateEvent.SaveSuccess)
                    }
                    is Outcome.Error -> {
                        dispatchEvent(InstallmentUpdateEvent.SaveFailed(outcome.error.code.orEmpty(), outcome.error.message.orEmpty()))
                    }
                }
            }
        }
    }
}

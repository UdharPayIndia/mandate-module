package com.rocketpay.mandate.feature.installment.presentation.ui.retry.statemachine

import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.installment.domain.usecase.InstallmentUseCase
import com.udharpay.core.networkmanager.domain.entities.Outcome
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.long
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseAnalyticsHandler
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DateUtils
import com.rocketpay.mandate.common.mvistatemachine.contract.Next
import com.rocketpay.mandate.common.mvistatemachine.viewmodel.simple.SimpleStateMachineImpl
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import kotlinx.coroutines.CoroutineScope

internal class SelectRetryPeriodStateMachine (
    private val installmentUseCase: InstallmentUseCase
): SimpleStateMachineImpl<SelectRetryPeriodEvent, SelectRetryPeriodState, SelectRetryPeriodASF, SelectRetryPeriodUSF>(
    BaseAnalyticsHandler()
) {

    override fun startState(): SelectRetryPeriodState {
        return SelectRetryPeriodState()
    }

    override fun handleEvent(
        event: SelectRetryPeriodEvent,
        state: SelectRetryPeriodState
    ): Next<SelectRetryPeriodState?, SelectRetryPeriodASF?, SelectRetryPeriodUSF?> {
        return when (event) {
            is SelectRetryPeriodEvent.Init -> {
                next(
                    state.copy(
                        mandateId = event.mandateId,
                        installmentId = event.installmentId,
                    )
                )
            }
            is SelectRetryPeriodEvent.SelectImmediateDate -> {
                val minDate = DateUtils.addDay(System.currentTimeMillis(), 1)
                next(state.copy(retryDate = minDate.long(), retryDateError = null, isEnabled = true, retryOption = 0))
            }
            is SelectRetryPeriodEvent.SelectOtherDate -> {
                next(SelectRetryPeriodUSF.OpenRetryDateSelection(state.retryDate))
            }
            is SelectRetryPeriodEvent.RetryDateSelected -> {
                next(state.copy(retryDate = event.startDate.long(), retryDateError = null, isEnabled = true, retryOption = 1))
            }

            is SelectRetryPeriodEvent.SubmitRetry -> {
                next(
                    SelectRetryPeriodUSF.ShowRetryConfirmation(
                        ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_refresh),
                        ResourceManager.getInstance().getDrawable(R.color.rp_blue_2),
                        ResourceManager.getInstance().getString(R.string.rp_retry_installment_title),
                        if(state.retryOption == 1){
                            ResourceManager.getInstance().getString(R.string.rp_retry_installment_detail_other,
                            DateUtils.getDate(state.retryDate, DateUtils.MONTH_DATE_FORMAT))
                        }else{
                            ResourceManager.getInstance().getString(R.string.rp_retry_installment_detail)
                        },
                        ResourceManager.getInstance().getString(R.string.rp_retry),
                        ResourceManager.getInstance().getString(R.string.rp_cancel)
                    )
                )
            }
            is SelectRetryPeriodEvent.RetryConfirmed -> {
                next(
                    SelectRetryPeriodASF.RetryInstallment(state.mandateId, state.installmentId, state.retryDate),
                    SelectRetryPeriodUSF.ShowProgressDialog(
                        ResourceManager.getInstance().getString(R.string.rp_retrying_installment_title),
                        ResourceManager.getInstance().getString(R.string.rp_retrying_installment_detail),
                    )
                )
            }
            is SelectRetryPeriodEvent.CloseProgressDialog -> {
                next(SelectRetryPeriodUSF.DismissProgressDialog)
            }
            is SelectRetryPeriodEvent.RetryDismiss -> {
                next(SelectRetryPeriodUSF.DismissConfirmDialog)
            }
            is SelectRetryPeriodEvent.RetryInstallmentFailed -> {
                next(
                    SelectRetryPeriodUSF.ShowErrorDialog(
                        if(event.errorCode == "RETRY_NOT_AVAILABLE"){
                            ResourceManager.getInstance()
                                .getString(R.string.rp_retry_not_available)
                        }else {
                            ResourceManager.getInstance()
                                .getString(R.string.rp_retry_installment_failed)
                        },
                        event.errorMessage
                    )
                )
            }
            is SelectRetryPeriodEvent.RetryInstallmentSucceed -> {
                next(SelectRetryPeriodUSF.CloseScreen)
            }
        }
    }

    override suspend fun handleAsyncSideEffect(
        sideEffect: SelectRetryPeriodASF,
        dispatchEvent: (SelectRetryPeriodEvent) -> Unit,
        viewModelScope: CoroutineScope
    ) {
        when(sideEffect){
            is SelectRetryPeriodASF.RetryInstallment -> {
                when(val outcome = installmentUseCase.retryInstallment(sideEffect.mandateId, sideEffect.installmentId,
                    DateUtils.getDate(sideEffect.retryDate, DateUtils.SLASH_DATE_FORMAT))) {
                    is Outcome.Error -> {
                        dispatchEvent(SelectRetryPeriodEvent.RetryInstallmentFailed(outcome.error.code.orEmpty(), outcome.error.message.orEmpty()))
                    }
                    is Outcome.Success -> {
                        installmentUseCase.fetchInstallmentActions(sideEffect.installmentId)
                        dispatchEvent(SelectRetryPeriodEvent.RetryInstallmentSucceed)
                    }
                }
            }
        }
    }

}


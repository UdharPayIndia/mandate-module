package com.rocketpay.mandate.feature.installment.presentation.ui.installmentadd.statemachine

import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.installment.domain.usecase.InstallmentUseCase
import com.rocketpay.mandate.feature.mandate.domain.usecase.MandateUseCase
import com.udharpay.core.networkmanager.domain.entities.Outcome
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseAnalyticsHandler
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.AmountUtils
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DataValidator
import com.rocketpay.mandate.common.mvistatemachine.contract.Next
import com.rocketpay.mandate.common.mvistatemachine.contract.collectIn
import com.rocketpay.mandate.common.mvistatemachine.viewmodel.simple.SimpleStateMachineImpl
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import kotlinx.coroutines.CoroutineScope

internal class InstallmentAddStateMachine(
    private val mandateUseCase: MandateUseCase,
    private val installmentUseCase: InstallmentUseCase,
    private val dataValidator: DataValidator
) : SimpleStateMachineImpl<InstallmentAddEvent, InstallmentAddState, InstallmentAddASF, InstallmentAddUSF>(
    BaseAnalyticsHandler()
) {

    override fun startState(): InstallmentAddState {
        return InstallmentAddState()
    }

    override fun handleEvent(
        event: InstallmentAddEvent,
        state: InstallmentAddState
    ): Next<InstallmentAddState?, InstallmentAddASF?, InstallmentAddUSF?> {
        return when (event) {
            is InstallmentAddEvent.LoadData -> {
                if (event.mandateId == null) {
                    noChange()
                } else {
                    next(state.copy(mandateId = event.mandateId), InstallmentAddASF.LoadData(event.mandateId))
                }
            }
            is InstallmentAddEvent.DataLoaded -> {
                next(state.copy(mandate = event.mandate))
            }
            InstallmentAddEvent.AmountFocusChanged -> {
                next(InstallmentAddUSF.AmountFocusChanged)
            }
            is InstallmentAddEvent.AmountChanged -> {
                if (mandateUseCase.isValidAmount(event.amount)) {
                    next(state.copy(amount = event.amount, viewState = getViewState(state, state.dueDate, event.amount)))
                } else {
                    next(state.copy(viewState = InstallmentAddViewState.InvalidAmount))
                }
            }
            InstallmentAddEvent.OtpFocusChanged -> {
                next(InstallmentAddUSF.OtpFocusChanged)
            }
            is InstallmentAddEvent.OtpChanged -> {
                if (dataValidator.isValidOtp(event.otp)) {
                    next(state.copy(otp = event.otp, viewState = InstallmentAddViewState.VerifyOtp))
                } else {
                    next(state.copy(viewState = InstallmentAddViewState.InvalidOtp))
                }
            }
            InstallmentAddEvent.RequestOtpClick -> {
                if (state.mandate == null || state.dueDate == null) {
                    noChange()
                } else {
                    val header = ResourceManager.getInstance().getString(R.string.rp_installment_creation_request_otp_in_progress)
                    val message = ResourceManager.getInstance().getString(R.string.rp_installment_creation_request_otp_in_progress_detail)
                    next(InstallmentAddASF.RequestOtp(AmountUtils.stringToDouble(state.amount), state.dueDate, state.mandate.id), InstallmentAddUSF.ShowInProgress(header, message))
                }
            }
            InstallmentAddEvent.RequestOtpSuccess -> {
                next(state.copy(viewState = InstallmentAddViewState.ReadOrEnterOtp), InstallmentAddUSF.StartSmsListener(state.otpTimeout, state.interval))
            }
            is InstallmentAddEvent.RequestOtpFailed -> {
                val header = ResourceManager.getInstance().getString(R.string.rp_installment_creation_request_otp_failed)
                next(InstallmentAddUSF.ShowError(header, event.message))
            }
            InstallmentAddEvent.InstallmentCreationClick -> {
                if (state.mandate == null || state.dueDate == null) {
                    noChange()
                } else {
                    val header = ResourceManager.getInstance().getString(R.string.rp_installment_creation_in_progress)
                    val message = ResourceManager.getInstance().getString(R.string.rp_installment_creation_in_progress_detail)
                    next(InstallmentAddASF.CreateInstallment(AmountUtils.stringToDouble(state.amount), state.dueDate, state.otp, state.mandate.id), InstallmentAddUSF.ShowInProgress(header, message))
                }
            }
            is InstallmentAddEvent.InstallmentCreationSuccess -> {
                next(InstallmentAddUSF.CloseClick)
            }
            is InstallmentAddEvent.InstallmentCreationFailed -> {
                val header = ResourceManager.getInstance().getString(R.string.rp_installment_creation_failed)
                next(InstallmentAddUSF.ShowError(header, event.message))
            }
            is InstallmentAddEvent.EditClick -> {
                next(state.copy(viewState = InstallmentAddViewState.RequestOtp))
            }
            is InstallmentAddEvent.UpdateTimeLeft -> {
                next(state.copy(timeLeftToResendOtp = event.time))
            }
            InstallmentAddEvent.OtpTimeout -> {
                next(state.copy(viewState = InstallmentAddViewState.UnableToReadOtp))
            }
            InstallmentAddEvent.ResendOtp -> {
                if (state.mandate == null || state.dueDate == null) {
                    noChange()
                } else {
                    val header = ResourceManager.getInstance().getString(R.string.rp_installment_creation_request_otp_in_progress)
                    val message = ResourceManager.getInstance().getString(R.string.rp_installment_creation_request_otp_in_progress_detail)
                    next(InstallmentAddASF.RequestOtp(AmountUtils.stringToDouble(state.amount), state.dueDate, state.mandate.id), InstallmentAddUSF.ShowInProgress(header, message))
                }
            }
            is InstallmentAddEvent.ActionButtonClick -> {
                next(InstallmentAddUSF.CloseProgressDialog)
            }
            InstallmentAddEvent.DueDateClick -> {
                next(InstallmentAddUSF.OpenStartDateSelection(state.dueDate))
            }
            is InstallmentAddEvent.DueDateSelected -> {
                next(state.copy(dueDate = event.dueDate, viewState = getViewState(state, event.dueDate, state.amount)))
            }
        }
    }

    private fun getViewState(state: InstallmentAddState, dueDate: Long?, amount: String): InstallmentAddViewState {
        return if (dueDate != null && mandateUseCase.isValidAmount(amount)) {
            InstallmentAddViewState.RequestOtp
        } else {
            state.viewState
        }
    }

    override suspend fun handleAsyncSideEffect(
        sideEffect: InstallmentAddASF,
        dispatchEvent: (InstallmentAddEvent) -> Unit,
        viewModelScope: CoroutineScope
    ) {
        when (sideEffect) {
            is InstallmentAddASF.LoadData -> {
                mandateUseCase.getMandate(sideEffect.mandateId).collectIn(viewModelScope) {
                    dispatchEvent(InstallmentAddEvent.DataLoaded(mandate = it))
                }
            }
            is InstallmentAddASF.RequestOtp -> {
                when(val outcome = installmentUseCase.requestOtp(sideEffect.amount, sideEffect.dueDate, sideEffect.mandateId)) {
                    is Outcome.Error -> dispatchEvent(InstallmentAddEvent.RequestOtpFailed(outcome.error.message.orEmpty()))
                    is Outcome.Success -> dispatchEvent(InstallmentAddEvent.RequestOtpSuccess)
                }
            }
            is InstallmentAddASF.CreateInstallment -> {
                when(val outcome = installmentUseCase.createInstallment(sideEffect.amount, sideEffect.dueDate, sideEffect.otp, sideEffect.mandateId)) {
                    is Outcome.Error -> dispatchEvent(InstallmentAddEvent.InstallmentCreationFailed(outcome.error.message.orEmpty()))
                    is Outcome.Success -> dispatchEvent(InstallmentAddEvent.InstallmentCreationSuccess)
                }
            }
        }
    }
}

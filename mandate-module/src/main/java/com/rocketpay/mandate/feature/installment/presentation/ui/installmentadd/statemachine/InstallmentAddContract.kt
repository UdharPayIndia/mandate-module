package com.rocketpay.mandate.feature.installment.presentation.ui.installmentadd.statemachine

import com.rocketpay.mandate.feature.mandate.domain.entities.Mandate
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseEvent
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseState
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.Screen
import com.rocketpay.mandate.common.mvistatemachine.contract.AsyncSideEffect
import com.rocketpay.mandate.common.mvistatemachine.contract.UiSideEffect

internal data class InstallmentAddState(
    val mandateId: String = "",
    val mandate: Mandate? = null,
    val dueDate: Long? = null,
    val amount: String = "",
    val otp: String = "",
    val appSignature: String = "",
    val viewState: InstallmentAddViewState = InstallmentAddViewState.EnterAmount,
    val otpTimeout: Long = 1000L * 30,
    val interval: Long = 1000L,
    val timeLeftToResendOtp: Long = 0L
) : BaseState(InstallmentAddScreen)


sealed class InstallmentAddViewState {
    object EnterAmount : InstallmentAddViewState()
    object InvalidAmount : InstallmentAddViewState()
    object RequestOtp : InstallmentAddViewState()
    object ReadOrEnterOtp : InstallmentAddViewState()
    object InvalidOtp : InstallmentAddViewState()
    object UnableToReadOtp: InstallmentAddViewState()
    object VerifyOtp: InstallmentAddViewState()
}


internal sealed class InstallmentAddEvent(name: String? = null) : BaseEvent(name) {
    data class LoadData(val mandateId: String?): InstallmentAddEvent()
    data class DataLoaded(val mandate: Mandate?): InstallmentAddEvent()

    object AmountFocusChanged: InstallmentAddEvent()
    data class AmountChanged(val amount: String) : InstallmentAddEvent()

    object OtpFocusChanged: InstallmentAddEvent()
    data class OtpChanged(val otp: String) : InstallmentAddEvent()

    object RequestOtpClick: InstallmentAddEvent("installment_request_otp_click")
    object RequestOtpSuccess: InstallmentAddEvent("installment_request_otp_success")
    data class RequestOtpFailed(val message: String): InstallmentAddEvent("installment_request_otp_failed")

    object InstallmentCreationClick: InstallmentAddEvent("installment_create_click")
    object InstallmentCreationSuccess: InstallmentAddEvent("installment_created")
    data class InstallmentCreationFailed(val message: String): InstallmentAddEvent("installment_creation_failed")

    object EditClick: InstallmentAddEvent("installment_edit_click")
    data class UpdateTimeLeft(val time: Long): InstallmentAddEvent()
    object OtpTimeout : InstallmentAddEvent()
    object ResendOtp: InstallmentAddEvent("installment_resend_otp_click")
    object ActionButtonClick : InstallmentAddEvent()

    object DueDateClick : InstallmentAddEvent("installment_due_date_click")
    data class DueDateSelected(val dueDate: Long?) : InstallmentAddEvent()
}


sealed class InstallmentAddASF : AsyncSideEffect {
    data class LoadData(val mandateId: String): InstallmentAddASF()
    data class RequestOtp(val amount: Double, val dueDate: Long, val mandateId: String) : InstallmentAddASF()
    data class CreateInstallment(val amount: Double, val dueDate: Long, val otp: String, val mandateId: String) : InstallmentAddASF()
}


sealed class InstallmentAddUSF : UiSideEffect {
    data class StartSmsListener(val otpTimeout: Long, val interval: Long) : InstallmentAddUSF()
    object CloseClick : InstallmentAddUSF()
    data class ShowError(val header: String, val message: String) : InstallmentAddUSF()
    data class ShowInProgress(val header: String, val message: String) : InstallmentAddUSF()
    object CloseProgressDialog: InstallmentAddUSF()
    object AmountFocusChanged: InstallmentAddUSF()
    object OtpFocusChanged: InstallmentAddUSF()
    data class OpenStartDateSelection(val currentDueDate: Long?) : InstallmentAddUSF()
}

internal object InstallmentAddScreen : Screen("installment_create")

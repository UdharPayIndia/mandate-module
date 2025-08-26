package com.rocketpay.mandate.feature.installment.presentation.ui.installmentupdate.statemachine

import com.rocketpay.mandate.feature.mandate.domain.entities.PaymentMode
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseEvent
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseState
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.Screen
import com.rocketpay.mandate.common.mvistatemachine.contract.AsyncSideEffect
import com.rocketpay.mandate.common.mvistatemachine.contract.UiSideEffect

internal data class InstallmentUpdateState(
    val installmentId: String? = null,
    val mode: PaymentMode? = null,
    var reason: String? = null,
) : BaseState(InstallmentUpdateScreen)


internal sealed class InstallmentUpdateEvent(name: String? = null) : BaseEvent(name) {
    data class InitData(
        val installmentId: String?,
        val mode: String? = null,
        val reason: String? = null
    ): InstallmentUpdateEvent()
    data class UpdatePaymentMode(
        val paymentMode: PaymentMode
    ): InstallmentUpdateEvent()
    object SaveClick: InstallmentUpdateEvent("mark_paid_save_click")
    object SaveSuccess: InstallmentUpdateEvent()
    data class SaveFailed(
        val error: String,
        val message: String
    ): InstallmentUpdateEvent()
    object CloseProgressDialog: InstallmentUpdateEvent()
    object CloseClick: InstallmentUpdateEvent()
    data class ReasonChanged(val comment: String): InstallmentUpdateEvent()
}


internal sealed class InstallmentUpdateASF : AsyncSideEffect {
    data class MarkAsPaid(
        val installmentId: String,
        val mode: PaymentMode,
        val comment: String?
    ): InstallmentUpdateASF()
}


internal sealed class InstallmentUpdateUSF : UiSideEffect {
    data class ShowToast(
        val message: String
    ): InstallmentUpdateUSF()
    data class ShowProgressDialog(
        val header: String,
        val message: String
    ) : InstallmentUpdateUSF()
    object CancelProgressDialog: InstallmentUpdateUSF()
    object CloseScreen: InstallmentUpdateUSF()
}

internal object InstallmentUpdateScreen : Screen("installment_update")

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
    val viewState: InstallmentAddViewState = InstallmentAddViewState.EnterAmount,
) : BaseState(InstallmentAddScreen)


sealed class InstallmentAddViewState {
    object EnterAmount : InstallmentAddViewState()
    object InvalidAmount : InstallmentAddViewState()
    object VerifyAmount : InstallmentAddViewState()
}


internal sealed class InstallmentAddEvent(name: String? = null) : BaseEvent(name) {
    data class LoadData(val mandateId: String?): InstallmentAddEvent()
    data class DataLoaded(val mandate: Mandate?): InstallmentAddEvent()

    object AmountFocusChanged: InstallmentAddEvent()
    data class AmountChanged(val amount: String) : InstallmentAddEvent()

    object InstallmentCreationClick: InstallmentAddEvent("installment_create_click")
    object InstallmentCreationSuccess: InstallmentAddEvent("installment_created")
    data class InstallmentCreationFailed(val message: String): InstallmentAddEvent("installment_creation_failed")

    object ActionButtonClick : InstallmentAddEvent()
    object DueDateClick : InstallmentAddEvent("installment_due_date_click")
    data class DueDateSelected(val dueDate: Long?) : InstallmentAddEvent()
}


sealed class InstallmentAddASF : AsyncSideEffect {
    data class LoadData(val mandateId: String): InstallmentAddASF()
    data class CreateInstallment(val amount: Double, val dueDate: Long, val mandateId: String) : InstallmentAddASF()
}


sealed class InstallmentAddUSF : UiSideEffect {
    object CloseClick : InstallmentAddUSF()
    data class ShowError(val header: String, val message: String) : InstallmentAddUSF()
    data class ShowInProgress(val header: String, val message: String) : InstallmentAddUSF()
    object CloseProgressDialog: InstallmentAddUSF()
    object AmountFocusChanged: InstallmentAddUSF()
    data class OpenStartDateSelection(val currentDueDate: Long?) : InstallmentAddUSF()
}

internal object InstallmentAddScreen : Screen("installment_create")

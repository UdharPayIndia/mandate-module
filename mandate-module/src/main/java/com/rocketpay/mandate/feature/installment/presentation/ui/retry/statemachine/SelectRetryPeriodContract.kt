package com.rocketpay.mandate.feature.installment.presentation.ui.retry.statemachine

import android.graphics.drawable.Drawable
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseEvent
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseState
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.Screen
import com.rocketpay.mandate.common.mvistatemachine.contract.AsyncSideEffect
import com.rocketpay.mandate.common.mvistatemachine.contract.UiSideEffect

internal data class SelectRetryPeriodState(
    val mandateId: String = "",
    val installmentId: String = "",
    val retryOption: Int = -1,
    val retryDate: Long = 0,
    var retryDateError: String? = "",
    val isEnabled: Boolean = false
) : BaseState(SelectRetryPeriodScreen)

internal sealed class SelectRetryPeriodEvent(name: String? = null) : BaseEvent(name) {
    data class Init(
        val mandateId: String,
        val installmentId: String
    ): SelectRetryPeriodEvent()
    data object SelectImmediateDate: SelectRetryPeriodEvent()
    data object SelectOtherDate: SelectRetryPeriodEvent()
    data class RetryDateSelected(val startDate: Long?) : SelectRetryPeriodEvent()
    data object SubmitRetry: SelectRetryPeriodEvent()
    data object RetryConfirmed: SelectRetryPeriodEvent("installment_retry_confirm_click")
    data object RetryDismiss: SelectRetryPeriodEvent()
    data object CloseProgressDialog: SelectRetryPeriodEvent()
    data class RetryInstallmentFailed(
        val errorCode: String,
        val errorMessage: String
    ): SelectRetryPeriodEvent()
    data object RetryInstallmentSucceed: SelectRetryPeriodEvent("installment_retried")
}


internal sealed class SelectRetryPeriodASF : AsyncSideEffect {
    data class RetryInstallment(
        val mandateId: String,
        val installmentId: String,
        val retryDate: Long
    ): SelectRetryPeriodASF()
}


internal sealed class SelectRetryPeriodUSF : UiSideEffect {
    data class ShowRetryConfirmation(
        val headerDrawable: Drawable,
        val headerBackground: Drawable,
        val title: String,
        val detail: String,
        val actionText: String,
        val secondaryBtnText: String
    ): SelectRetryPeriodUSF()
    data class ShowProgressDialog(
        val title: String,
        val detail: String
    ): SelectRetryPeriodUSF()
    data class ShowErrorDialog(
        val title: String,
        val detail: String
    ): SelectRetryPeriodUSF()
    data object DismissProgressDialog: SelectRetryPeriodUSF()
    data object DismissConfirmDialog: SelectRetryPeriodUSF()
    data object CloseScreen: SelectRetryPeriodUSF()
    data class OpenRetryDateSelection(val currentSelectedDate: Long?): SelectRetryPeriodUSF()
}

internal object SelectRetryPeriodScreen : Screen("select_retry_time")

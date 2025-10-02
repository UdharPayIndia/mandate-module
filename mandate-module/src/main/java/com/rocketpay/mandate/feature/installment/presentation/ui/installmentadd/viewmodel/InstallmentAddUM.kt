package com.rocketpay.mandate.feature.installment.presentation.ui.installmentadd.viewmodel

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialogVM
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DateUtils
import com.rocketpay.mandate.common.basemodule.main.viewmodel.BaseMainUM
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentadd.statemachine.InstallmentAddEvent
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentadd.statemachine.InstallmentAddState
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentadd.statemachine.InstallmentAddViewState

internal class InstallmentAddUM(private val dispatchEvent: (InstallmentAddEvent) -> Unit) : BaseMainUM() {

    val progressDialogVM = ProgressDialogVM({
        dispatchEvent(InstallmentAddEvent.ActionButtonClick)
    })

    val amount = ObservableField<String>()
    val amountErrorMessage = ObservableField<String>()

    val submitOrVerify = ObservableField<String>()
    val submitOrVerifyEnable = ObservableBoolean()

    val dueDate = ObservableField<String>()

    fun updateMobileNumber(mobileNumber: CharSequence) {
        dispatchEvent(InstallmentAddEvent.AmountChanged(mobileNumber.toString()))
    }

    fun onMobileNumberFocusChanged(hasFocus: Boolean) {
        if (hasFocus) {
            dispatchEvent(InstallmentAddEvent.AmountFocusChanged)
        }
    }

    fun submitOrVerifyOtpClick() {
        dispatchEvent(InstallmentAddEvent.InstallmentCreationClick)
    }

    fun onStartDateClick() {
        dispatchEvent(InstallmentAddEvent.DueDateClick)
    }

    fun handleState(state: InstallmentAddState) {
        if (state.dueDate == null) {
            dueDate.set(ResourceManager.getInstance().getString(R.string.rp_due_date))
        } else {
            dueDate.set(DateUtils.getDate(state.dueDate, DateUtils.SLASH_DATE_FORMAT_WITH_TWO_DIGIT_YEAR))
        }

        when(state.viewState) {
            InstallmentAddViewState.EnterAmount -> {
                amountErrorMessage.set(null)
                submitOrVerify.set(ResourceManager.getInstance().getString(R.string.rp_create_installment_verify_otp))
                submitOrVerifyEnable.set(false)
            }
            InstallmentAddViewState.InvalidAmount -> {
                amountErrorMessage.set(ResourceManager.getInstance().getString(R.string.rp_error_mobile_number))
                submitOrVerify.set(ResourceManager.getInstance().getString(R.string.rp_create_installment_verify_otp))
                submitOrVerifyEnable.set(false)
            }
            InstallmentAddViewState.VerifyAmount -> {
                amountErrorMessage.set(null)
                submitOrVerify.set(ResourceManager.getInstance().getString(R.string.rp_create_installment_verify_otp))
                submitOrVerifyEnable.set(true)
            }
        }
    }
}

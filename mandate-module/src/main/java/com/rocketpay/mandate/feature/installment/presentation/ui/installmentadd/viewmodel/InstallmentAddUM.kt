package com.rocketpay.mandate.feature.installment.presentation.ui.installmentadd.viewmodel

import android.text.SpannableString
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentadd.statemachine.InstallmentAddEvent
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentadd.statemachine.InstallmentAddState
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentadd.statemachine.InstallmentAddViewState
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.getSpannable
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.setTextColor
import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialogVM
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DateUtils
import com.rocketpay.mandate.common.basemodule.main.viewmodel.BaseMainUM
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal class InstallmentAddUM(private val dispatchEvent: (InstallmentAddEvent) -> Unit) : BaseMainUM() {

    val progressDialogVM = ProgressDialogVM({
        dispatchEvent(InstallmentAddEvent.ActionButtonClick)
    })

    val amountVisibility = ObservableInt()
    val amount = ObservableField<String>()

    val amountErrorMessage = ObservableField<String>()

    val otp = ObservableField("")
    val resendMessage = ObservableField<String>()
    val resendActionLayoutVisibility = ObservableInt()
    val resendButtonVisibility = ObservableInt()
    val resendTimeLeft = ObservableField<String>()
    val resendMessageColor = ObservableInt()

    val editMessage = ObservableField<SpannableString>()

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

    fun updateOtp(otp: CharSequence) {
        dispatchEvent(InstallmentAddEvent.OtpChanged(otp.toString()))
    }

    fun onOtpFocusChanged(hasFocus: Boolean) {
        if (hasFocus) {
            dispatchEvent(InstallmentAddEvent.OtpFocusChanged)
        }
    }

    fun submitOrVerifyOtpClick() {
        if (amountVisibility.get() == View.VISIBLE) {
            dispatchEvent(InstallmentAddEvent.RequestOtpClick)
        } else {
            dispatchEvent(InstallmentAddEvent.InstallmentCreationClick)
        }
    }

    fun editClick() {
        dispatchEvent(InstallmentAddEvent.EditClick)
    }

    fun resendOtpClick() {
        dispatchEvent(InstallmentAddEvent.ResendOtp)
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
                amountVisibility.set(View.VISIBLE)
                amountErrorMessage.set(null)

                submitOrVerify.set(ResourceManager.getInstance().getString(R.string.rp_create_installment_get_otp))
                submitOrVerifyEnable.set(false)
            }
            InstallmentAddViewState.InvalidAmount -> {
                amountVisibility.set(View.VISIBLE)
                amountErrorMessage.set(ResourceManager.getInstance().getString(R.string.rp_error_mobile_number))

                submitOrVerify.set(ResourceManager.getInstance().getString(R.string.rp_create_installment_get_otp))
                submitOrVerifyEnable.set(false)
            }
            InstallmentAddViewState.RequestOtp -> {
                amountVisibility.set(View.VISIBLE)
                amountErrorMessage.set(null)

                submitOrVerify.set(ResourceManager.getInstance().getString(R.string.rp_create_installment_get_otp))
                submitOrVerifyEnable.set(true)
            }
            InstallmentAddViewState.ReadOrEnterOtp -> {
                amountVisibility.set(View.GONE)

                resendMessageColor.set(ResourceManager.getInstance().getColor(R.color.rp_grey_3))
                resendMessage.set(ResourceManager.getInstance().getString(R.string.rp_resend_otp_after))
                resendActionLayoutVisibility.set(View.VISIBLE)
                resendButtonVisibility.set(View.GONE)
                val timeLeft = state.timeLeftToResendOtp / 1000
                val formattedTimeLeft = ResourceManager.getInstance().getString(R.string.rp_otp_time_left, timeLeft)
                resendTimeLeft.set(formattedTimeLeft)

                val mobileNumber = state.mandate?.customerDetail?.mobileNumber ?: ""
                val message = ResourceManager.getInstance().getString(R.string.rp_otp_send_on, mobileNumber)
                    .getSpannable()
                    .setTextColor(mobileNumber, ResourceManager.getInstance().getColor(R.color.rp_blue_2))
                editMessage.set(message)

                submitOrVerify.set(ResourceManager.getInstance().getString(R.string.rp_create_installment_verify_otp))
                submitOrVerifyEnable.set(false)
            }
            InstallmentAddViewState.InvalidOtp -> {
                amountVisibility.set(View.GONE)

                resendMessageColor.set(ResourceManager.getInstance().getColor(R.color.rp_red_2))
                resendMessage.set(ResourceManager.getInstance().getString(R.string.rp_error_otp))
                resendActionLayoutVisibility.set(View.GONE)

                submitOrVerify.set(ResourceManager.getInstance().getString(R.string.rp_create_installment_verify_otp))
                submitOrVerifyEnable.set(false)
            }
            InstallmentAddViewState.UnableToReadOtp -> {
                amountVisibility.set(View.GONE)

                resendMessageColor.set(ResourceManager.getInstance().getColor(R.color.rp_grey_3))
                resendMessage.set(ResourceManager.getInstance().getString(R.string.rp_did_not_get_otp))
                resendActionLayoutVisibility.set(View.VISIBLE)
                resendButtonVisibility.set(View.VISIBLE)

                submitOrVerify.set(ResourceManager.getInstance().getString(R.string.rp_create_installment_verify_otp))
                submitOrVerifyEnable.set(false)
            }
            InstallmentAddViewState.VerifyOtp -> {
                amountVisibility.set(View.GONE)

                resendMessageColor.set(ResourceManager.getInstance().getColor(R.color.rp_grey_3))
                resendMessage.set(ResourceManager.getInstance().getString(R.string.rp_did_not_get_otp))
                resendActionLayoutVisibility.set(View.VISIBLE)
                resendButtonVisibility.set(View.VISIBLE)

                submitOrVerify.set(ResourceManager.getInstance().getString(R.string.rp_create_installment_verify_otp))
                submitOrVerifyEnable.set(true)
            }
        }
    }
}

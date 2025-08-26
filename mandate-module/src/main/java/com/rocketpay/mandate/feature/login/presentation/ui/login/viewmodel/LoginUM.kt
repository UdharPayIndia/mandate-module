package com.rocketpay.mandate.feature.login.presentation.ui.login.viewmodel

import android.text.SpannableString
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.login.presentation.ui.login.statemachine.LoginEvent
import com.rocketpay.mandate.feature.login.presentation.ui.login.statemachine.LoginState
import com.rocketpay.mandate.feature.login.presentation.ui.login.statemachine.LoginViewState
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.getSpannable
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.setTextColor
import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialogVM
import com.rocketpay.mandate.common.basemodule.main.viewmodel.BaseMainUM
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal class LoginUM(private val dispatchEvent: (LoginEvent) -> Unit) : BaseMainUM() {

    val progressDialogVM = ProgressDialogVM ({
        dispatchEvent(LoginEvent.ActionButtonClick)
    })

    val mobileNumberVisibility = ObservableInt()
    val countryCode = ObservableField<String>()
    val mobileNumber = ObservableField<String>()

    val mobileErrorMessage = ObservableField<String>()

    val otp = ObservableField("")
    val resendMessage = ObservableField<String>()
    val resendActionLayoutVisibility = ObservableInt()
    val resendButtonVisibility = ObservableInt()
    val resendTimeLeft = ObservableField<String>()
    val resendMessageColor = ObservableInt()

    val editMessage = ObservableField<SpannableString>()

    val submitOrVerify = ObservableField<String>()
    val submitOrVerifyEnable = ObservableBoolean()

    fun updateMobileNumber(mobileNumber: CharSequence) {
        dispatchEvent(LoginEvent.MobileNumberChanged(mobileNumber.toString()))
    }

    fun onMobileNumberFocusChanged(hasFocus: Boolean) {
        if (hasFocus) {
            dispatchEvent(LoginEvent.MobileNumberFocusChanged)
        }
    }

    fun updateOtp(otp: CharSequence) {
        dispatchEvent(LoginEvent.OtpChanged(otp.toString()))
    }

    fun onOtpFocusChanged(hasFocus: Boolean) {
        if (hasFocus) {
            dispatchEvent(LoginEvent.OtpFocusChanged)
        }
    }

    fun submitOrVerifyOtpClick() {
        if (mobileNumberVisibility.get() == View.VISIBLE) {
            dispatchEvent(LoginEvent.RequestOtpClick)
        } else {
            dispatchEvent(LoginEvent.VerifyOtpClick)
        }
    }

    fun editClick() {
        dispatchEvent(LoginEvent.EditClick)
    }

    fun resendOtpClick() {
        dispatchEvent(LoginEvent.ResendOtp)
    }

    fun handleState(state: LoginState) {
        countryCode.set(state.countryCode)
        mobileNumber.set(state.mobileNumber)

        when(state.viewState) {
            LoginViewState.EnterMobileNumber -> {
                mobileNumberVisibility.set(View.VISIBLE)
                mobileErrorMessage.set(null)

                submitOrVerify.set(ResourceManager.getInstance().getString(R.string.rp_get_otp))
                submitOrVerifyEnable.set(false)
            }
            LoginViewState.InvalidMobileNumber -> {
                mobileNumberVisibility.set(View.VISIBLE)
                mobileErrorMessage.set(ResourceManager.getInstance().getString(R.string.rp_error_mobile_number))

                submitOrVerify.set(ResourceManager.getInstance().getString(R.string.rp_get_otp))
                submitOrVerifyEnable.set(false)
            }
            LoginViewState.RequestOtp -> {
                mobileNumberVisibility.set(View.VISIBLE)
                mobileErrorMessage.set(null)

                submitOrVerify.set(ResourceManager.getInstance().getString(R.string.rp_get_otp))
                submitOrVerifyEnable.set(true)
            }
            LoginViewState.ReadOrEnterOtp -> {
                mobileNumberVisibility.set(View.GONE)

                resendMessageColor.set(ResourceManager.getInstance().getColor(R.color.rp_grey_3))
                resendMessage.set(ResourceManager.getInstance().getString(R.string.rp_resend_otp_after))
                resendActionLayoutVisibility.set(View.VISIBLE)
                resendButtonVisibility.set(View.GONE)
                val timeLeft = state.timeLeftToResendOtp / 1000
                val formattedTimeLeft = ResourceManager.getInstance().getString(R.string.rp_otp_time_left, timeLeft)
                resendTimeLeft.set(formattedTimeLeft)

                val mobileNumber = "${state.countryCode}${state.mobileNumber}"
                val message = ResourceManager.getInstance().getString(R.string.rp_otp_send_on, mobileNumber)
                    .getSpannable()
                    .setTextColor(mobileNumber, ResourceManager.getInstance().getColor(R.color.rp_blue_2))
                editMessage.set(message)

                submitOrVerify.set(ResourceManager.getInstance().getString(R.string.rp_verify_otp))
                submitOrVerifyEnable.set(false)
            }
            LoginViewState.InvalidOtp -> {
                mobileNumberVisibility.set(View.GONE)

                resendMessageColor.set(ResourceManager.getInstance().getColor(R.color.rp_red_2))
                resendMessage.set(ResourceManager.getInstance().getString(R.string.rp_error_otp))
                resendActionLayoutVisibility.set(View.GONE)

                submitOrVerify.set(ResourceManager.getInstance().getString(R.string.rp_verify_otp))
                submitOrVerifyEnable.set(false)
            }
            LoginViewState.UnableToReadOtp -> {
                mobileNumberVisibility.set(View.GONE)

                resendMessageColor.set(ResourceManager.getInstance().getColor(R.color.rp_grey_3))
                resendMessage.set(ResourceManager.getInstance().getString(R.string.rp_did_not_get_otp))
                resendActionLayoutVisibility.set(View.VISIBLE)
                resendButtonVisibility.set(View.VISIBLE)

                submitOrVerify.set(ResourceManager.getInstance().getString(R.string.rp_verify_otp))
                submitOrVerifyEnable.set(false)
            }
            LoginViewState.VerifyOtp -> {
                mobileNumberVisibility.set(View.GONE)

                resendMessageColor.set(ResourceManager.getInstance().getColor(R.color.rp_grey_3))
                resendMessage.set(ResourceManager.getInstance().getString(R.string.rp_did_not_get_otp))
                resendActionLayoutVisibility.set(View.VISIBLE)
                resendButtonVisibility.set(View.VISIBLE)

                submitOrVerify.set(ResourceManager.getInstance().getString(R.string.rp_verify_otp))
                submitOrVerifyEnable.set(true)
            }
        }
    }
}

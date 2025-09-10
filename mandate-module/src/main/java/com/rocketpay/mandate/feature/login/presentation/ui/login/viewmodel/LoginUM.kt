package com.rocketpay.mandate.feature.login.presentation.ui.login.viewmodel

import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialogVM
import com.rocketpay.mandate.common.basemodule.main.viewmodel.BaseMainUM
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import com.rocketpay.mandate.feature.login.presentation.ui.login.statemachine.LoginEvent
import com.rocketpay.mandate.feature.login.presentation.ui.login.statemachine.LoginState
import com.rocketpay.mandate.feature.login.presentation.ui.login.statemachine.LoginViewState

internal class LoginUM(private val dispatchEvent: (LoginEvent) -> Unit) : BaseMainUM() {

    val progressDialogVM = ProgressDialogVM ({
        dispatchEvent(LoginEvent.ActionButtonClick)
    })

    val mobileNumberVisibility = ObservableInt()
    val countryCode = ObservableField<String>()
    val mobileNumber = ObservableField<String>()

    val mobileErrorMessage = ObservableField<String>()

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

    fun submitOrVerifyOtpClick() {
        dispatchEvent(LoginEvent.VerifyUserClick)
    }

    fun handleState(state: LoginState) {
        countryCode.set(state.countryCode)
        mobileNumber.set(state.mobileNumber)

        when(state.viewState) {
            LoginViewState.EnterMobileNumber -> {
                mobileNumberVisibility.set(View.VISIBLE)
                mobileErrorMessage.set(null)

                submitOrVerify.set(ResourceManager.getInstance().getString(R.string.rp_verify_user))
                submitOrVerifyEnable.set(false)
            }
            LoginViewState.InvalidMobileNumber -> {
                mobileNumberVisibility.set(View.VISIBLE)
                mobileErrorMessage.set(ResourceManager.getInstance().getString(R.string.rp_error_mobile_number))

                submitOrVerify.set(ResourceManager.getInstance().getString(R.string.rp_get_otp))
                submitOrVerifyEnable.set(false)
            }
            LoginViewState.VerifyUser -> {
                mobileNumberVisibility.set(View.VISIBLE)
                mobileErrorMessage.set(null)

                submitOrVerify.set(ResourceManager.getInstance().getString(R.string.rp_verify_user))
                submitOrVerifyEnable.set(true)
            }
        }
    }
}

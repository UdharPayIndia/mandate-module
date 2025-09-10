package com.rocketpay.mandate.feature.login.presentation.ui.login.statemachine

import com.rocketpay.mandate.feature.login.domain.entities.User
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseEvent
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseState
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.Screen
import com.rocketpay.mandate.common.mvistatemachine.contract.AsyncSideEffect
import com.rocketpay.mandate.common.mvistatemachine.contract.UiSideEffect

internal data class LoginState(
    val countryCode: String = "",
    val mobileNumber: String = "",
    val otp: String = "",
    val appSignature: String = "",
    val viewState: LoginViewState = LoginViewState.EnterMobileNumber,
    val otpTimeout: Long = 1000L * 30,
    val interval: Long = 1000L,
    val timeLeftToResendOtp: Long = 0L
) : BaseState(LoginScreen)


sealed class LoginViewState {
    object EnterMobileNumber : LoginViewState()
    object InvalidMobileNumber : LoginViewState()
    object VerifyUser : LoginViewState()
}

const val EVENT_SIGNED_UP = "login_success"

internal sealed class LoginEvent(name: String? = null) : BaseEvent(name) {
    object MobileNumberFocusChanged: LoginEvent()
    data class CountryCodeChanged(val countryCode: String) : LoginEvent()
    data class MobileNumberChanged(val mobileNumber: String) : LoginEvent()
    object VerifyUserClick: LoginEvent("verify_otp_click")
    data class VerifyUserFailed(val message: String): LoginEvent("otp_verification_failed")
    data class VerifyUserSuccess(val user: User, val isKyced: Boolean): LoginEvent(EVENT_SIGNED_UP)

    object ActionButtonClick : LoginEvent()

    object Init: LoginEvent()

    data class PhoneHintReceived(val mobileNumber: String): LoginEvent("phone_hint_received")
    data class CheckKyc(val user: User) : LoginEvent()
}


internal sealed class LoginASF : AsyncSideEffect {
    object LoadCountryCode: LoginASF()
    data class ValidateUser(val countryCode: String, val mobileNumber: String, val otp: String) : LoginASF()
    data class CheckKyc(val user: User): LoginASF()
}


internal sealed class LoginUSF : UiSideEffect {
    data class GotoHome(val isKyced: Boolean) : LoginUSF()
    data class ShowError(val header: String, val message: String) : LoginUSF()
    data class ShowInProgress(val header: String, val message: String) : LoginUSF()
    object CloseProgressDialog: LoginUSF()
    object MobileNumberFocusChanged: LoginUSF()
    object RequestPhoneHint: LoginUSF()
}

internal object LoginScreen : Screen("login")

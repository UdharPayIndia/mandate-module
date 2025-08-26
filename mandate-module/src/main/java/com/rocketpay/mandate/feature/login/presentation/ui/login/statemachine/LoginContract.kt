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
    object RequestOtp : LoginViewState()
    object ReadOrEnterOtp : LoginViewState()
    object InvalidOtp : LoginViewState()
    object UnableToReadOtp: LoginViewState()
    object VerifyOtp: LoginViewState()
}

const val EVENT_SIGNED_UP = "login_success"

internal sealed class LoginEvent(name: String? = null) : BaseEvent(name) {
    object MobileNumberFocusChanged: LoginEvent()
    object OtpFocusChanged: LoginEvent()

    data class CountryCodeChanged(val countryCode: String) : LoginEvent()
    data class MobileNumberChanged(val mobileNumber: String) : LoginEvent()
    data class OtpChanged(val otp: String) : LoginEvent()
    object RequestOtpClick: LoginEvent("request_otp_click")
    object VerifyOtpClick: LoginEvent("verify_otp_click")
    object ResendOtp: LoginEvent("resend_otp_click")
    object EditClick: LoginEvent("edit_click")

    data class RequestOtpFailed(val errorCode: String, val message: String): LoginEvent("request_otp_failed")
    object RequestOtpSuccess: LoginEvent("request_otp_success")
    data class VerifyOtpFailed(val message: String): LoginEvent("otp_verification_failed")
    data class VerifyOtpSuccess(val user: User): LoginEvent(EVENT_SIGNED_UP)

    object OtpTimeout : LoginEvent("otp_timeout")
    data class UpdateTimeLeft(val time: Long): LoginEvent()

    object ActionButtonClick : LoginEvent()

    object Init: LoginEvent()

    data class PhoneHintReceived(val mobileNumber: String): LoginEvent("phone_hint_received")
    data class OtpHintReceived(val otp: String): LoginEvent("otp_hint_received")
    data class CheckKyc(val user: User) : LoginEvent()
}


internal sealed class LoginASF : AsyncSideEffect {
    object LoadCountryCode: LoginASF()
    data class ValidateOtp(val countryCode: String, val mobileNumber: String, val otp: String) : LoginASF()
    data class RequestOtp(val countryCode: String, val mobileNumber: String) : LoginASF()
    data class CheckKyc(val user: User): LoginASF()
}


internal sealed class LoginUSF : UiSideEffect {
    data class StartSmsListener(val otpTimeout: Long, val interval: Long) : LoginUSF()
    object GotoHome : LoginUSF()
    data class ShowError(val header: String, val message: String) : LoginUSF()
    data class ShowInProgress(val header: String, val message: String) : LoginUSF()
    object CloseProgressDialog: LoginUSF()
    object MobileNumberFocusChanged: LoginUSF()
    object OtpFocusChanged: LoginUSF()
    object RequestPhoneHint: LoginUSF()
}

internal object LoginScreen : Screen("login")

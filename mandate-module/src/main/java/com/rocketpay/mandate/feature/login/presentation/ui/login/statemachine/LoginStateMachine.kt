package com.rocketpay.mandate.feature.login.presentation.ui.login.statemachine

import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.login.data.entities.CreateDeviceRequest
import com.rocketpay.mandate.feature.login.domain.usecase.LoginUseCase
import com.rocketpay.mandate.main.init.MandateManager
import com.udharpay.core.networkmanager.domain.entities.Outcome
import com.rocketpay.mandate.common.basemodule.common.data.network.NetworkUtils
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseAnalyticsHandler
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.AdvertisementUtils
import com.rocketpay.mandate.common.mvistatemachine.contract.Next
import com.rocketpay.mandate.common.mvistatemachine.viewmodel.simple.SimpleStateMachineImpl
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import com.rocketpay.mandate.feature.kyc.domain.usecase.KycUseCase
import com.rocketpay.mandate.feature.property.domain.usecase.PropertyUseCase
import com.rocketpay.mandate.main.GlobalState
import kotlinx.coroutines.CoroutineScope

internal class LoginStateMachine(
    private val loginUseCase: LoginUseCase,
    private val kycUseCase: KycUseCase,
    private val propertyUseCase: PropertyUseCase
): SimpleStateMachineImpl<LoginEvent, LoginState, LoginASF, LoginUSF>(BaseAnalyticsHandler()) {

    override fun startState(): LoginState {
        return LoginState()
    }

    override fun handleEvent(
        event: LoginEvent,
        state: LoginState
    ): Next<LoginState?, LoginASF?, LoginUSF?> {
        return when (event) {
            is LoginEvent.Init -> {
                val mobileNumber = MandateManager.getInstance().getLoginMobileNumber()
                if(!mobileNumber.isNullOrEmpty()){
                    val countryCode = loginUseCase.getCountryCode()
                    val header = ResourceManager.getInstance().getString(R.string.rp_request_otp_in_progress)
                    val message = ResourceManager.getInstance().getString(R.string.rp_request_otp_in_progress_detail)
                    if(NetworkUtils.isNetworkAvailable()){
                        next(state.copy(mobileNumber = mobileNumber,
                            countryCode = countryCode),
                            LoginASF.RequestOtp(countryCode, mobileNumber),
                            LoginUSF.ShowInProgress(header, message))
                    }else{
                        next(state.copy(mobileNumber = mobileNumber,
                            countryCode = loginUseCase.getCountryCode()),
                            LoginUSF.ShowError(ResourceManager.getInstance().getString(R.string.rp_otp_request_failed),
                            ResourceManager.getInstance().getString(R.string.rp_no_internet_connectivity_please_connect_to_internet)))
                    }
                }else{
                    next(LoginASF.LoadCountryCode, LoginUSF.RequestPhoneHint)
                }
            }
            is LoginEvent.CountryCodeChanged -> {
                if (loginUseCase.validateCountryCode(event.countryCode)) {
                    next(state.copy(countryCode = event.countryCode))
                } else {
                    next(state.copy(countryCode = event.countryCode))
                }
            }
            is LoginEvent.MobileNumberChanged -> {
                if (loginUseCase.validateMobileNumber(event.mobileNumber)) {
                    next(state.copy(mobileNumber = event.mobileNumber, viewState = LoginViewState.RequestOtp))
                } else {
                    next(state.copy(viewState = LoginViewState.InvalidMobileNumber))
                }
            }
            LoginEvent.MobileNumberFocusChanged -> {
                next(LoginUSF.MobileNumberFocusChanged)
            }
            LoginEvent.OtpFocusChanged -> {
                next(LoginUSF.OtpFocusChanged)
            }
            is LoginEvent.OtpChanged -> {
                if (loginUseCase.validateOtp(event.otp)) {
                    if(NetworkUtils.isNetworkAvailable()){
                        next(state.copy(otp = event.otp, viewState = LoginViewState.VerifyOtp))
                    }else{
                        next(LoginUSF.ShowError(ResourceManager.getInstance().getString(R.string.rp_otp_verification_failed),
                            ResourceManager.getInstance().getString(R.string.rp_no_internet_connectivity_please_connect_to_internet)))
                    }
                } else {
                    next(state.copy(viewState = LoginViewState.InvalidOtp))
                }
            }
            is LoginEvent.EditClick -> {
                next(state.copy(viewState = LoginViewState.RequestOtp))
            }
            LoginEvent.VerifyOtpClick -> {
                val header = ResourceManager.getInstance().getString(R.string.rp_otp_verification_in_progress)
                val message = ResourceManager.getInstance().getString(R.string.rp_otp_verification_in_progress_detail)
                next(LoginASF.ValidateOtp(state.countryCode, state.mobileNumber, state.otp), LoginUSF.ShowInProgress(header, message))
            }
            LoginEvent.RequestOtpClick -> {
                val header = ResourceManager.getInstance().getString(R.string.rp_request_otp_in_progress)
                val message = ResourceManager.getInstance().getString(R.string.rp_request_otp_in_progress_detail)
                if(NetworkUtils.isNetworkAvailable()){
                    next(LoginASF.RequestOtp(state.countryCode, state.mobileNumber), LoginUSF.ShowInProgress(header, message))
                }else{
                    next(LoginUSF.ShowError(ResourceManager.getInstance().getString(R.string.rp_otp_request_failed),
                        ResourceManager.getInstance().getString(R.string.rp_no_internet_connectivity_please_connect_to_internet)))
                }
            }
            is LoginEvent.RequestOtpFailed -> {
                val header = ResourceManager.getInstance().getString(R.string.rp_request_otp_fail)
                next(state.copy(viewState = LoginViewState.RequestOtp), LoginUSF.ShowError(header, event.message))
            }
            LoginEvent.RequestOtpSuccess -> {
                next(state.copy(viewState = LoginViewState.ReadOrEnterOtp), LoginUSF.StartSmsListener(state.otpTimeout, state.interval))
            }
            is LoginEvent.VerifyOtpFailed -> {
                val header = ResourceManager.getInstance().getString(R.string.rp_otp_verification_failed)
                next(state.copy(viewState = LoginViewState.VerifyOtp), LoginUSF.ShowError(header, event.message))
            }
            is LoginEvent.VerifyOtpSuccess -> {
                GlobalState.isLogin.value = true
                next(LoginUSF.GotoHome)
            }
            is LoginEvent.UpdateTimeLeft -> {
                next(state.copy(timeLeftToResendOtp = event.time))
            }
            LoginEvent.OtpTimeout -> {
                next(state.copy(viewState = LoginViewState.UnableToReadOtp))
            }
            LoginEvent.ResendOtp -> {
                val header = ResourceManager.getInstance().getString(R.string.rp_request_otp_in_progress)
                val message = ResourceManager.getInstance().getString(R.string.rp_request_otp_in_progress_detail)
                if(NetworkUtils.isNetworkAvailable()){
                    next(state.copy(), LoginASF.RequestOtp(state.countryCode, state.mobileNumber), LoginUSF.ShowInProgress(header, message))
                }else{
                    next(LoginUSF.ShowError(ResourceManager.getInstance().getString(R.string.rp_otp_request_failed),
                        ResourceManager.getInstance().getString(R.string.rp_no_internet_connectivity_please_connect_to_internet)))
                }
            }
            is LoginEvent.ActionButtonClick -> {
                next(LoginUSF.CloseProgressDialog)
            }
            is LoginEvent.PhoneHintReceived -> {
                noChange()
            }
            is LoginEvent.CheckKyc -> {
                next(LoginASF.CheckKyc(event.user))
            }
            is LoginEvent.OtpHintReceived -> {
                noChange()
            }
        }
    }

    override suspend fun handleAsyncSideEffect(
        sideEffect: LoginASF,
        dispatchEvent: (LoginEvent) -> Unit,
        viewModelScope: CoroutineScope
    ) {
        when (sideEffect) {
            LoginASF.LoadCountryCode -> {
                dispatchEvent(LoginEvent.CountryCodeChanged(loginUseCase.getCountryCode()))
            }
            is LoginASF.RequestOtp -> {
                when(val outcome = loginUseCase.requestOtp(
                    "${sideEffect.countryCode}${sideEffect.mobileNumber}"
                )) {
                    is Outcome.Error -> dispatchEvent(LoginEvent.RequestOtpFailed(outcome.error.code.orEmpty(), outcome.error.message.orEmpty()))
                    is Outcome.Success -> dispatchEvent(LoginEvent.RequestOtpSuccess)
                }
            }
            is LoginASF.ValidateOtp -> {
                val advertisingId = AdvertisementUtils.getAdvertisementId(MandateManager.getInstance().getContext())
                when(val outcome = loginUseCase.createDevice(
                    CreateDeviceRequest(uid = advertisingId)
                )){
                    is Outcome.Success -> {
                        val mobileNumber = "${sideEffect.countryCode}${sideEffect.mobileNumber}"
                        when(val outcome =loginUseCase.verifyOtp(
                            mobileNumber,
                            enterpriseId = MandateManager.getInstance().getEnterpriseId(),
                            sideEffect.otp, outcome.data.id)) {
                            is Outcome.Error -> {
                                dispatchEvent(LoginEvent.VerifyOtpFailed(outcome.error.message.orEmpty()))
                            }
                            is Outcome.Success -> {
                                val user = outcome.data
                                dispatchEvent(LoginEvent.CheckKyc(user))
                            }
                        }
                    }
                    is Outcome.Error -> {
                        dispatchEvent(LoginEvent.VerifyOtpFailed(outcome.error.message.orEmpty()))
                    }
                }
            }
            is LoginASF.CheckKyc -> {
                when(kycUseCase.fetchKyc(propertyUseCase)){
                    is Outcome.Success -> {
                        dispatchEvent(LoginEvent.VerifyOtpSuccess(sideEffect.user))
                    }
                    is Outcome.Error -> {
                        dispatchEvent(LoginEvent.VerifyOtpSuccess(sideEffect.user))
                    }
                }
            }
        }
    }
}

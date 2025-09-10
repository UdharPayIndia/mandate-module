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
                val secretToken = MandateManager.getInstance().getSecretToken()
                if(!mobileNumber.isNullOrEmpty() && !secretToken.isNullOrEmpty()){
                    val countryCode = loginUseCase.getCountryCode()
                    val header = ResourceManager.getInstance().getString(R.string.rp_token_verification_in_progress)
                    val message = ResourceManager.getInstance().getString(R.string.rp_token_verification_in_progress_detail)
                    if(NetworkUtils.isNetworkAvailable()){
                        next(state.copy(mobileNumber = mobileNumber,
                            countryCode = countryCode,
                            otp = secretToken),
                            LoginASF.ValidateUser(countryCode, mobileNumber, secretToken),
                            LoginUSF.ShowInProgress(header, message))
                    }else{
                        next(state.copy(mobileNumber = mobileNumber,
                            countryCode = loginUseCase.getCountryCode()),
                            LoginUSF.ShowError(ResourceManager.getInstance().getString(R.string.rp_token_verification_failed),
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
                    next(state.copy(mobileNumber = event.mobileNumber, viewState = LoginViewState.VerifyUser))
                } else {
                    next(state.copy(viewState = LoginViewState.InvalidMobileNumber))
                }
            }
            LoginEvent.MobileNumberFocusChanged -> {
                next(LoginUSF.MobileNumberFocusChanged)
            }
            LoginEvent.VerifyUserClick -> {
                val header = ResourceManager.getInstance().getString(R.string.rp_token_verification_in_progress)
                val message = ResourceManager.getInstance().getString(R.string.rp_token_verification_in_progress_detail)
                next(LoginASF.ValidateUser(state.countryCode, state.mobileNumber, state.otp), LoginUSF.ShowInProgress(header, message))
            }
            is LoginEvent.VerifyUserFailed -> {
                val header = ResourceManager.getInstance().getString(R.string.rp_token_verification_failed)
                next(state.copy(viewState = LoginViewState.VerifyUser), LoginUSF.ShowError(header, event.message))
            }
            is LoginEvent.VerifyUserSuccess -> {
                GlobalState.isLogin.value = true
                next(LoginUSF.GotoHome(event.isKyced))
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
            is LoginASF.ValidateUser -> {
                val advertisingId = AdvertisementUtils.getAdvertisementId(MandateManager.getInstance().getContext())
                when(val outcome = loginUseCase.createDevice(
                    CreateDeviceRequest(uid = advertisingId)
                )){
                    is Outcome.Success -> {
                        val mobileNumber = "${sideEffect.countryCode}${sideEffect.mobileNumber}"
                        when(val outcome =loginUseCase.verifyToken(
                            mobileNumber,
                            enterpriseId = MandateManager.getInstance().getEnterpriseId(),
                            sideEffect.otp,
                            outcome.data.id
                        )) {
                            is Outcome.Error -> {
                                dispatchEvent(LoginEvent.VerifyUserFailed(outcome.error.message.orEmpty()))
                            }
                            is Outcome.Success -> {
                                val user = outcome.data
                                dispatchEvent(LoginEvent.CheckKyc(user))
                            }
                        }
                    }
                    is Outcome.Error -> {
                        dispatchEvent(LoginEvent.VerifyUserFailed(outcome.error.message.orEmpty()))
                    }
                }
            }
            is LoginASF.CheckKyc -> {
                when(kycUseCase.fetchKyc(propertyUseCase)){
                    is Outcome.Success -> {
                        dispatchEvent(LoginEvent.VerifyUserSuccess(sideEffect.user, kycUseCase.isKycCompleted()))
                    }
                    is Outcome.Error -> {
                        dispatchEvent(LoginEvent.VerifyUserSuccess(sideEffect.user, kycUseCase.isKycCompleted()))
                    }
                }
            }
        }
    }
}

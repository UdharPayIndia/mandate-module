package com.rocketpay.mandate.feature.profile.presentation.ui.statemachine

import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseAnalyticsHandler
import com.rocketpay.mandate.common.mvistatemachine.contract.Next
import com.rocketpay.mandate.common.mvistatemachine.contract.collectIn
import com.rocketpay.mandate.common.mvistatemachine.viewmodel.simple.SimpleStateMachineImpl
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import com.rocketpay.mandate.common.syncmanager.client.SyncManager
import com.rocketpay.mandate.feature.kyc.data.KycSyncer
import com.rocketpay.mandate.feature.kyc.domain.usecase.KycUseCase
import com.rocketpay.mandate.feature.login.domain.usecase.LoginUseCase
import com.rocketpay.mandate.feature.profile.domain.entities.UserProfile
import com.rocketpay.mandate.feature.property.domain.usecase.PropertyUseCase
import com.rocketpay.mandate.feature.property.presentation.utils.PropertyUtils
import com.rocketpay.mandate.feature.settlements.presentation.ui.utils.SettlementUtils
import kotlinx.coroutines.CoroutineScope


internal class UserProfileStateMachine(
    val kycUseCase: KycUseCase,
    val propertyUseCase: PropertyUseCase,
    val loginUseCase: LoginUseCase
): SimpleStateMachineImpl<UserProfileEvent, UserProfileState, UserProfileASF, UserProfileUSF>(
    BaseAnalyticsHandler()
) {

    override fun startState(): UserProfileState {
        return UserProfileState()
    }

    override fun handleEvent(
        event: UserProfileEvent,
        state: UserProfileState
    ): Next<UserProfileState?, UserProfileASF?, UserProfileUSF?> {
        return when (event) {
            is UserProfileEvent.LoadUserProfile -> {
                next(UserProfileASF.LoadUserProfile)
            }
            is UserProfileEvent.UserProfileLoaded -> {
                next(state.copy(userProfile = event.userProfile))
            }
            is UserProfileEvent.LoadBusinessProfile -> {
                next(UserProfileASF.LoadBusinessProfile)
            }
            is UserProfileEvent.BusinessProfileLoaded -> {
                next(state.copy(businessName = event.businessName))
            }
            is UserProfileEvent.BusinessProfileClick -> {
                next(UserProfileUSF.GotoBusinessProfile)
            }
            is UserProfileEvent.KycClick -> {
                next(UserProfileUSF.GotoKyc)
            }
            is UserProfileEvent.BankAccountsClick -> {
                next(UserProfileUSF.OpenBankAccountList)
            }
            UserProfileEvent.LoadKyc -> {
                next(UserProfileASF.LoadKyc)
            }
            is UserProfileEvent.KycLoaded -> {
                next(state.copy(kycStateEnum = event.kycStateEnum))
            }
            UserProfileEvent.RefreshKyc -> {
                next(UserProfileASF.RefreshKyc)
            }
        }
    }

    override suspend fun handleAsyncSideEffect(
        sideEffect: UserProfileASF,
        dispatchEvent: (UserProfileEvent) -> Unit,
        viewModelScope: CoroutineScope
    ) {
        when (sideEffect) {
            UserProfileASF.LoadUserProfile -> {
                dispatchEvent(UserProfileEvent.UserProfileLoaded(UserProfile(
                    mobileNumber = loginUseCase.getMobileNumber(),
                    name = loginUseCase.getName(),
                    url = "")))
            }
            UserProfileASF.LoadKyc -> {
                kycUseCase.getKycStatus().collectIn(viewModelScope) {
                    dispatchEvent(UserProfileEvent.KycLoaded(it))
                }
            }
            UserProfileASF.RefreshKyc -> {
                SyncManager.getInstance().enqueue(KycSyncer.TYPE)
            }
            UserProfileASF.LoadBusinessProfile -> {
                dispatchEvent(UserProfileEvent.BusinessProfileLoaded(
                    propertyUseCase.getPropertyValue(PropertyUtils.BUSINESS_NAME)))
            }
        }
    }

    fun getSharedMessage(shortLinkURL: String): String {
        return ResourceManager.getInstance().getString(R.string.rp_share_app_download_now, shortLinkURL)
    }
}

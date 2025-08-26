package com.rocketpay.mandate.feature.profile.presentation.ui.statemachine

import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseEvent
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseState
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.Screen
import com.rocketpay.mandate.common.mvistatemachine.contract.AsyncSideEffect
import com.rocketpay.mandate.common.mvistatemachine.contract.UiSideEffect
import com.rocketpay.mandate.feature.kyc.domain.entities.KycStateEnum
import com.rocketpay.mandate.feature.profile.domain.entities.UserProfile

internal data class UserProfileState(
    val userProfile: UserProfile? = null,
    val businessName: String? = null,
    val kycStateEnum: KycStateEnum = KycStateEnum.Pending,
    val isKycFlow: Boolean = false,
) : BaseState(UserProfileScreen)


internal sealed class UserProfileEvent(name: String? = null) : BaseEvent(name) {
    object LoadUserProfile : UserProfileEvent()
    data class UserProfileLoaded(val userProfile: UserProfile) : UserProfileEvent()
    object LoadBusinessProfile : UserProfileEvent()
    data class BusinessProfileLoaded(val businessName: String?) : UserProfileEvent()
    object BusinessProfileClick : UserProfileEvent("business_profile_click")
    object KycClick : UserProfileEvent("kyc_click")
    object BankAccountsClick: UserProfileEvent("bank_accounts_click")
    object LoadKyc: UserProfileEvent()
    data class KycLoaded(val kycStateEnum: KycStateEnum): UserProfileEvent()
    object RefreshKyc: UserProfileEvent()
}


internal sealed class UserProfileASF : AsyncSideEffect {
    object LoadUserProfile: UserProfileASF()
    object LoadBusinessProfile: UserProfileASF()
    object LoadKyc: UserProfileASF()
    object RefreshKyc: UserProfileASF()

}

internal sealed class UserProfileUSF : UiSideEffect {
    object GotoBusinessProfile: UserProfileUSF()
    object OpenBankAccountList: UserProfileUSF()
    object GotoKyc: UserProfileUSF()

}

internal object UserProfileScreen : Screen("user_profile")

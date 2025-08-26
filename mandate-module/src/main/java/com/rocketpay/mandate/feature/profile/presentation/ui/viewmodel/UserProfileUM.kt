package com.rocketpay.mandate.feature.profile.presentation.ui.viewmodel

import android.graphics.drawable.Drawable
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.basemodule.main.viewmodel.BaseMainUM
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import com.rocketpay.mandate.feature.kyc.domain.entities.KycStateEnum
import com.rocketpay.mandate.feature.profile.presentation.ui.statemachine.UserProfileEvent
import com.rocketpay.mandate.feature.profile.presentation.ui.statemachine.UserProfileState

internal class UserProfileUM(private val dispatchEvent: (UserProfileEvent) -> Unit) : BaseMainUM() {

    val userName = ObservableField<String>()
    val mobileNumber = ObservableField<String>()
    val userPhoto = ObservableField<String>()

    val kycStatusVisibility = ObservableInt(View.VISIBLE)
    val kycStatusIcon = ObservableField<Drawable>()
    val kycTitle = ObservableField<String>()
    val kycSubtitle = ObservableField<String>()

    val businessNameAlertVisibility = ObservableInt(View.VISIBLE)
    val businessNameTitle = ObservableField<String>()
    val businessName = ObservableField<String>()



    fun handleState(state: UserProfileState) {
        state.userProfile?.let { userProfile ->
            userName.set(userProfile.name)
            mobileNumber.set(userProfile.mobileNumber)
            userPhoto.set(userProfile.url)
        }

        if (state.businessName.isNullOrEmpty()) {
            businessNameTitle.set(ResourceManager.getInstance().getString(R.string.rp_add_business_name))
            businessNameAlertVisibility.set(View.VISIBLE)
            businessName.set(ResourceManager.getInstance().getString(R.string.rp_this_will_be_shown_to_your_customer))
        } else {
            businessNameTitle.set(ResourceManager.getInstance().getString(R.string.rp_business_info))
            businessNameAlertVisibility.set(View.GONE)
            businessName.set(state.businessName)
        }

        updateKycInfo(state)
    }

    private fun updateKycInfo(state: UserProfileState) {
        when(state.kycStateEnum) {
            KycStateEnum.Pending, KycStateEnum.Rejected, KycStateEnum.UnderReview, KycStateEnum.Initiated -> {
                kycTitle.set(ResourceManager.getInstance().getString(R.string.rp_kyc_profile_title))
                kycSubtitle.set(ResourceManager.getInstance().getString(R.string.rp_kyc_profile_subtitle_not_success))
                kycStatusIcon.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_info_filled, ResourceManager.getInstance().getColor(R.color.rp_orange_1)))
                kycStatusVisibility.set(View.VISIBLE)
            }
            KycStateEnum.Completed -> {
                kycTitle.set(ResourceManager.getInstance().getString(R.string.rp_kyc_profile_title))
                kycSubtitle.set(ResourceManager.getInstance().getString(R.string.rp_kyc_profile_subtitle_success))
                kycStatusIcon.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_success, ResourceManager.getInstance().getColor(R.color.rp_green_2)))
                kycStatusVisibility.set(View.GONE)
            }
        }
    }

    fun onKycClick() {
        dispatchEvent(UserProfileEvent.KycClick)
    }

    fun onBusinessProfileClick() {
        dispatchEvent(UserProfileEvent.BusinessProfileClick)
    }

    fun onBankAccountsClick() {
        dispatchEvent(UserProfileEvent.BankAccountsClick)
    }

}

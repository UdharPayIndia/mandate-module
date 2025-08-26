package com.rocketpay.mandate.feature.kyc.presentation.ui.identityintro.viewmodel

import androidx.databinding.ObservableField
import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.kyc.presentation.ui.identityintro.statemachine.KycIdentityIntroEvent
import com.rocketpay.mandate.feature.kyc.presentation.ui.identityintro.statemachine.KycIdentityIntroState
import com.rocketpay.mandate.common.basemodule.main.viewmodel.BaseMainUM
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal class KycIdentityIntroUM(private val dispatchEvent: (KycIdentityIntroEvent) -> Unit) : BaseMainUM() {

    val titleText = ObservableField<String>(ResourceManager.getInstance().getString(R.string.rp_use_addhar_card))

    fun handleState(state: KycIdentityIntroState) {
        if(!state.panName.isNullOrEmpty()){
            titleText.set(ResourceManager.getInstance().getString(R.string.rp_use_addhar_card_of, state.panName))
        }else{
            titleText.set(ResourceManager.getInstance().getString(R.string.rp_use_addhar_card))
        }
    }

    fun onNextClick() {
        dispatchEvent(KycIdentityIntroEvent.NextClick)
    }
}
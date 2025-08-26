package com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.viewmodel

import android.graphics.drawable.Drawable
import androidx.lifecycle.MutableLiveData
import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.kyc.domain.entities.KycStateEnum
import com.rocketpay.mandate.feature.kyc.domain.entities.KycWorkFlow
import com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.statemachine.KycEvent
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.toCapitalise
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal class ItemKycBusinessPanVM(
    private val kycItem: KycWorkFlow,
    private val itemClick: (KycEvent) -> Unit,
    selectedItem: MutableLiveData<KycWorkFlow>,
    kycEnumState: KycStateEnum,
    position: Int,
    ownerName: String?
) :ItemKycBaseVM(kycItem, itemClick, selectedItem, kycEnumState, position, ownerName) {
    override fun getTitleText(): String {
        return ResourceManager.getInstance().getString(R.string.rp_enter_pan_number)
    }

    override fun getSubTitleText(): String {
        return if(!ownerName.isNullOrEmpty()){
            ResourceManager.getInstance().getString(R.string.rp_please_ensure_you_enter_your_correct_pan_label,
                ownerName.toCapitalise())
        }else{
            ResourceManager.getInstance().getString(R.string.rp_please_ensure_you_enter_your_correct_pan)
        }
    }

    override fun getIllustration(): Drawable {
        return ResourceManager.getInstance().getDrawable(R.mipmap.rp_illustration_pan)
    }

    override fun setButtonText(){
        buttonText.set(if(isInputChanged()){
            ResourceManager.getInstance().getString(R.string.rp_verify)
        }else{
            ResourceManager.getInstance().getString(R.string.rp_done)
        })
    }
}

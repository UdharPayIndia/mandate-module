package com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.viewmodel

import android.graphics.drawable.Drawable
import androidx.lifecycle.MutableLiveData
import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.kyc.domain.entities.KycStateEnum
import com.rocketpay.mandate.feature.kyc.domain.entities.KycWorkFlow
import com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.statemachine.KycEvent
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal class ItemKycBusinessBankVM(
    private val kycItem: KycWorkFlow,
    private val itemClick: (KycEvent) -> Unit,
    selectedItem: MutableLiveData<KycWorkFlow>,
    kycEnumState: KycStateEnum,
    position: Int
) : ItemKycBaseVM(kycItem, itemClick, selectedItem, kycEnumState, position) {

    override fun getTitleText(): String {
        return ResourceManager.getInstance().getString(R.string.rp_enter_bank_details)
    }

    override fun getSubTitleText(): String {
        return ResourceManager.getInstance().getString(R.string.rp_money_collected_from_customers_will_be_deposited)
    }

    override fun getIllustration(): Drawable {
        return ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_bank, ResourceManager.getInstance().getColor(R.color.rp_blue_1))
    }

    override fun setButtonText(){
        buttonText.set(if(isInputChanged()){
            ResourceManager.getInstance().getString(R.string.rp_verify)
        }else{
            ResourceManager.getInstance().getString(R.string.rp_done)
        })
    }
}

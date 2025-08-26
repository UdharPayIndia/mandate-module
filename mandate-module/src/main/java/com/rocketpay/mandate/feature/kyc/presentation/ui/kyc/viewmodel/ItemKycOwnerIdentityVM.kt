package com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.viewmodel

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.kyc.domain.entities.KycStateEnum
import com.rocketpay.mandate.feature.kyc.domain.entities.KycWorkFlow
import com.rocketpay.mandate.feature.kyc.domain.entities.KycWorkFlowName
import com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.statemachine.KycEvent
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.RecyclerViewAdapter
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal class ItemKycOwnerIdentityVM(
    private val kycItem: KycWorkFlow,
    private val itemClick: (KycEvent) -> Unit,
    selectedItem: MutableLiveData<KycWorkFlow>,
    kycEnumState: KycStateEnum,
    position: Int
) : RecyclerViewAdapter.RecyclerViewItemViewModel {

    val itemKycCompletedVM = ItemKycCompletedVM(
        kycItem,
        selectedItem,
        ResourceManager.getInstance().getString(R.string.rp_selfie_and_aadhaar),
        ResourceManager.getInstance().getString(R.string.rp_take_clear_selfie_and_keep_aadhaar),
        ResourceManager.getInstance().getDrawable(R.mipmap.rp_illustration_aadhaar),
        kycEnumState,
        position
    )

    var verifyEnable = ObservableBoolean(true)


    fun onVerifyClick() {
        if(kycItem.name == KycWorkFlowName.SdkWorkFlow){
            itemClick(KycEvent.ShowIdentityIntroBottomSheet(kycItem))
        }else{
            itemClick(KycEvent.InitItem(kycItem))
        }
    }
}

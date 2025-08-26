package com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.viewmodel

import android.graphics.drawable.Drawable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.kyc.domain.entities.KycStateEnum
import com.rocketpay.mandate.feature.kyc.domain.entities.KycWorkFlow
import com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.statemachine.KycEvent
import com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.statemachine.KycStateMachine
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal class ItemKycBusinessProofVM(
    private val kycItem: KycWorkFlow,
    private val itemClick: (KycEvent) -> Unit,
    selectedItem: MutableLiveData<KycWorkFlow>,
    kycEnumState: KycStateEnum,
    position: Int
) :ItemKycBaseVM(kycItem, itemClick, selectedItem, kycEnumState, position) {

    val isCompleted = ObservableBoolean(KycStateEnum.isInReviewOrCompleted(kycItem.status.value))
    val documentTypeText = ObservableField<String>()
    val ownerNameText = ObservableField<String>()
    val isOwnerEditEnabled = ObservableBoolean()
    val isOwnerVisible = ObservableBoolean()
    val ownerNameInput = kycItem.input.find { it.name == KycStateMachine.BUSINESS_OWNER_NAME_KEY }

    init {
        val documentNumber = kycItem.output.get(KycStateMachine.BUSINESS_REGISTRATION_NUMBER)?.asString
        documentTypeText.set(
            if(!documentNumber.isNullOrEmpty()){
                kycItem.output.get(KycStateMachine.BUSINESS_PROOF_SELECTED)?.asString +" \u2022 " + documentNumber
            }else{
                kycItem.output.get(KycStateMachine.BUSINESS_PROOF_SELECTED)?.asString
            }
        )
        val ownerName = kycItem.output.get(KycStateMachine.BUSINESS_OWNER_NAME_KEY)?.asString
        ownerNameText.set(ownerName)
        isOwnerEditEnabled.set((ownerNameInput?.meta?.options?.size ?: 0) > 1)
        isOwnerVisible.set(!ownerName.isNullOrEmpty())
    }

    override fun getTitleText(): String {
        return ResourceManager.getInstance().getString(R.string.rp_upload_business_proof)
    }

    override fun getSubTitleText(): String {
        return ResourceManager.getInstance().getString(R.string.rp_upload_government_issued_document)
    }

    override fun getIllustration(): Drawable {
        return ResourceManager.getInstance().getDrawable(R.mipmap.rp_illustration_pan)
    }

    override fun setButtonText(){
        buttonText.set(ResourceManager.getInstance().getString(R.string.rp_upload_business_proof))
    }

    fun onEditOwnerClick(){
        val nextStep = KycStateMachine.getActionEvent(kycItem, ownerNameInput, kycItem.output)
        if(nextStep != null) {
            itemClick(nextStep)
        }else{
            itemKycCompletedVM.onExpandClick()
        }
    }

}

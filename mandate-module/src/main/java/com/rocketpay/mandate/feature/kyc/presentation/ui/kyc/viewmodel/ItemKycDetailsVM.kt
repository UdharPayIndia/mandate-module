package com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.viewmodel

import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.kyc.domain.entities.KycIdentityDocumentTypeEnum
import com.rocketpay.mandate.feature.kyc.domain.entities.KycStateEnum
import com.rocketpay.mandate.feature.kyc.domain.entities.KycWorkFlow
import com.rocketpay.mandate.feature.kyc.domain.entities.KycWorkFlowName
import com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.statemachine.KycStateMachine
import com.rocketpay.mandate.main.init.MandateManager
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.RecyclerViewAdapter
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.toCapitalise
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.UiUtils
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import com.rocketpay.mandate.feature.common.domain.CommonUseCase

internal class ItemKycDetailsVM(private val kycWorkFlow: List<KycWorkFlow>,
                       private val kycState: String,
                       private val kycType: String?) : RecyclerViewAdapter.RecyclerViewItemViewModel {

    val statusBackground = ObservableInt(ResourceManager.getInstance().getColor(R.color.rp_green_4))
    val statusText = ObservableField<String>()
    val nameText = ObservableField<String>()
    val phoneNumberText = ObservableField<String>()
    val businessDocumentName = ObservableField<String>()
    val businessDocumentValue = ObservableField<String>()
    val businessVisibility = ObservableInt(View.GONE)
    val identityDocumentName = ObservableField<String>()
    val identityDocumentValue = ObservableField<String>()
    val panNumberText = ObservableField<String>()
    val identityVisibility = ObservableInt(View.GONE)

    init {
        val panDetails = kycWorkFlow.find { it.name == KycWorkFlowName.PanDetailsVerification }
        val businessDetails = kycWorkFlow.find { it.name == KycWorkFlowName.BusinessProof}
        nameText.set(panDetails?.output?.get(KycStateMachine.PAN_NAME_KEY)?.asString?.toCapitalise())
        panNumberText.set(UiUtils.maskText(panDetails?.output?.get(KycStateMachine.PAN_KEY)?.asString))
        phoneNumberText.set(UiUtils.maskText(CommonUseCase.getInstance().getMobileNumber()))
        val documentProofValue = businessDetails?.output?.get(KycStateMachine.BUSINESS_PROOF_SELECTED)?.asString
        val documentRegistrationValue = businessDetails?.output?.get(KycStateMachine.BUSINESS_REGISTRATION_NUMBER)?.asString
        businessDocumentName.set(documentProofValue.toCapitalise())
        businessDocumentValue.set(UiUtils.maskText(documentRegistrationValue))
        businessVisibility.set(if(documentRegistrationValue.isNullOrEmpty()){
            View.GONE
        }else{
            View.VISIBLE
        })

        val identityDetails = kycWorkFlow.find { it.name == KycWorkFlowName.SdkWorkFlow}
        val identityProofValue = identityDetails?.output?.get(KycStateMachine.BUSINESS_IDENTITY_SELECTED)?.asString
        val identityRegistrationValue = identityDetails?.output?.get(KycStateMachine.BUSINESS_IDENTITY_NUMBER)?.asString
        identityDocumentName.set(KycIdentityDocumentTypeEnum.get(identityProofValue)?.translation ?: identityProofValue)
        identityDocumentValue.set(UiUtils.maskText(identityRegistrationValue))
        identityVisibility.set(if(identityRegistrationValue.isNullOrEmpty()){
            View.GONE
        }else{
            View.VISIBLE
        })
        setState()
    }

    private fun setState() {
        when(KycStateEnum.get(kycState)){
            KycStateEnum.Completed ->{
                statusBackground.set(ResourceManager.getInstance().getColor(R.color.rp_green_4))
                if(kycType == "kyb") {
                    statusText.set(
                        ResourceManager.getInstance()
                            .getString(R.string.rp_your_business_kyc_is_complete)
                    )
                }else{
                    statusText.set(
                        ResourceManager.getInstance()
                            .getString(R.string.rp_your_personal_kyc_is_complete)
                    )
                }
            }
            else -> {
                statusBackground.set(ResourceManager.getInstance().getColor(R.color.rp_yellow_3))
                if(kycType == "kyb") {
                    statusText.set(
                        ResourceManager.getInstance()
                            .getString(R.string.rp_your_business_kyc_is_under_review)
                    )
                }else{
                    statusText.set(
                        ResourceManager.getInstance()
                            .getString(R.string.rp_your_personal_kyc_is_under_review)
                    )
                }
            }
        }
    }

}
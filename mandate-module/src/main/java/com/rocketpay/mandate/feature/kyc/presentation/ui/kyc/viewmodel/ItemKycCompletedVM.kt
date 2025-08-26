package com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.viewmodel

import android.graphics.drawable.Drawable
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.kyc.domain.entities.KycInputTypeEnum
import com.rocketpay.mandate.feature.kyc.domain.entities.KycItemState
import com.rocketpay.mandate.feature.kyc.domain.entities.KycStateEnum
import com.rocketpay.mandate.feature.kyc.domain.entities.KycWorkFlow
import com.rocketpay.mandate.feature.kyc.domain.entities.KycWorkFlowName
import com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.statemachine.KycStateMachine
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.toCapitalise
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal class ItemKycCompletedVM(
    val kycItem: KycWorkFlow,
    val selectedItem: MutableLiveData<KycWorkFlow>,
    private val titleString: String,
    private val subTitleString: String,
    private val illustration: Drawable,
    private val kycEnumState: KycStateEnum,
    private val position: Int
) {

    val titleText = ObservableField<String>()
    val subTitleText = ObservableField<String>()
    val illustrationDrawable = ObservableField<Drawable>()
    val detailLayoutVisibility = ObservableInt()
    val isSelected = ObservableBoolean()

    init {
        observeItemSelected()
        updateUi(selectedItem.value, kycItem)
    }

    private fun updateUi(selectedItem: KycWorkFlow?, kycItem: KycWorkFlow) {
        setText(selectedItem, kycItem)
        illustrationDrawable.set(illustration)
        isSelected.set((selectedItem == kycItem || kycItem.isExpanded) || kycItem.isPreviousStepPending)

        when(kycItem.status) {
            KycItemState.Completed -> {
                detailLayoutVisibility.set(View.GONE)
            }
            KycItemState.Initiated -> {
                if (selectedItem != null && selectedItem.id == kycItem.id) {
                    detailLayoutVisibility.set(View.VISIBLE)
                } else {
                    detailLayoutVisibility.set(View.GONE)
                }
            }
            KycItemState.UnderReview, KycItemState.Pending, KycItemState.Rejected -> {
                detailLayoutVisibility.set(View.VISIBLE)
            }
        }
    }

    private fun setText(selectedItem: KycWorkFlow?, kycItem: KycWorkFlow){
        var titleSubString = ""
        when(kycItem.name){
            KycWorkFlowName.BusinessProof -> {
                if(selectedItem != kycItem && !kycItem.isExpanded){
                    titleSubString = ResourceManager.getInstance().getString(R.string.rp_business_details)
                    val outputString = getOutputString()
                    if(!outputString.isNullOrEmpty()) {
                        subTitleText.set(outputString)
                    }else{
                        subTitleText.set(subTitleString)
                    }
                }else{
                    titleSubString = titleString
                    subTitleText.set(subTitleString)
                }
            }
            KycWorkFlowName.PanDetailsVerification -> {
                if(selectedItem != kycItem && !kycItem.isExpanded){
                    titleSubString = ResourceManager.getInstance().getString(R.string.rp_pan_details)
                    var outputString = getOutputString()
                    val panName = kycItem.output.get(KycStateMachine.PAN_NAME_KEY)?.asString
                    if(!outputString.isNullOrEmpty() && !panName.isNullOrEmpty()){
                        outputString += " \u2022 ${panName.toCapitalise()}"
                    }
                    if(!outputString.isNullOrEmpty()) {
                        subTitleText.set(outputString)
                    }else{
                        subTitleText.set(subTitleString)
                    }
                }else{
                    titleSubString = titleString
                    subTitleText.set(subTitleString)
                }
            }
            KycWorkFlowName.BankVerification -> {
                if(selectedItem != kycItem && !kycItem.isExpanded){
                    titleSubString = ResourceManager.getInstance().getString(R.string.rp_bank_details)
                    val outputString = getOutputString()
                    if(!outputString.isNullOrEmpty()) {
                        subTitleText.set(outputString)
                    }else{
                        subTitleText.set(subTitleString)
                    }
                }else{
                    titleSubString = titleString
                    subTitleText.set(subTitleString)
                }
            }
            is KycWorkFlowName.SdkWorkFlow -> {
                titleSubString = titleString
                subTitleText.set(subTitleString)
            }
        }
        val title = ResourceManager.getInstance().getString(
            R.string.rp_step_X_kyc,
            (position + 1),
            titleSubString
        )
        titleText.set(title)
    }

    private fun getOutputString(): String {
        var outputString = ""
        kycItem.input.sortedBy { it.priority }.forEachIndexed { index, it ->
            if(it.type != KycInputTypeEnum.Document.value){
                val output = kycItem.output.get(it.name)?.asString ?: ""
                if(!output.isNullOrEmpty() && index != 0){
                    outputString += " \u2022 "
                }
                outputString += output
            }
        }
        return outputString
    }

    private fun observeItemSelected() {
        selectedItem.observeForever {
            updateUi(selectedItem.value, kycItem)
        }
    }

    fun onExpandClick() {
        kycItem.isExpanded = !kycItem.isExpanded
        updateUi(selectedItem.value, kycItem)
        if(kycItem.isExpanded) {
            detailLayoutVisibility.set(View.VISIBLE)
        }else{
            detailLayoutVisibility.set(View.GONE)
        }
    }
}

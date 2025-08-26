package com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.viewmodel

import android.graphics.drawable.Drawable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.kyc.domain.entities.KycInputTypeEnum
import com.rocketpay.mandate.feature.kyc.domain.entities.KycItemState
import com.rocketpay.mandate.feature.kyc.domain.entities.KycStateEnum
import com.rocketpay.mandate.feature.kyc.domain.entities.KycWorkFlow
import com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.adapter.KycInputAdapter
import com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.statemachine.KycEvent
import com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.statemachine.KycStateMachine
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.RecyclerViewAdapter
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DataValidator
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal abstract class ItemKycBaseVM(
    private val kycItem: KycWorkFlow,
    private val itemClick: (KycEvent) -> Unit,
    selectedItem: MutableLiveData<KycWorkFlow>,
    kycEnumState: KycStateEnum,
    position: Int,
    val ownerName: String? = null
): RecyclerViewAdapter.RecyclerViewItemViewModel {

    private val dataValidator = DataValidator()
    private val textInputs = kycItem.input.filter { it.type == KycInputTypeEnum.Text.value }.sortedBy { it.priority }

    val itemKycCompletedVM = ItemKycCompletedVM(
        kycItem,
        selectedItem,
        getTitleText(),
        getSubTitleText(),
        getIllustration(),
        kycEnumState,
        position
    )


    val adapter = KycInputAdapter()
    val adapterObservable = ObservableField(adapter)
    val buttonText = ObservableField<String>(ResourceManager.getInstance().getString(R.string.rp_verify))
    var verifyEnable = ObservableBoolean()

    init {
        //Set Input Field
        adapter.itemClick = itemClick
        adapter.onInputVerified = {
            setButtonText()
            checkVerifyEnable()
        }
        textInputs.forEach {
            it.value = kycItem.output.get(it.name)?.asString ?: ""
        }
        adapter.swapData(textInputs)
        //Set Button Text
        setButtonText()
        checkVerifyEnable()
    }

    abstract fun getTitleText(): String
    abstract fun getSubTitleText(): String
    abstract fun getIllustration(): Drawable
    abstract fun setButtonText()

    fun checkVerifyEnable() {
        var isVerifyEnabled = true
        textInputs.forEach {
            if (!dataValidator.isValidInput(it.value, it.meta?.regex)) {
                isVerifyEnabled = false
            }
        }
        verifyEnable.set(isVerifyEnabled)
    }

    fun isInputChanged(): Boolean{
        var isInputChanged = false
        textInputs.forEach {
            val output = kycItem.output.get(it.name)?.asString ?: ""
            if(output != it.value){
                isInputChanged = true
            }
        }
        return isInputChanged || kycItem.status != KycItemState.Completed
    }

    fun onVerifyClick() {
        val firstInput = kycItem.input.sortedBy { it.priority }[0]
        val jsonObject = JsonObject()
        textInputs.forEach {
            jsonObject.addProperty(it.name, it.value)
        }
        val nextStep = KycStateMachine.getActionEvent(kycItem, firstInput, jsonObject)
        if(nextStep != null) {
            itemClick(nextStep)
        }else if(isInputChanged()){
            itemClick(KycEvent.SubmitItem(kycItem, jsonObject))
        }else{
            itemKycCompletedVM.onExpandClick()
        }
    }
}
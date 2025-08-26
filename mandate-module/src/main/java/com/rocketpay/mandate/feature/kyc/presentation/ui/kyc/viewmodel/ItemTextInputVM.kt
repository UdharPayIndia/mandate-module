package com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.viewmodel

import androidx.databinding.ObservableField
import com.rocketpay.mandate.feature.kyc.domain.entities.KycItemInputMeta
import com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.statemachine.KycEvent
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.RecyclerViewAdapter
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DataValidator

internal class ItemTextInputVM(
    private val kycItemInputMeta: KycItemInputMeta,
    private val itemClick: (KycEvent) -> Unit,
    private val onInputVerified: () -> Unit
): RecyclerViewAdapter.RecyclerViewItemViewModel  {

    private val dataValidator = DataValidator()
    private val inputMeta = kycItemInputMeta.meta
    private var inputValue :String = ""

    val inputHint = ObservableField<String>(inputMeta?.hint)
    val inputText = ObservableField<String>()
    val inputError = ObservableField<String>("")

    init {
        if(!kycItemInputMeta.value.isNullOrEmpty()){
            onTextChanged(kycItemInputMeta.value ?: "")
        }
    }

    fun onTextChanged(value: CharSequence) {
        inputValue = value.toString()
        kycItemInputMeta.value = inputValue
        inputText.set(inputValue)
        if (dataValidator.isValidInput(inputValue, inputMeta?.regex)) {
            inputError.set(null)
        } else {
            inputError.set(inputMeta?.error)
        }
        onInputVerified.invoke()
    }

    fun onClearClick(){
        onTextChanged("")
    }

}
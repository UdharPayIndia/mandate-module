package com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountadd.viewmodel

import android.graphics.drawable.Drawable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountadd.statemachine.BankAccountAddEvent
import com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountadd.statemachine.BankAccountAddState
import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialogVM
import com.rocketpay.mandate.common.basemodule.main.viewmodel.BaseMainUM
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal class BankAccountAddUM(private val dispatchEvent: (BankAccountAddEvent) -> Unit) : BaseMainUM() {

    val progressDialogVM = ProgressDialogVM({
        dispatchEvent(BankAccountAddEvent.ActionButtonClick(it))
    }, {
        dispatchEvent(BankAccountAddEvent.EditClick)
    })

    val infoText = ObservableField<String>()
    val name = ObservableField("")
    val nameError = ObservableField("")
    val ifscCodeError = ObservableField("")
    val accountNumberError = ObservableField("")
    var verifyEnable = ObservableBoolean()
    val checkBoxDrawable = ObservableField<Drawable>(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_checkbox_empty))
    val checkBoxVisibility = ObservableBoolean()
    val isFromOnBoarding = ObservableBoolean()

    fun onNameTextChanged(name: CharSequence) {
        dispatchEvent(BankAccountAddEvent.NameTextChanged(name.toString()))
    }

    fun onNameFocusChanged(hasFocus: Boolean) {
        if (hasFocus) {
            dispatchEvent(BankAccountAddEvent.NameFocusChanged)
        }
    }

    fun onIfscTextChanged(ifsc: CharSequence) {
        dispatchEvent(BankAccountAddEvent.IfscTextChanged(ifsc.toString()))
    }

    fun onIfscFocusChanged(hasFocus: Boolean) {
        if (hasFocus) {
            dispatchEvent(BankAccountAddEvent.IfscFocusChanged)
        }
    }

    fun onAccountNumberTextChanged(accountNumber: CharSequence) {
        dispatchEvent(BankAccountAddEvent.AccountNumberTextChanged(accountNumber.toString()))
    }

    fun onVerifyClick() {
        dispatchEvent(BankAccountAddEvent.VerifyAccountDetail)
    }

    fun handleState(state: BankAccountAddState) {
        name.set(state.name)
        nameError.set(state.nameError)
        accountNumberError.set(state.numberError)
        ifscCodeError.set(state.ifscError)
        verifyEnable.set(state.verifyEnable)

        if(state.isFromOnBoarding){
            isFromOnBoarding.set(true)
        }else{
            isFromOnBoarding.set(false)
        }
        toolbarIcon.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_back))

        if(state.isPrimary){
            checkBoxDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_checkbox_filled))
        }else{
            checkBoxDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_checkbox_empty))
        }

        if(state.isBankAccountsEmpty){
            checkBoxVisibility.set(false)
            infoText.set(null)
        }else{
            checkBoxVisibility.set(true)
            if(!state.userProfileName.isNullOrEmpty()){
                infoText.set(ResourceManager.getInstance().getString(R.string.rp_note_enter_bank_details_of, state.userProfileName))
            }else{
                infoText.set(ResourceManager.getInstance().getString(R.string.rp_note_enter_your_bank_details))
            }
        }
    }

    fun onPrimaryButtonChecked(){
        dispatchEvent(BankAccountAddEvent.PrimaryButtonCheck)
    }
}

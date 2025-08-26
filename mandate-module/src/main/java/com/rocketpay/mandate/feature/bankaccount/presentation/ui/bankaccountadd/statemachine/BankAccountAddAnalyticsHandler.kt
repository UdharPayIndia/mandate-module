package com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountadd.statemachine

import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialogStatus
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseAnalyticsHandler

internal class BankAccountAddAnalyticsHandler: BaseAnalyticsHandler<BankAccountAddEvent, BankAccountAddState>() {

    private var accountNumberFocusEventSent = false
    private var nameFocusEventSent = false
    private var ifscFocusEventSent = false

    override fun updateEventParameter(
        event: BankAccountAddEvent,
        state: BankAccountAddState,
        paramBuilder: HashMap<String, Any>
    ) {
        super.updateEventParameter(event, state, paramBuilder)
        paramBuilder["is_from_onboarding"] = state.isFromOnBoarding
        paramBuilder["source"] = state.source
        when(event) {
            is BankAccountAddEvent.NameFocusChanged -> {
                if (nameFocusEventSent) {
                    event.name = null
                }
                nameFocusEventSent = true
            }
            is BankAccountAddEvent.AccountNumberFocusChange -> {
                if (accountNumberFocusEventSent) {
                    event.name = null
                }
                accountNumberFocusEventSent = true
            }
            is BankAccountAddEvent.IfscFocusChanged -> {
                if (ifscFocusEventSent) {
                    event.name = null
                }
                ifscFocusEventSent = true
            }
            is BankAccountAddEvent.PrimaryButtonCheck -> {
                paramBuilder["isChecked"] = state.isPrimary
            }
            is BankAccountAddEvent.ActionButtonClick -> {
                if (event.state is ProgressDialogStatus.Success) {
                    if(state.isFromOnBoarding){
                        event.name = "bank_account_confirmed"
                    }
                }
            }
            else -> {

            }
        }
    }
}

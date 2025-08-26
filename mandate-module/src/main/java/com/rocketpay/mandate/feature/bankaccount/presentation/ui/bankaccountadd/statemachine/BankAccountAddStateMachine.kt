package com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountadd.statemachine

import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.bankaccount.domain.usecase.BankAccountUseCase
import com.rocketpay.mandate.main.init.MandateManager
import com.udharpay.core.networkmanager.domain.entities.Outcome
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.getSpannable
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.makeBold
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.setTextColor
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.toCapitalise
import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialogStatus
import com.rocketpay.mandate.common.mvistatemachine.contract.Next
import com.rocketpay.mandate.common.mvistatemachine.contract.collectIn
import com.rocketpay.mandate.common.mvistatemachine.viewmodel.simple.SimpleStateMachineImpl
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import com.rocketpay.mandate.feature.common.domain.CommonUseCase
import kotlinx.coroutines.CoroutineScope

internal class BankAccountAddStateMachine(
    private val bankAccountUseCase: BankAccountUseCase
) : SimpleStateMachineImpl<BankAccountAddEvent, BankAccountAddState, BankAccountAddASF, BankAccountAddUSF>(
    BankAccountAddAnalyticsHandler()
) {

    override fun startState(): BankAccountAddState {
        return BankAccountAddState()
    }

    override fun handleEvent(
        event: BankAccountAddEvent,
        state: BankAccountAddState
    ): Next<BankAccountAddState?, BankAccountAddASF?, BankAccountAddUSF?> {
        return when (event) {
            is BankAccountAddEvent.LoadData -> {
                next(state.copy(isFromOnBoarding = event.isFromOnBoarding, source = event.source),BankAccountAddASF.LoadData)
            }
            is BankAccountAddEvent.DataLoaded -> {
                val name = CommonUseCase.getInstance().getName()
                next(state.copy(name = name, userProfileName = name, isBankAccountsEmpty = event.bankAccounts.isEmpty()))
            }
            is BankAccountAddEvent.NameTextChanged -> {
                val accountHolderName = event.accountHolderName
                val newState = if (bankAccountUseCase.isValidName(accountHolderName)) {
                    state.copy(nameError = null, name = accountHolderName)
                } else {
                    state.copy(nameError = ResourceManager.getInstance().getString(R.string.rp_error_name))
                }
                val verifyEnable = shouldEnableVerify(newState)
                next(newState.copy(verifyEnable = verifyEnable))
            }
            is BankAccountAddEvent.IfscTextChanged -> {
                val newState = if (bankAccountUseCase.isValidIfsc(event.ifsc)) {
                    state.copy(ifscError = null, ifsc = event.ifsc)
                } else {
                    state.copy(ifscError = ResourceManager.getInstance().getString(R.string.rp_error_ifsc))
                }
                val verifyEnable = shouldEnableVerify(newState)
                next(newState.copy(verifyEnable = verifyEnable))
            }
            is BankAccountAddEvent.AccountNumberTextChanged -> {
                val newState = if (bankAccountUseCase.isValidAccountNumber(event.accountNumber)) {
                    state.copy(numberError = null, number = event.accountNumber)
                } else {
                    state.copy(numberError = ResourceManager.getInstance().getString(R.string.rp_error_account))
                }
                val verifyEnable = shouldEnableVerify(newState)
                next(newState.copy(verifyEnable = verifyEnable))
            }
            BankAccountAddEvent.VerifyAccountDetail -> {
                val header = ResourceManager.getInstance().getString(R.string.rp_bank_account_progress_header)
                val message = ResourceManager.getInstance().getString(R.string.rp_bank_account_progress_message)
                next(BankAccountAddASF.AddBankAccount(state.name, state.number, state.ifsc, state.upiId, state.isPrimary),
                    BankAccountAddUSF.AddBankAccountInProgress(header, message))
            }
            is BankAccountAddEvent.AddBankAccountFail -> {
                val header = ResourceManager.getInstance().getString(R.string.rp_bank_account_fail_header)
                next(BankAccountAddUSF.AddBankAccountFail(header, event.error.message.orEmpty()))
            }
            is BankAccountAddEvent.AddBankAccountSuccess -> {
                val name = event.bankAccount.accountHolderName.toCapitalise()
                val header = "${ResourceManager.getInstance().getString(R.string.rp_banking_name)} : $name".getSpannable()
                    .setTextColor(name, ResourceManager.getInstance().getColor(R.color.rp_green_1))
                val message = ResourceManager.getInstance().getString(R.string.rp_bank_account_success_message,
                    event.bankAccount.accountNumber, event.bankAccount.ifsc).getSpannable()
                    .makeBold(event.bankAccount.accountNumber)
                    .makeBold(event.bankAccount.ifsc)
                next(BankAccountAddUSF.AddBankAccountSuccess(header, message,
                    if(state.isFromOnBoarding){
                        ResourceManager.getInstance().getString(R.string.rp_confirm)
                    }else{
                        ResourceManager.getInstance().getString(R.string.rp_ok)
                    },
                    if(state.isFromOnBoarding){
                        ResourceManager.getInstance().getString(R.string.rp_change_account)
                    }else{
                        null
                    }))
            }
            is BankAccountAddEvent.ActionButtonClick -> {
                if (event.state is ProgressDialogStatus.Success) {
                    next(BankAccountAddUSF.GotoNextScreen)
                } else {
                    next(BankAccountAddUSF.CloseProgressDialog)
                }
            }
            is BankAccountAddEvent.NameFocusChanged -> {
                next(BankAccountAddUSF.MoveNameFieldUp)
            }
            BankAccountAddEvent.AccountNumberFocusChange -> {
                next(BankAccountAddUSF.AccountNumberFocusChange)
            }
            is BankAccountAddEvent.PrimaryButtonCheck -> {
                next(state.copy(isPrimary = !state.isPrimary))
            }
            is BankAccountAddEvent.MarkBankAsPrimary -> {
                next(BankAccountAddASF.MarkBankAsPrimary(event.bankAccount))
            }
            is BankAccountAddEvent.IfscFocusChanged -> {
                next(BankAccountAddUSF.MoveNameFieldUp)
            }
            is BankAccountAddEvent.EditClick -> {
                next(BankAccountAddUSF.CloseProgressDialog)
            }
        }
    }

    private fun shouldEnableVerify(state: BankAccountAddState): Boolean {
        return ((!state.isFromOnBoarding && state.name.isNotEmpty() && state.nameError.isNullOrEmpty()) || state.isFromOnBoarding)
                && state.ifsc.isNotEmpty() && state.ifscError.isNullOrEmpty()
                && state.number.isNotEmpty() && state.numberError.isNullOrEmpty()
    }

    override suspend fun handleAsyncSideEffect(
        sideEffect: BankAccountAddASF,
        dispatchEvent: (BankAccountAddEvent) -> Unit,
        viewModelScope: CoroutineScope
    ) {
        when (sideEffect) {
            is BankAccountAddASF.LoadData -> {
                bankAccountUseCase.getBankAccounts().collectIn(viewModelScope) {
                    dispatchEvent(BankAccountAddEvent.DataLoaded(it))
                }
            }
            is BankAccountAddASF.AddBankAccount -> {
                when(val outcome = bankAccountUseCase.addBankAccount(sideEffect.accountNumber, sideEffect.ifsc, sideEffect.accountHolderName, sideEffect.upiId)) {
                    is Outcome.Error -> {
                        dispatchEvent(BankAccountAddEvent.AddBankAccountFail(outcome.error))
                    }
                    is Outcome.Success -> {
                        if (sideEffect.isPrimary) {
                            dispatchEvent(BankAccountAddEvent.MarkBankAsPrimary(outcome.data))
                        } else {
                            dispatchEvent(BankAccountAddEvent.AddBankAccountSuccess(outcome.data))
                        }
                    }
                }
            }
            is BankAccountAddASF.MarkBankAsPrimary -> {
                when(val result = bankAccountUseCase.markBankAccountAsPrimary(sideEffect.bankAccount)){
                    is Outcome.Success -> {
                        dispatchEvent(BankAccountAddEvent.AddBankAccountSuccess(sideEffect.bankAccount))
                    }
                    is Outcome.Error -> {
                        dispatchEvent(BankAccountAddEvent.AddBankAccountSuccess(sideEffect.bankAccount))
                    }
                }
            }
        }
    }
}

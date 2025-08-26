package com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountadd.statemachine

import android.text.SpannableString
import com.rocketpay.mandate.feature.bankaccount.domain.entities.BankAccount
import com.udharpay.core.networkmanager.domain.entities.GenericErrorResponse
import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialogStatus
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseEvent
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseState
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.Screen
import com.rocketpay.mandate.common.mvistatemachine.contract.AsyncSideEffect
import com.rocketpay.mandate.common.mvistatemachine.contract.UiSideEffect

internal data class BankAccountAddState(
    val userProfileName: String = "",
    val name: String = "",
    val number: String = "",
    val ifsc: String = "",
    val upiId: String = "",
    val nameError: String? = null,
    val ifscError: String? = null,
    val numberError: String? = null,
    val verifyEnable: Boolean = false,
    val isPrimary: Boolean = false,
    val isBankAccountsEmpty: Boolean = true,
    val isFromOnBoarding: Boolean = false,
    val source: String = ""
) : BaseState(BankAccountAddScreen)


internal sealed class BankAccountAddEvent(name: String? = null) : BaseEvent(name) {
    data class LoadData(val isFromOnBoarding: Boolean, val source: String): BankAccountAddEvent()
    data class DataLoaded(val bankAccounts: List<BankAccount>) : BankAccountAddEvent()
    data class NameTextChanged(val accountHolderName: String) : BankAccountAddEvent()
    data class IfscTextChanged(val ifsc: String) : BankAccountAddEvent()
    data class AccountNumberTextChanged(val accountNumber: String) : BankAccountAddEvent()
    object NameFocusChanged: BankAccountAddEvent()
    object IfscFocusChanged: BankAccountAddEvent()
    object AccountNumberFocusChange: BankAccountAddEvent()
    object VerifyAccountDetail : BankAccountAddEvent("verify_bank_account_click")
    data class AddBankAccountFail(val error: GenericErrorResponse): BankAccountAddEvent("bank_account_verification_failed")
    data class AddBankAccountSuccess(val bankAccount: BankAccount) : BankAccountAddEvent("bank_account_verified")
    data class ActionButtonClick(val state: ProgressDialogStatus) : BankAccountAddEvent("")
    object PrimaryButtonCheck:BankAccountAddEvent("bank_account_primary_checked")
    data class MarkBankAsPrimary(val bankAccount: BankAccount): BankAccountAddEvent()
    data object EditClick: BankAccountAddEvent("edit_bank_account_click")
}


internal sealed class BankAccountAddASF : AsyncSideEffect {
    object LoadData: BankAccountAddASF()
    data class AddBankAccount(val accountHolderName: String, val accountNumber: String, val ifsc: String, val upiId: String, val isPrimary: Boolean) : BankAccountAddASF()
    data class MarkBankAsPrimary(val bankAccount: BankAccount): BankAccountAddASF()

}


internal sealed class BankAccountAddUSF : UiSideEffect {
    data class AddBankAccountInProgress(val header: String, val message: String) : BankAccountAddUSF()
    data class AddBankAccountFail(val header: String, val message: String) : BankAccountAddUSF()
    data class AddBankAccountSuccess(val header: SpannableString, val message: SpannableString, val primaryButtonText: String, val secondaryButtonText: String?) : BankAccountAddUSF()
    object CloseProgressDialog: BankAccountAddUSF()
    object GotoNextScreen: BankAccountAddUSF()
    object MoveNameFieldUp: BankAccountAddUSF()
    object MoveUpiIdFieldUp: BankAccountAddUSF()
    object AccountNumberFocusChange: BankAccountAddUSF()
}

internal object BankAccountAddScreen : Screen("add_bank_account")

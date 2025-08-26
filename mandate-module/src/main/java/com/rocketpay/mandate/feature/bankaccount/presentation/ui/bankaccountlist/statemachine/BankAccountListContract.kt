package com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountlist.statemachine

import android.graphics.drawable.Drawable
import android.view.View
import com.rocketpay.mandate.feature.bankaccount.domain.entities.BankAccount
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseEvent
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseState
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.Screen
import com.rocketpay.mandate.common.mvistatemachine.contract.AsyncSideEffect
import com.rocketpay.mandate.common.mvistatemachine.contract.UiSideEffect

internal data class BankAccountListState(
    var bankAccounts: List<BankAccount> = emptyList(),
    var selectedBankAccount: BankAccount? = null
) : BaseState(BankAccountListScreen)


internal sealed class BankAccountListEvent(name: String? = null) : BaseEvent(name) {
    object LoadBankAccounts : BankAccountListEvent()
    object RefreshBankAccounts: BankAccountListEvent()
    data class BankAccountListLoaded(val bankAccounts: List<BankAccount>) : BankAccountListEvent()
    object AddBankAccount: BankAccountListEvent("add_bank_account_click")
    data class MenuClick(val view: View, val bankAccount: BankAccount): BankAccountListEvent()
    data class DeleteBankAccountClick(val bankAccount: BankAccount): BankAccountListEvent("delete_bank_account_clicked")
    object DeleteBankAccountConfirmed: BankAccountListEvent("delete_bank_account_confirmed")
    object DeleteBankAccountSuccess: BankAccountListEvent("delete_bank_account_success")
    data class DeleteBankAccountFailed(val errorMessage: String): BankAccountListEvent(" delete_bank_account_failed")
    data class SetBankAccountPrimaryClick(val bankAccount: BankAccount): BankAccountListEvent("set_bank_account_primary_clicked")
    object PrimaryBankAccountSuccess: BankAccountListEvent("set_bank_account_primary_success")
    data class PrimaryBankAccountFailed(val errorMessage: String): BankAccountListEvent("set_bank_account_primary_failed")
    object CloseDialogClick : BankAccountListEvent()
}


internal sealed class BankAccountListASF : AsyncSideEffect {
    object LoadBankAccountList : BankAccountListASF()
    object RefreshBankAccounts: BankAccountListASF()
    data class DeleteBankAccount(val bankAccount: BankAccount?): BankAccountListASF()
    data class SetBankAccountPrimary(val bankAccount: BankAccount): BankAccountListASF()

}


internal sealed class BankAccountListUSF : UiSideEffect {
    data class UpdateBankAccounts(val bankAccounts: List<BankAccount>): BankAccountListUSF()
    object GotoAddBankAccount: BankAccountListUSF()
    data class OpenBankAccountActionMenu(val view: View, val bankAccount: BankAccount): BankAccountListUSF()
    data class ShowProgressDialog(val header: String, val message: String) : BankAccountListUSF()
    object CloseProgressDialog: BankAccountListUSF()
    data class ShowSuccessMessage(val message: String) : BankAccountListUSF()
    data class ShowErrorDialog(val header: String, val message: String) : BankAccountListUSF()
    data class ShowDeleteBankConfirmationDialog(
        val headerDrawable: Drawable,
        val headerBackground: Drawable,
        val title: String,
        val detail: String,
        val actionText: String,
        val secondaryBtnText: String
    ): BankAccountListUSF()
}

internal object BankAccountListScreen : Screen("bank_account_list")

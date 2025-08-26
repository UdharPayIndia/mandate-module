package com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountlist.viewmodel

import android.graphics.drawable.Drawable
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountlist.statemachine.BankAccountListEvent
import com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountlist.statemachine.BankAccountListState
import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialogVM
import com.rocketpay.mandate.common.basemodule.main.viewmodel.BaseMainUM

internal class BankAccountListUM(private val dispatchEvent: (BankAccountListEvent) -> Unit) : BaseMainUM() {

    val emptyStateVisibility = ObservableInt()
    val illustration = ObservableField<Drawable>()
    val illustrationDescription = ObservableField<String>()
    val progressDialogVM = ProgressDialogVM({
        dispatchEvent(BankAccountListEvent.CloseDialogClick)
    })
    val bankDeleteConfirmationDialogVM = ProgressDialogVM(
        { dispatchEvent(BankAccountListEvent.DeleteBankAccountConfirmed) },
        { dispatchEvent(BankAccountListEvent.CloseDialogClick) }
    )
    fun onAddBankAccountClick() {
        dispatchEvent(BankAccountListEvent.AddBankAccount)
    }

    fun handleState(state: BankAccountListState) {
        if (state.bankAccounts.isEmpty()) {
            emptyStateVisibility.set(View.VISIBLE)
        } else {
            emptyStateVisibility.set(View.GONE)
        }
    }
}

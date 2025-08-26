package com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountlist.viewmodel

import android.graphics.drawable.Drawable
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.bankaccount.domain.entities.BankAccount
import com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountlist.statemachine.Bank
import com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountlist.statemachine.BankAccountListEvent
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.RecyclerViewAdapter
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal class ItemBankAccountVM(
    val bankAccount: BankAccount,
    val itemClick: (BankAccountListEvent) -> Unit,
    val position: Int
) : RecyclerViewAdapter.RecyclerViewItemViewModel {

    val background = ObservableField<Drawable>()
    val accountType = ObservableField<String>()
    val accountTypeTextColor = ObservableInt()
    val accountTypeVisibility = ObservableInt()
    val menuVisibility = ObservableInt()
    val verifiedVisibility = ObservableInt()
    val bankIcon = ObservableField<Drawable>()
    val bankName = ObservableField<String>()
    val bankAccountNumber = ObservableField<String>()
    val ifscCode = ObservableField<String>()
    val branch = ObservableField<String>()
    val ifscDetailsVisibility = ObservableBoolean()

    init {
        when (position) {
            0 -> {
                accountType.set(
                    ResourceManager.getInstance().getString(R.string.rp_primary_bank_account)
                )
                accountTypeTextColor.set(ResourceManager.getInstance().getColor(R.color.rp_grey_6))
                accountTypeVisibility.set(View.VISIBLE)
                background.set(ResourceManager.getInstance().getDrawable(R.color.rp_blue_2))
                menuVisibility.set(View.GONE)
                verifiedVisibility.set(
                    if (bankAccount.isVerified) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                )
            }

            1 -> {
                accountType.set(
                    ResourceManager.getInstance().getString(R.string.rp_other_bank_account)
                )
                accountTypeTextColor.set(ResourceManager.getInstance().getColor(R.color.rp_grey_3))
                accountTypeVisibility.set(View.VISIBLE)
                background.set(ResourceManager.getInstance().getDrawable(R.color.rp_grey_5))
                menuVisibility.set(View.VISIBLE)
                verifiedVisibility.set(View.GONE)
            }

            else -> {
                accountTypeVisibility.set(View.GONE)
                background.set(ResourceManager.getInstance().getDrawable(R.color.rp_grey_5))
                menuVisibility.set(View.VISIBLE)
                verifiedVisibility.set(View.GONE)
            }
        }


        val bank = Bank.getBank(bankAccount.ifsc)
        if (bank == null) {
            bankIcon.set(
                ResourceManager.getInstance().getDrawable(
                    R.drawable.rp_ic_bank,
                    ResourceManager.getInstance().getColor(R.color.rp_blue_4)
                )
            )
        } else {
            bankIcon.set(ResourceManager.getInstance().getDrawable(bank.icon))
        }

        if (bankAccount.accountNumber.isNullOrEmpty()) {
            bankName.set(bankAccount.upiId)
            bankAccountNumber.set(null)
            ifscDetailsVisibility.set(false)
        } else {
            ifscCode.set(bankAccount.ifsc)
            branch.set(bankAccount.branchName)
            bankName.set(bankAccount.bankName)
            bankAccountNumber.set(bankAccount.accountNumber)
            ifscDetailsVisibility.set(true)
        }
    }

    fun onMenuClick(view: View) {
        itemClick(BankAccountListEvent.MenuClick(view, bankAccount))
    }
}

package com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountlist.adapter

import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.bankaccount.domain.entities.BankAccount
import com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountlist.statemachine.BankAccountListEvent
import com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountlist.viewmodel.ItemBankAccountVM
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.RecyclerViewAdapter

internal class BankAccountAdapter : RecyclerViewAdapter() {

    lateinit var itemClick: (BankAccountListEvent) -> Unit

    companion object {
        const val VIEW_TYPE_BANK_ACCOUNT_ITEM = "VIEW_TYPE_BANK_ACCOUNT_ITEM"
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position].viewType) {
            VIEW_TYPE_BANK_ACCOUNT_ITEM -> R.layout.item_bank_account_rp
            else -> R.layout.item_bank_account_rp
        }
    }

    override fun getViewModel(position: Int): RecyclerViewItemViewModel {
        return ItemBankAccountVM(list[position].any as BankAccount, itemClick, position)
    }

    fun swapData(items: List<BankAccount>) {
        list.clear()
        list.addAll(items.sortedByDescending { it.isPrimary }.map { RecyclerViewItem(VIEW_TYPE_BANK_ACCOUNT_ITEM, it) })
        notifyDataSetChanged()
    }
}

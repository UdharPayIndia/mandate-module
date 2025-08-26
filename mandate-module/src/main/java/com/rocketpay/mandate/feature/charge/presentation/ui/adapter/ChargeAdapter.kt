package com.rocketpay.mandate.feature.charge.presentation.ui.adapter

import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.charge.domain.entities.Charge
import com.rocketpay.mandate.feature.charge.presentation.ui.viewmodel.ItemChargeVM
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.RecyclerViewAdapter

internal class ChargeAdapter : RecyclerViewAdapter() {

    lateinit var itemClick: (Charge) -> Unit

    companion object {
        const val VIEW_TYPE_CHARGE_1_ITEM = "VIEW_TYPE_CHARGE_1_ITEM"
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position].viewType) {
            VIEW_TYPE_CHARGE_1_ITEM -> R.layout.item_charge_rp
            else -> R.layout.item_charge_rp
        }
    }

    override fun getViewModel(position: Int): RecyclerViewItemViewModel {
        return ItemChargeVM(list[position] as Charge, itemClick)
    }

    fun swapData(items: List<String>) {
        list.clear()
        list.addAll(items.map { RecyclerViewItem(VIEW_TYPE_CHARGE_1_ITEM, it) })
        notifyDataSetChanged()
    }
}

package com.rocketpay.mandate.feature.settlements.presentation.ui.detail.adapter

import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.installment.domain.entities.Installment
import com.rocketpay.mandate.feature.settlements.presentation.ui.detail.statemachine.SettlementDetailEvent
import com.rocketpay.mandate.feature.settlements.presentation.ui.detail.viewmodel.ItemSettledInstallmentListVM
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.RecyclerViewAdapter

internal class SettledInstallmentListAdapter : RecyclerViewAdapter() {

    lateinit var itemClick: (SettlementDetailEvent) -> Unit

    companion object {
        const val VIEW_TYPE_ITEM = "VIEW_TYPE_ITEM"
        const val VIEW_TYPE_REFUNDED = "VIEW_TYPE_REFUNDED"
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_settled_installment_list_rp
    }

    override fun getViewModel(position: Int): RecyclerViewItemViewModel {
        return when(list[position].viewType){
            VIEW_TYPE_REFUNDED -> {
                ItemSettledInstallmentListVM(list[position].any as Installment, itemClick, true)
            }

            else -> {
                ItemSettledInstallmentListVM(list[position].any as Installment, itemClick, false)
            }
        }

    }

    fun swapData(installments: List<Installment>, refundedInstallments: List<Installment>) {
        list.clear()
        installments.forEach {
            list.add(RecyclerViewItem(VIEW_TYPE_ITEM, it))
        }
        refundedInstallments.forEach {
            list.add(RecyclerViewItem(VIEW_TYPE_REFUNDED, it))
        }
        notifyDataSetChanged()
    }
}

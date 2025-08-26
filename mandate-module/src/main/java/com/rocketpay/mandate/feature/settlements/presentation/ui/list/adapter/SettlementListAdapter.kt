package com.rocketpay.mandate.feature.settlements.presentation.ui.list.adapter

import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.list.viewmodel.ItemDateListVM
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.list.viewmodel.ItemEndOfListVM
import com.rocketpay.mandate.feature.settlements.domain.entities.PaymentOrder
import com.rocketpay.mandate.feature.settlements.presentation.ui.list.statemachine.SettlementListEvent
import com.rocketpay.mandate.feature.settlements.presentation.ui.list.viewmodel.ItemSettlementItemVM
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.RecyclerViewAdapter
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DateUtils
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal class SettlementListAdapter : RecyclerViewAdapter() {

    lateinit var itemClick: (SettlementListEvent) -> Unit

    companion object {
        const val VIEW_TYPE_ITEM = "VIEW_TYPE_ITEM"
        const val VIEW_TYPE_DATE = "VIEW_TYPE_DATE"
        const val VIEW_TYPE_END = "VIEW_TYPE_END"
        const val VIEW_LOADING = "VIEW_LOADING"

    }

    override fun getItemViewType(position: Int): Int {
        return when(list[position].viewType){
            VIEW_TYPE_DATE -> {
                R.layout.item_date_rp
            }
            VIEW_TYPE_END -> {
                R.layout.item_end_of_list_rp
            }
            VIEW_LOADING -> {
                R.layout.item_loading_rp
            }
            else -> {
                R.layout.item_settlement_rp
            }
        }
    }

    override fun getViewModel(position: Int): RecyclerViewItemViewModel {
        return when(list[position].viewType){
            VIEW_TYPE_DATE -> {
                ItemDateListVM(list[position].any as String, false)
            }
            VIEW_LOADING -> {
                ItemDateListVM("", true)
            }
            VIEW_TYPE_END -> {
                ItemEndOfListVM(
                    ResourceManager.getInstance()
                        .getString(R.string.rp_for_older_data_please_reach_out_to_us)
                )
            }
            else -> {
                ItemSettlementItemVM(list[position].any as PaymentOrder, itemClick)
            }
        }
    }

    fun swapData(
        items: List<PaymentOrder>,
        isLastPage: Boolean
    ) {
        list.clear()
        val groupedList = items.groupBy { DateUtils.getDate(it.createdAt, DateUtils.MONTH_WITH_YEAR) }
        groupedList.keys.forEach {
            list.add(RecyclerViewItem(VIEW_TYPE_DATE, it))
            groupedList.get(it)?.forEach {
                list.add(RecyclerViewItem(VIEW_TYPE_ITEM, it))
            }
        }
        if(isLastPage){
            list.add(RecyclerViewItem(VIEW_TYPE_END, null))
        }else{
            list.add(RecyclerViewItem(VIEW_LOADING, null))
        }
        notifyDataSetChanged()
    }
}

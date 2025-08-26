package com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.list.adapter

import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.installment.data.datasource.local.InstallmentWithMandateEntity
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.list.statemachine.PaymentTrackerListEvent
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.list.viewmodel.ItemDateListVM
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.list.viewmodel.ItemEndOfListVM
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.list.viewmodel.ItemInstallmentListVM
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.RecyclerViewAdapter
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DateUtils
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal class InstallmentListAdapter : RecyclerViewAdapter() {

    lateinit var itemClick: (PaymentTrackerListEvent) -> Unit
    private var hideTag: Boolean = false

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
                R.layout.item_installment_list_rp
            }
        }
    }

    override fun getViewModel(position: Int): RecyclerViewItemViewModel {
        return when(list[position].viewType){
            VIEW_TYPE_DATE -> {
                ItemDateListVM(list[position].any as String, true)
            }
            VIEW_LOADING -> {
                ItemDateListVM("", true)
            }
            VIEW_TYPE_END -> {
                ItemEndOfListVM(list[position].any as String)
            }
            else -> {
                ItemInstallmentListVM(
                    list[position].any as InstallmentWithMandateEntity,
                    itemClick,
                    hideTag
                )
            }
        }
    }

    fun swapData(items: List<InstallmentWithMandateEntity>,
                 isLastPage: Boolean,
                 hideTag: Boolean) {
        this.hideTag = hideTag
        list.clear()
        val groupedList =
            items.groupBy { DateUtils.getDate(it.installment.dueDate, DateUtils.MONTH_DATE_FORMAT_WITH_NAME) }
        groupedList.keys.forEach {
            list.add(RecyclerViewItem(VIEW_TYPE_DATE, it))
            groupedList.get(it)?.forEach {
                list.add(RecyclerViewItem(VIEW_TYPE_ITEM, it))
            }
        }
        if(isLastPage){
            if(hideTag) {
                list.add(RecyclerViewItem(VIEW_TYPE_END, ResourceManager.getInstance().getString(R.string.rp_for_older_data_please_reach_out_to_us)))
            }else{
                list.add(RecyclerViewItem(VIEW_TYPE_END, ResourceManager.getInstance().getString(R.string.rp_this_is_the_end_of_the_list)))
            }
        }else{
            list.add(RecyclerViewItem(VIEW_LOADING, null))
        }
        notifyDataSetChanged()
    }
}

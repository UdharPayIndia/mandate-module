package com.rocketpay.mandate.feature.settlements.presentation.ui.report.adpater

import androidx.databinding.ObservableField
import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.RecyclerViewAdapter
import com.rocketpay.mandate.feature.settlements.presentation.ui.report.statemachine.DownloadReportEvent
import com.rocketpay.mandate.feature.settlements.presentation.ui.report.statemachine.PeriodRangeEnum
import com.rocketpay.mandate.feature.settlements.presentation.ui.report.viewmodel.ItemPeriodRangeVM

internal class PeriodRangeAdapter : RecyclerViewAdapter() {

    lateinit var itemClick: (DownloadReportEvent) -> Unit
    lateinit var selectedPeriodRange: ObservableField<PeriodRangeEnum>
    lateinit var fromDate: ObservableField<String>
    lateinit var toDate: ObservableField<String>

    companion object {
        const val VIEW_TYPE_ITEM = "VIEW_TYPE_ITEM"
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_period_range_rp
    }

    override fun getViewModel(position: Int): RecyclerViewItemViewModel {
        return ItemPeriodRangeVM(list[position].any as PeriodRangeEnum, itemClick, selectedPeriodRange, fromDate, toDate)
    }

    fun swapData(items: List<PeriodRangeEnum>) {
        list.clear()
        list.addAll(items.map { RecyclerViewItem(VIEW_TYPE_ITEM, it) })
        notifyDataSetChanged()
    }
}

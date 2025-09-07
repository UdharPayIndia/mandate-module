package com.rocketpay.mandate.feature.settlements.presentation.ui.list.viewmodel

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.RecyclerViewAdapter
import com.rocketpay.mandate.feature.settlements.presentation.ui.list.statemachine.SettlementListEvent

internal class ItemSettlementHeaderVM(
    private val date: String,
    val isFirst: Boolean,
    val itemClick : (SettlementListEvent) -> Unit
): RecyclerViewAdapter.RecyclerViewItemViewModel {
    val dateText = ObservableField<String>(date)
    val downloadVisibility = ObservableBoolean(isFirst)

    fun onDownloadReportClick(){
        itemClick.invoke(SettlementListEvent.DownloadReportClick)
    }
}
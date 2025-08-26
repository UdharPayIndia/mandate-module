package com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.list.viewmodel

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.RecyclerViewAdapter

internal class ItemDateListVM(private val date: String, val centerAligned: Boolean): RecyclerViewAdapter.RecyclerViewItemViewModel {
    val isCenterAligned = ObservableBoolean(centerAligned)
    val dateText = ObservableField<String>(date)
}
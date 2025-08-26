package com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.list.viewmodel

import androidx.databinding.ObservableField
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.RecyclerViewAdapter

internal class ItemEndOfListVM(private val text: String): RecyclerViewAdapter.RecyclerViewItemViewModel {
    val titleText = ObservableField<String>(text)
}
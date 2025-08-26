package com.rocketpay.mandate.feature.settlements.presentation.ui.list.viewmodel

import androidx.databinding.ObservableField
import com.rocketpay.mandate.feature.settlements.domain.entities.PaymentOrder
import com.rocketpay.mandate.feature.settlements.presentation.ui.list.statemachine.SettlementListEvent
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.RecyclerViewAdapter
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.AmountUtils
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DateUtils

internal class ItemSettlementItemVM (
    val paymentOrder: PaymentOrder,
    val itemClick: (SettlementListEvent) -> Unit,
): RecyclerViewAdapter.RecyclerViewItemViewModel {
    val dateText = ObservableField<String>()
    val timeText = ObservableField<String>()
    val amountText = ObservableField<String>()

    init {
        dateText.set(DateUtils.getDate(paymentOrder.createdAt, DateUtils.MONTH_DATE_FORMAT))
        timeText.set(DateUtils.getDate(paymentOrder.createdAt, DateUtils.TIME_WITH_DAY_NAME))
        amountText.set(AmountUtils.format(paymentOrder.getSettlementAmount()))
    }

    fun onItemClick() {
        itemClick(SettlementListEvent.SettlementClick(paymentOrder))
    }
}

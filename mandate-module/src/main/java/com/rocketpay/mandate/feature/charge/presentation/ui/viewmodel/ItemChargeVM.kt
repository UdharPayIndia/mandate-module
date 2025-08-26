package com.rocketpay.mandate.feature.charge.presentation.ui.viewmodel

import com.rocketpay.mandate.feature.charge.domain.entities.Charge
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.RecyclerViewAdapter

internal class ItemChargeVM(
    val charge: Charge,
    val itemClick: (Charge) -> Unit
) : RecyclerViewAdapter.RecyclerViewItemViewModel {

    init {
    }

    fun onItemClick() {
        itemClick(charge)
    }
}

package com.rocketpay.mandate.feature.product.presentation.ui.order.list.viewmodel

import androidx.databinding.ObservableField
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.double
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.AmountUtils
import com.rocketpay.mandate.feature.product.domain.entities.ProductWallet

internal class ItemProductSummaryVM {
    val balanceValue = ObservableField<String>("0")
    val inProcessValue = ObservableField<String>("0")
    val purchasedValue = ObservableField<String>("0")
    val usedValue = ObservableField<String>("0")

    fun setData(productWallet: ProductWallet?) {
        balanceValue.set(AmountUtils.format(productWallet?.outstanding.double(), false))
        purchasedValue.set(AmountUtils.format(productWallet?.payin.double(), false))
        inProcessValue.set(AmountUtils.format(productWallet?.underPayout.double(), false))
        usedValue.set(AmountUtils.format(productWallet?.payout.double(), false))
    }
}
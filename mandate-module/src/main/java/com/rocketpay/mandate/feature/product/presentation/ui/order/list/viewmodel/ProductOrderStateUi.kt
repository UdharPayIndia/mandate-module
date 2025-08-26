package com.rocketpay.mandate.feature.product.presentation.ui.order.list.viewmodel

import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.product.domain.entities.ProductOrderStateEnum

internal sealed class ProductOrderStateUi(val text: Int, val background: Int) {
    object Successful : ProductOrderStateUi(R.string.rp_successful, R.color.rp_green_2)
    object Failed : ProductOrderStateUi(R.string.rp_failed, R.color.rp_red_2)
    object Pending : ProductOrderStateUi(R.string.rp_pending, R.color.rp_yellow_2)

    companion object {
        val map by lazy {
            mapOf(
                ProductOrderStateEnum.Created to Pending,
                ProductOrderStateEnum.Failed to Failed,
                ProductOrderStateEnum.Success to Successful,
                ProductOrderStateEnum.InProgress to Pending,
                ProductOrderStateEnum.SettlementFailed to Successful,
                ProductOrderStateEnum.SettlementSuccess to Successful,
                ProductOrderStateEnum.SettlementInitiated to Successful
            )
        }

        fun get(state: ProductOrderStateEnum): ProductOrderStateUi {
            return map[state] ?: Pending
        }
    }
}

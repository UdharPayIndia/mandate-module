package com.rocketpay.mandate.feature.product.presentation.ui.order.detail.statemachine

import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseEvent
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseState
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.Screen
import com.rocketpay.mandate.common.mvistatemachine.contract.AsyncSideEffect
import com.rocketpay.mandate.common.mvistatemachine.contract.UiSideEffect
import com.rocketpay.mandate.feature.product.domain.entities.ProductOrder

internal data class ProductOrderDetailState (
    val productOrderId: String = "",
    var productOrder: ProductOrder? = null,
    var productType: String = ""
) : BaseState(ProductOrderDetailScreen)

internal sealed class ProductOrderDetailEvent(name: String? = null) : BaseEvent(name) {
    data class LoadProductOrder(
        val productOrderId: String,
        val productType: String
    ) : ProductOrderDetailEvent()
    data class ProductOrderLoaded(
        val productOrder: ProductOrder?
    ) : ProductOrderDetailEvent()
    data class RocketPayTransactionIdCopyClick(
        val message: String,
        val link: String
    ): ProductOrderDetailEvent("installment_rocket_pay_txn_id_copy_click")
    data object RefreshClick: ProductOrderDetailEvent()
}

internal sealed class ProductOrderDetailASF : AsyncSideEffect {
    data class LoadProductOrder(
        val productOrderId: String,
        val productType: String
    ): ProductOrderDetailASF()
    data class RefreshProductOrder(
        val productType: String,
        val productOrderId: String
    ) : ProductOrderDetailASF()
}

internal sealed class ProductOrderDetailUSF : UiSideEffect {
    data class ShowToast(
        val message: String
    ) : ProductOrderDetailUSF()
    data class Copy(
        val message: String,
        val link: String
    ) : ProductOrderDetailUSF()
}

internal object ProductOrderDetailScreen : Screen("product_order_detail")

package com.rocketpay.mandate.feature.product.presentation.ui.order.list.statemachine

import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseEvent
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseState
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.Screen
import com.rocketpay.mandate.common.mvistatemachine.contract.AsyncSideEffect
import com.rocketpay.mandate.common.mvistatemachine.contract.UiSideEffect
import com.rocketpay.mandate.feature.product.domain.entities.ProductOrder
import com.rocketpay.mandate.feature.product.domain.entities.ProductWallet

internal data class ProductOrderListState(
    val productType: String = "",
    val productWallet: ProductWallet? = null,
    var productOrderList: List<ProductOrder> = emptyList(),
    val isRefreshing: Boolean = false,
) : BaseState(ProductOrderListScreen)

internal sealed class ProductOrderListEvent(name: String? = null) : BaseEvent(name) {
    data class LoadProductOrders(val productType: String) : ProductOrderListEvent()
    data class ProductOrderListLoaded(val list: List<ProductOrder>) : ProductOrderListEvent()
    data class ProductOrderClick(val order: ProductOrder) : ProductOrderListEvent()
    data object CloseClick : ProductOrderListEvent()
    data object RefreshClick : ProductOrderListEvent()
    data class UnableToRefresh(val message: String) : ProductOrderListEvent()
    data object DataRefreshed : ProductOrderListEvent()
    data class LoadProductWallet(val productType: String): ProductOrderListEvent()
    data class UpdateProductWallet(val productWallet: ProductWallet?): ProductOrderListEvent()

}

internal sealed class ProductOrderListASF : AsyncSideEffect {
    data class LoadProductWallet(val productType: String): ProductOrderListASF()
    data class LoadProductOrders(val productType: String) : ProductOrderListASF()
    data class RefreshData(val productType: String) : ProductOrderListASF()
}

internal sealed class ProductOrderListUSF : UiSideEffect {
    data class UpdateProductOrders(val productType: String, val productOrders: List<ProductOrder>) : ProductOrderListUSF()
    data object CloseScreen : ProductOrderListUSF()
    data class ShowToast(val message: String) : ProductOrderListUSF()
    data class OpenProductOrderDetails(val productOrder: ProductOrder, val productType: String) : ProductOrderListUSF()
}

internal object ProductOrderListScreen : Screen("product_order_list")

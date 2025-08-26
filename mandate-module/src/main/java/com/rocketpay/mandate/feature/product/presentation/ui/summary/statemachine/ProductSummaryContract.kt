package com.rocketpay.mandate.feature.product.presentation.ui.summary.statemachine

import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseEvent
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseState
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.Screen
import com.rocketpay.mandate.common.mvistatemachine.contract.AsyncSideEffect
import com.rocketpay.mandate.common.mvistatemachine.contract.UiSideEffect
import com.rocketpay.mandate.feature.product.domain.entities.ProductWallet

internal data class ProductSummaryState(
    val productType: String = "",
    val featureName: String = "",
    val productWallet: ProductWallet? = null,
    val askLocation: Boolean = false,
) : BaseState(ProductSummaryScreen)

internal sealed class ProductSummaryEvent(name: String? = null) : BaseEvent(name) {
    data object FetchProductWallet: ProductSummaryEvent()
    data class LoadProductWallet(val productType: String): ProductSummaryEvent()
    data class UpdateProductWalletBalance(val productWallet: ProductWallet?) : ProductSummaryEvent()
    data object HistoryClick: ProductSummaryEvent("")
    data class ShowLoading(val header: String, val message: String = "") : ProductSummaryEvent()
    data object CloseLoading: ProductSummaryEvent()
}

internal sealed class ProductSummaryASF : AsyncSideEffect {
    data class LoadProductWallet(val productType: String): ProductSummaryASF()
    data object FetchProductWallet: ProductSummaryASF()
}

internal sealed class ProductSummaryUSF : UiSideEffect {
    data class OpenHistoryPage(val productType: String) : ProductSummaryUSF()
    data class ShowToast(val message: String): ProductSummaryUSF()
    data class ShowLoading(val header: String, val message: String = "") : ProductSummaryUSF()
    data object CloseLoading: ProductSummaryUSF()
}

internal object ProductSummaryScreen : Screen("product_summary")

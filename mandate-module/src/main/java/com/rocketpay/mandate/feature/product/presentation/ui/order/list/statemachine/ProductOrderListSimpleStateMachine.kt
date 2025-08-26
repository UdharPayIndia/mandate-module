package com.rocketpay.mandate.feature.product.presentation.ui.order.list.statemachine

import androidx.lifecycle.viewModelScope
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseAnalyticsHandler
import com.rocketpay.mandate.common.mvistatemachine.contract.Next
import com.rocketpay.mandate.common.mvistatemachine.contract.collectIn
import com.rocketpay.mandate.common.mvistatemachine.viewmodel.simple.SimpleStateMachineImpl
import com.rocketpay.mandate.common.syncmanager.client.SyncManager
import com.rocketpay.mandate.feature.product.data.ProductOrderSyncer
import com.rocketpay.mandate.feature.product.data.ProductWalletSyncer
import com.rocketpay.mandate.feature.product.domain.entities.ProductOrderTypeEnum
import com.rocketpay.mandate.feature.product.domain.usecase.ProductUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay

internal class ProductOrderListSimpleStateMachine (
    private val productUseCase: ProductUseCase
) : SimpleStateMachineImpl<ProductOrderListEvent, ProductOrderListState, ProductOrderListASF, ProductOrderListUSF>(
    BaseAnalyticsHandler()
) {

    override fun startState(): ProductOrderListState {
        return ProductOrderListState()
    }

    override fun handleEvent(event: ProductOrderListEvent, state: ProductOrderListState): Next<ProductOrderListState?, ProductOrderListASF?, ProductOrderListUSF?> {
        return when (event) {
            is ProductOrderListEvent.LoadProductOrders -> {
                next(state.copy(productType = event.productType),
                    ProductOrderListASF.LoadProductOrders(event.productType)
                )
            }
            is ProductOrderListEvent.ProductOrderListLoaded -> {
                next(state.copy(productOrderList = event.list),
                    ProductOrderListUSF.UpdateProductOrders(state.productType, event.list)
                )
            }
            is ProductOrderListEvent.CloseClick -> {
                next(ProductOrderListUSF.CloseScreen)
            }
            is ProductOrderListEvent.ProductOrderClick -> {
                if (event.order.orderType != ProductOrderTypeEnum.Redeem.value) {
                    next(
                        ProductOrderListUSF.OpenProductOrderDetails(
                            event.order,
                            state.productType
                        )
                    )
                }else{
                    noChange()
                }
            }
            is ProductOrderListEvent.RefreshClick -> {
                next(state.copy(isRefreshing = true),
                    ProductOrderListASF.RefreshData(state.productType)
                )
            }
            is ProductOrderListEvent.UnableToRefresh -> {
                next(state.copy(isRefreshing = false), ProductOrderListUSF.ShowToast(event.message))
            }
            is ProductOrderListEvent.DataRefreshed -> {
                next(state.copy(isRefreshing = false))
            }
            is ProductOrderListEvent.LoadProductWallet -> {
                next(ProductOrderListASF.LoadProductWallet(event.productType))
            }
            is ProductOrderListEvent.UpdateProductWallet -> {
                next(state.copy(
                    productWallet = event.productWallet
                ))
            }
        }
    }

    override suspend fun handleAsyncSideEffect(sideEffect: ProductOrderListASF, dispatchEvent: (ProductOrderListEvent) -> Unit, scope: CoroutineScope) {
        when (sideEffect) {
            is ProductOrderListASF.LoadProductWallet -> {
                productUseCase.getProductWallet(sideEffect.productType).collectIn(viewModelScope){
                    dispatchEvent(ProductOrderListEvent.UpdateProductWallet(it))
                }
            }
            is ProductOrderListASF.LoadProductOrders -> {
                SyncManager.getInstance().enqueue(ProductOrderSyncer.TYPE)
                productUseCase.getProductOrders(sideEffect.productType).collectIn(viewModelScope) {
                    dispatchEvent(ProductOrderListEvent.ProductOrderListLoaded(it))
                }
            }
            is ProductOrderListASF.RefreshData -> {
                SyncManager.getInstance().enqueue(ProductWalletSyncer.TYPE)
                SyncManager.getInstance().enqueue(ProductOrderSyncer.TYPE)
                delay(100)
                dispatchEvent(ProductOrderListEvent.DataRefreshed)
            }
        }
    }
}

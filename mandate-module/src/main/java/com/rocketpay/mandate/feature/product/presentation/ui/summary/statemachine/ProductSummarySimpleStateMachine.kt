package com.rocketpay.mandate.feature.product.presentation.ui.summary.statemachine

import androidx.lifecycle.viewModelScope
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseAnalyticsHandler
import com.rocketpay.mandate.common.mvistatemachine.contract.Next
import com.rocketpay.mandate.common.mvistatemachine.contract.collectIn
import com.rocketpay.mandate.common.mvistatemachine.viewmodel.simple.SimpleStateMachineImpl
import com.rocketpay.mandate.common.syncmanager.client.SyncManager
import com.rocketpay.mandate.feature.product.data.ProductWalletSyncer
import com.rocketpay.mandate.feature.product.domain.entities.ProductTypeEnum
import com.rocketpay.mandate.feature.product.domain.usecase.ProductUseCase
import com.rocketpay.mandate.feature.property.domain.usecase.PropertyUseCase
import kotlinx.coroutines.CoroutineScope

internal class ProductSummarySimpleStateMachine(
    private val productUseCase: ProductUseCase,
    private val propertyUseCase: PropertyUseCase,
) : SimpleStateMachineImpl<ProductSummaryEvent, ProductSummaryState, ProductSummaryASF, ProductSummaryUSF>(
    BaseAnalyticsHandler()
) {

    override fun startState(): ProductSummaryState {
        return ProductSummaryState()
    }

    override fun handleEvent(event: ProductSummaryEvent, state: ProductSummaryState): Next<ProductSummaryState?, ProductSummaryASF?, ProductSummaryUSF?> {
        return when (event) {
            is ProductSummaryEvent.LoadProductWallet -> {
                next(state.copy(productType = event.productType),
                    ProductSummaryASF.LoadProductWallet(event.productType))
            }
            is ProductSummaryEvent.UpdateProductWalletBalance -> {
                if(state.productType == ProductTypeEnum.Installment.value){
                    next(
                        state.copy(productWallet = event.productWallet
                    ))
                }else{
                    next(state.copy(productWallet = event.productWallet))
                }
            }
            is ProductSummaryEvent.FetchProductWallet -> {
                next(ProductSummaryASF.FetchProductWallet)
            }
            is ProductSummaryEvent.HistoryClick -> {
                next(ProductSummaryUSF.OpenHistoryPage(state.productType))
            }
            is ProductSummaryEvent.ShowLoading -> {
                next(ProductSummaryUSF.ShowLoading(event.header, event.message))
            }
            is ProductSummaryEvent.CloseLoading -> {
                next(ProductSummaryUSF.CloseLoading)
            }
        }
    }

    override suspend fun handleAsyncSideEffect(sideEffect: ProductSummaryASF, dispatchEvent: (ProductSummaryEvent) -> Unit, scope: CoroutineScope) {
        when (sideEffect) {
            is ProductSummaryASF.LoadProductWallet -> {
                productUseCase.getProductWallet(sideEffect.productType).collectIn(viewModelScope){
                    dispatchEvent(ProductSummaryEvent.UpdateProductWalletBalance(it))
                }
            }
            is ProductSummaryASF.FetchProductWallet -> {
                SyncManager.getInstance().enqueue(ProductWalletSyncer.TYPE)
            }
        }
    }
}

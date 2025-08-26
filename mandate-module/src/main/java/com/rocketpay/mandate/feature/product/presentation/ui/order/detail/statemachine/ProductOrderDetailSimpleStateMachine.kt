package com.rocketpay.mandate.feature.product.presentation.ui.order.detail.statemachine

import androidx.lifecycle.viewModelScope
import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseAnalyticsHandler
import com.rocketpay.mandate.common.mvistatemachine.contract.Next
import com.rocketpay.mandate.common.mvistatemachine.contract.collectIn
import com.rocketpay.mandate.common.mvistatemachine.viewmodel.simple.SimpleStateMachineImpl
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import com.rocketpay.mandate.feature.product.domain.usecase.ProductUseCase
import com.udharpay.core.networkmanager.domain.entities.Outcome
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class ProductOrderDetailSimpleStateMachine(
    private val productUseCase: ProductUseCase
) : SimpleStateMachineImpl<ProductOrderDetailEvent, ProductOrderDetailState, ProductOrderDetailASF, ProductOrderDetailUSF>(
    BaseAnalyticsHandler()
) {

    override fun startState(): ProductOrderDetailState {
        return ProductOrderDetailState()
    }

    override fun handleEvent(
        event: ProductOrderDetailEvent,
        state: ProductOrderDetailState
    ): Next<ProductOrderDetailState?, ProductOrderDetailASF?, ProductOrderDetailUSF?> {
        return when (event) {
            is ProductOrderDetailEvent.LoadProductOrder -> {
                next(
                    state.copy(
                        productType = event.productType,
                        productOrderId = event.productOrderId
                    ),
                    ProductOrderDetailASF.LoadProductOrder(
                        event.productOrderId,
                        event.productType
                    )
                )
            }
            is ProductOrderDetailEvent.ProductOrderLoaded -> {
                next(state.copy(
                    productOrder = event.productOrder
                ))
            }
            is ProductOrderDetailEvent.RocketPayTransactionIdCopyClick -> {
                next(
                    ProductOrderDetailUSF.Copy(
                    event.message,
                    event.link)
                )
            }
            is ProductOrderDetailEvent.RefreshClick -> {
                next(
                    ProductOrderDetailASF.RefreshProductOrder(
                    state.productType,
                    state.productOrderId
                ),
                    ProductOrderDetailUSF.ShowToast(
                        ResourceManager.getInstance().getString(R.string.rp_refreshing_data)
                    )
                )
            }
        }
    }

    override suspend fun handleAsyncSideEffect(
        sideEffect: ProductOrderDetailASF,
        dispatchEvent: (ProductOrderDetailEvent) -> Unit,
        scope: CoroutineScope
    ) {
        when (sideEffect) {
            is ProductOrderDetailASF.LoadProductOrder -> {
                withContext(Dispatchers.IO){
                    productUseCase.getProductOrder(sideEffect.productOrderId)
                        .collectIn(viewModelScope){
                            dispatchEvent(ProductOrderDetailEvent.ProductOrderLoaded(it))
                        }
                }
            }
            is ProductOrderDetailASF.RefreshProductOrder -> {
                if (!sideEffect.productOrderId.isNullOrEmpty()) {
                    when(val outcome = productUseCase.refreshProductOrder(sideEffect.productOrderId)){
                        is Outcome.Success -> {

                        }
                        is Outcome.Error -> {

                        }
                    }
                }
            }
        }
    }
}

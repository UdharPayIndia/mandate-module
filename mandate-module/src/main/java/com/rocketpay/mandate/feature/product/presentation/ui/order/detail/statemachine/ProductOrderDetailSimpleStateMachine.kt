package com.rocketpay.mandate.feature.product.presentation.ui.order.detail.statemachine

import androidx.lifecycle.viewModelScope
import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseAnalyticsHandler
import com.rocketpay.mandate.common.mvistatemachine.contract.Next
import com.rocketpay.mandate.common.mvistatemachine.contract.collectIn
import com.rocketpay.mandate.common.mvistatemachine.viewmodel.simple.SimpleStateMachineImpl
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import com.rocketpay.mandate.feature.installment.domain.usecase.InstallmentUseCase
import com.rocketpay.mandate.feature.mandate.domain.usecase.MandateUseCase
import com.rocketpay.mandate.feature.product.domain.entities.ProductOrderTypeEnum
import com.rocketpay.mandate.feature.product.domain.entities.ProductTypeEnum
import com.rocketpay.mandate.feature.product.domain.usecase.ProductUseCase
import com.udharpay.core.networkmanager.domain.entities.Outcome
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class ProductOrderDetailSimpleStateMachine(
    private val productUseCase: ProductUseCase,
    private val mandateUseCase: MandateUseCase,
    private val installmentUseCase: InstallmentUseCase
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
                if(event.productOrder?.orderType in arrayOf(ProductOrderTypeEnum.Redeem.value,
                        ProductOrderTypeEnum.RedeemRefund.value)){
                    next(state.copy(productOrder = event.productOrder),
                        ProductOrderDetailASF.LoadReferenceDetails(
                            state.productType,
                            event.productOrder?.orderType.orEmpty(),
                            event.productOrder?.referenceId.orEmpty()))
                }else{
                    next(state.copy(productOrder = event.productOrder))
                }
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
            is ProductOrderDetailEvent.MandateLoaded -> {
                next(state.copy(mandate = event.mandate))
            }
            is ProductOrderDetailEvent.InstallmentLoaded -> {
                next(state.copy(installment = event.installment))
            }
            is ProductOrderDetailEvent.ReferenceItemClick -> {
                when(state.productOrder?.productType){
                    else -> {
                        if(state.mandate != null && !state.mandate.isDeleted){
                            next(ProductOrderDetailUSF.OpenMandate(state.mandate))
                        }else if(state.installment != null){
                            next(ProductOrderDetailUSF.OpenInstallment(state.installment))
                        }else {
                            noChange()
                        }
                    }
                }
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
            is ProductOrderDetailASF.LoadReferenceDetails -> {
                var referenceId = sideEffect.referenceId
                if(sideEffect.orderType == ProductOrderTypeEnum.RedeemRefund.value) {
                    val productOrder = productUseCase.getProductOrderNonLive(sideEffect.referenceId)
                    if (productOrder != null) {
                        referenceId = productOrder.referenceId
                    }
                }
                when(sideEffect.productType){
                    else -> {
                        val mandate = mandateUseCase.getMandateNonLiveByGatewayId(referenceId)
                        if(mandate != null){
                            dispatchEvent(ProductOrderDetailEvent.MandateLoaded(mandate))
                        }else{
                            val installment = installmentUseCase.getInstallmentNonLive(referenceId)
                            if (installment != null) {
                                dispatchEvent(ProductOrderDetailEvent.InstallmentLoaded(installment))
                            }
                        }
                    }

                }
            }
        }
    }
}

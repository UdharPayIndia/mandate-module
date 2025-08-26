package com.rocketpay.mandate.feature.product.presentation.injection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rocketpay.mandate.feature.product.domain.usecase.ProductUseCase
import com.rocketpay.mandate.feature.product.presentation.ui.order.detail.statemachine.ProductOrderDetailSimpleStateMachine
import com.rocketpay.mandate.feature.product.presentation.ui.order.list.statemachine.ProductOrderListSimpleStateMachine
import com.rocketpay.mandate.feature.product.presentation.ui.summary.statemachine.ProductSummarySimpleStateMachine
import com.rocketpay.mandate.feature.property.domain.usecase.PropertyUseCase


@Suppress("UNCHECKED_CAST")
internal open class ProductStateMachineFactory(
    private val productUseCase: ProductUseCase,
    private val propertyUseCase: PropertyUseCase
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ProductSummarySimpleStateMachine::class.java) -> ProductSummarySimpleStateMachine(productUseCase, propertyUseCase) as T
            modelClass.isAssignableFrom(ProductOrderListSimpleStateMachine::class.java) -> ProductOrderListSimpleStateMachine(productUseCase) as T
            modelClass.isAssignableFrom(ProductOrderDetailSimpleStateMachine::class.java) -> ProductOrderDetailSimpleStateMachine(productUseCase) as T
            else -> throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}

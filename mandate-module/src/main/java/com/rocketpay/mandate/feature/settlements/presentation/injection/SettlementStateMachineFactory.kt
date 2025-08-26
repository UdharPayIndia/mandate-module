package com.rocketpay.mandate.feature.settlements.presentation.injection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rocketpay.mandate.feature.installment.domain.usecase.InstallmentUseCase
import com.rocketpay.mandate.feature.settlements.domain.usecase.PaymentOrderUseCase
import com.rocketpay.mandate.feature.settlements.presentation.ui.detail.statemachine.SettlementDetailStateMachine
import com.rocketpay.mandate.feature.settlements.presentation.ui.list.statemachine.SettlementListStateMachine
import com.rocketpay.mandate.feature.settlements.presentation.ui.main.statemachine.SettlementMainStateMachine
import com.rocketpay.mandate.feature.property.domain.usecase.PropertyUseCase

@Suppress("UNCHECKED_CAST")
internal open class SettlementStateMachineFactory(
    private val paymentOrderUseCase: PaymentOrderUseCase,
    private val propertyUseCase: PropertyUseCase,
    private val installmentUseCase: InstallmentUseCase
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SettlementMainStateMachine::class.java) -> SettlementMainStateMachine(paymentOrderUseCase) as T
            modelClass.isAssignableFrom(SettlementListStateMachine::class.java) -> SettlementListStateMachine(paymentOrderUseCase, propertyUseCase) as T
            modelClass.isAssignableFrom(SettlementDetailStateMachine::class.java) -> SettlementDetailStateMachine(paymentOrderUseCase, installmentUseCase) as T
            else -> throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}

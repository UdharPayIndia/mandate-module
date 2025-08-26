package com.rocketpay.mandate.feature.charge.presentation.injection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rocketpay.mandate.feature.charge.domain.usecase.ChargeUseCase
import com.rocketpay.mandate.feature.charge.presentation.ui.statemachine.ChargeSimpleStateMachine

@Suppress("UNCHECKED_CAST")
internal open class ChargeStateMachineFactory(private val chargeUseCase: ChargeUseCase): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ChargeSimpleStateMachine::class.java) -> ChargeSimpleStateMachine(chargeUseCase) as T
            else -> throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}

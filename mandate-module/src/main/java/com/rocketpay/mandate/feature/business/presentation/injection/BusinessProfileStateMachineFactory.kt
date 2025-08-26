package com.rocketpay.mandate.feature.business.presentation.injection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rocketpay.mandate.feature.business.domain.usecase.BusinessProfileUseCase
import com.rocketpay.mandate.feature.business.presentation.ui.statemachine.BusinessProfileStateMachine
import com.rocketpay.mandate.feature.property.domain.usecase.PropertyUseCase

@Suppress("UNCHECKED_CAST")
internal open class BusinessProfileStateMachineFactory(
    val businessProfileUseCase: BusinessProfileUseCase,
    val propertyUseCase: PropertyUseCase
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(BusinessProfileStateMachine::class.java) -> BusinessProfileStateMachine(businessProfileUseCase,propertyUseCase) as T
            else -> throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}

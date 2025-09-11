package com.rocketpay.mandate.feature.aboutus.presentation.injection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rocketpay.mandate.feature.property.domain.usecase.PropertyUseCase
import com.udharpay.feature.aboutus.presentation.ui.statemachine.AboutUsStateMachine

@Suppress("UNCHECKED_CAST")
internal open class AboutUsStateMachineFactory(private val propertyUseCase: PropertyUseCase): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AboutUsStateMachine::class.java) -> AboutUsStateMachine(propertyUseCase) as T
            else -> throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}

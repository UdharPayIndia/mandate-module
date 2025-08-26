package com.rocketpay.mandate.feature.login.presentation.injection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rocketpay.mandate.feature.kyc.domain.usecase.KycUseCase
import com.rocketpay.mandate.feature.login.domain.usecase.LoginUseCase
import com.rocketpay.mandate.feature.login.presentation.ui.login.statemachine.LoginStateMachine
import com.rocketpay.mandate.feature.property.domain.usecase.PropertyUseCase

@Suppress("UNCHECKED_CAST")
internal open class LoginStateMachineFactory(
    private val loginUseCase: LoginUseCase,
    private val kycUseCase: KycUseCase,
    private val propertyUseCase: PropertyUseCase
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginStateMachine::class.java) -> LoginStateMachine(loginUseCase, kycUseCase, propertyUseCase) as T
            else -> throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}

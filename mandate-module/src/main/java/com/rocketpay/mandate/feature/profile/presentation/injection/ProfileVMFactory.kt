package com.rocketpay.mandate.feature.profile.presentation.injection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rocketpay.mandate.feature.kyc.domain.usecase.KycUseCase
import com.rocketpay.mandate.feature.login.domain.usecase.LoginUseCase
import com.rocketpay.mandate.feature.profile.presentation.ui.statemachine.UserProfileStateMachine
import com.rocketpay.mandate.feature.property.domain.usecase.PropertyUseCase


@Suppress("UNCHECKED_CAST")
internal class ProfileVMFactory(
    private val kycUseCase: KycUseCase,
    private val propertyUseCase: PropertyUseCase,
    private val loginUseCase: LoginUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(UserProfileStateMachine::class.java) -> UserProfileStateMachine(kycUseCase, propertyUseCase, loginUseCase) as T
            else -> throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}
package com.rocketpay.mandate.main.presentation.injection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rocketpay.mandate.feature.kyc.domain.usecase.KycUseCase
import com.rocketpay.mandate.feature.login.domain.usecase.LoginUseCase
import com.rocketpay.mandate.main.presentation.viewmodel.RpMainVM

@Suppress("UNCHECKED_CAST")
internal class RpMainVMFactory(
    private val loginUseCase: LoginUseCase,
    private val kycUseCase: KycUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RpMainVM::class.java) -> RpMainVM(loginUseCase, kycUseCase) as T
            else -> throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}
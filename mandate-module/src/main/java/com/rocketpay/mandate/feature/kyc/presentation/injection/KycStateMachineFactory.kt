package com.rocketpay.mandate.feature.kyc.presentation.injection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rocketpay.mandate.feature.image.domain.usecase.ImageUseCase
import com.rocketpay.mandate.feature.kyc.domain.usecase.KycUseCase
import com.rocketpay.mandate.feature.kyc.presentation.ui.identityintro.statemachine.KycIdentityIntroStateMachine
import com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.statemachine.KycStateMachine
import com.rocketpay.mandate.feature.property.domain.usecase.PropertyUseCase

@Suppress("UNCHECKED_CAST")
internal open class KycStateMachineFactory(private val kycUseCase: KycUseCase,
                                  private val imageUseCase: ImageUseCase,
                                  private val propertyUseCase: PropertyUseCase
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(KycStateMachine::class.java) -> KycStateMachine(kycUseCase, imageUseCase, propertyUseCase) as T
            modelClass.isAssignableFrom(KycIdentityIntroStateMachine::class.java) -> KycIdentityIntroStateMachine(kycUseCase) as T
            else -> throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}

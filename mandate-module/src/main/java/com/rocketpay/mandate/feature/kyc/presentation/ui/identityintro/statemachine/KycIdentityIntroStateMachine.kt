package com.rocketpay.mandate.feature.kyc.presentation.ui.identityintro.statemachine

import com.rocketpay.mandate.feature.kyc.domain.usecase.KycUseCase
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseAnalyticsHandler
import com.rocketpay.mandate.common.mvistatemachine.contract.Next
import com.rocketpay.mandate.common.mvistatemachine.viewmodel.simple.SimpleStateMachineImpl
import kotlinx.coroutines.CoroutineScope

internal class KycIdentityIntroStateMachine(
    private val kycUseCase: KycUseCase,
) : SimpleStateMachineImpl<KycIdentityIntroEvent, KycIdentityIntroState, KycIdentityIntroASF, KycIdentityIntroUSF>(
    BaseAnalyticsHandler()
) {
    override fun startState(): KycIdentityIntroState {
        return KycIdentityIntroState()
    }

    override fun handleEvent(
        event: KycIdentityIntroEvent,
        state: KycIdentityIntroState,
    ): Next<KycIdentityIntroState?, KycIdentityIntroASF?, KycIdentityIntroUSF?> {
        return when (event) {
            is KycIdentityIntroEvent.LoadData ->{
                next(state.copy(panName = event.panName))
            }
            is KycIdentityIntroEvent.NextClick -> {
                next(KycIdentityIntroUSF.CloseBottomSheet)
            }
        }
    }

    override suspend fun handleAsyncSideEffect(
        sideEffect: KycIdentityIntroASF,
        dispatchEvent: (KycIdentityIntroEvent) -> Unit,
        viewModelScope: CoroutineScope,
    ) {
    }

}
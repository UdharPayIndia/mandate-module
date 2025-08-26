package com.rocketpay.mandate.feature.charge.presentation.ui.statemachine

import com.rocketpay.mandate.feature.charge.domain.usecase.ChargeUseCase
import com.rocketpay.mandate.feature.charge.presentation.ui.view.ChargeFlowType
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseAnalyticsHandler
import com.rocketpay.mandate.common.mvistatemachine.contract.Next
import com.rocketpay.mandate.common.mvistatemachine.viewmodel.simple.SimpleStateMachineImpl
import kotlinx.coroutines.CoroutineScope

internal class ChargeSimpleStateMachine(
    private val chargeUseCase: ChargeUseCase
): SimpleStateMachineImpl<ChargeEvent, ChargeState, ChargeASF, ChargeUSF>(BaseAnalyticsHandler()) {

    override fun startState(): ChargeState {
        return ChargeState()
    }

    override fun handleEvent(
        event: ChargeEvent,
        state: ChargeState
    ): Next<ChargeState?, ChargeASF?, ChargeUSF?> {
        return when (event) {
            is ChargeEvent.LoadData -> {
                val charge = chargeUseCase.getCharge()
                val chargeFlowType = ChargeFlowType.get(event.flowType)
                if (chargeFlowType == null) {
                    next(state.copy(charge = charge, isCashFreeEnabled = event.isCashFreeEnabled))
                } else {
                    next(state.copy(charge = charge, flowType = chargeFlowType, event.isCashFreeEnabled))
                }
            }
            is ChargeEvent.DismissClick -> {
                next(ChargeUSF.CloseBottomSheet)
            }
        }
    }

    override suspend fun handleAsyncSideEffect(
        sideEffect: ChargeASF,
        dispatchEvent: (ChargeEvent) -> Unit,
        viewModelScope: CoroutineScope
    ) {
        // No side async effect as of now
    }
}

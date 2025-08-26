package com.rocketpay.mandate.feature.settlements.presentation.ui.main.statemachine

import com.rocketpay.mandate.feature.settlements.data.PaymentOrderSyncer
import com.rocketpay.mandate.feature.settlements.domain.usecase.PaymentOrderUseCase
import com.rocketpay.mandate.common.mvistatemachine.contract.Next
import com.rocketpay.mandate.common.mvistatemachine.viewmodel.simple.SimpleStateMachineImpl
import com.rocketpay.mandate.common.syncmanager.client.SyncManager
import kotlinx.coroutines.CoroutineScope

internal class SettlementMainStateMachine (
    private val paymentOrderUseCase: PaymentOrderUseCase,
) : SimpleStateMachineImpl<SettlementMainEvent, SettlementMainState, SettlementMainASF, SettlementMainUSF>(
    SettlementMainAnalyticsHandler()
) {

    override fun startState(): SettlementMainState {
        return SettlementMainState()
    }

    override fun handleEvent(
        event: SettlementMainEvent,
        state: SettlementMainState
    ): Next<SettlementMainState?, SettlementMainASF?, SettlementMainUSF?> {
        return when (event) {
            is SettlementMainEvent.Init -> {
                next(state.copy(
                    selectedIndex = event.selectedIndex
                ), SettlementMainASF.Init)
            }
            is SettlementMainEvent.UpdateSelectedState ->{
                if(event.updateFragment){
                    next(state.copy(selectedIndex = event.selectedIndex),
                        SettlementMainUSF.UpdateFragment(event.selectedIndex))
                }else{
                    next(state.copy(selectedIndex = event.selectedIndex))
                }
            }
        }
    }

    override suspend fun handleAsyncSideEffect(
        sideEffect: SettlementMainASF,
        dispatchEvent: (SettlementMainEvent) -> Unit,
        viewModelScope: CoroutineScope
    ) {
        when(sideEffect){
            is SettlementMainASF.Init -> {
                SyncManager.getInstance().enqueue(PaymentOrderSyncer.TYPE)
            }
        }
    }

}

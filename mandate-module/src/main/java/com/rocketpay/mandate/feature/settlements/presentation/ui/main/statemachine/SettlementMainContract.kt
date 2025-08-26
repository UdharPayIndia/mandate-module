package com.rocketpay.mandate.feature.settlements.presentation.ui.main.statemachine

import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseEvent
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseState
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.Screen
import com.rocketpay.mandate.common.mvistatemachine.contract.AsyncSideEffect
import com.rocketpay.mandate.common.mvistatemachine.contract.UiSideEffect

internal data class SettlementMainState(
    val selectedIndex: Int = 0,
) : BaseState(SettlementMainScreen)

internal sealed class SettlementMainEvent(name: String? = null) : BaseEvent(name) {
    data class Init(val selectedIndex: Int): SettlementMainEvent()
    data class UpdateSelectedState(val selectedIndex: Int, val updateFragment: Boolean = false): SettlementMainEvent("")
}

internal sealed class SettlementMainASF : AsyncSideEffect {
    data object Init: SettlementMainASF()
}

internal sealed class SettlementMainUSF : UiSideEffect {
    data class ShowToast(val message: String) : SettlementMainUSF()
    data class UpdateFragment(val selectedIndex: Int): SettlementMainUSF()
}

internal object SettlementMainScreen : Screen("settlement_main")

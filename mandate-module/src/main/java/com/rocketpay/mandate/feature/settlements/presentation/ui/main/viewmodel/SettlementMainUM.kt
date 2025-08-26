package com.rocketpay.mandate.feature.settlements.presentation.ui.main.viewmodel

import com.rocketpay.mandate.feature.settlements.presentation.ui.main.statemachine.SettlementMainEvent
import com.rocketpay.mandate.feature.settlements.presentation.ui.main.statemachine.SettlementMainState
import com.rocketpay.mandate.common.basemodule.main.viewmodel.BaseMainUM

internal class SettlementMainUM(private val dispatchEvent: (SettlementMainEvent) -> Unit) : BaseMainUM() {

    fun handleState(state: SettlementMainState) {
    }

}
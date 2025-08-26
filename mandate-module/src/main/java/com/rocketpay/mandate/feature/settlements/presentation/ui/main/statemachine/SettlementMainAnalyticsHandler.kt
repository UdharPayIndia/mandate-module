package com.rocketpay.mandate.feature.settlements.presentation.ui.main.statemachine

import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseAnalyticsHandler

internal class SettlementMainAnalyticsHandler: BaseAnalyticsHandler<SettlementMainEvent, SettlementMainState>()  {

    override fun updateEventParameter(
        event: SettlementMainEvent,
        state: SettlementMainState,
        paramBuilder: HashMap<String, Any>
    ) {
        super.updateEventParameter(event, state, paramBuilder)
        when(event){
            is SettlementMainEvent.UpdateSelectedState -> {
                if(!event.updateFragment){
                    when(event.selectedIndex){
                        1 -> event.name = "transaction_tab_clicked"
                        else -> event.name = "settlement_tab_clicked"
                    }
                }
            }
            else -> {

            }
        }
    }

}
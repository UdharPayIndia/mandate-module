package com.rocketpay.mandate.feature.installment.presentation.ui.installmentupdate.statemachine

import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseAnalyticsHandler

internal class InstallmentUpdateAnalyticsHandler: BaseAnalyticsHandler<InstallmentUpdateEvent, InstallmentUpdateState>()  {

    override fun updateEventParameter(
        event: InstallmentUpdateEvent,
        state: InstallmentUpdateState,
        paramBuilder: HashMap<String, Any>
    ) {
        super.updateEventParameter(event, state, paramBuilder)
        when(event){
            is InstallmentUpdateEvent.SaveClick -> {
                paramBuilder["payment_mode"] = state.mode?.translation ?: ""
                paramBuilder["comment"] = state.reason ?: ""
            }
            else -> {

            }
        }
    }
}
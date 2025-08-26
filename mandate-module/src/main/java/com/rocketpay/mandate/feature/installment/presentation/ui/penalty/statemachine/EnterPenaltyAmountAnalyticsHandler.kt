package com.rocketpay.mandate.feature.installment.presentation.ui.penalty.statemachine

import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseAnalyticsHandler

internal class EnterPenaltyAmountAnalyticsHandler : BaseAnalyticsHandler<EnterPenaltyAmountEvent, EnterPenaltyAmountState>() {

    override fun updateCommonEventParameter(
        state: EnterPenaltyAmountState,
        paramBuilder: HashMap<String, Any>
    ) {
        super.updateCommonEventParameter(state, paramBuilder)
    }

    override fun updateEventParameter(
        event: EnterPenaltyAmountEvent,
        state: EnterPenaltyAmountState,
        paramBuilder: HashMap<String, Any>
    ) {
        super.updateEventParameter(event, state, paramBuilder)
        when(event) {
            is EnterPenaltyAmountEvent.ChargePenaltyConfirmed,
            is EnterPenaltyAmountEvent.ChargePenaltyDismiss -> {
                paramBuilder["installment_id"] = state.installmentId ?: ""
                paramBuilder["penalty_amount"] = state.installmentAmount
            }
            else -> {

            }
        }
    }

}

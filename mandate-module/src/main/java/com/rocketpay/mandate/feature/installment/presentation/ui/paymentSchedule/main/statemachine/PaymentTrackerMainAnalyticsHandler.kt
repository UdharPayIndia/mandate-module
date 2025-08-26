package com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.main.statemachine

import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.list.viewmodel.PaymentTrackerType
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseAnalyticsHandler

internal class PaymentTrackerMainAnalyticsHandler: BaseAnalyticsHandler<PaymentTrackerMainEvent, PaymentTrackerMainState>()  {

    override fun updateEventParameter(
        event: PaymentTrackerMainEvent,
        state: PaymentTrackerMainState,
        paramBuilder: HashMap<String, Any>
    ) {
        super.updateEventParameter(event, state, paramBuilder)
        paramBuilder["isSuperKeyFlow"] = state.isSuperKeyFlow
        when(event){
            is PaymentTrackerMainEvent.UpdateSelectedState -> {
                if(event.updateFragment){
                    when(event.selectedIndex){
                        PaymentTrackerType.Outstanding.index -> event.name = "sk_outstanding_tab"
                        PaymentTrackerType.Upcoming.index -> event.name = "sk_upcoming_tab"
                        PaymentTrackerType.Collected.index -> event.name = "sk_collected_tab"
                    }
                }
            }
            else -> {

            }
        }
    }

}
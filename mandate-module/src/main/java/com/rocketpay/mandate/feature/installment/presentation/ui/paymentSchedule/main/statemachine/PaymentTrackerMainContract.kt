package com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.main.statemachine

import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseEvent
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseState
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.Screen
import com.rocketpay.mandate.common.mvistatemachine.contract.AsyncSideEffect
import com.rocketpay.mandate.common.mvistatemachine.contract.UiSideEffect

internal data class PaymentTrackerMainState(
    val installmentAmountSummary: InstallmentAmountSummary? = null,
    val isRefreshing: Boolean = false,
    val selectedIndex: Int = 1,
    val isSuperKeyFlow: Boolean = false
) : BaseState(PaymentTrackerMainScreen)

internal sealed class PaymentTrackerMainEvent(name: String? = null) : BaseEvent(name) {
    data class Init(val selectedIndex: Int, val isSuperKeyFlow: Boolean): PaymentTrackerMainEvent()
    data class InstallmentAmountSummaryLoaded(val summary: InstallmentAmountSummary?) : PaymentTrackerMainEvent()
    data class UpdateSelectedState(val selectedIndex: Int, val updateFragment: Boolean = false): PaymentTrackerMainEvent("")
    data class InstallmentSummaryUpdated(
        val isOutstandingUpdated: Boolean,
        val isUpcomingUpdated: Boolean,
        val isCollectedUpdated: Boolean): PaymentTrackerMainEvent()
    data object ViewSettlementDashBoard: PaymentTrackerMainEvent("settlement_dashboard_click")
}

internal sealed class PaymentTrackerMainASF : AsyncSideEffect {
    data class LoadInstallmentSummary(val isSuperKeyFlow: Boolean) : PaymentTrackerMainASF()
    data class CheckIsUpdated(val updatedSummary: InstallmentAmountSummary?,
                                    val existingSummary: InstallmentAmountSummary?): PaymentTrackerMainASF()
}

internal sealed class PaymentTrackerMainUSF : UiSideEffect {
    data class ShowToast(val message: String) : PaymentTrackerMainUSF()
    data class UpdateFragment(val selectedIndex: Int): PaymentTrackerMainUSF()
    data class RefreshInstallments(val isOutstandingUpdated: Boolean,
                               val isUpcomingUpdated: Boolean,
                               val isCollectedUpdated: Boolean): PaymentTrackerMainUSF()
    data object ViewSettlementDashBoard: PaymentTrackerMainUSF()
}

internal object PaymentTrackerMainScreen : Screen("payment_tracker")

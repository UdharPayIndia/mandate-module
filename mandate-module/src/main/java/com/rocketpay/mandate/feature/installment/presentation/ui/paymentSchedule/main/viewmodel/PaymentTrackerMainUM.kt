package com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.main.viewmodel

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.main.statemachine.PaymentTrackerMainEvent
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.main.statemachine.PaymentTrackerMainState
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.double
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.int
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.AmountUtils
import com.rocketpay.mandate.common.basemodule.main.viewmodel.BaseMainUM
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal class PaymentTrackerMainUM(private val dispatchEvent: (PaymentTrackerMainEvent) -> Unit) : BaseMainUM() {

    val outstandingAmount = ObservableField<String>()
    val upcomingAmount = ObservableField<String>()
    val collectedAmount = ObservableField<String>()

    val isOutstandingSelected = ObservableBoolean()
    val outstandingCountText = ObservableField<String>(ResourceManager.getInstance().getString(R.string.rp_outstanding))

    val isUpcomingSelected = ObservableBoolean()
    val upcomingCountText = ObservableField<String>(ResourceManager.getInstance().getString(R.string.rp_upcoming))

    val isCollectedSelected = ObservableBoolean()
    val collectedCountText = ObservableField<String>(ResourceManager.getInstance().getString(R.string.rp_collected))

    fun handleState(state: PaymentTrackerMainState) {
        val outstanding = state.installmentAmountSummary?.outstandingAmount.double()
        val upcoming = state.installmentAmountSummary?.upcomingAmount.double()
        val collected = state.installmentAmountSummary?.collectedAmount.double()
        outstandingAmount.set(AmountUtils.format(outstanding))
        upcomingAmount.set(AmountUtils.format(upcoming))
        collectedAmount.set(AmountUtils.format(collected))

        val outstandingCount = state.installmentAmountSummary?.outstandingCount.int()
        val upcomingCount = state.installmentAmountSummary?.upcomingCount.int()
        val collectedCount = state.installmentAmountSummary?.collectedCount.int()
        outstandingCountText.set("${ResourceManager.getInstance().getString(R.string.rp_outstanding)} (${outstandingCount})")
        upcomingCountText.set("${ResourceManager.getInstance().getString(R.string.rp_upcoming)} (${upcomingCount})")
        collectedCountText.set("${ResourceManager.getInstance().getString(R.string.rp_collected)} (${collectedCount})")

        isOutstandingSelected.set(state.selectedIndex == 0)
        isUpcomingSelected.set(state.selectedIndex == 1)
        isCollectedSelected.set(state.selectedIndex == 2)

    }

    fun onOutstandingClicked(){
        dispatchEvent(PaymentTrackerMainEvent.UpdateSelectedState(0, true))
    }

    fun onUpcomingClicked(){
        dispatchEvent(PaymentTrackerMainEvent.UpdateSelectedState(1, true))
    }

    fun onCollectedClicked(){
        dispatchEvent(PaymentTrackerMainEvent.UpdateSelectedState(2, true))
    }

    fun onViewSettlementsClick(){
        dispatchEvent(PaymentTrackerMainEvent.ViewSettlementDashBoard)
    }
}
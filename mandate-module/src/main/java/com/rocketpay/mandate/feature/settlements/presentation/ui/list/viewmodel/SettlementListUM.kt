package com.rocketpay.mandate.feature.settlements.presentation.ui.list.viewmodel

import android.text.SpannableString
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.settlements.presentation.ui.list.statemachine.SettlementListEvent
import com.rocketpay.mandate.feature.settlements.presentation.ui.list.statemachine.SettlementListState
import com.rocketpay.mandate.feature.settlements.presentation.ui.utils.SettlementUtils
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.getSpannable
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.setTextSize
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.AmountUtils
import com.rocketpay.mandate.common.basemodule.main.viewmodel.BaseMainUM
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal class SettlementListUM (private val dispatchEvent: (SettlementListEvent) -> Unit) : BaseMainUM() {


    val emptyStateVisibility = ObservableBoolean()
    val titleText = ObservableField<String>()
    val subText = ObservableField<String>()
    val pendingSettlementAmount = ObservableField<SpannableString>()

    val settlementBannerMessage = ObservableField<String>()
    val settlementBannerClickable = ObservableBoolean()

    var isLastPage: Boolean = false
    var isLoading: Boolean = false
    var settlementSize: Int = 0

    fun handleState(state: SettlementListState) {
        settlementSize = state.paymentOrders.size
        isLastPage = state.isLastPage
        isLoading = state.isLoading

        pendingSettlementAmount.set(
            AmountUtils.format(state.outstandingSettlementBalance)
            .getSpannable().setTextSize(AmountUtils.CURRENCY_SYMBOL, 0.60f))

        settlementBannerMessage.set(state.bannerMessage)
        settlementBannerClickable.set(state.bannerMessage.contains("kyc", ignoreCase = true)
                || state.bannerMessage.contains(SettlementUtils.BANK_ACCOUNT, ignoreCase = true))

        if (state.paymentOrders.isEmpty()) {
            emptyStateVisibility.set(true)
            titleText.set(ResourceManager.getInstance().getString(R.string.rp_no_settlements_present))
            subText.set(ResourceManager.getInstance().getString(R.string.rp_future_settlements_will_be_shown_here))
        }else{
            emptyStateVisibility.set(false)
        }
    }

    fun onSettlementBannerClick() {
        if (settlementBannerMessage.get()?.contains(SettlementUtils.BANK_ACCOUNT) == true) {
            dispatchEvent(SettlementListEvent.SettlementAccountBannerClick)
        } else {
            dispatchEvent(SettlementListEvent.SettlementBannerClick)
        }
    }
}
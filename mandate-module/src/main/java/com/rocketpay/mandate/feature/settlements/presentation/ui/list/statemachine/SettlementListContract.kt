package com.rocketpay.mandate.feature.settlements.presentation.ui.list.statemachine

import com.rocketpay.mandate.feature.settlements.domain.entities.PaymentOrder
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseEvent
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseState
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.Screen
import com.rocketpay.mandate.common.mvistatemachine.contract.AsyncSideEffect
import com.rocketpay.mandate.common.mvistatemachine.contract.UiSideEffect

internal data class SettlementListState(
    val outstandingSettlementBalance: Double = 0.0,
    val paymentOrders: ArrayList<PaymentOrder> = arrayListOf(),
    val isLoading: Boolean = false,
    val isLastPage: Boolean = false,
    val lastFetchedTimeStamp: Long = 0L,
    val limit: Int = 20,
    val orderByDesc: Boolean = false,
    val isRefreshing: Boolean = false,
    val bannerMessage: String = ""
) : BaseState(SettlementListScreen)

internal sealed class SettlementListEvent(name: String? = null) : BaseEvent(name) {
    data object Init: SettlementListEvent()
    data object LoadOutstandingSettlementBalance: SettlementListEvent()
    data class SetOutstandingSettlementBalance(
        val value: Double
    ): SettlementListEvent()
    data class LoadSettlements(
        val orderByDesc: Boolean,
    ): SettlementListEvent()
    data object LoadSettlementBannerInfo: SettlementListEvent()
    data class SettlementLoaded(
        val paymentOrders: ArrayList<PaymentOrder>,
    ) : SettlementListEvent()
    data class SettlementClick(
        val paymentOrder: PaymentOrder
    ) : SettlementListEvent("settlement_item_clicked")
    data object FetchNextSettlements: SettlementListEvent()
    data object RefreshClick: SettlementListEvent()
    data class UpdateDataRefreshed(val isRefreshing: Boolean):SettlementListEvent()
    data class UpdateBannerMessage(val message: String): SettlementListEvent()
    data object SettlementBannerClick: SettlementListEvent("settlement_kyc_banner_click")
    data object SettlementAccountBannerClick: SettlementListEvent("settlement_account_banner_click")
}

internal sealed class SettlementListASF : AsyncSideEffect {
    data object Init: SettlementListASF()
    data object LoadOutstandingSettlementBalance: SettlementListASF()
    data class LoadSettlements(
        val lastFetchedTimeStamp: Long,
        val limit: Int,
        val orderByDesc: Boolean,
    ) : SettlementListASF()
    data object LoadSettlementBannerInfo: SettlementListASF()
    data object RefreshData: SettlementListASF()
}

internal sealed class SettlementListUSF : UiSideEffect {
    data class UpdateSettlements(
        val paymentOrders: List<PaymentOrder>,
        val isLastPage: Boolean
    ): SettlementListUSF()
    data class ShowToast(val message: String) : SettlementListUSF()
    data class OpenSettlementDetails(val settlementId: String): SettlementListUSF()
    data object OpenKyc: SettlementListUSF()
    data object OpenBankAccount: SettlementListUSF()
}

internal object SettlementListScreen : Screen("settlement_list")

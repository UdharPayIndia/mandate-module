package com.rocketpay.mandate.feature.mandate.presentation.ui.mandatelist.statemachine

import com.rocketpay.mandate.common.basemodule.common.presentation.bottomsheetselection.ItemDialogBottomSheet
import com.rocketpay.mandate.feature.mandate.domain.entities.Mandate
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseEvent
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseState
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.Screen
import com.rocketpay.mandate.common.mvistatemachine.contract.AsyncSideEffect
import com.rocketpay.mandate.common.mvistatemachine.contract.UiSideEffect

internal data class MandateListState(
    val mandates: List<Mandate> = emptyList(),
    val isRefreshing: Boolean = false,
    val isMandateCreated: Boolean = false,

    val mandateSearchFilterSort: MandateSearchFilterSort = MandateSearchFilterSort(),
    val filteredMandates: List<Mandate> = emptyList(),
    val isTokenizationEnabled: Boolean = true,
) : BaseState(MandateListScreen)

internal sealed class MandateListEvent(name: String? = null) : BaseEvent(name) {
    data object LoadMandates: MandateListEvent()
    data class MandatesLoaded(val mandates: List<Mandate>, val isMandateCreated: Boolean) : MandateListEvent()
    data class MandateClick(val mandate: Mandate) : MandateListEvent("mandate_click")
    object AddMandateClick: MandateListEvent("create_mandate_click")
    object RefreshClick: MandateListEvent()
    data class UpdateDataRefreshed(val isRefreshing: Boolean) : MandateListEvent()

    // Search, Filter, Sort events
    object SearchClick: MandateListEvent("search_click")
    data class SearchCloseClick(val fromUser: Boolean): MandateListEvent("search_close_click")
    object SortClick: MandateListEvent("sort_click")
    object FilterClick: MandateListEvent("filter_click")

    data class QueryChange(val text: String): MandateListEvent()
    data class SortSelected(val sortType: String): MandateListEvent("sort_selected")
    data class FilterSelected(val filterType: String): MandateListEvent("filter_selected")

    data class MandateFiltered(val filteredMandate: List<Mandate>, val mandateSearchFilterSort: MandateSearchFilterSort) : MandateListEvent()
    data object OpenPaymentTracker: MandateListEvent("payment_tracker_click")
    data object UserProfileClick: MandateListEvent()
}

internal sealed class MandateListASF : AsyncSideEffect {
    data class LoadMandates(val tag: String) : MandateListASF()
    object RefreshData : MandateListASF()
    data class FilterMandate(
        val mandates: List<Mandate>,
        val mandateSearchFilterSort: MandateSearchFilterSort,
    ): MandateListASF()
}

internal sealed class MandateListUSF : UiSideEffect {
    data class UpdateMandates(val mandates: List<Mandate>): MandateListUSF()
    data class ShowToast(val message: String) : MandateListUSF()

    // Search, Filter, Sort events
    object OpenKeyboard: MandateListUSF()
    object CloseKeyboard: MandateListUSF()
    data class ShowSortDropDown(val currentPosition: MandateSort, val sortTypes: List<ItemDialogBottomSheet>): MandateListUSF()
    data class ShowFilterDropDown(val currentPosition: MandateFilter, val sortTypes: List<ItemDialogBottomSheet>): MandateListUSF()
    data class RestFilter(val mandateSearchFilterSort: MandateSearchFilterSort): MandateListUSF()

    data class GotoMandateDetail(val mandate: Mandate, val isManual: Boolean = false) : MandateListUSF()
    object GotoAddMandate: MandateListUSF()
    data class OpenBankAccountAddition(val source: String): MandateListUSF()
    data object OpenPaymentTracker: MandateListUSF()
    data object UserProfileClick: MandateListUSF()
}

internal object MandateListScreen : Screen("mandate_list")

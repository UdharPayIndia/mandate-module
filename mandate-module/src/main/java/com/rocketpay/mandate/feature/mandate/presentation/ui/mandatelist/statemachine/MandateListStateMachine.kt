package com.rocketpay.mandate.feature.mandate.presentation.ui.mandatelist.statemachine

import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.mandate.data.MandateSyncer
import com.rocketpay.mandate.feature.mandate.domain.entities.Mandate
import com.rocketpay.mandate.feature.mandate.domain.entities.MandateState
import com.rocketpay.mandate.feature.mandate.domain.entities.PaymentMethod
import com.rocketpay.mandate.feature.mandate.domain.usecase.MandateUseCase
import com.rocketpay.mandate.main.GlobalState
import com.rocketpay.mandate.common.basemodule.common.presentation.bottomsheetselection.ItemDialogBottomSheet
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseAnalyticsHandler
import com.rocketpay.mandate.common.mvistatemachine.contract.Next
import com.rocketpay.mandate.common.mvistatemachine.contract.collectIn
import com.rocketpay.mandate.common.mvistatemachine.viewmodel.simple.SimpleStateMachineImpl
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import com.rocketpay.mandate.common.syncmanager.client.SyncManager
import kotlinx.coroutines.CoroutineScope

internal class MandateListStateMachine(
    private val mandateUseCase: MandateUseCase,
) : SimpleStateMachineImpl<MandateListEvent, MandateListState, MandateListASF, MandateListUSF>(
    BaseAnalyticsHandler()
) {

    override fun startState(): MandateListState {
        return MandateListState()
    }

    override fun handleEvent(
        event: MandateListEvent,
        state: MandateListState
    ): Next<MandateListState?, MandateListASF?, MandateListUSF?> {
        return when (event) {
            is MandateListEvent.LoadMandates -> {
                next(
                    state.copy(),
                    MandateListASF.LoadMandates(state.screen.name))
            }
            is MandateListEvent.MandatesLoaded -> {
                next(
                    state.copy(
                        mandates = event.mandates,
                        isMandateCreated = event.isMandateCreated
                    ),
                    MandateListASF.FilterMandate(
                        event.mandates,
                        state.mandateSearchFilterSort,
                    )
                )
            }
            is MandateListEvent.MandateClick -> {
                next(MandateListUSF.GotoMandateDetail(event.mandate,
                    event.mandate.paymentMethodDetail.method == PaymentMethod.Manual))
            }
            is MandateListEvent.AddMandateClick -> {
                next(MandateListUSF.GotoAddMandate)
            }
            is MandateListEvent.RefreshClick -> {
                next(state.copy(isRefreshing = true), MandateListASF.RefreshData)
            }
            is MandateListEvent.UpdateDataRefreshed -> {
                next(state.copy(isRefreshing = event.isRefreshing))
            }
            /////////////////////////////////////////////////////////////////////////
            //////////////// Search, Filter, Sort Event handling/////////////////////
            /////////////////////////////////////////////////////////////////////////

            is MandateListEvent.SearchClick -> {
                next(MandateListUSF.OpenKeyboard)
            }
            is MandateListEvent.SearchCloseClick -> {
                val newState = if(event.fromUser){
                    state.copy(mandateSearchFilterSort = MandateSearchFilterSort(queryText = "",
                        mandateFilter = state.mandateSearchFilterSort.mandateFilter,
                        mandateSort = state.mandateSearchFilterSort.mandateSort))
                }else{
                    state
                }
                next(newState, MandateListUSF.CloseKeyboard)
            }
            is MandateListEvent.SortClick -> {
                next(MandateListUSF.ShowSortDropDown(state.mandateSearchFilterSort.mandateSort, getMandateSortList()))
            }
            is MandateListEvent.FilterClick -> {
                next(MandateListUSF.ShowFilterDropDown(state.mandateSearchFilterSort.mandateFilter, getMandateFilterList(state.mandates)))
            }
            is MandateListEvent.QueryChange -> {
                val mandateSearchFilterSort = state.mandateSearchFilterSort.copy(queryText = event.text)
                next(MandateListASF.FilterMandate(state.mandates, mandateSearchFilterSort))
            }
            is MandateListEvent.SortSelected -> {
                val mandateSearchFilterSort = state.mandateSearchFilterSort.copy(mandateSort = MandateSort.get(event.sortType))
                next(MandateListASF.FilterMandate(state.mandates, mandateSearchFilterSort))
            }
            is MandateListEvent.FilterSelected -> {
                val mandateSearchFilterSort = state.mandateSearchFilterSort.copy(mandateFilter = MandateFilter.get(event.filterType))
                next(MandateListASF.FilterMandate(state.mandates, mandateSearchFilterSort))
            }
            is MandateListEvent.MandateFiltered -> {
                next(
                    state.copy(
                        filteredMandates = event.filteredMandate,
                        mandateSearchFilterSort = event.mandateSearchFilterSort
                    ),
                    MandateListUSF.UpdateMandates(event.filteredMandate)
                )
            }
            is MandateListEvent.OpenPaymentTracker -> {
                next(MandateListUSF.OpenPaymentTracker)
            }
            is MandateListEvent.UserProfileClick -> {
                next(MandateListUSF.UserProfileClick)
            }
        }
    }

    private fun getMandateFilterList(
        mandates: List<Mandate>
    ): List<ItemDialogBottomSheet> {
        val items = mutableListOf<ItemDialogBottomSheet>()
        val groupedMandates = mandates.groupBy { it.state }
        MandateFilter.map.entries.forEach {
            val itemDialogBottomSheet = ItemDialogBottomSheet(
                it.value.value,
                getMandateFilterSuffixText(it.value.value, groupedMandates, mandates.size),
                getMandateFilterPostfixText(it.value.value)
            )
            items.add(itemDialogBottomSheet)
        }
        return items
    }

    private fun getMandateFilterSuffixText(
        value: String,
        groupedMandates: Map<MandateState, List<Mandate>>,
        totalMandates: Int
    ): String {
        val pair = when(MandateFilter.get(value)) {
            MandateFilter.AllMandates -> {
                Pair(R.string.rp_all_mandates, totalMandates)
            }
            MandateFilter.Active -> {
                val userAccepted = groupedMandates[MandateState.UserAccepted]?.size ?: 0
                val active = groupedMandates[MandateState.Active]?.size ?: 0
                val partiallyCollected = groupedMandates[MandateState.PartiallyCollected]?.size ?: 0
                Pair(R.string.rp_active, active + userAccepted + partiallyCollected)
            }
            MandateFilter.Pending -> {
                Pair(R.string.rp_pending, groupedMandates[MandateState.Pending]?.size ?: 0)
            }
            MandateFilter.Completed -> {
                Pair(R.string.rp_finished, groupedMandates[MandateState.Completed]?.size ?: 0)
            }
            MandateFilter.Paused -> {
                Pair(R.string.rp_paused, groupedMandates[MandateState.Paused]?.size ?: 0)
            }
            MandateFilter.Others -> {
                val cancelled = groupedMandates[MandateState.Cancelled]?.size ?: 0
                val terminated = groupedMandates[MandateState.Terminated]?.size ?: 0
                val expired = groupedMandates[MandateState.Expired]?.size ?: 0
                Pair(R.string.rp_mandate_filter_others, cancelled + terminated + expired)
            }
        }

        val stateText = ResourceManager.getInstance().getString(pair.first)
        return ResourceManager.getInstance().getString(R.string.rp_filter_place_holder, stateText, pair.second)
    }

    private fun getMandateFilterPostfixText(value: String): String {
        return if (MandateFilter.get(value) is MandateFilter.AllMandates) {
            ResourceManager.getInstance().getString(R.string.rp_filter_default)
        } else {
            ""
        }
    }

    private fun getMandateSortList(
    ): List<ItemDialogBottomSheet> {
        val items = mutableListOf<ItemDialogBottomSheet>()
        MandateSort.map.entries.forEach {
            val itemDialogBottomSheet = ItemDialogBottomSheet(
                it.value.value,
                ResourceManager.getInstance().getString(it.value.text),
                getMandateSortPostfixText(it.value.value)
            )
            items.add(itemDialogBottomSheet)
        }
        return items
    }

    private fun getMandateSortPostfixText(value: String): String {
        return if (MandateSort.get(value) is MandateSort.LatestUpdate) {
            ResourceManager.getInstance().getString(R.string.rp_sort_default)
        } else {
            ""
        }
    }

    override suspend fun handleAsyncSideEffect(
        sideEffect: MandateListASF,
        dispatchEvent: (MandateListEvent) -> Unit,
        viewModelScope: CoroutineScope
    ) {
        when (sideEffect) {
            is MandateListASF.LoadMandates -> {
                GlobalState.isRefreshing.collectIn(viewModelScope){
                    dispatchEvent(MandateListEvent.UpdateDataRefreshed(it))
                }
                mandateUseCase.getAllMandatesWithSubText().collectIn(viewModelScope) {
                    dispatchEvent(MandateListEvent.MandatesLoaded(it, it.isNotEmpty()))
                }
            }
            MandateListASF.RefreshData -> {
                SyncManager.getInstance().enqueue(MandateSyncer.TYPE)
            }

            is MandateListASF.FilterMandate -> {
                val filteredMandate = mandateUseCase.getFilteredMandate(
                    sideEffect.mandates,
                    sideEffect.mandateSearchFilterSort,
                )
                dispatchEvent(MandateListEvent.MandateFiltered(filteredMandate, sideEffect.mandateSearchFilterSort))
            }
        }
    }

}

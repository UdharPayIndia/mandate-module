package com.rocketpay.mandate.feature.mandate.presentation.ui.mandatelist.viewmodel

import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatelist.statemachine.MandateFilter
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatelist.statemachine.MandateListEvent
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatelist.statemachine.MandateListState
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatelist.statemachine.MandateSort
import com.rocketpay.mandate.common.basemodule.main.viewmodel.BaseMainUM
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal class MandateListUM(private val dispatchEvent: (MandateListEvent) -> Unit) : BaseMainUM() {

    val emptyStateVisibility = ObservableInt()
    val downArrowVisibility = ObservableInt()
    val createMandateText = ObservableField<String>()

    val mandateSummaryVisibility = ObservableBoolean()
    val isCreateMandateEnabled = ObservableBoolean()
    val searchKeyWord = ObservableField<String>()
    val isOutstandingBalanceUpdated = MutableLiveData<Boolean>()

    fun onAddMandateClick() {
        dispatchEvent(MandateListEvent.AddMandateClick)
    }

    fun handleState(state: MandateListState) {
        if (state.mandates.isEmpty()) {
            searchFilterSortLayoutVisibility.set(View.GONE)
            downArrowVisibility.set(View.VISIBLE)
            emptyStateVisibility.set(View.GONE)
        } else {
            searchFilterSortLayoutVisibility.set(View.VISIBLE)
            downArrowVisibility.set(View.GONE)
            if (state.filteredMandates.isEmpty()) {
                if (state.mandateSearchFilterSort.queryText.isNotEmpty() ||
                    state.mandateSearchFilterSort.mandateSort !is MandateSort.LatestUpdate
                    || state.mandateSearchFilterSort.mandateFilter !is MandateFilter.AllMandates) {
                    emptyStateVisibility.set(View.VISIBLE)
                } else {
                    emptyStateVisibility.set(View.GONE)
                }
            } else {
                emptyStateVisibility.set(View.GONE)
            }
        }

        handleCreateButton(state)
        handleFilterLayout(state)
        mandateSummaryVisibility.set(true)
        isCreateMandateEnabled.set(true)
        searchKeyWord.set(state.mandateSearchFilterSort.queryText)
    }

    private fun handleCreateButton(state: MandateListState) {
        if (state.isMandateCreated) {
            createMandateText.set(ResourceManager.getInstance().getString(R.string.rp_create_new_payment))
        } else {
            createMandateText.set(ResourceManager.getInstance().getString(R.string.rp_step_1_create_new_mandate))
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////

    val searchFilterSortLayoutVisibility = ObservableInt()
    val filterVisibility = ObservableInt()
    val sortVisibility = ObservableInt()

    private fun handleFilterLayout(state: MandateListState) {
        if (state.mandateSearchFilterSort.mandateFilter is MandateFilter.AllMandates) {
            filterVisibility.set(View.GONE)
        } else {
            filterVisibility.set(View.VISIBLE)
        }

        if (state.mandateSearchFilterSort.mandateSort is MandateSort.LatestUpdate) {
            sortVisibility.set(View.GONE)
        } else {
            sortVisibility.set(View.VISIBLE)
        }
    }

    fun onSearchClick() {
        dispatchEvent(MandateListEvent.SearchClick)
    }

    fun onCloseClick() {
        dispatchEvent(MandateListEvent.SearchCloseClick(true))
    }

    fun onSortClick() {
        dispatchEvent(MandateListEvent.SortClick)
    }

    fun onFilterClick() {
        dispatchEvent(MandateListEvent.FilterClick)
    }

    fun onSearchFocusChange(hasFocus: Boolean) {
        if (hasFocus) {
            dispatchEvent(MandateListEvent.SearchClick)
        } else {
            dispatchEvent(MandateListEvent.SearchCloseClick(false))
        }
    }

    fun onSearchTextChanged(s: CharSequence) {
        dispatchEvent(MandateListEvent.QueryChange(s.toString()))
    }

    fun onPaymentTrackerClicked(){
        dispatchEvent(MandateListEvent.OpenPaymentTracker)
    }
}

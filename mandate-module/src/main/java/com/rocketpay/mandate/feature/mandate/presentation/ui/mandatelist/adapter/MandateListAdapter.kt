package com.rocketpay.mandate.feature.mandate.presentation.ui.mandatelist.adapter

import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.mandate.domain.entities.Mandate
import com.rocketpay.mandate.feature.mandate.domain.entities.SubtextUiState
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatelist.statemachine.MandateListEvent
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatelist.viewmodel.ItemMandateListVM
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.RecyclerViewAdapter

internal class MandateListAdapter : RecyclerViewAdapter() {

    lateinit var itemClick: (MandateListEvent) -> Unit

    companion object {
        const val VIEW_TYPE_MANDATE = "VIEW_TYPE_MANDATE"
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_mandate_list_rp
    }

    override fun getViewModel(position: Int): RecyclerViewItemViewModel {
        return ItemMandateListVM(list[position].any as Mandate, itemClick)
    }

    fun swapData(items: List<Mandate>) {
        list.clear()
        list.addAll(items.sortedByDescending { it.uiState == SubtextUiState.Unread }
            .map { RecyclerViewItem(VIEW_TYPE_MANDATE, it) })
        notifyDataSetChanged()
    }
}

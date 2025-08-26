package com.rocketpay.mandate.common.basemodule.common.presentation.bottomsheetselection

import androidx.databinding.ObservableField
import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.RecyclerViewAdapter

internal class DialogBottomSheetSelectionAdapter(
    private val onItemClick: (itemDialogBottomSheet: ItemDialogBottomSheet) -> Unit,
    private val selectedPosition: ObservableField<String>
) : RecyclerViewAdapter() {

    companion object {
        const val VIEW_TYPE_ITEM_FILTER = "VIEW_TYPE_ITEM_FILTER"
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.bottom_sheet_selection_item_rp
    }

    override fun getViewModel(position: Int): RecyclerViewItemViewModel {
        return DialogBottomSheetSelectionItem(
            onItemClick,
            selectedPosition,
            list[position].any as ItemDialogBottomSheet
        )
    }

    fun swapData(sortOrFilters: List<ItemDialogBottomSheet>?) {
        list.clear()
        sortOrFilters?.forEach {
            list.add(RecyclerViewItem(VIEW_TYPE_ITEM_FILTER, it))
        }
        notifyDataSetChanged()
    }
}

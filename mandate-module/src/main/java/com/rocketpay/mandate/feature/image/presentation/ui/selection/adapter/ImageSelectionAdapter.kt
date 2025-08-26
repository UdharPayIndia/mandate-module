package com.rocketpay.mandate.feature.image.presentation.ui.selection.adapter

import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.image.presentation.ui.selection.statemachine.ImageSelectionEvent
import com.rocketpay.mandate.feature.image.presentation.ui.selection.viewmodel.DocumentSelectionMedia
import com.rocketpay.mandate.feature.image.presentation.ui.selection.viewmodel.ItemImageSelectionVM
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.RecyclerViewAdapter

internal class ImageSelectionAdapter  : RecyclerViewAdapter() {

    lateinit var itemClick: (ImageSelectionEvent) -> Unit

    companion object {
        const val VIEW_TYPE_ITEM = "VIEW_TYPE_ITEM"
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position].viewType) {
            VIEW_TYPE_ITEM -> R.layout.item_image_selection_rp
            else -> R.layout.item_image_selection_rp
        }
    }

    override fun getViewModel(position: Int): RecyclerViewItemViewModel {
        return ItemImageSelectionVM(
            list[position].any as DocumentSelectionMedia,
            itemClick,
            position
        )
    }

    fun swapData(items: List<DocumentSelectionMedia>) {
        list.clear()
        list.addAll(items.map { RecyclerViewItem(VIEW_TYPE_ITEM, it) })
        notifyDataSetChanged()
    }
}

package com.rocketpay.mandate.feature.image.presentation.ui.selection.viewmodel

import android.graphics.drawable.Drawable
import androidx.databinding.ObservableField
import com.rocketpay.mandate.feature.image.presentation.ui.selection.statemachine.ImageSelectionEvent
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.RecyclerViewAdapter

internal class ItemImageSelectionVM (
    val imageSelection: DocumentSelectionMedia,
    val itemClick: (ImageSelectionEvent) -> Unit,
    val position: Int
) : RecyclerViewAdapter.RecyclerViewItemViewModel {

    val name = ObservableField<String>(imageSelection.name)
    val drawable = ObservableField<Drawable>(imageSelection.icon)

    fun onItemClick() {
        itemClick(imageSelection.event)
    }
}

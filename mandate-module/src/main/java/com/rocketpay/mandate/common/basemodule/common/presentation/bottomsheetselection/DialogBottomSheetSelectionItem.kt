package com.rocketpay.mandate.common.basemodule.common.presentation.bottomsheetselection

import android.graphics.drawable.Drawable
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.RecyclerViewAdapter
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal class DialogBottomSheetSelectionItem(
    val onItemClick: (position: ItemDialogBottomSheet) -> Unit,
    private val selectedPosition: ObservableField<String>,
    private val itemDialogBottomSheet: ItemDialogBottomSheet
) : RecyclerViewAdapter.RecyclerViewItemViewModel {

    val prefix = ObservableField<String>()
    val suffix = ObservableField<String>()
    val prefixTextColor = ObservableInt()
    val suffixTextColor = ObservableInt()
    val selectedDrawable = ObservableField<Drawable>()

    init {
        updateUi(selectedPosition.get())
        observeItemSelected(selectedPosition)
    }

    private fun updateUi(selectedPosition: String?) {
        prefix.set(itemDialogBottomSheet.prefix)
        suffix.set(itemDialogBottomSheet.suffix)

        if (itemDialogBottomSheet.type == selectedPosition) {
            selectedDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_radio_filled))
        } else {
            selectedDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_radio_empty))
        }
    }

    private fun observeItemSelected(selectedPosition: ObservableField<String>) {
        selectedPosition.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                updateUi((sender as ObservableField<*>).get() as String?)
            }
        })
    }

    fun onItemClick() {
        onItemClick(itemDialogBottomSheet)
        selectedPosition.set(itemDialogBottomSheet.type)
    }
}

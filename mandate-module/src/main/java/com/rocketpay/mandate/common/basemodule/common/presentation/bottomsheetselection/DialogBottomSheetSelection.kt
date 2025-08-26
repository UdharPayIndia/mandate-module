package com.rocketpay.mandate.common.basemodule.common.presentation.bottomsheetselection

import android.content.Context
import androidx.databinding.ObservableField
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.DividerItemDecoration
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import com.rocketpay.mandate.databinding.BottomSheetSelectionRpBinding

internal class DialogBottomSheetSelection(
    context: Context,
    val title: String,
    currentPosition: String,
    val subtitle: String? = null,
    val onItemClick: (itemDialogBottomSheet: ItemDialogBottomSheet) -> Unit,
) : BottomSheetDialog(context, R.style.RpBottomSheetDialog) {

    private val selectedPosition = ObservableField(currentPosition)
    private var dialogBottomSheetSelectionAdapter: DialogBottomSheetSelectionAdapter

    init {
        val dialogBottomSheetSelectionBinding = BottomSheetSelectionRpBinding.inflate(layoutInflater)
        dialogBottomSheetSelectionBinding.vm = DialogBottomSheetSelectionVM(title, subtitle)
        dialogBottomSheetSelectionAdapter = DialogBottomSheetSelectionAdapter(onItemClick, selectedPosition)
        dialogBottomSheetSelectionBinding.rvKycModes.adapter = dialogBottomSheetSelectionAdapter
        dialogBottomSheetSelectionBinding.rvKycModes.addItemDecoration(
            DividerItemDecoration(
                ResourceManager.getInstance().getDrawable(R.drawable.rp_divider_hr),
                showFirstDivider = true,
                showLastDivider = false,
                extraPaddingLeft = 0.toFloat(),
                extraPaddingRight = 0.toFloat(),
                dividerStartPosition = 0
            )
        )
        setContentView(dialogBottomSheetSelectionBinding.root)
    }

    fun updateList(itemDialogBottomSheetList: List<ItemDialogBottomSheet>) {
        dialogBottomSheetSelectionAdapter.swapData(itemDialogBottomSheetList)
    }

    fun updateCurrentPosition(selectedPosition: String) {
        this.selectedPosition.set(selectedPosition)
    }
}

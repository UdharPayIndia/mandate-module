package com.rocketpay.mandate.common.basemodule.common.presentation.bottomsheetconfirmation

import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialogStatus

internal class BottomSheetConfirmationVM(
    private val actionBtnClick: (ProgressDialogStatus) -> Unit,
    private val secondaryActionBtnClick: ((ProgressDialogStatus) -> Unit)? = null
) {

    var state: ProgressDialogStatus = ProgressDialogStatus.Progress

    val headerUrl = ObservableField<String>()
    val headerBackgroundDrawable = ObservableField<Drawable>()
    val headerDrawable = ObservableField<Drawable>()

    val title = ObservableField<SpannableString>()
    val detail = ObservableField<SpannableString>()
    val progressBarVisibility = ObservableInt()

    val actionBtnText = ObservableField<String>()
    val secondaryActionBtnText = ObservableField<String>()

    open fun setInitState(
        headerBackground: Drawable,
        headerIcon: Drawable,
        titleText: SpannableString,
        detailText: String,
        actionText: String?,
        secondaryBtnText: String? = null
    ) {
        state = ProgressDialogStatus.Init
        headerBackgroundDrawable.set(headerBackground)
        headerDrawable.set(headerIcon)
        title.set(titleText)
        detail.set(SpannableString(detailText))
        progressBarVisibility.set(View.GONE)
        actionBtnText.set(actionText)
        secondaryActionBtnText.set(secondaryBtnText)
    }

    open fun onActionBtnClick() {
        actionBtnClick(state)
    }

    open fun onSecondaryActionBtnClick() {
        secondaryActionBtnClick?.invoke(state)
    }

}
package com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog

import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal open class ProgressDialogVM(
    private val actionBtnClick: (ProgressDialogStatus) -> Unit,
    private val secondaryActionBtnClick: ((ProgressDialogStatus) -> Unit)? = null
) {
    var state: ProgressDialogStatus = ProgressDialogStatus.Progress

    val headerBackgroundDrawable = ObservableField<Drawable>()
    val headerDrawable = ObservableField<Drawable>()

    val title = ObservableField<SpannableString>()
    val detail = ObservableField<SpannableString>()
    val progressBarVisibility = ObservableInt()

    val actionBtnBackgroundColor = ObservableInt(ResourceManager.getInstance().getColor(R.color.rp_blue_1))
    val actionBtnText = ObservableField<String>()
    val secondaryBtnTextColor = ObservableInt(ResourceManager.getInstance().getColor(R.color.rp_blue_1))
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
        actionBtnBackgroundColor.set(ResourceManager.getInstance().getColor(R.color.rp_blue_1))
        secondaryActionBtnText.set(secondaryBtnText)
        secondaryBtnTextColor.set(ResourceManager.getInstance().getColor(R.color.rp_blue_1))
    }

    open fun setProgressState(titleText: String, subtitleText: String) {
        state = ProgressDialogStatus.Progress
        headerBackgroundDrawable.set(ResourceManager.getInstance().getDrawable(R.color.rp_yellow_1))
        headerDrawable.set(null)
        title.set(SpannableString(titleText))
        detail.set(SpannableString(subtitleText))
        progressBarVisibility.set(View.VISIBLE)
        actionBtnText.set(null)
        actionBtnBackgroundColor.set(ResourceManager.getInstance().getColor(R.color.rp_blue_1))
        secondaryActionBtnText.set(null)
        secondaryBtnTextColor.set(ResourceManager.getInstance().getColor(R.color.rp_blue_1))
    }

    open fun setErrorState(titleText: String, subtitleText: String) {
        state = ProgressDialogStatus.Error
        headerBackgroundDrawable.set(ResourceManager.getInstance().getDrawable(R.color.rp_red_2))
        headerDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_warning_triangle,
            ResourceManager.getInstance().getColor(R.color.rp_grey_6)))
        title.set(SpannableString(titleText))
        detail.set(SpannableString(subtitleText))
        progressBarVisibility.set(View.GONE)
        actionBtnText.set(ResourceManager.getInstance().getString(R.string.rp_dismiss))
        actionBtnBackgroundColor.set(ResourceManager.getInstance().getColor(R.color.rp_blue_1))
        secondaryActionBtnText.set(null)
        secondaryBtnTextColor.set(ResourceManager.getInstance().getColor(R.color.rp_blue_1))
    }

    open fun setErrorState(titleText: String, subtitleText: String, primaryButtonText: String, secondaryButtonText: String?) {
        state = ProgressDialogStatus.Error
        headerBackgroundDrawable.set(ResourceManager.getInstance().getDrawable(R.color.rp_red_2))
        headerDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_warning_triangle))
        title.set(SpannableString(titleText))
        detail.set(SpannableString(subtitleText))
        progressBarVisibility.set(View.GONE)
        actionBtnText.set(primaryButtonText)
        actionBtnBackgroundColor.set(ResourceManager.getInstance().getColor(R.color.rp_blue_1))
        secondaryActionBtnText.set(secondaryButtonText)
        secondaryBtnTextColor.set(ResourceManager.getInstance().getColor(R.color.rp_blue_1))
    }

    open fun setSuccessState(titleText: SpannableString, subtitleText: SpannableString) {
        setSuccessState(titleText, subtitleText, ResourceManager.getInstance().getString(R.string.rp_ok), null)
    }

    open fun setSuccessState(titleText: SpannableString,
                             subtitleText: SpannableString,
                             primaryButtonText: String, secondaryButtonText: String?) {
        state = ProgressDialogStatus.Success
        headerBackgroundDrawable.set(ResourceManager.getInstance().getDrawable(R.color.rp_green_2))
        headerDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_success))
        title.set(titleText)
        detail.set(subtitleText)
        progressBarVisibility.set(View.GONE)
        actionBtnText.set(primaryButtonText)
        actionBtnBackgroundColor.set(ResourceManager.getInstance().getColor(R.color.rp_green_2))
        secondaryActionBtnText.set(secondaryButtonText)
        secondaryBtnTextColor.set(ResourceManager.getInstance().getColor(R.color.rp_green_2))
    }

    open fun onActionBtnClick() {
        actionBtnClick(state)
    }

    open fun onSecondaryActionBtnClick() {
        secondaryActionBtnClick?.invoke(state)
    }
}

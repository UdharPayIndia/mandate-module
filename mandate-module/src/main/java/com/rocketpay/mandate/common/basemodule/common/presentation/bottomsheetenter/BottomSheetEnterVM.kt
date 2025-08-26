package com.rocketpay.mandate.common.basemodule.common.presentation.bottomsheetenter

import androidx.databinding.ObservableField

internal class BottomSheetEnterVM(
    val title: String,
    val hint: String,
    val primaryActionText: String,
    val secondaryActionText: String,
    val textChange: (String) -> Unit,
    val primaryClick: (String) -> Unit,
    val secondaryClick: () -> Unit
) {
    val text = ObservableField<String>()
    val helperText = ObservableField<String>()

    fun onTextChange(string: CharSequence) {
        textChange(string.toString())
    }

    fun onPrimaryCtaClick() {
        primaryClick(text.get() ?: "")
    }

    fun onSecondaryCtaClick() {
        secondaryClick()
    }
}

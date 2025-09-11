package com.rocketpay.mandate.feature.webview.presentation.ui.viewmodel

import android.view.View
import com.rocketpay.mandate.common.basemodule.main.viewmodel.BaseMainUM
import com.rocketpay.mandate.feature.webview.presentation.ui.statemachine.GenericWebviewEvent
import com.rocketpay.mandate.feature.webview.presentation.ui.statemachine.GenericWebviewState

internal class GenericWebviewUM(private val dispatchEvent: (GenericWebviewEvent) -> Unit) : BaseMainUM() {

    fun handleState(state: GenericWebviewState) {
        toolbarTitleString.set(state.toolbarTitle)
        val tempToolbarVisibility = if (state.toolbarTitle.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
        toolbarVisibility.set(tempToolbarVisibility)
    }
}

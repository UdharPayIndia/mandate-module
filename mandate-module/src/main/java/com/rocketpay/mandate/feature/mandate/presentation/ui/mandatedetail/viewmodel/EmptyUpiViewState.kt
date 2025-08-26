package com.rocketpay.mandate.feature.mandate.presentation.ui.mandatedetail.viewmodel

internal sealed class EmptyUpiViewState(val value: String) {
    object Before2Min : EmptyUpiViewState("BEFORE_2_MIN")
    object After2Min : EmptyUpiViewState("AFTER_2_MIN")

    companion object {
        val map by lazy {
            mapOf(
                "BEFORE_2_MIN" to Before2Min,
                "AFTER_2_MIN" to After2Min
            )
        }
    }
}

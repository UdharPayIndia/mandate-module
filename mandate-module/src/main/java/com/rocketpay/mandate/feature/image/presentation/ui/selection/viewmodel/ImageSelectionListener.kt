package com.rocketpay.mandate.feature.image.presentation.ui.selection.viewmodel

internal interface ImageSelectionListener {
    fun onImageChange(uri: String?, fileSize: Double)
}

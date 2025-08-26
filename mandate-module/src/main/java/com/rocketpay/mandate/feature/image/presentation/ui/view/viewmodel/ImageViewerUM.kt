package com.rocketpay.mandate.feature.image.presentation.ui.view.viewmodel

import androidx.databinding.ObservableField
import com.rocketpay.mandate.feature.image.presentation.ui.view.statemachinde.ImageViewerEvent
import com.rocketpay.mandate.feature.image.presentation.ui.view.statemachinde.ImageViewerState
import com.rocketpay.mandate.common.basemodule.main.viewmodel.BaseMainUM

internal class ImageViewerUM(private val dispatchEvent: (ImageViewerEvent) -> Unit) : BaseMainUM() {

    val imageUrl = ObservableField<String>()
    val imageText = ObservableField<String>()

    fun onBackClick() {
        dispatchEvent(ImageViewerEvent.CloseClick)
    }

    fun handleState(state: ImageViewerState) {
        imageUrl.set(state.imageUrl)
        imageText.set(state.imageUrl)
    }
}

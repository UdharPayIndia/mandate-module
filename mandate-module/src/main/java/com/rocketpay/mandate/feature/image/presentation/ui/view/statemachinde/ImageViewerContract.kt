package com.rocketpay.mandate.feature.image.presentation.ui.view.statemachinde

import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseEvent
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseState
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.Screen
import com.rocketpay.mandate.common.mvistatemachine.contract.AsyncSideEffect
import com.rocketpay.mandate.common.mvistatemachine.contract.UiSideEffect

internal data class ImageViewerState (
    val imageUrl: String = ""
) : BaseState(ImageViewerScreen)


internal sealed class ImageViewerEvent(name: String? = null) : BaseEvent(name) {
    data class LoadData(val url: String): ImageViewerEvent()
    object CloseClick : ImageViewerEvent()
}


internal sealed class ImageViewerASF : AsyncSideEffect {
}


internal sealed class ImageViewerUSF : UiSideEffect {
    object CloseScreen: ImageViewerUSF()
}

internal object ImageViewerScreen : Screen("image_viewer")

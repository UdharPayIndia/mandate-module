package com.rocketpay.mandate.feature.image.presentation.ui.view.statemachinde

import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseAnalyticsHandler
import com.rocketpay.mandate.common.mvistatemachine.contract.Next
import com.rocketpay.mandate.common.mvistatemachine.viewmodel.simple.SimpleStateMachineImpl
import kotlinx.coroutines.CoroutineScope

internal class ImageViewerSimpleStateMachine () : SimpleStateMachineImpl<ImageViewerEvent, ImageViewerState, ImageViewerASF, ImageViewerUSF>(
    BaseAnalyticsHandler()
) {

    override fun startState(): ImageViewerState {
        return ImageViewerState()
    }

    override fun handleEvent(
        event: ImageViewerEvent,
        state: ImageViewerState
    ): Next<ImageViewerState?, ImageViewerASF?, ImageViewerUSF?> {
        return when (event) {
            is ImageViewerEvent.LoadData -> {
                next(state.copy(imageUrl = event.url))
            }
            is ImageViewerEvent.CloseClick -> {
                next(ImageViewerUSF.CloseScreen)
            }
        }
    }

    override suspend fun handleAsyncSideEffect(
        sideEffect: ImageViewerASF,
        dispatchEvent: (ImageViewerEvent) -> Unit,
        viewModelScope: CoroutineScope
    ) {

    }
}

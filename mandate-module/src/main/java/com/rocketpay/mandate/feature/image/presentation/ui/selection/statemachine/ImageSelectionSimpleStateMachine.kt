package com.rocketpay.mandate.feature.image.presentation.ui.selection.statemachine

import com.rocketpay.mandate.feature.image.domain.usecase.ImageUseCase
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseAnalyticsHandler
import com.rocketpay.mandate.common.mvistatemachine.contract.Next
import com.rocketpay.mandate.common.mvistatemachine.viewmodel.simple.SimpleStateMachineImpl
import kotlinx.coroutines.CoroutineScope

internal class ImageSelectionSimpleStateMachine (
    private val imageUseCase: ImageUseCase
) : SimpleStateMachineImpl<ImageSelectionEvent, ImageSelectionState, ImageSelectionASF, ImageSelectionUSF>(
    BaseAnalyticsHandler()
) {

    override fun startState(): ImageSelectionState {
        return ImageSelectionState()
    }

    override fun handleEvent(
        event: ImageSelectionEvent,
        state: ImageSelectionState
    ): Next<ImageSelectionState?, ImageSelectionASF?, ImageSelectionUSF?> {
        return when (event) {
            is ImageSelectionEvent.Init -> {
                next(state.copy(title = event.title,
                    subTitle = event.subTitle,
                    allowedExtensions = event.allowedExtension,
                    maxSizeLimit = event.maxSizeLimit))
            }
            is ImageSelectionEvent.LoadData -> {
                noChange()
            }
            is ImageSelectionEvent.OpenCamera -> {
                next(ImageSelectionUSF.CameraClick)
            }
            is ImageSelectionEvent.OpenPhoto -> {
                next(ImageSelectionUSF.GalleryClick)
            }
            is ImageSelectionEvent.OpenDocument -> {
                next(ImageSelectionUSF.DocumentClick)
            }
            is ImageSelectionEvent.SettingClick -> {
                next(ImageSelectionUSF.OpenSetting)
            }
        }
    }

    override suspend fun handleAsyncSideEffect(
        sideEffect: ImageSelectionASF,
        dispatchEvent: (ImageSelectionEvent) -> Unit,
        viewModelScope: CoroutineScope
    ) {
        when (sideEffect) {
            else -> {

            }
        }
    }
}

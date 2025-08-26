package com.rocketpay.mandate.feature.image.presentation.injection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rocketpay.mandate.feature.image.domain.usecase.ImageUseCase
import com.rocketpay.mandate.feature.image.presentation.ui.selection.statemachine.ImageSelectionSimpleStateMachine
import com.rocketpay.mandate.feature.image.presentation.ui.view.statemachinde.ImageViewerSimpleStateMachine

@Suppress("UNCHECKED_CAST")
internal open class ImageStateMachineFactory(private val imageUseCase: ImageUseCase): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ImageSelectionSimpleStateMachine::class.java) -> ImageSelectionSimpleStateMachine(imageUseCase) as T
            modelClass.isAssignableFrom(ImageViewerSimpleStateMachine::class.java) -> ImageViewerSimpleStateMachine() as T
            else -> throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}

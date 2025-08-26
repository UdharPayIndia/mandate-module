package com.rocketpay.mandate.feature.image.presentation.injection

import com.rocketpay.mandate.feature.image.presentation.ui.selection.view.ImageSelectionFragment
import com.rocketpay.mandate.feature.image.presentation.ui.view.view.ImageViewerFragment
import dagger.Component

@Component(modules = [ImageModule::class])
internal interface ImageComponent {

    fun inject(imageSelectionFragment: ImageSelectionFragment)
    fun inject(imageViewerFragment: ImageViewerFragment)

    object Initializer {

        fun init(): ImageComponent {
            return DaggerImageComponent.builder()
                .build()
        }
    }
}

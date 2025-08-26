package com.rocketpay.mandate.feature.image.presentation.injection

import com.rocketpay.mandate.feature.image.data.ImageRepositoryImpl
import com.rocketpay.mandate.feature.image.data.datasource.remote.ImageService
import com.rocketpay.mandate.feature.image.domain.repositories.ImageRepository
import com.rocketpay.mandate.feature.image.domain.usecase.ImageUseCase
import com.rocketpay.mandate.feature.image.presentation.ui.selection.adapter.ImageSelectionAdapter
import dagger.Module
import dagger.Provides

@Module
internal open class ImageModule {

    @Provides
    internal fun provideImageSelectionAdapter(): ImageSelectionAdapter {
        return ImageSelectionAdapter()
    }

    @Provides
    internal fun provideImageStateMachineFactory(imageUseCase: ImageUseCase): ImageStateMachineFactory {
        return ImageStateMachineFactory(imageUseCase)
    }

    @Provides
    internal fun provideImageUseCase(imageRepository: ImageRepository): ImageUseCase {
        return ImageUseCase(imageRepository)
    }

    @Provides
    internal fun provideImageRepository(imageService: ImageService): ImageRepository {
        return ImageRepositoryImpl(imageService)
    }

    @Provides
    internal fun provideImageService(): ImageService {
        return ImageService()
    }

}

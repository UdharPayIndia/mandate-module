package com.rocketpay.mandate.feature.aboutus.presentation.injection


import com.rocketpay.mandate.feature.property.domain.usecase.PropertyUseCase
import com.rocketpay.mandate.feature.property.presentation.injection.PropertyModule
import dagger.Module
import dagger.Provides

@Module(includes = [PropertyModule::class])
internal open class AboutUsModule {

    @Provides
    fun provideAboutUsStateMachineFactory(propertyUseCase: PropertyUseCase): AboutUsStateMachineFactory {
        return AboutUsStateMachineFactory(propertyUseCase)
    }
}

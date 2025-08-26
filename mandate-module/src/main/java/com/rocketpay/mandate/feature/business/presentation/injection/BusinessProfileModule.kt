package com.rocketpay.mandate.feature.business.presentation.injection

import com.rocketpay.mandate.feature.business.data.BusinessPropertyRepositoryImpl
import com.rocketpay.mandate.feature.business.data.datasource.remote.BusinessPropertyService
import com.rocketpay.mandate.feature.business.domain.repositories.BusinessPropertyRepository
import com.rocketpay.mandate.feature.business.domain.usecase.BusinessProfileUseCase
import com.rocketpay.mandate.feature.property.domain.usecase.PropertyUseCase
import com.rocketpay.mandate.feature.property.presentation.injection.PropertyModule
import dagger.Module
import dagger.Provides


@Module(includes = [PropertyModule::class])
internal open class BusinessProfileModule {

    @Provides
    internal fun provideBusinessProfileStateMachineFactory(
        businessProfileUseCase: BusinessProfileUseCase,
        propertyUseCase: PropertyUseCase
       ): BusinessProfileStateMachineFactory {
        return BusinessProfileStateMachineFactory(businessProfileUseCase, propertyUseCase)
    }

    @Provides
    internal fun provideBusinessProfileUseCase(): BusinessProfileUseCase{
        return BusinessProfileUseCase()
    }

    @Provides
    internal fun provideBusinessPropertyRepository(businessProfileService: BusinessPropertyService): BusinessPropertyRepository {
        return BusinessPropertyRepositoryImpl(businessProfileService)
    }

    @Provides
    internal fun provideBusinessPropertyService(): BusinessPropertyService {
        return BusinessPropertyService()
    }

}

package com.rocketpay.mandate.feature.property.presentation.injection

import com.rocketpay.mandate.feature.property.data.PropertyRepositoryImpl
import com.rocketpay.mandate.feature.property.data.datasource.local.PropertyDao
import com.rocketpay.mandate.feature.property.data.mapper.PropertyDomToEntMapper
import com.rocketpay.mandate.feature.property.data.mapper.PropertyEntToDomMapper
import com.rocketpay.mandate.feature.property.domain.repositories.PropertyRepository
import com.rocketpay.mandate.feature.property.domain.usecase.PropertyUseCase
import com.rocketpay.mandate.main.database.MandateDatabase
import dagger.Module
import dagger.Provides

@Module
internal open class PropertyModule {

    @Provides
    internal fun providePropertyUseCase(propertyRepository: PropertyRepository): PropertyUseCase {
        return PropertyUseCase(propertyRepository)
    }

    @Provides
    internal fun providePropertyRepository(propertyDao: PropertyDao): PropertyRepository {
        return PropertyRepositoryImpl(
            propertyDao,
            PropertyEntToDomMapper(),
            PropertyDomToEntMapper()
        )
    }

    @Provides
    internal fun providePropertyDao(): PropertyDao {
        return MandateDatabase.instance.propertyDao()
    }
}

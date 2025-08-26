package com.rocketpay.mandate.feature.kyc.presentation.injection


import com.rocketpay.mandate.feature.image.domain.usecase.ImageUseCase
import com.rocketpay.mandate.feature.image.presentation.injection.ImageModule
import com.rocketpay.mandate.feature.kyc.data.KycRepositoryImpl
import com.rocketpay.mandate.feature.kyc.data.datasource.local.KycDao
import com.rocketpay.mandate.feature.kyc.data.datasource.local.KycDataStore
import com.rocketpay.mandate.feature.kyc.data.datasource.remote.KycService
import com.rocketpay.mandate.feature.kyc.data.mapper.KycDtoToEntMapper
import com.rocketpay.mandate.feature.kyc.data.mapper.KycEntToDomMapper
import com.rocketpay.mandate.feature.kyc.data.mapper.KycItemDtoToDomMapper
import com.rocketpay.mandate.feature.kyc.domain.repositories.KycRepository
import com.rocketpay.mandate.feature.kyc.domain.usecase.KycUseCase
import com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.adapter.KycAdapter
import com.rocketpay.mandate.main.database.MandateDatabase
import com.rocketpay.mandate.main.init.MandateManager
import com.rocketpay.mandate.feature.property.domain.usecase.PropertyUseCase
import com.rocketpay.mandate.feature.property.presentation.injection.PropertyModule
import com.rocketpay.mandate.common.basemodule.common.data.cache.DataStore
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DataValidator
import dagger.Module
import dagger.Provides

@Module(includes = [ImageModule::class, PropertyModule::class])
internal open class KycModule {

    @Provides
    internal fun provideKycAdapter(): KycAdapter {
        return KycAdapter()
    }

    @Provides
    internal fun provideKycStateMachineFactory(kycUseCase: KycUseCase, imageUseCase: ImageUseCase,
                                               propertyUseCase: PropertyUseCase
    ): KycStateMachineFactory {
        return KycStateMachineFactory(kycUseCase, imageUseCase, propertyUseCase)
    }

    @Provides
    internal fun provideKycUseCase(kycRepository: KycRepository): KycUseCase {
        return KycUseCase(kycRepository, DataValidator())
    }

    @Provides
    internal fun provideKycRepository(kycService: KycService, kycDataStore: KycDataStore, kycDao: KycDao): KycRepository {
        return KycRepositoryImpl(
            kycDataStore,
            kycDao,
            kycService,
            KycDtoToEntMapper(),
            KycEntToDomMapper(KycItemDtoToDomMapper())
        )
    }

    @Provides
    internal fun provideKycService(): KycService {
        return KycService()
    }

    @Provides
    internal fun provideKycDataStore(): KycDataStore {
        return KycDataStore(DataStore(MandateManager.getInstance().getContext(), KycDataStore.KYC_DATA_STORE_NAME))
    }

    @Provides
    internal fun provideKycDao(): KycDao {
        return MandateDatabase.instance.kycDao()
    }
}

package com.rocketpay.mandate.feature.charge.presentation.injection


import com.rocketpay.mandate.feature.charge.data.ChargeRepositoryImpl
import com.rocketpay.mandate.feature.charge.domain.repositories.ChargeRepository
import com.rocketpay.mandate.feature.charge.domain.usecase.ChargeUseCase
import com.rocketpay.mandate.feature.charge.presentation.ui.adapter.ChargeAdapter
import dagger.Module
import dagger.Provides

@Module
internal open class ChargeModule {

    @Provides
    internal fun provideChargeAdapter(): ChargeAdapter {
        return ChargeAdapter()
    }

    @Provides
    internal fun provideSimpleStateMachineFactory(chargeUseCase: ChargeUseCase): ChargeStateMachineFactory {
        return ChargeStateMachineFactory(chargeUseCase)
    }

    @Provides
    internal fun provideChargeUseCase(chargeRepository: ChargeRepository): ChargeUseCase {
        return ChargeUseCase(chargeRepository)
    }

    @Provides
    internal fun provideChargeRepository(): ChargeRepository {
        return ChargeRepositoryImpl()
    }
}

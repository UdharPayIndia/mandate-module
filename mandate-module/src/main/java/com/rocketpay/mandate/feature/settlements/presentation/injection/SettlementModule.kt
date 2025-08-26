package com.rocketpay.mandate.feature.settlements.presentation.injection

import com.rocketpay.mandate.feature.installment.domain.usecase.InstallmentUseCase
import com.rocketpay.mandate.feature.installment.presentation.injection.InstallmentModule
import com.rocketpay.mandate.feature.property.domain.usecase.PropertyUseCase
import com.rocketpay.mandate.feature.property.presentation.injection.PropertyModule
import com.rocketpay.mandate.feature.settlements.data.PaymentOrderRepositoryImpl
import com.rocketpay.mandate.feature.settlements.data.datasource.local.PaymentOrderDao
import com.rocketpay.mandate.feature.settlements.data.datasource.local.SettlementDataStore
import com.rocketpay.mandate.feature.settlements.data.datasource.remote.PaymentOrderService
import com.rocketpay.mandate.feature.settlements.data.mapper.PaymentOrderDtoToEntityMapper
import com.rocketpay.mandate.feature.settlements.data.mapper.PaymentOrderEntToDomMapper
import com.rocketpay.mandate.feature.settlements.domain.repositories.PaymentOrderRepository
import com.rocketpay.mandate.feature.settlements.domain.usecase.PaymentOrderUseCase
import com.rocketpay.mandate.feature.settlements.presentation.ui.detail.adapter.SettledInstallmentListAdapter
import com.rocketpay.mandate.feature.settlements.presentation.ui.list.adapter.SettlementListAdapter
import com.rocketpay.mandate.main.database.MandateDatabase
import dagger.Module
import dagger.Provides

@Module(includes = [PropertyModule::class, InstallmentModule::class])
internal open class SettlementModule {

    @Provides
    internal fun provideSettledInstallmentListAdapter(): SettledInstallmentListAdapter {
        return SettledInstallmentListAdapter()
    }

    @Provides
    internal fun provideSSettlementListAdapter(): SettlementListAdapter {
        return SettlementListAdapter()
    }

    @Provides
    internal fun provideSettlementStateMachineFactory(
        paymentOrderUseCase: PaymentOrderUseCase,
        propertyUseCase: PropertyUseCase,
        installmentUseCase: InstallmentUseCase
    ): SettlementStateMachineFactory {
        return SettlementStateMachineFactory(paymentOrderUseCase, propertyUseCase, installmentUseCase)
    }

    @Provides
    internal fun provideSettlementUseCase(
        paymentOrderRepository: PaymentOrderRepository,
    ): PaymentOrderUseCase {
        return PaymentOrderUseCase(paymentOrderRepository)
    }

    @Provides
    internal fun provideSettlementRepository(
        paymentOrderService: PaymentOrderService,
        paymentOrderDao: PaymentOrderDao,
        settlementDataStore: SettlementDataStore
    ): PaymentOrderRepository {
        return PaymentOrderRepositoryImpl(
            paymentOrderService,
            paymentOrderDao,
            settlementDataStore,
            PaymentOrderDtoToEntityMapper(),
            PaymentOrderEntToDomMapper()
        )
    }

    @Provides
    internal fun provideSettlementService(): PaymentOrderService {
        return PaymentOrderService()
    }

    @Provides
    internal fun provideSettlementDao(): PaymentOrderDao {
        return MandateDatabase.instance.paymentOrderDao()
    }

    @Provides
    internal fun provideSettlementDataStore(): SettlementDataStore {
        return SettlementDataStore()
    }

}

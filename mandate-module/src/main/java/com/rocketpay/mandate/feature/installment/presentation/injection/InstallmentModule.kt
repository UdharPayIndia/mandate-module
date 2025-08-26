package com.rocketpay.mandate.feature.installment.presentation.injection

import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DataValidator
import com.rocketpay.mandate.feature.installment.data.InstallmentRepositoryImpl
import com.rocketpay.mandate.feature.installment.data.datasource.local.InstallmentDao
import com.rocketpay.mandate.feature.installment.data.datasource.local.InstallmentDataStore
import com.rocketpay.mandate.feature.installment.data.datasource.remote.InstallmentService
import com.rocketpay.mandate.feature.installment.data.mapper.InstallmentDtoToEntMapper
import com.rocketpay.mandate.feature.installment.data.mapper.InstallmentEntToDomMapper
import com.rocketpay.mandate.feature.installment.data.mapper.InstallmentPenaltyDtoToEntMapper
import com.rocketpay.mandate.feature.installment.domain.repositories.InstallmentRepository
import com.rocketpay.mandate.feature.installment.domain.usecase.InstallmentUseCase
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentdetail.adapter.InstallmentDetailAdapter
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.list.adapter.InstallmentListAdapter
import com.rocketpay.mandate.feature.mandate.domain.usecase.MandateUseCase
import com.rocketpay.mandate.feature.property.domain.usecase.PropertyUseCase
import com.rocketpay.mandate.feature.settlements.domain.usecase.PaymentOrderUseCase
import com.rocketpay.mandate.main.database.MandateDatabase
import dagger.Module
import dagger.Provides

@Module
internal open class InstallmentModule {

    @Provides
    internal fun provideInstallmentListAdapter(): InstallmentListAdapter {
        return InstallmentListAdapter()
    }

    @Provides
    internal fun provideInstallmentDetailAdapter(): InstallmentDetailAdapter {
        return InstallmentDetailAdapter()
    }

    @Provides
    internal fun provideInstallmentStateMachineFactory(
        mandateUseCase: MandateUseCase,
        installmentUseCase: InstallmentUseCase,
        propertyUseCase: PropertyUseCase,
        paymentOrderUseCase: PaymentOrderUseCase
    ): InstallmentStateMachineFactory {
        return InstallmentStateMachineFactory(mandateUseCase, installmentUseCase, propertyUseCase, paymentOrderUseCase,
            DataValidator())
    }

    @Provides
    internal fun provideInstallmentUseCase(installmentRepository: InstallmentRepository): InstallmentUseCase {
        return InstallmentUseCase(installmentRepository)
    }

    @Provides
    internal fun providePaymentRepository(installmentService: InstallmentService, installmentDao: InstallmentDao,
                                 installmentDataStore: InstallmentDataStore
    ): InstallmentRepository {
        return InstallmentRepositoryImpl(
            installmentService,
            installmentDao,
            installmentDataStore,
            InstallmentDtoToEntMapper(),
            InstallmentEntToDomMapper(),
            InstallmentPenaltyDtoToEntMapper()
        )
    }

    @Provides
    internal fun providePaymentDaoService(): InstallmentService {
        return InstallmentService()
    }

    @Provides
    internal fun providePaymentDaoDao(): InstallmentDao {
        return MandateDatabase.instance.installmentDao()
    }

    @Provides
    internal fun provideInstallmentDataStore(): InstallmentDataStore {
        return InstallmentDataStore()
    }
}

package com.rocketpay.mandate.feature.mandate.presentation.injection

import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DataValidator
import com.rocketpay.mandate.feature.bankaccount.domain.usecase.BankAccountUseCase
import com.rocketpay.mandate.feature.bankaccount.presentation.injection.BankAccountModule
import com.rocketpay.mandate.feature.charge.domain.usecase.ChargeUseCase
import com.rocketpay.mandate.feature.charge.presentation.injection.ChargeModule
import com.rocketpay.mandate.feature.installment.domain.repositories.InstallmentRepository
import com.rocketpay.mandate.feature.installment.domain.usecase.InstallmentUseCase
import com.rocketpay.mandate.feature.installment.presentation.injection.InstallmentModule
import com.rocketpay.mandate.feature.kyc.domain.usecase.KycUseCase
import com.rocketpay.mandate.feature.kyc.presentation.injection.KycModule
import com.rocketpay.mandate.feature.mandate.data.MandateRepositoryImpl
import com.rocketpay.mandate.feature.mandate.data.datasource.local.MandateDao
import com.rocketpay.mandate.feature.mandate.data.datasource.local.MandateDataStore
import com.rocketpay.mandate.feature.mandate.data.datasource.local.MandateSubtextDao
import com.rocketpay.mandate.feature.mandate.data.datasource.remote.MandateService
import com.rocketpay.mandate.feature.mandate.data.mapper.CreateMandateMapper
import com.rocketpay.mandate.feature.mandate.data.mapper.MandateDomToEntMapper
import com.rocketpay.mandate.feature.mandate.data.mapper.MandateDtoToEntMapper
import com.rocketpay.mandate.feature.mandate.data.mapper.MandateEntToDomMapper
import com.rocketpay.mandate.feature.mandate.data.mapper.MandateWithSubtextEntToDomMapper
import com.rocketpay.mandate.feature.mandate.domain.repositories.MandateRepository
import com.rocketpay.mandate.feature.mandate.domain.usecase.MandateUseCase
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.adapter.CouponListAdapter
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatedetail.adapter.MandateDetailAdapter
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatelist.adapter.MandateListAdapter
import com.rocketpay.mandate.feature.permission.common.PermissionModule
import com.rocketpay.mandate.feature.permission.feature.domain.usecases.PermissionUseCase
import com.rocketpay.mandate.feature.product.domain.usecase.ProductUseCase
import com.rocketpay.mandate.feature.product.presentation.injection.ProductModule
import com.rocketpay.mandate.feature.property.domain.usecase.PropertyUseCase
import com.rocketpay.mandate.feature.property.presentation.injection.PropertyModule
import com.rocketpay.mandate.feature.settlements.domain.usecase.PaymentOrderUseCase
import com.rocketpay.mandate.feature.settlements.presentation.injection.SettlementModule
import com.rocketpay.mandate.main.database.MandateDatabase
import dagger.Module
import dagger.Provides

@Module(includes = [
    InstallmentModule::class,
    BankAccountModule::class,
    KycModule::class,
    PermissionModule::class,
    PropertyModule::class,
    SettlementModule::class,
    SettlementModule::class,
    ChargeModule::class,
    ProductModule::class
])
internal open class MandateModule {

    @Provides
    internal fun provideCouponListAdapter(): CouponListAdapter {
        return CouponListAdapter()
    }

    @Provides
    internal fun provideMandateListAdapter(): MandateListAdapter {
        return MandateListAdapter()
    }

    @Provides
    internal fun provideMandateDetailAdapter(): MandateDetailAdapter {
        return MandateDetailAdapter()
    }

    @Provides
    internal fun provideMandateStateMachineFactory(
        mandateUseCase: MandateUseCase, installmentUseCase: InstallmentUseCase,
        bankAccountUseCase: BankAccountUseCase, permissionUseCase: PermissionUseCase,
        kycUseCase: KycUseCase,
        propertyUseCase: PropertyUseCase,
        paymentOrderUseCase: PaymentOrderUseCase,
        chargeUseCase: ChargeUseCase,
        productUseCase: ProductUseCase
    ): MandateStateMachineFactory {
        return MandateStateMachineFactory(mandateUseCase, installmentUseCase, bankAccountUseCase,
            permissionUseCase, kycUseCase, propertyUseCase
            , chargeUseCase, productUseCase)
    }

    @Provides
    internal fun provideMandateUseCase(mandateRepository: MandateRepository,
                                       installmentRepository: InstallmentRepository): MandateUseCase {
        return MandateUseCase(mandateRepository, DataValidator(), installmentRepository)
    }

    @Provides
    internal fun provideMandateRepository(mandateService: MandateService, mandateDao: MandateDao,
                                          mandateSubtextDao: MandateSubtextDao): MandateRepository {
        return MandateRepositoryImpl(
            mandateService,
            mandateDao,
            mandateSubtextDao,
            CreateMandateMapper(),
            MandateDtoToEntMapper(),
            MandateEntToDomMapper(),
            MandateDomToEntMapper(),
            MandateWithSubtextEntToDomMapper(),
            MandateDataStore()
        )
    }

    @Provides
    internal fun provideMandateService(): MandateService {
        return MandateService()
    }

    @Provides
    internal fun provideMandateDao(): MandateDao {
        return MandateDatabase.instance.mandateDao()
    }

    @Provides
    internal fun provideMandateSubtextDao(): MandateSubtextDao {
        return MandateDatabase.instance.mandateSubtextDao()
    }

}

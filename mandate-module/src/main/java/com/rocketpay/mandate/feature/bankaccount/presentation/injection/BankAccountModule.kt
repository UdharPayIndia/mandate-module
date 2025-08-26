package com.rocketpay.mandate.feature.bankaccount.presentation.injection


import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DataValidator
import com.rocketpay.mandate.feature.bankaccount.data.BankAccountRepositoryImpl
import com.rocketpay.mandate.feature.bankaccount.data.datasource.local.BankAccountDao
import com.rocketpay.mandate.feature.bankaccount.data.datasource.local.BankAccountDataStore
import com.rocketpay.mandate.feature.bankaccount.data.datasource.remote.BankAccountService
import com.rocketpay.mandate.feature.bankaccount.data.mapper.BankAccountDomToDtoMapper
import com.rocketpay.mandate.feature.bankaccount.data.mapper.BankAccountDomToEntMapper
import com.rocketpay.mandate.feature.bankaccount.data.mapper.BankAccountDtoToDomMapper
import com.rocketpay.mandate.feature.bankaccount.data.mapper.BankAccountDtoToEntMapper
import com.rocketpay.mandate.feature.bankaccount.data.mapper.BankAccountEntToDomMapper
import com.rocketpay.mandate.feature.bankaccount.domain.repositories.BankAccountRepository
import com.rocketpay.mandate.feature.bankaccount.domain.usecase.BankAccountUseCase
import com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountlist.adapter.BankAccountAdapter
import com.rocketpay.mandate.feature.login.presentation.injection.LoginModule
import com.rocketpay.mandate.main.database.MandateDatabase
import dagger.Module
import dagger.Provides

@Module(includes = [LoginModule::class])
internal open class BankAccountModule {

    @Provides
    internal fun provideBankAccountAdapter(): BankAccountAdapter {
        return BankAccountAdapter()
    }

    @Provides
    internal fun provideBankAccountStateMachineFactory(bankAccountUseCase: BankAccountUseCase): BankAccountStateMachineFactory {
        return BankAccountStateMachineFactory(bankAccountUseCase)
    }

    @Provides
    internal fun provideBankAccountUseCase(bankAccountRepository: BankAccountRepository): BankAccountUseCase {
        return BankAccountUseCase(bankAccountRepository, DataValidator())
    }

    @Provides
    internal fun provideBankAccountRepository(bankAccountService: BankAccountService, bankAccountDataStore: BankAccountDataStore, bankAccountDao: BankAccountDao): BankAccountRepository {
        return BankAccountRepositoryImpl(
            bankAccountDao,
            bankAccountService,
            bankAccountDataStore,
            BankAccountDomToDtoMapper(),
            BankAccountDtoToDomMapper(),
            BankAccountDtoToEntMapper(),
            BankAccountEntToDomMapper(),
            BankAccountDomToEntMapper()
        )
    }

    @Provides
    internal fun provideBankAccountService(): BankAccountService {
        return BankAccountService()
    }

    @Provides
    internal fun provideBankAccountDataStore(): BankAccountDataStore {
        return BankAccountDataStore()
    }

    @Provides
    internal fun provideBankAccountDao(): BankAccountDao {
        return MandateDatabase.instance.bankAccountDao()
    }
}

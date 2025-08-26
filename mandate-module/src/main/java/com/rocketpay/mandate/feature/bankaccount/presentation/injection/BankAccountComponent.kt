package com.rocketpay.mandate.feature.bankaccount.presentation.injection

import com.rocketpay.mandate.feature.bankaccount.data.BankAccountSyncer
import com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountadd.view.BankAccountAddFragment
import com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountlist.view.BankAccountListFragment
import dagger.Component

@Component(modules = [BankAccountModule::class])
internal interface BankAccountComponent {

    fun inject(bankAccountListFragment: BankAccountListFragment)
    fun inject(addBankAccountFragment: BankAccountAddFragment)
    fun inject(bankAccountSyncer: BankAccountSyncer)

    object Initializer {

        fun init(): BankAccountComponent {
            val bankAccountModule = BankAccountModule()
            return DaggerBankAccountComponent.builder()
                .bankAccountModule(bankAccountModule)
                .build()
        }
    }
}

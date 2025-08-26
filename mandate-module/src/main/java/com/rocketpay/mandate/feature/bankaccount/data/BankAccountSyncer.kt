package com.rocketpay.mandate.feature.bankaccount.data

import com.rocketpay.mandate.feature.bankaccount.domain.repositories.BankAccountRepository
import com.rocketpay.mandate.feature.bankaccount.presentation.injection.BankAccountComponent
import com.rocketpay.mandate.feature.login.domain.repositories.LoginRepository
import com.udharpay.core.networkmanager.domain.entities.Outcome
import com.udharpay.core.syncmanager.domain.enities.DefaultSyncConstraint
import com.udharpay.core.syncmanager.domain.enities.ExistingSyncPolicy
import com.udharpay.core.syncmanager.domain.enities.SyncFailurePolicy
import com.udharpay.core.syncmanager.domain.enities.SyncPriority
import com.udharpay.core.syncmanager.domain.enities.SyncStatus
import com.udharpay.core.syncmanager.domain.repositories.Sync
import com.udharpay.core.syncmanager.domain.repositories.Syncer
import javax.inject.Inject

internal class BankAccountSync: Sync {
    override fun dependencies() = emptyList<String>()
    override fun priority() = SyncPriority.Medium
    override fun existingSyncPolicy() = ExistingSyncPolicy.Keep
    override fun syncFailurePolicy() = SyncFailurePolicy.Cascade
    override fun syncer() = BankAccountSyncer()
    override fun constraint() = DefaultSyncConstraint.getLoginAndNetworkConstraint(
        isLoginRequire = true,
        isNetworkRequire = true
    )
}

internal class BankAccountSyncer: Syncer {

    @Inject
    internal lateinit var bankAccountRepository: BankAccountRepository
    @Inject
    internal lateinit var loginRepository: LoginRepository

    companion object {
        const val TYPE = "bank_account"
    }

    init {
        BankAccountComponent.Initializer.init().inject(this)
    }

    override suspend fun sync(): SyncStatus {
        return when (val outcome = bankAccountRepository.fetchBankAccounts()) {
            is Outcome.Success -> {
                val primaryBankAccount = outcome.data.find { it.primary }
                loginRepository.setName(primaryBankAccount?.account_holder_name_at_bank.orEmpty())
                SyncStatus.Success
            }
            is Outcome.Error -> {
                SyncStatus.Failed
            }
        }
    }
}

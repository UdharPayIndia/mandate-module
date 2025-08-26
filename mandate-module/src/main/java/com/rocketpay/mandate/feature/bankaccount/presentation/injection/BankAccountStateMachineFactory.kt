package com.rocketpay.mandate.feature.bankaccount.presentation.injection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rocketpay.mandate.feature.bankaccount.domain.usecase.BankAccountUseCase
import com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountadd.statemachine.BankAccountAddStateMachine
import com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountlist.statemachine.BankAccountListStateMachine

@Suppress("UNCHECKED_CAST")
internal open class BankAccountStateMachineFactory(private val bankAccountUseCase: BankAccountUseCase): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(BankAccountListStateMachine::class.java) -> BankAccountListStateMachine(bankAccountUseCase) as T
            modelClass.isAssignableFrom(BankAccountAddStateMachine::class.java) -> BankAccountAddStateMachine(bankAccountUseCase) as T
            else -> throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}

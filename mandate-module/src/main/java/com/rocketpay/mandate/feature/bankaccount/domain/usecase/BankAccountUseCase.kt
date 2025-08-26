package com.rocketpay.mandate.feature.bankaccount.domain.usecase

import com.rocketpay.mandate.feature.bankaccount.domain.entities.BankAccount
import com.rocketpay.mandate.feature.bankaccount.domain.repositories.BankAccountRepository
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.viewmodel.UpiApplication
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.viewmodel.UpiIdError
import com.udharpay.core.networkmanager.domain.entities.GenericErrorResponse
import com.udharpay.core.networkmanager.domain.entities.Outcome
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DataValidator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

internal class BankAccountUseCase internal constructor(
    private val bankAccountRepository: BankAccountRepository,
    private val dataValidator: DataValidator
) {

    fun isValidName(name: String): Boolean {
        return dataValidator.isValidName(name)
    }

    fun isValidIfsc(ifsc: String): Boolean {
        return dataValidator.isValidIfsc(ifsc)
    }

    fun isValidAccountNumber(accountNumber: String): Boolean {
        return dataValidator.isValidBankAccountNumber(accountNumber)
    }

    suspend fun addBankAccount(
        number: String,
        ifsc: String,
        name: String,
        upiId: String
    ): Outcome<BankAccount> {
        val bankAccount = BankAccount(
            id = "",
            accountNumber = number,
            ifsc = ifsc,
            accountHolderName = name,
            branchName = "",
            upiId = upiId,
            bankName = "",
            isPrimary = false,
            isVerified = false,
            isDeleted = false
        )
        return bankAccountRepository.addBankAccount(bankAccount)
    }

    fun getBankAccounts(): Flow<List<BankAccount>> {
        return bankAccountRepository.getBankAccounts()
    }

    suspend fun getNonLiveBankAccounts(): List<BankAccount>{
        return withContext(Dispatchers.IO){ bankAccountRepository.getNonLiveBankAccounts() }
    }

    suspend fun markBankAccountAsPrimary(bankAccount: BankAccount): Outcome<GenericErrorResponse>{
        return withContext(Dispatchers.IO) { bankAccountRepository.markBankAccountAsPrimary(bankAccount) }
    }

    suspend fun deleteBankAccount(bankAccount: BankAccount): Outcome<Unit>{
        return withContext(Dispatchers.IO) { bankAccountRepository.deleteBankAccount(bankAccount) }
    }
}

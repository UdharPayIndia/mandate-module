package com.rocketpay.mandate.feature.bankaccount.domain.repositories

import com.udharpay.core.networkmanager.domain.entities.GenericErrorResponse
import com.udharpay.core.networkmanager.domain.entities.Outcome
import com.rocketpay.mandate.feature.bankaccount.data.entities.BankAccountDto
import com.rocketpay.mandate.feature.bankaccount.domain.entities.BankAccount
import kotlinx.coroutines.flow.Flow

internal interface BankAccountRepository {
    suspend fun addBankAccount(bankAccount: BankAccount): Outcome<BankAccount>
    suspend fun fetchBankAccounts(): Outcome<List<BankAccountDto>>
    fun getBankAccounts(): Flow<List<BankAccount>>
    suspend fun getNonLiveBankAccounts(): List<BankAccount>
    fun saveBankAccounts(bankAccounts: List<BankAccountDto>)
    fun isBankAccountAdded(): Flow<Boolean>
    suspend fun markBankAccountAsPrimary(bankAccount: BankAccount): Outcome<GenericErrorResponse>
    suspend fun deleteBankAccount(bankAccount: BankAccount): Outcome<Unit>
    fun getUserNameOfPrimaryAccount(): String
}

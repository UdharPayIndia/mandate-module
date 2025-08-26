package com.rocketpay.mandate.feature.bankaccount.data

import com.rocketpay.mandate.feature.bankaccount.data.datasource.local.BankAccountDao
import com.udharpay.core.networkmanager.domain.entities.GenericErrorResponse
import com.udharpay.core.networkmanager.domain.entities.Outcome
import com.rocketpay.mandate.feature.bankaccount.data.datasource.local.BankAccountDataStore
import com.rocketpay.mandate.feature.bankaccount.data.datasource.remote.BankAccountService
import com.rocketpay.mandate.feature.bankaccount.data.entities.BankAccountDto
import com.rocketpay.mandate.feature.bankaccount.data.entities.BankAccountEntity
import com.rocketpay.mandate.feature.bankaccount.data.mapper.*
import com.rocketpay.mandate.feature.bankaccount.domain.entities.BankAccount
import com.rocketpay.mandate.feature.bankaccount.domain.repositories.BankAccountRepository
import com.rocketpay.mandate.common.syncmanager.client.SyncManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

internal class BankAccountRepositoryImpl(
    private val bankAccountDao: BankAccountDao,
    private val bankAccountService: BankAccountService,
    private val bankAccountDataStore: BankAccountDataStore,
    private val bankAccountDomToDtoMapper: BankAccountDomToDtoMapper,
    private val bankAccountDtoToDomMapper: BankAccountDtoToDomMapper,
    private val bankAccountDtoToEntMapper: BankAccountDtoToEntMapper,
    private val bankAccountEntToDomMapper: BankAccountEntToDomMapper,
    private val bankAccountDomToEntMapper: BankAccountDomToEntMapper
): BankAccountRepository {

    override suspend fun addBankAccount(bankAccount: BankAccount): Outcome<BankAccount> {
        return when(val outcome = bankAccountService.addBankAccount(bankAccountDomToDtoMapper.map(bankAccount))) {
            is Outcome.Success -> {
                SyncManager.getInstance().enqueue(BankAccountSyncer.TYPE)
                Outcome.Success(bankAccountDtoToDomMapper.map(outcome.data))
            }
            is Outcome.Error -> outcome
        }
    }

    override suspend fun fetchBankAccounts(): Outcome<List<BankAccountDto>> {
        return when(val outcome = bankAccountService.getBankAccounts()) {
            is Outcome.Success -> {
                if (outcome.data.isNotEmpty()) {
                    saveBankAccounts(outcome.data)
                }
                Outcome.Success(outcome.data)
            }
            is Outcome.Error -> outcome
        }
    }

    override fun getBankAccounts(): Flow<List<BankAccount>> {
        return bankAccountDao.getAll().transform {
            emit(bankAccountEntToDomMapper.mapList(it))
        }
    }

    override suspend fun getNonLiveBankAccounts(): List<BankAccount> {
        val bankAccounts = bankAccountDao.getAllNonLive()
        return if(bankAccounts.isNotEmpty()){
            bankAccountEntToDomMapper.mapList(bankAccounts)
        }else{
            emptyList()
        }
    }

    override fun isBankAccountAdded(): Flow<Boolean> {
        return bankAccountDao.getAll().transform {
            emit(it.isNotEmpty())
        }
    }

    override fun saveBankAccounts(bankAccounts: List<BankAccountDto>) {
        bankAccountDao.insertAll(deleteNonExistent(bankAccountDtoToEntMapper.mapList(bankAccounts)))
    }

    private fun deleteNonExistent(bankAccounts: List<BankAccountEntity>): ArrayList<BankAccountEntity>{
        val oldBankAccounts = bankAccountDao.getAllNonLive()
        val newBankAccounts = ArrayList(bankAccounts)
        val newBankAccountNumbers = newBankAccounts.map { it.accountNumber }
        val nonExistingBankAccount = oldBankAccounts.filterNot { it.accountNumber in newBankAccountNumbers }
        nonExistingBankAccount.forEach {
            it.isDeleted = true
        }
        newBankAccounts.addAll(nonExistingBankAccount)
        return newBankAccounts
    }


    override suspend fun markBankAccountAsPrimary(bankAccount: BankAccount): Outcome<GenericErrorResponse>{
        return when(val result = bankAccountService.markBankAccountAsPrimary(bankAccount.id)){
            is Outcome.Success -> {
                bankAccount.isPrimary = true
                bankAccountDao.insertOne(bankAccountDomToEntMapper.map(bankAccount))
                SyncManager.getInstance().enqueue(BankAccountSyncer.TYPE)
                result
            }
            is Outcome.Error -> {
                result
            }
        }
    }

    override suspend fun deleteBankAccount(bankAccount: BankAccount): Outcome<Unit>{
        return when(val result = bankAccountService.deleteBankAccount(bankAccount.id)){
            is Outcome.Success -> {
                bankAccount.isDeleted = true
                bankAccountDao.insertOne(bankAccountDomToEntMapper.map(bankAccount))
                SyncManager.getInstance().enqueue(BankAccountSyncer.TYPE)
                result
            }
            is Outcome.Error -> {
                result
            }
        }
    }

    override fun getUserNameOfPrimaryAccount(): String{
        val primaryBankAccount = bankAccountDao.getAllNonLive().find { it.isPrimary }
        return primaryBankAccount?.accountHolderName.orEmpty()
    }
}

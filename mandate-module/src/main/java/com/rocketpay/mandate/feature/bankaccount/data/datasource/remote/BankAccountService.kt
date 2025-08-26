package com.rocketpay.mandate.feature.bankaccount.data.datasource.remote

import com.rocketpay.mandate.feature.bankaccount.data.entities.BankAccountDto
import com.udharpay.core.networkmanager.data.NetworkRequestBuilder
import com.udharpay.core.networkmanager.data.delete
import com.udharpay.core.networkmanager.data.get
import com.udharpay.core.networkmanager.data.post
import com.udharpay.core.networkmanager.domain.entities.GenericErrorResponse
import com.udharpay.core.networkmanager.domain.entities.Outcome

internal class BankAccountService {

    suspend fun addBankAccount(bankAccountDto: BankAccountDto): Outcome<BankAccountDto> {
        return NetworkRequestBuilder()
            .subUrl("/api/merchant/bank_details")
            .body(bankAccountDto)
            .build()
            .post()
    }

    suspend fun getBankAccounts(): Outcome<List<BankAccountDto>> {
        return NetworkRequestBuilder()
            .subUrl("/api/merchant/bank_details")
            .build()
            .get()
    }

    suspend fun markBankAccountAsPrimary(bankAccountId: String): Outcome<GenericErrorResponse> {
        return NetworkRequestBuilder()
            .subUrl("/api/merchant/bank_details/{id}/mark_primary")
            .pathParameter("id", bankAccountId)
            .body(Any())
            .build()
            .post()
    }

    suspend fun deleteBankAccount(bankAccountId: String): Outcome<Unit> {
        return NetworkRequestBuilder()
            .subUrl("/api/merchant/bank_details/{id}")
            .pathParameter("id", bankAccountId)
            .build()
            .delete()
    }
}

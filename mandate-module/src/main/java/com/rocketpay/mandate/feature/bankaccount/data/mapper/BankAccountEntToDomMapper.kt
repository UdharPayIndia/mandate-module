package com.rocketpay.mandate.feature.bankaccount.data.mapper

import com.rocketpay.mandate.feature.bankaccount.data.entities.BankAccountEntity
import com.rocketpay.mandate.feature.bankaccount.domain.entities.BankAccount
import com.udharpay.kernel.kernelcommon.mapper.ListMapper

internal class BankAccountEntToDomMapper : ListMapper<BankAccountEntity, BankAccount> {
    override fun map(source: BankAccountEntity): BankAccount {
        return BankAccount(
            id = source.id,
            accountNumber = source.accountNumber ?: "",
            ifsc = source.ifsc ?: "",
            accountHolderName = source.accountHolderName ?: "",
            branchName = source.branchName ?: "",
            bankName = source.bankName ?: "",
            upiId = source.vpa ?: "",
            isPrimary = source.isPrimary,
            isVerified = source.isVerified,
            isDeleted = source.isDeleted
        )
    }
}

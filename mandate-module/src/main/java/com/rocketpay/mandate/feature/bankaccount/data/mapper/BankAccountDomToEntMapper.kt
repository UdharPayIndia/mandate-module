package com.rocketpay.mandate.feature.bankaccount.data.mapper

import com.rocketpay.mandate.feature.bankaccount.data.entities.BankAccountEntity
import com.rocketpay.mandate.feature.bankaccount.domain.entities.BankAccount
import com.udharpay.kernel.kernelcommon.mapper.ListMapper

internal class BankAccountDomToEntMapper : ListMapper<BankAccount, BankAccountEntity> {
    override fun map(source: BankAccount): BankAccountEntity {
        return BankAccountEntity(
            id = source.id,
            accountNumber = source.accountNumber,
            ifsc = source.ifsc,
            accountHolderName = source.accountHolderName,
            branchName = source.branchName,
            vpa = source.upiId,
            bankName = source.bankName,
            isPrimary = source.isPrimary,
            isVerified = source.isVerified,
            isDeleted = source.isDeleted
        )
    }
}
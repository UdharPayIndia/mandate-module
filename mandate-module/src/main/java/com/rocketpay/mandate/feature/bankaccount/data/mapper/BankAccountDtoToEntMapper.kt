package com.rocketpay.mandate.feature.bankaccount.data.mapper

import com.rocketpay.mandate.feature.bankaccount.data.entities.BankAccountDto
import com.rocketpay.mandate.feature.bankaccount.data.entities.BankAccountEntity
import com.udharpay.kernel.kernelcommon.mapper.ListMapper

internal class BankAccountDtoToEntMapper : ListMapper<BankAccountDto, BankAccountEntity> {
    override fun map(source: BankAccountDto): BankAccountEntity {
        return BankAccountEntity(
            id = source._id ?: "",
            accountNumber = source.account_number ?: "",
            ifsc = source.ifsc,
            accountHolderName = source.account_holder_name_at_bank,
            branchName = source.branch_name,
            bankName = source.bank_name,
            vpa = source.vpa,
            isPrimary = source.primary,
            isVerified = source.verified,
            isDeleted = source.is_deleted
        )
    }
}

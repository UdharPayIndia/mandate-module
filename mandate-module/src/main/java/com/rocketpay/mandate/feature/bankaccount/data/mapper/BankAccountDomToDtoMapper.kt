package com.rocketpay.mandate.feature.bankaccount.data.mapper

import com.rocketpay.mandate.feature.bankaccount.data.entities.BankAccountDto
import com.rocketpay.mandate.feature.bankaccount.domain.entities.BankAccount
import com.udharpay.kernel.kernelcommon.mapper.ListMapper

internal class BankAccountDomToDtoMapper : ListMapper<BankAccount, BankAccountDto> {
    override fun map(source: BankAccount): BankAccountDto {
        return BankAccountDto(
            _id = source.id,
            account_number = source.accountNumber,
            ifsc = source.ifsc,
            account_holder_name_at_bank = source.accountHolderName,
            branch_name = source.branchName,
            vpa = source.upiId,
            bank_name = source.bankName,
            primary = source.isPrimary,
            verified = source.isVerified,
            is_deleted = source.isDeleted
        )
    }
}

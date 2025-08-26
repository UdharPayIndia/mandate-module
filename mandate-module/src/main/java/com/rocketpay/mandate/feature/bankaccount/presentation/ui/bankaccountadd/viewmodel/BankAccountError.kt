package com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountadd.viewmodel

internal sealed class BankAccountError(val value: String, val displayString: String) {
    object IfscMismatch : BankAccountError("IFSC_MISMATCH", "Ifsc code is not matching with provided account number")
    object AccountNotExist : BankAccountError("ACCOUNT_NOT_EXIST", "Account number not exist with bank record")
    object NameMismatch : BankAccountError("NAME_MISMATCH", "Provided name is not matching with bank's account holder name. Please fill the correct details & try again!")

    companion object {
        val map by lazy {
            mapOf(
                "IFSC_MISMATCH" to IfscMismatch,
                "ACCOUNT_NOT_EXIST" to AccountNotExist,
                "NAME_MISMATCH" to NameMismatch
            )
        }

        fun get(code: String): BankAccountError {
            return map[code] ?: AccountNotExist
        }
    }
}

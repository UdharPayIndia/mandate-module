package com.rocketpay.mandate.feature.bankaccount.domain.entities

internal data class BankAccountDetail(
    val mobileNumber: String,
    val bankName: String,
    val accountNumber: String,
    val ifsc: String,
    val accountHolderName: String,
    val branchName: String,
    val upiId: String
)

package com.rocketpay.mandate.feature.bankaccount.domain.entities

internal data class BankAccount(
    val id: String,
    val accountNumber: String,
    val ifsc: String,
    val accountHolderName: String,
    val branchName: String,
    val bankName: String,
    val upiId: String,
    var isPrimary: Boolean,
    val isVerified: Boolean,
    var isDeleted: Boolean
)

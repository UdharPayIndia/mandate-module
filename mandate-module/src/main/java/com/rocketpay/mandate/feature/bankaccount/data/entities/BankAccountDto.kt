package com.rocketpay.mandate.feature.bankaccount.data.entities

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal class BankAccountDto(
    val _id: String?,
    val account_number: String?,
    val ifsc: String?,
    @SerializedName("account_holder_name_at_bank", alternate = ["account_holder_name"])
    val account_holder_name_at_bank: String?,
    val branch_name: String?,
    val bank_name: String?,
    val vpa: String?,
    val primary: Boolean,
    val verified: Boolean,
    val is_deleted: Boolean
)

package com.rocketpay.mandate.feature.bankaccount.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "bank_account",
)
internal data class BankAccountEntity(

    @ColumnInfo(name = "account_number")
    val accountNumber: String,

    @ColumnInfo(name = "ifsc")
    val ifsc: String?,

    @ColumnInfo(name = "account_holder_name")
    val accountHolderName: String?,

    @ColumnInfo(name = "branch_name")
    val branchName: String?,

    @ColumnInfo(name = "bank_name")
    val bankName: String?,

    @ColumnInfo(name = "vpa")
    val vpa: String?,

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "is_primary")
    val isPrimary: Boolean = false,

    @ColumnInfo(name = "is_verified")
    val isVerified: Boolean = false,

    @ColumnInfo(name = "is_deleted") var isDeleted: Boolean = false,
)

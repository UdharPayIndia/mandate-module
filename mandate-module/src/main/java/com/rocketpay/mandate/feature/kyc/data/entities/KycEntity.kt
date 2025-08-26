package com.rocketpay.mandate.feature.kyc.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "kyc",
)
internal data class KycEntity(

    @PrimaryKey
    @ColumnInfo(name = "type")
    val id: String,

    @ColumnInfo(name = "state")
    val state: String,

    @ColumnInfo(name = "workflow")
    val workflow: String
)

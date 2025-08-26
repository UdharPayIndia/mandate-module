package com.rocketpay.mandate.feature.product.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(
    tableName = "productWallet",
    indices = []
)
internal class ProductWalletEntity(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "createdAt")
    val createdAt: Long,

    @ColumnInfo(name = "updatedAt")
    val updatedAt: Long,

    @ColumnInfo(name = "payin")
    val payin: Double,

    @ColumnInfo(name = "outstanding")
    val outstanding: Double,

    @ColumnInfo(name = "underPayout")
    val underPayout: Double,

    @ColumnInfo(name = "payout")
    val payout: Double,

    @ColumnInfo(name = "currency")
    val currency: String,

    @ColumnInfo(name = "unit")
    val unit: String,

    @ColumnInfo(name = "tenantId")
    var tenantId: String,

    @ColumnInfo(name = "accountId")
    val accountId: String,

    @ColumnInfo(name = "productType")
    val productType: String,
)
package com.rocketpay.mandate.feature.product.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "productOrder",
    indices = []
)
internal class ProductOrderEntity(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "isDeleted")
    val isDeleted: Boolean,

    @ColumnInfo(name = "createdAt")
    val createdAt: Long,

    @ColumnInfo(name = "updatedAt")
    val updatedAt: Long,

    @ColumnInfo(name = "serverSequence")
    val serverSequence: Long,

    @ColumnInfo(name = "tenantId")
    val tenantId: String,

    @ColumnInfo(name = "referenceId")
    val referenceId: String,

    @ColumnInfo(name = "state")
    val state: String,

    @ColumnInfo(name = "benefitString")
    val benefitString: String,

    @ColumnInfo(name = "priceString")
    val priceString: String,

    @ColumnInfo(name = "paymentOrderId")
    val paymentOrderId: String,

    @ColumnInfo(name = "orderType")
    val orderType: String,

    @ColumnInfo(name = "productType")
    val productType: String,

    @ColumnInfo(name = "metaString")
    val metaString: String,
)
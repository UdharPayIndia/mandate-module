package com.rocketpay.mandate.feature.product.domain.entities

import androidx.annotation.Keep

@Keep
internal class ProductOrder(
    val id: String,
    val deleted: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    val serverSequence: Long,
    val tenantId: String,
    val referenceId: String,
    val benefit: Unit?,
    val price: Unit?,
    val state: String?,
    val paymentOrderId: String?,
    val orderType: String?,
    val productType: String?,
    val meta: ProductMeta?,
)

@Keep
internal class ProductMeta(
    val paymentUrl: String,
    val gatewayReferenceId: String,
    val utr: String
)
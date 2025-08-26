package com.rocketpay.mandate.feature.product.domain.entities

import androidx.annotation.Keep

@Keep
internal class ProductWallet(
    val id: String,
    val createdAt: Long?,
    val updatedAt: Long?,
    val payin: Double,
    val outstanding: Double,
    val underPayout: Double,
    val payout: Double,
    val currency: String,
    val unit: String,
    val tenantId: String,
    val accountId: String,
    val productType: String,
)
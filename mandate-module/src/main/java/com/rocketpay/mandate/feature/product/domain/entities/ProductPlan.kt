package com.rocketpay.mandate.feature.product.domain.entities

import androidx.annotation.Keep

@Keep
internal class ProductPlan(
    val id: String,
    val deleted: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    val tenantId: String,
    val templateId: String,
    val attribute: Attribute,
    val price: Unit,
    val benefit: Unit,
    val sellerId: String
)

@Keep
internal class Unit(
    val mrp: Double,
    val sp: Double,
    val currency: String?,
    val unit: String?
)

@Keep
internal class Attribute(
    val title: String,
    val subTitle: String,
    val cashbackBenefit: String
)
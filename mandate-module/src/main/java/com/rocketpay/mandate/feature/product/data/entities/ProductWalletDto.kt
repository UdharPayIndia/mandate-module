package com.rocketpay.mandate.feature.product.data.entities

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName


@Keep
internal class ProductWalletDto(
    @SerializedName("id") val id: String,

    @SerializedName("created_at") val createdAt: Long?,

    @SerializedName("updated_at") val updatedAt: Long?,

    @SerializedName("payin") val payin: String?,

    @SerializedName("outstanding") val outstanding: String?,

    @SerializedName("under_payout") val underPayout: String?,

    @SerializedName("payout") val payout: String?,

    @SerializedName("currency") val currency: String?,

    @SerializedName("unit") val unit: String?,

    @SerializedName("tenant_id") val tenantId: String?,

    @SerializedName("account_id") val accountId: String?,

    @SerializedName("product_type") val productType: String?,
)
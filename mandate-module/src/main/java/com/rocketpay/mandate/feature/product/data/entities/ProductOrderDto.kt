package com.rocketpay.mandate.feature.product.data.entities

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal class ProductOrderDto(
    @SerializedName("id")  val id: String,

    @SerializedName("deleted") val deleted: Boolean?,

    @SerializedName("created_at") val createdAt: Long?,

    @SerializedName("updated_at") val updatedAt: Long?,

    @SerializedName("sync_sequence") val syncSequence: Long?,

    @SerializedName("tenant_id") val tenantId: String?,

    @SerializedName("reference_id") val referenceId: String?,

    @SerializedName("benefit") val benefit: UnitDto?,

    @SerializedName("price") val price: UnitDto?,

    @SerializedName("state") val state: String?,

    @SerializedName("payment_order_id") val paymentOrderId: String?,

    @SerializedName("order_type") val orderType: String?,

    @SerializedName("product_type") val productType: String?,

    @SerializedName("meta") val meta: ProductMetaDto?,
)

@Keep
internal class ProductMetaDto(
    @SerializedName("payment_url") val paymentUrl: String?,
    @SerializedName("gateway_reference_id") val gatewayReferenceId: String?

)
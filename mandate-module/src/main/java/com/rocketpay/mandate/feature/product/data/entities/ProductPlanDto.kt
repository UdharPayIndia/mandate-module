package com.rocketpay.mandate.feature.product.data.entities

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal class ProductPlanDto(

    @SerializedName("id") val id: String,

    @SerializedName("deleted") val deleted: Boolean?,

    @SerializedName("created_at") val createdAt: Long?,

    @SerializedName("updated_at") val updatedAt: Long?,

    @SerializedName("tenant_id") val tenantId: String?,

    @SerializedName("template_id") val templateId: String?,

    @SerializedName("attribute") val attribute: AttributeDto?,

    @SerializedName("price") val price: UnitDto?,

    @SerializedName("benefit") val benefit: UnitDto?,

    @SerializedName("seller_id") val sellerId: String?
)

@Keep
internal class UnitDto(
    @SerializedName("mrp") val mrp: String?,

    @SerializedName("sp", alternate = ["value"]) val sp: String?,

    @SerializedName("currency") val currency: String?,

    @SerializedName("unit") val unit: String?
)

@Keep
internal class AttributeDto(
    @SerializedName("title") val title: String?,

    @SerializedName("sub_title") val subTitle: String?,

    @SerializedName("cashback_benefit") val cashbackBenefit: String?
)
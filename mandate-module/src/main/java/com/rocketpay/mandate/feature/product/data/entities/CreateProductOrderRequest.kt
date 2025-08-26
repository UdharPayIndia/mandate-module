package com.rocketpay.mandate.feature.product.data.entities

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal class CreateProductOrderRequest(
    @SerializedName("product_type") val productType: String,
    @SerializedName("product_id") val productId: String,
    @SerializedName("method") val method: String,
    @SerializedName("order_type") val orderType: String = "PURCHASE",
    @SerializedName("client_meta") val clientMeta: ClientMetaRequest = ClientMetaRequest()
)

@Keep
internal class ClientMetaRequest(
    @SerializedName("description") val description: String = ""
)
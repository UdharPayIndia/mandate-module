package com.rocketpay.mandate.feature.product.data.entities

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal class ClaimFreeTrailRequest(
    @SerializedName("product_type") val productType: String
)
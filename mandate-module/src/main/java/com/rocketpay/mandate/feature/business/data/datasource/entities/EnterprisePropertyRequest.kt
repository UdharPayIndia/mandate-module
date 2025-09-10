package com.rocketpay.mandate.feature.business.data.datasource.entities

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
class EnterprisePropertyRequest(
    @SerializedName("account_ids")
    val accountIds: List<String>,

    @SerializedName("property_names")
    val propertyNames: List<String>,

    @SerializedName("use_default")
    val useDefault: Boolean = true
)
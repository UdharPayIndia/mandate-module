package com.udharpay.core.networkmanager.domain.entities

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class FallbackGenericErrorResponse(

    @SerializedName(value = "code", alternate = ["error", "errorCode"])
    var code: String? = null,

    @SerializedName(value = "message", alternate = ["description", "detail"])
    var message: String? = null,

    @SerializedName("status", alternate = ["resStatus", "Status", "statusCode"])
    var status: Int? = null,

    @SerializedName("meta")
    var meta: JsonObject? = null
)

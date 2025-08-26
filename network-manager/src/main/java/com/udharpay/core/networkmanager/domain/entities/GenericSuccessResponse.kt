package com.udharpay.core.networkmanager.domain.entities

import com.google.gson.annotations.SerializedName

class GenericSuccessResponse(
    @SerializedName(value = "success", alternate = ["isSuccess, isSuccessful"])
    var success: Boolean
)

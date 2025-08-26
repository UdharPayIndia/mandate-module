package com.rocketpay.mandate.feature.login.data.entities

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class ValidateOtpRequest(

    @SerializedName("subject_id")
    val subjectId: String,

    @SerializedName("secret")
    val secret:String,

    @SerializedName("device_id")
    val deviceId:String,

    @SerializedName("enterprise_id")
    val enterpriseId: String,

    @SerializedName("subject_type")
    val subjectType: String = "PHONE",

    @SerializedName("method")
    val method: String = "OTP",

    @SerializedName("use_case")
    var useCase: String = "ROCKETPAY_LOGIN"
)

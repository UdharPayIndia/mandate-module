package com.rocketpay.mandate.feature.login.data.entities

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class OtpRequest(

    @SerializedName("subject_id")
    var subjectId: String,

    @SerializedName("context")
    var context: Signature,

    @SerializedName("subject_type")
    var subjectType: String = "PHONE",

    @SerializedName("method")
    var method: String = "OTP",

    @SerializedName("use_case")
    var useCase: String = "ROCKETPAY_LOGIN",

)

@Keep
internal data class Signature(
    @SerializedName("signature")
    var signature: String
)


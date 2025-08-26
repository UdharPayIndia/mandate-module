package com.rocketpay.mandate.feature.login.data.entities

import android.os.Build
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.rocketpay.mandate.BuildConfig
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.LanguageUtils

@Keep
internal data class CreateDeviceRequest(

    @SerializedName("uid")
    var uid: String?,

    @SerializedName("uid_type")
    var uidType: String = "AD_ID",

    @SerializedName("manufacturer")
    var manufacturer: String = Build.MANUFACTURER,

    @SerializedName("model")
    var model: String = Build.MODEL,

    @SerializedName("platform")
    var platform: String = "sdk",

    @SerializedName("platform_version")
    var platformVersion: String = Build.VERSION.SDK_INT.toString(),

    @SerializedName("app_version")
    var appVersion: String = BuildConfig.VERSION_CODE.toString(),

    @SerializedName("language")
    var language: String = LanguageUtils.getDeviceLocale(),
)
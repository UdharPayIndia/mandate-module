package com.rocketpay.mandate.feature.login.data.entities

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
internal class DeviceResponseDto(
    @SerializedName("id") val id: String,

    @SerializedName("uid") var uid: String?,

    @SerializedName("uid_type") var uidType: String?,

    @SerializedName("manufacturer") var manufacturer: String?,

    @SerializedName("model") var model: String?,

    @SerializedName("platform") var platform: String?,

    @SerializedName("platform_version") var platformVersion: String?,

    @SerializedName("app_version") var appVersion: String?,

    @SerializedName("language") var language: String?,
) : Serializable
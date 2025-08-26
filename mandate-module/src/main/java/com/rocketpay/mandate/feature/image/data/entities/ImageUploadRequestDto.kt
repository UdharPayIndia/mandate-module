package com.rocketpay.mandate.feature.image.data.entities

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
internal data class ImageUploadRequestDto(@SerializedName("profile_picture") val profile_picture: String? = null,
                            @SerializedName("aadhar_front") val aadhar_front: String? = null,
                            @SerializedName("aadhar_back") val aadhar_back: String? = null,
                            @SerializedName("pan") val pan: String? = null,
                            @SerializedName("signature") val signature: String? = null): Serializable
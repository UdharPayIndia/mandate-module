package com.rocketpay.mandate.feature.image.data.entities

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal class ImagePreSignedResponseDto(@SerializedName("upload_url") val uploadUrl: String,
                               @SerializedName("access_url") val accessUrl: String,
                               @SerializedName("object_key") val objectKey: String?)
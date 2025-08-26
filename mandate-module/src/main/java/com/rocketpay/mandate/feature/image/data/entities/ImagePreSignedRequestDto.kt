package com.rocketpay.mandate.feature.image.data.entities

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal class ImagePreSignedRequestDto(@SerializedName("document_type") val documentType: String,
                               @SerializedName("size") val size: String,
                               @SerializedName("file_extension") val fileExtension: String,
                               @SerializedName("product") val product: String = "KEY")
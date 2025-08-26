package com.rocketpay.mandate.feature.mandate.domain.entities

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal class WhatsAppMessageConfig(@SerializedName("experiment") val experiment: String,
                            @SerializedName("data") val data: List<WhatsAppMessageData>)

@Keep
internal class WhatsAppMessageData(@SerializedName("type")val type: String,
                          @SerializedName("message") val message: String)
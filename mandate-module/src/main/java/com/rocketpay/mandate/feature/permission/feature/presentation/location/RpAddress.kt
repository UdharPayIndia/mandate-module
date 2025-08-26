package com.rocketpay.mandate.feature.permission.feature.presentation.location

import androidx.annotation.Keep
import java.io.Serializable

@Keep
internal class RpAddress(
    var buildingNumber: String = "",
    var fullformAddress: String = "",
    var landmark: String = "",
    var city: String = "",
    var state: String = "",
    var country: String = "",
    var pincode: String = "",
    var latitude: String = "",
    var longitude: String = "",
    var accuracy: Float = 0.0F
): Serializable

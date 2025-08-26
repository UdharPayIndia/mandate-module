package com.rocketpay.mandate.feature.permission.common

internal sealed class PermissionType(val value: String) {
    object Call : PermissionType("CALL")
    object Camera : PermissionType("CAMERA")
    object Contact : PermissionType("CONTACT")
    object Location : PermissionType("LOCATION")
    object Storage : PermissionType("STORAGE")
    object Gps : PermissionType("GPS")
    object Sms : PermissionType("SMS")
    object ApproximateLocation: PermissionType("APPROXIMATE_LOCATION")

    companion object {
        val map by lazy {
            mapOf(
                "CALL" to Call,
                "CAMERA" to Camera,
                "CONTACT" to Contact,
                "LOCATION" to Location,
                "STORAGE" to Storage,
                "GPS" to Gps,
                "SMS" to Sms,
                "APPROXIMATE_LOCATION" to ApproximateLocation
            )
        }
    }
}

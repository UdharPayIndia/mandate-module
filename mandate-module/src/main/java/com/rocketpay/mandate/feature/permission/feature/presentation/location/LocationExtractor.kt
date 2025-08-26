package com.rocketpay.mandate.feature.permission.feature.presentation.location

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import com.google.android.gms.location.LocationResult
import java.util.Locale

internal class LocationExtractor(context: Context) {

    private val geoCoder = Geocoder(context, Locale.US)

    fun getAddress(locationResult: LocationResult?): RpAddress? {
        var kbAddress: RpAddress? = null
        if (locationResult != null) {
            for (location in locationResult.locations) {
                location?.let { it ->
                    kbAddress = getAddress(it)
                }
            }
        }
        return kbAddress
    }

    private fun getAddress(location: Location): RpAddress? {
        var kbAddress: RpAddress? = null
        try {
            geoCoder.getFromLocation(location.latitude, location.longitude, 1)?.forEach { address ->
                kbAddress = if(address != null){
                    RpAddress(
                        "",
                        getAddressLine(address),
                        "",
                        address.locality.orEmpty(),
                        address.adminArea.orEmpty(),
                        address.countryCode.orEmpty(),
                        address.postalCode.orEmpty(),
                        location.latitude.toString(), location.longitude.toString(), location.accuracy
                    )
                }else{
                    RpAddress(latitude = location.latitude.toString(),
                        longitude = location.longitude.toString(),
                        accuracy = location.accuracy)
                }
            }
        } catch (e: Exception) {
        }
        return kbAddress
    }

    private fun getAddressLine(address: Address): String {
        try {
            val freeFormAddress =
                address.getAddressLine(0).replace(address.countryName.orEmpty(), "")
                    .replace(address.adminArea.orEmpty(), "")
                    .replace(address.locality.orEmpty(), "")
                    .replace(address.postalCode.orEmpty(), "")
            val keywords = freeFormAddress.split(",")
            val correctFreeFormAddress = StringBuffer("")
            keywords.forEachIndexed { index, s ->
                if (!s.isBlank()) {
                    correctFreeFormAddress.append(
                        if (index == keywords.size - 1) {
                            s
                        } else {
                            "$s ,"
                        }
                    )
                }
            }
            if (correctFreeFormAddress.trim().last().equals(",")) {
                correctFreeFormAddress.dropLast(1)
            }
            return correctFreeFormAddress.toString()
        }catch (e: Exception){
            return ""
        }
    }
}

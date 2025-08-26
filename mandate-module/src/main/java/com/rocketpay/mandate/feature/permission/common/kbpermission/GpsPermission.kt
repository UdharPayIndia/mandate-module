package com.rocketpay.mandate.feature.permission.common.kbpermission

import android.app.Activity
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.IntentSender.SendIntentException
import android.location.LocationManager
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.rocketpay.mandate.feature.permission.common.BasePermission

internal class GpsPermission : BasePermission(arrayOf()) {

    override fun requestPermission(fragment: Fragment, requestCode: Int) {
        val gpsSwitchListener = fragment as GpsSwitchListener

        LocationServices
            .getSettingsClient(fragment.requireActivity())
            .checkLocationSettings(getLocationSettingsRequest())
            .addOnSuccessListener { gpsSwitchListener.onGpsAllowed() }
            .addOnFailureListener {
                val statusCode = (it as ApiException).statusCode
                when(statusCode){
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        if (it is ResolvableApiException) {
                            try {
                                gpsSwitchListener.onGpsRequest()
                                fragment.startIntentSenderForResult(it.resolution.intentSender, requestCode,
                                    null, 0, 0, 0, null)
                            } catch (sendEx: SendIntentException) {
                                gpsSwitchListener.onGpsDeny()
                            }
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        gpsSwitchListener.onGpsDeny()
                    }
                }
            }
    }

    override fun isAllowed(context: Context): Boolean {
        return (context.getSystemService(LOCATION_SERVICE) as LocationManager).isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    override fun isPermanentDenied(activity: Activity): Boolean {
        return false
    }

    private fun getLocationSettingsRequest(): LocationSettingsRequest {
        return LocationSettingsRequest.Builder()
            .addLocationRequest(getLocationRequest())
            .setAlwaysShow(true)
            .build()
    }

    private fun getLocationRequest(): LocationRequest {
        return LocationRequest.create()
            .setInterval(500)
            .setFastestInterval(250)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
    }
}

package com.rocketpay.mandate.feature.permission.feature.presentation.location

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.rocketpay.mandate.feature.permission.feature.presentation.location.injection.LocationComponent
import javax.inject.Inject

internal class LocationManager (val context: Context) {

    @Inject
    internal lateinit var locationRegister: LocationRegister
    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    @Inject
    internal lateinit var locationExtractor: LocationExtractor

    companion object {
        private const val LOCATION_UPDATE_INTERVAL = 5000L
    }

    fun registerLocationListener(locationListener: LocationListener) {
        if (!::locationRegister.isInitialized) {
            LocationComponent.Initializer.init(context).inject(this)
        }
        locationRegister.registerLocationListener(locationListener)
    }

    @SuppressLint("MissingPermission")
    fun startRequestLocation(locationUpdateInterval: Long = LOCATION_UPDATE_INTERVAL) {
        fusedLocationProviderClient.requestLocationUpdates(getRequest(locationUpdateInterval), locationCallback, null)
    }

    private fun getRequest(locationUpdateInterval: Long): LocationRequest {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = locationUpdateInterval
        return locationRequest
    }

    private var locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationRegister.notify(locationExtractor.getAddress(locationResult))
        }
    }

    fun unregisterLocationListener(locationListener: LocationListener) {
        if (::locationRegister.isInitialized) {
            locationRegister.unregisterLocationListener(locationListener)
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }
}

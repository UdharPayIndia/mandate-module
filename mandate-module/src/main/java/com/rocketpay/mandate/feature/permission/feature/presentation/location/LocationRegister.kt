package com.rocketpay.mandate.feature.permission.feature.presentation.location

internal class LocationRegister {

    private var locationListeners: MutableList<LocationListener> = arrayListOf()

    fun registerLocationListener(locationListener: LocationListener) {
        locationListeners.add(locationListener)
    }

    fun unregisterLocationListener(locationListener: LocationListener) {
        locationListeners.remove(locationListener)
    }

    fun notify(kbAddress: RpAddress?) {
        locationListeners.forEach {
            it.onLocation(kbAddress)
        }
    }
}

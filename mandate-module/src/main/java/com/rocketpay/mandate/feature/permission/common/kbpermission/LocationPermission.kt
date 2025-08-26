package com.rocketpay.mandate.feature.permission.common.kbpermission

import com.rocketpay.mandate.feature.permission.common.BasePermission

internal open class LocationPermission : BasePermission(arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION))

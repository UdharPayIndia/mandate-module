package com.rocketpay.mandate.feature.permission.common.kbpermission

import com.rocketpay.mandate.feature.permission.common.BasePermission

internal open class ApproximateLocationPermission : BasePermission(arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION))

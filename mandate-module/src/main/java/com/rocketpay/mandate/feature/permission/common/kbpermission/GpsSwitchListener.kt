package com.rocketpay.mandate.feature.permission.common.kbpermission

internal interface GpsSwitchListener {
    fun onGpsAllowed()
    fun onGpsDeny()
    fun onGpsRequest()
}

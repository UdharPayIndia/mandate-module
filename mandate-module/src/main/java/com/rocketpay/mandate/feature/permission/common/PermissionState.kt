package com.rocketpay.mandate.feature.permission.common

internal sealed class PermissionState {
    object Pd : PermissionState()
    object Settings : PermissionState()
    object Permission : PermissionState()
}

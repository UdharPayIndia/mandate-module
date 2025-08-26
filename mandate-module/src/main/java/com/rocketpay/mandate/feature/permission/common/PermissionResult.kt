package com.rocketpay.mandate.feature.permission.common

internal sealed class PermissionResult {
    object Allowed : PermissionResult()
    object Denied : PermissionResult()
    object PermanentDenied : PermissionResult()
}

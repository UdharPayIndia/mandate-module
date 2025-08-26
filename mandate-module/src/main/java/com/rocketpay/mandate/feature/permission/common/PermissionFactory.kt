package com.rocketpay.mandate.feature.permission.common

import com.rocketpay.mandate.feature.permission.common.kbpermission.ApproximateLocationPermission
import com.rocketpay.mandate.feature.permission.common.kbpermission.CallPermission
import com.rocketpay.mandate.feature.permission.common.kbpermission.CameraPermission
import com.rocketpay.mandate.feature.permission.common.kbpermission.ContactPermission
import com.rocketpay.mandate.feature.permission.common.kbpermission.GpsPermission
import com.rocketpay.mandate.feature.permission.common.kbpermission.LocationPermission
import com.rocketpay.mandate.feature.permission.common.kbpermission.SmsPermission
import com.rocketpay.mandate.feature.permission.common.kbpermission.StoragePermission

internal class PermissionFactory {

    fun getPermission(permissionType: PermissionType): Permission {
        return when (permissionType) {
            PermissionType.Call -> CallPermission()
            PermissionType.Camera -> CameraPermission()
            PermissionType.Contact -> ContactPermission()
            PermissionType.Location -> LocationPermission()
            PermissionType.ApproximateLocation -> ApproximateLocationPermission()
            PermissionType.Storage -> StoragePermission()
            PermissionType.Gps -> GpsPermission()
            PermissionType.Sms -> SmsPermission()
        }
    }
}

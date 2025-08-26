package com.rocketpay.mandate.feature.permission.common

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

internal abstract class BasePermission(private var requestedPermissions: Array<String>) : Permission {

    override fun requestPermission(fragment: Fragment, requestCode: Int) {
        fragment.requestPermissions(requestedPermissions, requestCode)
    }

    override fun isAllowed(context: Context): Boolean {
        var isPermissionGiven = true
        requestedPermissions.forEach {
            isPermissionGiven = isPermissionGiven && ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
        return isPermissionGiven
    }

    override fun isPermanentDenied(activity: Activity): Boolean {
        var isPermanentDenied = true
        requestedPermissions.forEach {
            isPermanentDenied = isPermanentDenied && !ActivityCompat.shouldShowRequestPermissionRationale(activity, it)
        }
        return isPermanentDenied
    }
}

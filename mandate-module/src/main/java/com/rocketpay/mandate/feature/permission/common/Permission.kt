package com.rocketpay.mandate.feature.permission.common

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment

internal interface Permission {

    fun requestPermission(fragment: Fragment, requestCode: Int)

    fun isAllowed(context: Context): Boolean

    fun isPermanentDenied(activity: Activity): Boolean
}

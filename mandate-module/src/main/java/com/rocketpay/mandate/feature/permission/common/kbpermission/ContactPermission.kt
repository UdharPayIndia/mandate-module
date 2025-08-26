package com.rocketpay.mandate.feature.permission.common.kbpermission

import android.Manifest
import com.rocketpay.mandate.feature.permission.common.BasePermission

internal class ContactPermission : BasePermission(arrayOf(Manifest.permission.READ_CONTACTS))

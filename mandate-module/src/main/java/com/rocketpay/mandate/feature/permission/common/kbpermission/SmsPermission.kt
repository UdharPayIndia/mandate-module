package com.rocketpay.mandate.feature.permission.common.kbpermission

import android.Manifest
import com.rocketpay.mandate.feature.permission.common.BasePermission

internal class SmsPermission : BasePermission(arrayOf(Manifest.permission.READ_SMS))
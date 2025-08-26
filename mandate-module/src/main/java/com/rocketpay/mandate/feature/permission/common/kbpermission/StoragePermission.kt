package com.rocketpay.mandate.feature.permission.common.kbpermission

import android.Manifest
import com.rocketpay.mandate.feature.permission.common.BasePermission

internal class StoragePermission : BasePermission(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))

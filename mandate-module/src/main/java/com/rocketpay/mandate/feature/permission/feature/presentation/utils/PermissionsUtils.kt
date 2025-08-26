package com.rocketpay.mandate.feature.permission.feature.presentation.utils

import androidx.fragment.app.Fragment
import com.rocketpay.mandate.feature.permission.common.PermissionFactory
import com.rocketpay.mandate.feature.permission.common.PermissionType
import com.rocketpay.mandate.feature.permission.feature.data.PermissionRepositoryImpl
import com.rocketpay.mandate.feature.permission.feature.data.local.datastore.PermissionDataStore
import com.rocketpay.mandate.feature.permission.feature.domain.entities.PermissionAttemptStatus
import com.rocketpay.mandate.feature.permission.feature.domain.usecases.PermissionUseCase
import com.rocketpay.mandate.common.basemodule.common.data.cache.DataStore
import com.rocketpay.mandate.main.init.MandateManager

internal object PermissionsUtils {

    const val REQUEST_PERMISSION = 320

    private val permissionRepository = PermissionUseCase(
        PermissionRepositoryImpl(
            PermissionDataStore(
                DataStore(MandateManager.getInstance().getContext(), "permission")
            )
        )
    )

    fun checkPermission(permissionType: PermissionType): Boolean {
        return PermissionFactory().getPermission(permissionType).isAllowed(MandateManager.getInstance().getContext())
    }

    fun checkAndRequestPermission(fragment: Fragment, permissionType: PermissionType, onAllowed: () -> Unit, onPermanentDenied: () -> Unit, onDenied: () -> Unit) {
        val permission = PermissionFactory().getPermission(permissionType)
        when {
            permission.isAllowed(fragment.requireContext()) -> {
                onAllowed()
            }
            permissionRepository.getPermissionAttemptedStatus(permissionType) is PermissionAttemptStatus.Attempted
                    && permission.isPermanentDenied(fragment.requireActivity()) -> {
                onPermanentDenied()
            }
            else -> {
                onDenied()
            }
        }
    }

    fun requestPermissions(fragment: Fragment, permissionType: PermissionType) {
        val permission = PermissionFactory().getPermission(permissionType)
        permission.requestPermission(fragment, REQUEST_PERMISSION)
        if (permissionRepository.getPermissionAttemptedStatus(permissionType) is PermissionAttemptStatus.NotAttempted) {
            permissionRepository.setPermissionAttemptedStatus(permissionType, PermissionAttemptStatus.Attempted)
        }
    }
}

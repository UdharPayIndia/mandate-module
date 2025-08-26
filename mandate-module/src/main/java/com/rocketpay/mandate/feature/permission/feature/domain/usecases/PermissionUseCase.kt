package com.rocketpay.mandate.feature.permission.feature.domain.usecases

import com.rocketpay.mandate.feature.permission.common.PermissionType
import com.rocketpay.mandate.feature.permission.feature.domain.entities.PermissionAttemptStatus
import com.rocketpay.mandate.feature.permission.feature.domain.entities.ProminentDisclosureStatus
import com.rocketpay.mandate.feature.permission.feature.domain.repositories.PermissionRepository

internal class PermissionUseCase internal constructor(private val permissionRepository: PermissionRepository) {

    fun getPermissionAttemptedStatus(permissionType: PermissionType): PermissionAttemptStatus {
        return permissionRepository.getPermissionAttemptedStatus(permissionType)
    }

    fun setPermissionAttemptedStatus(
        permissionType: PermissionType,
        permissionAttemptStatus: PermissionAttemptStatus
    ) {
        permissionRepository.setPermissionAttemptedStatus(permissionType, permissionAttemptStatus)
    }

    fun getProminentDisclosureStatus(permissionType: PermissionType): ProminentDisclosureStatus {
        return permissionRepository.getProminentDisclosureStatus(permissionType)
    }

    fun setProminentDisclosureStatus(
        permissionType: PermissionType,
        prominentDisclosureStatus: ProminentDisclosureStatus
    ) {
        permissionRepository.setProminentDisclosureStatus(permissionType, prominentDisclosureStatus)
    }
}

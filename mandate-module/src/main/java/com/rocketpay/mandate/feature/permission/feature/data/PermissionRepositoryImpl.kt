package com.rocketpay.mandate.feature.permission.feature.data

import com.rocketpay.mandate.feature.permission.common.PermissionType
import com.rocketpay.mandate.feature.permission.feature.data.local.datastore.PermissionDataStore
import com.rocketpay.mandate.feature.permission.feature.domain.entities.PermissionAttemptStatus
import com.rocketpay.mandate.feature.permission.feature.domain.entities.ProminentDisclosureStatus
import com.rocketpay.mandate.feature.permission.feature.domain.repositories.PermissionRepository

internal class PermissionRepositoryImpl(private val permissionDataStore: PermissionDataStore):
    PermissionRepository {

    override fun getPermissionAttemptedStatus(permissionType: PermissionType): PermissionAttemptStatus {
        return PermissionAttemptStatus.get(permissionDataStore.getPermissionAttemptedStatus(permissionType))
    }

    override fun setPermissionAttemptedStatus(
        permissionType: PermissionType,
        permissionAttemptStatus: PermissionAttemptStatus
    ) {
        permissionDataStore.setPermissionAttemptedStatus(permissionType, permissionAttemptStatus)
    }

    override fun getProminentDisclosureStatus(permissionType: PermissionType): ProminentDisclosureStatus {
        return ProminentDisclosureStatus.get(permissionDataStore.getProminentDisclosureStatus(permissionType))
    }

    override fun setProminentDisclosureStatus(
        permissionType: PermissionType,
        prominentDisclosureStatus: ProminentDisclosureStatus
    ) {
        permissionDataStore.setProminentDisclosureStatus(permissionType, prominentDisclosureStatus)
    }
}

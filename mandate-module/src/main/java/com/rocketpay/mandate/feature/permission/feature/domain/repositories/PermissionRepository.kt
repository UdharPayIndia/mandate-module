package com.rocketpay.mandate.feature.permission.feature.domain.repositories

import com.rocketpay.mandate.feature.permission.common.PermissionType
import com.rocketpay.mandate.feature.permission.feature.domain.entities.PermissionAttemptStatus
import com.rocketpay.mandate.feature.permission.feature.domain.entities.ProminentDisclosureStatus

internal interface PermissionRepository {

    fun getPermissionAttemptedStatus(permissionType: PermissionType): PermissionAttemptStatus

    fun setPermissionAttemptedStatus(permissionType: PermissionType, permissionAttemptStatus: PermissionAttemptStatus)

    fun getProminentDisclosureStatus(permissionType: PermissionType): ProminentDisclosureStatus

    fun setProminentDisclosureStatus(permissionType: PermissionType, prominentDisclosureStatus: ProminentDisclosureStatus)
}

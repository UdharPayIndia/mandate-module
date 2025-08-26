package com.rocketpay.mandate.feature.permission.feature.data.local.datastore

import com.rocketpay.mandate.feature.permission.common.PermissionType
import com.rocketpay.mandate.feature.permission.feature.domain.entities.PermissionAttemptStatus
import com.rocketpay.mandate.feature.permission.feature.domain.entities.ProminentDisclosureStatus
import com.rocketpay.mandate.common.basemodule.common.data.cache.DataStore

internal class PermissionDataStore(private val dataStore: DataStore) {

    companion object {
        private const val PERMISSION_ATTEMPTED_STATUS = "permissionAttemptedStatus"
        private const val PROMINENT_DISCLOSURE_STATUS = "prominentDisclosureStatus"
    }

    fun getPermissionAttemptedStatus(permissionType: PermissionType): String {
        return dataStore.getString("${permissionType.value}_${PERMISSION_ATTEMPTED_STATUS}", PermissionAttemptStatus.NotAttempted.value)
    }

    fun setPermissionAttemptedStatus(
        permissionType: PermissionType,
        permissionAttemptStatus: PermissionAttemptStatus
    ) {
        dataStore.setString("${permissionType.value}_${PERMISSION_ATTEMPTED_STATUS}", permissionAttemptStatus.value)
    }

    fun getProminentDisclosureStatus(permissionType: PermissionType): String {
        return dataStore.getString("${permissionType.value}_${PROMINENT_DISCLOSURE_STATUS}", ProminentDisclosureStatus.Denied.value)
    }

    fun setProminentDisclosureStatus(
        permissionType: PermissionType,
        prominentDisclosureStatus: ProminentDisclosureStatus
    ) {
        dataStore.setString("${permissionType.value}_${PROMINENT_DISCLOSURE_STATUS}", prominentDisclosureStatus.value)
    }
}

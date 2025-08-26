package com.rocketpay.mandate.feature.permission.feature.domain.entities

internal sealed class PermissionAttemptStatus(val value: String) {
    object Attempted: PermissionAttemptStatus("ATTEMPTED")
    object NotAttempted: PermissionAttemptStatus("NOT_ATTEMPTED")

    companion object {
        val map by lazy {
            mapOf(
                "ATTEMPTED" to Attempted,
                "NOT_ATTEMPTED" to NotAttempted
            )
        }

        fun get(permissionAttemptedStatus: String): PermissionAttemptStatus {
            return map[permissionAttemptedStatus] ?: NotAttempted
        }
    }
}

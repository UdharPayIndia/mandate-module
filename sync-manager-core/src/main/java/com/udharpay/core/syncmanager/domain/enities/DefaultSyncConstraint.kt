package com.udharpay.core.syncmanager.domain.enities

object DefaultSyncConstraint {

    const val NETWORK = "network"
    const val LOGIN = "login"
    const val FORCE_LOGOUT = "forceLogout"

    private val booleanConstraintResolver: (Any, Any) -> Boolean  = { currentValue: Any, expectedValue: Any ->
        val newCurrentValue = currentValue as Boolean
        val newExpectedValue = expectedValue as Boolean
        if (newExpectedValue) {
            newCurrentValue
        } else {
            true
        }
    }

    fun getDefaultLoginConstraint(isLoginRequire: Boolean): SyncConstraint {
        return SyncConstraint.Builder()
            .setConstraint(LOGIN, isLoginRequire, booleanConstraintResolver)
            .build()
    }

    fun getDefaultNetworkConstraint(isNetworkRequire: Boolean): SyncConstraint {
        return SyncConstraint.Builder()
            .setConstraint(NETWORK, isNetworkRequire, booleanConstraintResolver)
            .build()
    }

    fun getDefaultForceLogoutConstraint(isForceLogout: Boolean): SyncConstraint {
        return SyncConstraint.Builder()
            .setConstraint(FORCE_LOGOUT, isForceLogout, booleanConstraintResolver)
            .build()
    }

    fun getLoginAndNetworkConstraint(isLoginRequire: Boolean, isNetworkRequire: Boolean): SyncConstraint {
        return SyncConstraint.Builder()
            .setConstraint(LOGIN, isLoginRequire, booleanConstraintResolver)
            .setConstraint(NETWORK, isNetworkRequire, booleanConstraintResolver)
            .build()
    }

    fun getLoginNetworkAndForceLogoutConstraint(isLoginRequire: Boolean, isNetworkRequire: Boolean, isForceLogout: Boolean): SyncConstraint {
        return SyncConstraint.Builder()
            .setConstraint(LOGIN, isLoginRequire, booleanConstraintResolver)
            .setConstraint(NETWORK, isNetworkRequire, booleanConstraintResolver)
            .setConstraint(FORCE_LOGOUT, isForceLogout, booleanConstraintResolver)
            .build()
    }
}

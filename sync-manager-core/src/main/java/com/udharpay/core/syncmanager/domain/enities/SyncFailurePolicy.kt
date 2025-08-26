package com.udharpay.core.syncmanager.domain.enities

sealed class SyncFailurePolicy(val value: String) {
    object Ignore: SyncFailurePolicy("IGNORE")
    object Cascade: SyncFailurePolicy("CASCADE")

    companion object {
        val map by lazy {
            mapOf(
                "IGNORE" to Ignore,
                "CASCADE" to Cascade
            )
        }
    }
}


package com.udharpay.core.syncmanager.domain.enities

sealed class ExistingSyncPolicy(val value: String) {
    object Append: ExistingSyncPolicy("APPEND")
    object Keep: ExistingSyncPolicy("KEEP")

    companion object {
        val map by lazy {
            mapOf(
                "APPEND" to Append,
                "KEEP" to Keep
            )
        }
    }
}

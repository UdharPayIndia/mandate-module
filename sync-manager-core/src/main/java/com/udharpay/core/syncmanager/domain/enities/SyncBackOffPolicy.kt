package com.udharpay.core.syncmanager.domain.enities

sealed class SyncBackOffPolicy(val retryType: String) {
    object Linear: SyncBackOffPolicy("LINEAR")
    object Exponential: SyncBackOffPolicy("EXPONENTIAL")

    companion object {
        val map by lazy {
            mapOf(
                "LINEAR" to Linear,
                "EXPONENTIAL" to Exponential
            )
        }
    }
}

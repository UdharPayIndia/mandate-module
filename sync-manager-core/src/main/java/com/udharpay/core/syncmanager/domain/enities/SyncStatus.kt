package com.udharpay.core.syncmanager.domain.enities

sealed class SyncStatus(val value: String) {
    object Enqueue: SyncStatus("ENQUEUE")
    object InProgress: SyncStatus("IN_PROGRESS")
    object Success: SyncStatus("SUCCESS")
    object Failed: SyncStatus("FAILED")

    companion object {
        val map by lazy {
            mapOf(
                "ENQUEUE" to Enqueue,
                "IN_PROGRESS" to InProgress,
                "SUCCESS" to Success,
                "FAILED" to Failed
            )
        }
    }
}

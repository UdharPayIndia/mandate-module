package com.udharpay.core.syncmanager.domain.enities

data class SyncRequest(
        val id: String,
        val syncType: String,
        val syncStatus: SyncStatus,
        val retryCount: Int,
        val backOffTime: Long
)

package com.udharpay.core.syncmanager.domain.enities

data class SyncVertex(
        val syncType: String,
        val dependencies: List<String>,
        val priority: SyncPriority,
        val constraint: SyncConstraint,
        val retryCount: Int,
        val backOffTime: Long,
        val id: String
)

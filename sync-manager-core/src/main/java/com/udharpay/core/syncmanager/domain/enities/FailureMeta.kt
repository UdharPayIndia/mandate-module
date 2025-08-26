package com.udharpay.core.syncmanager.domain.enities

data class FailureMeta(
    val syncType: String,
    val requestId: String,
    val status: SyncStatus
) {
    var timeStamp: Long = 0L
}

package com.udharpay.core.syncmanager.data.entities

class SyncRequestEnt (
        var id: String,
        var syncType: String,
        var syncStatus: String,
        val retryCount: Int,
        val backOffTime: Long
)

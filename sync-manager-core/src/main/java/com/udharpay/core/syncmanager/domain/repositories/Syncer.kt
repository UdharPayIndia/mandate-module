package com.udharpay.core.syncmanager.domain.repositories

import com.udharpay.core.syncmanager.domain.enities.SyncStatus

interface Syncer {
    suspend fun sync(): SyncStatus
}

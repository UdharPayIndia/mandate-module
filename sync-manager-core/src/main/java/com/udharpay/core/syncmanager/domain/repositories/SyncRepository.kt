package com.udharpay.core.syncmanager.domain.repositories

import com.udharpay.core.syncmanager.domain.enities.SyncRequest
import com.udharpay.core.syncmanager.domain.enities.SyncStatus
import kotlinx.coroutines.flow.Flow

interface SyncRepository {
    fun add(requests: List<SyncRequest>)
    fun update(requests: List<SyncRequest>)
    fun getRequests(syncStatus: SyncStatus,backOffTime: Long): List<SyncRequest>
    fun getRequests(syncType: String): List<SyncRequest>
    fun getRequestCount(syncStatus: SyncStatus, backOffTime: Long): Long
    fun removeSuccessRequest()
    fun replaceInProgressWithEnqueue()
    fun removeDuplicateRequest()
    fun getSyncStatus(syncType: String): Flow<SyncStatus?>
    fun getSyncStatus(syncTypes: List<String>): Flow<HashMap<String, SyncStatus>>
    fun removeFailedRequest()
    fun replaceFailedWithEnqueue()
}

package com.udharpay.core.syncmanager.data.datasource

import com.udharpay.core.syncmanager.data.entities.SyncRequestEnt
import com.udharpay.core.syncmanager.domain.enities.SyncStatus
import com.udharpay.core.syncmanager.domain.enities.SyncTypeVsStatus
import kotlinx.coroutines.flow.Flow

interface SyncRequestLDS {
    fun update(requests: List<SyncRequestEnt>)

    fun add(requests: List<SyncRequestEnt>)
    fun getSyncRequests(syncStatus: String, backOffTime: Long): List<SyncRequestEnt>
    fun getSyncRequest(syncType: String): List<SyncRequestEnt>

    fun getRequestCount(syncStatus: SyncStatus, backOffTime: Long): Long

    fun removeSuccessRequest()
    fun removeFailedRequest()
    fun replaceFailedWithEnqueue()
    fun replaceInProgressWithEnqueue()
    fun removeDuplicate()

    fun getSyncStatus(syncType: String): Flow<String>
    fun getSyncStatus(syncTypes: List<String>): Flow<List<SyncTypeVsStatus>>

}

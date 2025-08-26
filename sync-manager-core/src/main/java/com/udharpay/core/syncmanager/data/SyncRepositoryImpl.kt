package com.udharpay.core.syncmanager.data

import com.udharpay.core.syncmanager.data.datasource.SyncRequestLDS
import com.udharpay.core.syncmanager.data.mapper.SyncDomToEntMapper
import com.udharpay.core.syncmanager.data.mapper.SyncEntToDomMapper
import com.udharpay.core.syncmanager.domain.enities.SyncRequest
import com.udharpay.core.syncmanager.domain.enities.SyncStatus
import com.udharpay.core.syncmanager.domain.repositories.SyncRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

class SyncRepositoryImpl(
    private val syncRequestLDS: SyncRequestLDS,
    private val syncEntToDomMapper: SyncEntToDomMapper,
    private val syncDomToEntMapper: SyncDomToEntMapper
): SyncRepository {

    override fun update(requests: List<SyncRequest>) {
        syncRequestLDS.update(syncDomToEntMapper.mapList(requests))
    }

    override fun add(requests: List<SyncRequest>) {
        syncRequestLDS.add(syncDomToEntMapper.mapList(requests))
    }

    override fun getRequests(syncStatus: SyncStatus, backOffTime: Long): List<SyncRequest> {
        return syncEntToDomMapper.mapList(syncRequestLDS.getSyncRequests(syncStatus.value, backOffTime))
    }

    override fun getRequests(syncType: String): List<SyncRequest> {
        return syncEntToDomMapper.mapList(syncRequestLDS.getSyncRequest(syncType))
    }

    override fun getRequestCount(syncStatus: SyncStatus, backOffTime: Long): Long {
        return syncRequestLDS.getRequestCount(syncStatus, backOffTime)
    }

    override fun removeSuccessRequest() {
        syncRequestLDS.removeSuccessRequest()
    }

    override fun removeFailedRequest() {
        syncRequestLDS.removeFailedRequest()
    }

    override fun replaceFailedWithEnqueue() {
        syncRequestLDS.replaceFailedWithEnqueue()
    }

    override fun replaceInProgressWithEnqueue() {
        syncRequestLDS.replaceInProgressWithEnqueue()
    }

    override fun removeDuplicateRequest() {
        syncRequestLDS.removeDuplicate()
    }

    override fun getSyncStatus(syncType: String): Flow<SyncStatus?> {
        return syncRequestLDS.getSyncStatus(syncType).transform {
            emit(SyncStatus.map[it])
        }
    }

    override fun getSyncStatus(syncTypes: List<String>): Flow<HashMap<String, SyncStatus>> {
        return syncRequestLDS.getSyncStatus(syncTypes).transform { syncTypeVsStatusList ->
            val hashMap: HashMap<String, SyncStatus> = hashMapOf()
            syncTypeVsStatusList.forEach {
                hashMap[it.syncType] = SyncStatus.map[it.syncStatus] ?: SyncStatus.Enqueue
            }
            emit(hashMap)
        }
    }
}

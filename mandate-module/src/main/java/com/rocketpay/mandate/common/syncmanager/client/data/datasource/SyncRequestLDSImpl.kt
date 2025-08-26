package com.rocketpay.mandate.common.syncmanager.client.data.datasource

import com.udharpay.core.syncmanager.data.datasource.SyncRequestLDS
import com.udharpay.core.syncmanager.data.entities.SyncRequestEnt
import com.udharpay.core.syncmanager.domain.enities.SyncStatus
import com.udharpay.core.syncmanager.domain.enities.SyncTypeVsStatus
import com.rocketpay.mandate.common.syncmanager.client.data.datasource.mapper.SyncEntToTableMapper
import com.rocketpay.mandate.common.syncmanager.client.data.datasource.mapper.SyncTableToEntMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

internal class SyncRequestLDSImpl(
    private val syncTableToEntMapper: SyncTableToEntMapper,
    private val syncEntToTableMapper: SyncEntToTableMapper,
    private val syncRequestDao: SyncRequestDao
): SyncRequestLDS {

    override fun update(requests: List<SyncRequestEnt>) {
        syncRequestDao.update(syncEntToTableMapper.mapList(requests))
    }

    override fun add(requests: List<SyncRequestEnt>) {
        syncRequestDao.insertAll(syncEntToTableMapper.mapList(requests))
    }

    override fun getSyncRequests(syncStatus: String, backOffTime: Long): List<SyncRequestEnt> {
        return syncTableToEntMapper.mapList(syncRequestDao.getAll(syncStatus, backOffTime))
    }

    override fun getSyncRequest(syncType: String): List<SyncRequestEnt> {
        return syncTableToEntMapper.mapList(syncRequestDao.getOne(syncType))
    }

    override fun getRequestCount(syncStatus: SyncStatus, backOffTime: Long): Long {
        return syncRequestDao.getCount(syncStatus.value, backOffTime)
    }

    override fun removeSuccessRequest() {
        syncRequestDao.removeSuccessRequest(SyncStatus.Success.value)
    }

    override fun removeFailedRequest() {
        syncRequestDao.removeFailedRequest(SyncStatus.Failed.value)
    }

    override fun replaceFailedWithEnqueue() {
        syncRequestDao.replaceFailedWithEnqueue(SyncStatus.Failed.value, SyncStatus.Enqueue.value)
    }

    override fun replaceInProgressWithEnqueue() {
        syncRequestDao.replace(SyncStatus.InProgress.value, SyncStatus.Enqueue.value)
    }

    override fun removeDuplicate() {
        syncRequestDao.removeDuplicate()
    }

    override fun getSyncStatus(syncType: String): Flow<String> {
        return syncRequestDao.getSyncStatus(syncType).distinctUntilChanged()
    }

    override fun getSyncStatus(syncTypes: List<String>): Flow<List<SyncTypeVsStatus>> {
        return syncRequestDao.getSyncStatuses(getSyncStatusQuery(syncTypes))
    }

    private fun getSyncStatusQuery(syncTypes: List<String>): String {
        var where = ""
        syncTypes.forEachIndexed { index, syncType ->
            where = when (index) {
                0 -> {
                    "type = '${syncType}'"
                }
                else -> {
                    " OR type = '${syncType}'"
                }
            }
        }
        return where
    }
}

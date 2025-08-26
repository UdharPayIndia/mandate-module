package com.udharpay.core.syncmanager.domain.usecases

import com.udharpay.kernel.kernelcommon.register.Register
import com.udharpay.core.syncmanager.domain.enities.SyncFailurePolicy
import com.udharpay.core.syncmanager.domain.enities.SyncRequest
import com.udharpay.core.syncmanager.domain.enities.SyncStatus
import com.udharpay.core.syncmanager.domain.enities.SyncVertex
import com.udharpay.core.syncmanager.domain.enities.graph.vertex.Vertex
import com.udharpay.core.syncmanager.domain.repositories.Sync

class FailurePolicyHandler {

    fun handleFailure(
        vertex: Vertex<SyncVertex>,
        syncStatus: SyncStatus,
        syncRequestManager: SyncRequestManager,
        syncGraphManager: SyncGraphManager,
        syncRegister: Register<String, Sync>
    ) {
        val sync = syncRegister.get(vertex.data.syncType)
        val failurePolicy = sync.syncFailurePolicy()
        val retryPolicy = sync.syncRetryPolicy()
        when(failurePolicy) {
            SyncFailurePolicy.Ignore -> {
                if (vertex.data.retryCount <= 0) {
                    syncGraphManager.handleVertexFailure(vertex)
                    syncGraphManager.onVertexExecutionFailed()

                    val request = map(vertex.data, syncStatus)

                    syncRequestManager.updateSyncRequest(request)
                } else {
                    syncGraphManager.handleVertexFailure(vertex)
                    syncGraphManager.onVertexExecutionFailed()

                    val backOffTime = syncRequestManager.idGenerator.currentTimeMillis() + retryPolicy.backOffTime
                    val retryCount = vertex.data.retryCount - 1
                    val request = map(vertex.data, syncStatus, retryCount, backOffTime)

                    syncRequestManager.updateSyncRequest(request)
                }
            }
            SyncFailurePolicy.Cascade -> {
                if (vertex.data.retryCount <= 0) {
                    val vertices = syncGraphManager.handleVerticesFailure(vertex)
                    syncGraphManager.onVertexExecutionFailed()

                    val requests = mapList(vertices.map { it.data }, syncStatus)

                    syncRequestManager.updateSyncRequests(requests)
                } else {
                    val vertices = syncGraphManager.handleVerticesFailure(vertex)
                    syncGraphManager.onVertexExecutionFailed()

                    val backOffTime = syncRequestManager.idGenerator.currentTimeMillis() + retryPolicy.backOffTime
                    val requests = mutableListOf<SyncRequest>()
                    vertices.forEach {
                        val retryCount = if  (it.data.id == vertex.data.id) {
                            it.data.retryCount - 1
                        } else {
                            it.data.retryCount
                        }

                        requests.add(map(it.data, syncStatus, retryCount, backOffTime))
                    }

                    syncRequestManager.updateSyncRequests(requests)
                }
            }
        }
    }

    fun map(source: SyncVertex, syncStatus: SyncStatus): SyncRequest {
        return SyncRequest(
            syncType = source.syncType,
            retryCount = source.retryCount,
            backOffTime = source.backOffTime,
            syncStatus = syncStatus,
            id = source.id,
        )
    }

    private fun map(source: SyncVertex, syncStatus: SyncStatus, retryCount: Int, backOffTime: Long): SyncRequest {
        return SyncRequest(
            syncType = source.syncType,
            retryCount = retryCount,
            backOffTime = backOffTime,
            syncStatus = syncStatus,
            id = source.id,
        )
    }

    private fun mapList(source: List<SyncVertex>, syncStatus: SyncStatus): List<SyncRequest> {
        return source.map { map(it, syncStatus) }
    }
}

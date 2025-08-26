package com.udharpay.core.syncmanager.domain.usecases

import com.udharpay.kernel.kernelcommon.idgenerator.IdGenerator
import com.udharpay.kernel.kernelcommon.register.Register
import com.udharpay.core.syncmanager.domain.enities.SyncRequest
import com.udharpay.core.syncmanager.domain.enities.SyncStatus
import com.udharpay.core.syncmanager.domain.repositories.Sync
import com.udharpay.core.syncmanager.domain.repositories.SyncRepository
import kotlinx.coroutines.flow.Flow

class SyncRequestManager(
    private val syncRepository: SyncRepository,
    private val syncRegister: Register<String, Sync>,
    val idGenerator: IdGenerator,
    private val existingSyncPolicyHandler: ExistingSyncPolicyHandler
) {
    private fun getSyncRequests(syncStatus: SyncStatus, backOffTime: Long): List<SyncRequest> {
        return syncRepository.getRequests(syncStatus, backOffTime)
    }

    fun updateSyncRequests(requests: List<SyncRequest>) {
        syncRepository.update(requests)
    }

    fun updateSyncRequest(request: SyncRequest) {
        syncRepository.update(listOf(request))
    }

    fun enqueueSyncRequest(requests: List<SyncRequest>) {
        syncRepository.add(requests)
    }

    fun getFilteredRequestWithExistingSycPolicy(syncTypes: List<String>): List<SyncRequest> {
        if (syncTypes.isEmpty()) {
            return emptyList()
        }

        val newRequests = createSyncRequests(syncTypes)
        if (newRequests.isEmpty()) {
            return emptyList()
        }

        val eligibleRequests = hashSetOf<SyncRequest>()
        newRequests.forEach { newRequest ->
            val existingSyncPolicy = syncRegister.get(newRequest.syncType).existingSyncPolicy()
            val existingRequests = syncRepository.getRequests(newRequest.syncType)
            if (existingRequests.isEmpty()) {
                eligibleRequests.add(newRequest)
            } else {
                existingRequests.forEach { existingRequest ->
                    if (existingSyncPolicyHandler.isEligibleRequest(existingRequest, existingSyncPolicy)) {
                        eligibleRequests.add(newRequest)
                    }
                }
            }
        }
        return eligibleRequests.toList()
    }

    fun getRequests(): List<SyncRequest> {
        val isRequestExist = isSyncRequestAvailable()
        if (!isRequestExist) {
            return emptyList()
        }

        val requests = mutableListOf<SyncRequest>()
        getSyncRequests(SyncStatus.Enqueue, idGenerator.currentTimeMillis()).forEach {
            if (syncRegister.isRegistered(it.syncType))  {
                requests.add(it)
            } else {
                // TODO: remove request for which sync register is not available
            }
        }
        return requests
    }

    private fun isSyncRequestAvailable(): Boolean {
        return syncRepository.getRequestCount(SyncStatus.Enqueue, idGenerator.currentTimeMillis()) > 0
    }

    private fun createSyncRequests(syncTypes: List<String>): List<SyncRequest> {
        val syncRequest = mutableListOf<SyncRequest>()
        syncTypes.forEach { syncType ->
            syncRequest.add(createSyncRequest(syncType))
        }
        return syncRequest
    }

    private fun createSyncRequest(syncType: String): SyncRequest {
        val syncRetryPolicy = syncRegister.get(syncType).syncRetryPolicy()
        return SyncRequest(
            id = idGenerator.uuid(),
            syncType = syncType,
            syncStatus = SyncStatus.Enqueue,
            retryCount = syncRetryPolicy.retryCount,
            backOffTime = syncRetryPolicy.backOffTime
        )
    }

    fun reset() {
        handleSuccessRequest()
        handleFailedRequest()
        handleRetryRequest()
        handleInProgressRequest()
        handleEnqueuedRequest()
    }

    private fun handleSuccessRequest() {
        syncRepository.removeSuccessRequest()
    }

    private fun handleFailedRequest() {
        syncRepository.removeFailedRequest()
    }

    private fun handleRetryRequest() {
        syncRepository.replaceFailedWithEnqueue()
    }

    private fun handleInProgressRequest() {
        syncRepository.replaceInProgressWithEnqueue()
    }

    private fun handleEnqueuedRequest() {
        syncRepository.removeDuplicateRequest()
    }

    fun getSyncStatus(syncType: String): Flow<SyncStatus?> {
        return syncRepository.getSyncStatus(syncType)
    }

    fun getSyncStatus(syncType: List<String>): Flow<HashMap<String, SyncStatus>> {
        return syncRepository.getSyncStatus(syncType)
    }
}

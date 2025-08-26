package com.udharpay.core.syncmanager.domain.usecases

import com.udharpay.kernel.kernelcommon.eventbus.EventBus
import com.udharpay.kernel.kernelcommon.register.Register
import com.udharpay.core.syncmanager.domain.enities.SyncEvent
import com.udharpay.core.syncmanager.domain.enities.SyncStatus
import com.udharpay.core.syncmanager.domain.enities.SyncVertex
import com.udharpay.core.syncmanager.domain.enities.graph.GraphStatus
import com.udharpay.core.syncmanager.domain.enities.graph.vertex.Vertex
import com.udharpay.core.syncmanager.domain.repositories.Sync
import com.udharpay.kernel.kernelcommon.taskexecutor.TaskExecutor
import kotlinx.coroutines.flow.Flow

class SyncManagerImpl(private val syncRequestManager: SyncRequestManager,
                      private val syncGraphManager: SyncGraphManager,
                      private val syncRequestToVertex: SyncRequestToVertexMapper,
                      private val syncRegister: Register<String, Sync>,
                      private val syncEventBus: EventBus<SyncEvent>,
                      private val taskExecutor: TaskExecutor,
                      private val syncAnalyticsHelper: SyncAnalyticsHandler,
                      private val syncImmediateExecutor: SyncImmediateExecutor,
                      private val failurePolicyHandler: FailurePolicyHandler
) {
    init {
        registerEventListener()
        registerGraphListener()
        initiateSync()
    }

    private fun registerEventListener() {
        taskExecutor.executeInBackground {
            syncEventBus.listen { event ->
                syncAnalyticsHelper.sendEvent(event)
                when (event) {
                    SyncEvent.Initiated -> {
                        handleInitialise()
                    }
                    SyncEvent.DeInitiated -> {
                        handleInitialise()
                    }
                    is SyncEvent.Enqueue -> {
                        handleEnqueue(event.syncTypes)
                    }
                    is SyncEvent.VertexAck -> {
                        handleVertexAck(event.vertex, event.syncStatus)
                    }
                    is SyncEvent.ConstraintChange -> {
                        handleConstraintChange()
                    }
                }
            }
        }
    }

    private fun registerGraphListener() {
        syncGraphManager.registerOnGraphExecutedListener {
            dispatchEvent(SyncEvent.DeInitiated)
        }
    }

    private fun handleEnqueue(syncTypes: List<String>) {
        val requests = syncRequestManager.getFilteredRequestWithExistingSycPolicy(syncTypes)
        if (requests.isNotEmpty()) {
            syncRequestManager.enqueueSyncRequest(requests)
        }
        handleInit()
    }

    private fun handleInit() {
        if (syncGraphManager.isIdle()) {
            val requests = syncRequestManager.getRequests()
            if (requests.isNotEmpty()) {
                syncGraphManager.init(syncRequestToVertex.mapList(requests))
            }
        }
    }

    private fun handleVertexAck(vertex: Vertex<SyncVertex>, syncStatus: SyncStatus) {
        when (syncStatus) {
            SyncStatus.Enqueue -> {
            }
            SyncStatus.InProgress -> {
                syncGraphManager.onVertexExecutionInProgress()
                syncRequestManager.updateSyncRequest(failurePolicyHandler.map(vertex.data, syncStatus))
            }
            SyncStatus.Success -> {
                syncGraphManager.onVertexExecutionSuccess(vertex)
                syncRequestManager.updateSyncRequest(failurePolicyHandler.map(vertex.data, syncStatus))
            }
            SyncStatus.Failed -> {
                failurePolicyHandler.handleFailure(vertex, syncStatus, syncRequestManager, syncGraphManager, syncRegister)
            }
        }
    }

    private fun handleInitialise() {
        handleReset()
        handleInit()
    }

    private fun handleReset() {
        syncRequestManager.reset()
        syncGraphManager.reset()
    }

    private fun handleConstraintChange() {
        handleInit()
    }

    private fun dispatchEvent(event: SyncEvent) {
        taskExecutor.executeInCurrent {
            syncEventBus.fire(event)
        }
    }

    fun initiateSync() {
        dispatchEvent(SyncEvent.Initiated)
    }

    //////////////////////////////////////////////////////////
    ////////////////////// Client APIs ///////////////////////
    //////////////////////////////////////////////////////////

    fun enqueue(syncType: String) {
        if (!syncRegister.isRegistered(syncType)) {
            throw Exception("Please provide valid syncType to enqueue")
        }
        dispatchEvent(SyncEvent.Enqueue(listOf(syncType)))
    }

    fun enqueue(syncTypes: List<String>) {
        syncTypes.forEach { syncType ->
            if (!syncRegister.isRegistered(syncType)) {
                throw Exception("Please provide valid syncTypes to enqueue")
            }
        }
        dispatchEvent(SyncEvent.Enqueue(syncTypes))
    }

    fun getStatus(syncType: String): Flow<SyncStatus?> {
        return syncRequestManager.getSyncStatus(syncType)
    }

    fun getStatus(syncTypes: List<String>): Flow<HashMap<String, SyncStatus>> {
        if (syncTypes.isEmpty()) {
            throw Exception("Please provide non empty list to check the status")
        }
        return syncRequestManager.getSyncStatus(syncTypes)
    }

    fun execute(syncType: String): Flow<SyncStatus> {
        if (!syncRegister.isRegistered(syncType)) {
            throw Exception("Please provide valid syncType to execute")
        }
        return syncImmediateExecutor.execute(syncType)
    }

    fun execute(syncTypes: List<String>): Flow<HashMap<String, SyncStatus>> {
        syncTypes.forEach { syncType ->
            if (!syncRegister.isRegistered(syncType)) {
                throw Exception("Please provide valid syncTypes to execute")
            }
        }
        return syncImmediateExecutor.execute(syncTypes)
    }

    fun getGraphStatus(): Flow<GraphStatus> {
        return syncGraphManager.graphStatus
    }
}

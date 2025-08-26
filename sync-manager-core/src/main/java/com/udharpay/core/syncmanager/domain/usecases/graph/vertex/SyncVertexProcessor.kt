package com.udharpay.core.syncmanager.domain.usecases.graph.vertex

import com.udharpay.kernel.kernelcommon.eventbus.EventBus
import com.udharpay.kernel.kernelcommon.register.Register
import com.udharpay.core.syncmanager.domain.enities.SyncEvent
import com.udharpay.core.syncmanager.domain.enities.SyncStatus
import com.udharpay.core.syncmanager.domain.enities.SyncVertex
import com.udharpay.core.syncmanager.domain.enities.graph.vertex.Vertex
import com.udharpay.core.syncmanager.domain.enities.graph.vertex.VertexProcessor
import com.udharpay.core.syncmanager.domain.repositories.Sync
import com.udharpay.kernel.kernelcommon.taskexecutor.TaskExecutor

class SyncVertexProcessor(
    private val syncRegister: Register<String, Sync>,
    private val syncEventBus: EventBus<SyncEvent>,
    private val taskExecutor: TaskExecutor,
): VertexProcessor<SyncVertex> {

    override fun process(vertex: Vertex<SyncVertex>) {
        taskExecutor.executeInCurrent {
            try {
                syncEventBus.fire(SyncEvent.VertexAck(vertex, SyncStatus.InProgress))
                val syncer = syncRegister.get(vertex.data.syncType).syncer()
                val result = taskExecutor.execute { syncer.sync() }
                syncEventBus.fire(SyncEvent.VertexAck(vertex, result))
            } catch (e: Exception) {
                syncEventBus.fire(SyncEvent.VertexAck(vertex, SyncStatus.Failed))
            }
        }
    }
}

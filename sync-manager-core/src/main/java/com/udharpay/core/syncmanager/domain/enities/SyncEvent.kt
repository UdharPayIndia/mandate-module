package com.udharpay.core.syncmanager.domain.enities

import com.udharpay.core.syncmanager.domain.enities.graph.vertex.Vertex

sealed class SyncEvent {
    object Initiated: SyncEvent()
    object DeInitiated: SyncEvent()
    data class Enqueue(val syncTypes: List<String>): SyncEvent()
    data class VertexAck(val vertex: Vertex<SyncVertex>, val syncStatus: SyncStatus): SyncEvent()
    data class ConstraintChange(val constraintType: String, val constraintValue: Any): SyncEvent()
}

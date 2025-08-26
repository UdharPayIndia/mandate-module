package com.udharpay.core.syncmanager.domain.usecases.graph

import com.udharpay.core.syncmanager.domain.enities.SyncVertex
import com.udharpay.core.syncmanager.domain.enities.graph.Graph
import com.udharpay.core.syncmanager.domain.enities.graph.GraphProcessor
import com.udharpay.core.syncmanager.domain.enities.graph.vertex.Vertex
import com.udharpay.core.syncmanager.domain.enities.graph.vertex.VertexProcessor

class SyncGraphProcessor(private val vertexProcessor: VertexProcessor<SyncVertex>): GraphProcessor<SyncVertex> {

    override fun process(graph: Graph<SyncVertex>, visited: MutableList<Vertex<SyncVertex>>) {
        graph.getVertexWithNoInDegree().sortedBy { it.data.priority.value }.forEach { vertex ->
            if (visited.contains(vertex)) return@forEach
            visited.add(vertex)
            vertexProcessor.process(vertex)
        }
    }
}

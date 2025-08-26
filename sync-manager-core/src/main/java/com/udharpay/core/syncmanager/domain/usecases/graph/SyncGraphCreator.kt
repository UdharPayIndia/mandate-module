package com.udharpay.core.syncmanager.domain.usecases.graph

import com.udharpay.core.syncmanager.domain.enities.SyncVertex
import com.udharpay.core.syncmanager.domain.enities.graph.DirectedGraph
import com.udharpay.core.syncmanager.domain.enities.graph.Graph
import com.udharpay.core.syncmanager.domain.enities.graph.GraphCreator
import com.udharpay.core.syncmanager.domain.enities.graph.vertex.Vertex

class SyncGraphCreator :
    GraphCreator<SyncVertex> {

    override fun create(list: List<SyncVertex>): Graph<SyncVertex> {
        val directedGraph = DirectedGraph<SyncVertex>()

        if (list.isEmpty()) return directedGraph

        val vertices = HashMap<String, Vertex<SyncVertex>>()

        list.forEach {
            vertices[it.syncType] = directedGraph.createVertex(it)
        }

        list.forEach {
            vertices[it.syncType]?.let { sourceVertex ->
                it.dependencies.forEach { dependant ->
                    vertices[dependant]?.let { destinationVertex ->
                        directedGraph.addEdge(sourceVertex, destinationVertex, destinationVertex.data.priority.value.toDouble())
                    }
                }
            }
        }

        return directedGraph
    }
}

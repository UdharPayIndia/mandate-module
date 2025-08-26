package com.udharpay.core.syncmanager.domain.usecases

import com.udharpay.core.syncmanager.domain.enities.SyncVertex
import com.udharpay.core.syncmanager.domain.enities.graph.Graph
import com.udharpay.core.syncmanager.domain.enities.graph.GraphCreator
import com.udharpay.core.syncmanager.domain.enities.graph.GraphProcessor
import com.udharpay.core.syncmanager.domain.enities.graph.GraphStatus
import com.udharpay.core.syncmanager.domain.enities.graph.vertex.Vertex
import kotlinx.coroutines.flow.MutableStateFlow

class SyncGraphManager(
    private val graphCreator: GraphCreator<SyncVertex>,
    private val graphProcessor: GraphProcessor<SyncVertex>,
    private val constraintHandler: ConstraintHandler
) {
    var graphStatus: MutableStateFlow<GraphStatus> = MutableStateFlow(GraphStatus.Idle)

    private lateinit var graph: Graph<SyncVertex>
    private lateinit var visited: MutableList<Vertex<SyncVertex>>

    lateinit var graphExecutedListener: () -> Unit

    fun registerOnGraphExecutedListener(graphExecutedListener: () -> Unit) {
        this.graphExecutedListener = graphExecutedListener
    }

    fun init(list: List<SyncVertex>) {
        graphStatus.value = GraphStatus.Creation
        graph = graphCreator.create(list)
        visited = mutableListOf()

        handleConstraint()

        if (graph.isNotEmpty()) {
            graphProcessor.process(graph, visited)
        } else {
            reset()
        }
    }

    private fun handleConstraint() {
        graph.vertices().forEach { vertex ->
            if (!constraintHandler.isConstraintMet(vertex.data.constraint)) {
                removeVertices(graph.getAllPathVertex(vertex))
            }
        }
    }

    fun onVertexExecutionInProgress() {
        graphStatus.value = GraphStatus.Execution
    }

    fun onVertexExecutionSuccess(vertex: Vertex<SyncVertex>) {
        graph.removeVertex(vertex)
        if (graph.isNotEmpty()) {
            graphStatus.value = GraphStatus.Execution
            graphProcessor.process(graph, visited)
        } else {
            graphStatus.value = GraphStatus.Executed
            graphExecutedListener()
        }
    }

    fun onVertexExecutionFailed() {
        if (graph.isNotEmpty()) {
            graphStatus.value = GraphStatus.Execution
            graphProcessor.process(graph, visited)
        } else {
            graphStatus.value = GraphStatus.Executed
            graphExecutedListener()
        }
    }

    fun handleVertexFailure(vertex: Vertex<SyncVertex>): Vertex<SyncVertex> {
        graph.removeVertex(vertex)
        return vertex
    }

    fun handleVerticesFailure(vertex: Vertex<SyncVertex>): List<Vertex<SyncVertex>> {
        val vertices = graph.getAllPathVertex(vertex)
        removeVertices(vertices)
        return vertices
    }

    private fun removeVertices(vertices: List<Vertex<SyncVertex>>) {
        vertices.forEach {
            graph.removeVertex(it)
        }
    }

    fun reset() {
        graphStatus.value = GraphStatus.Idle
        visited = mutableListOf()
    }

    fun isIdle(): Boolean {
        return graphStatus.value == GraphStatus.Idle
    }
}

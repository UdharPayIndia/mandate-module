package com.udharpay.core.syncmanager.domain.enities.graph

import com.udharpay.core.syncmanager.domain.enities.graph.vertex.Vertex

interface GraphProcessor<T> {
    fun process(graph: Graph<T>, visited: MutableList<Vertex<T>>)
}

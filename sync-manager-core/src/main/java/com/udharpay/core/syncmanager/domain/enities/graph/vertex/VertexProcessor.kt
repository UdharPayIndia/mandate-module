package com.udharpay.core.syncmanager.domain.enities.graph.vertex

interface VertexProcessor<T> {
    fun process(vertex: Vertex<T>)
}

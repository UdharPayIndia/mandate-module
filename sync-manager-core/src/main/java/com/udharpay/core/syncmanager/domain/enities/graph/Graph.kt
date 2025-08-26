package com.udharpay.core.syncmanager.domain.enities.graph

import com.udharpay.core.syncmanager.domain.enities.graph.vertex.Vertex

interface Graph<T> {

    fun createVertex(data: T): Vertex<T>

    fun addEdge(source: Vertex<T>, destination: Vertex<T>, weight: Double)

    fun weight(source: Vertex<T>, destination: Vertex<T>): Double

    fun getVertexWithNoInDegree(): List<Vertex<T>>

    fun vertices(): List<Vertex<T>>

    fun removeVertex(vertex: Vertex<T>)

    fun isNotEmpty(): Boolean

    fun getAllPathVertex(vertex: Vertex<T>): List<Vertex<T>>
}

package com.udharpay.core.syncmanager.domain.enities.graph.edge

import com.udharpay.core.syncmanager.domain.enities.graph.vertex.Vertex

data class Edge<T>(
        val source: Vertex<T>,
        val destination: Vertex<T>,
        val weight: Double = 0.0
)
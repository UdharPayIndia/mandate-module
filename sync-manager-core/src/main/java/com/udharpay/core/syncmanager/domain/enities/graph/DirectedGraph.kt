package com.udharpay.core.syncmanager.domain.enities.graph

import com.udharpay.core.syncmanager.domain.enities.graph.edge.Edge
import com.udharpay.core.syncmanager.domain.enities.graph.vertex.Vertex
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class DirectedGraph<T> :
  Graph<T> {

  private val adjacency: HashMap<Vertex<T>, ArrayList<Edge<T>>> = HashMap()

  override fun toString(): String {
    return buildString { // 1
      adjacency.forEach { (vertex, edges) -> // 2
        val edgeString = edges.joinToString { it.destination.data.toString() } // 3
        append("${vertex.data} ---> [ $edgeString ]\n") // 4
      }
    }
  }

  override fun createVertex(data: T): Vertex<T> {
    val vertex =
      Vertex(
        adjacency.count(),
        data
      )
    adjacency[vertex] = ArrayList()
    return vertex
  }

  override fun addEdge(source: Vertex<T>, destination: Vertex<T>, weight: Double) {
    val edge = Edge(
      source,
      destination,
      weight
    )
    adjacency[source]?.add(edge)
  }

  override fun weight(source: Vertex<T>, destination: Vertex<T>): Double {
    return edges(source).first { it.destination == destination }.weight
  }

  private fun edges(source: Vertex<T>) = adjacency[source] ?: arrayListOf()

  override fun removeVertex(vertex: Vertex<T>) {
    adjacency.remove(vertex)
    removeEdges(vertex)
  }

  private fun removeEdges(destination: Vertex<T>) {
    adjacency.forEach { (_, edges) ->
      val tempEdges = edges.filter { it.destination == destination }
      if  (tempEdges.isNotEmpty()) {
        edges.remove(tempEdges.first())
      }
    }
  }

  override fun getVertexWithNoInDegree(): List<Vertex<T>> {
    return adjacency.filter { it.value.size == 0 }.map { it.key }
  }

  override fun vertices(): List<Vertex<T>> {
    return adjacency.map { it.key }
  }

  override fun getAllPathVertex(vertex: Vertex<T>): List<Vertex<T>> {
    val destinations: Queue<Vertex<T>> = LinkedList()
    val queue: Queue<Vertex<T>> = LinkedList()
    queue.add(vertex)
    while (queue.isNotEmpty()) {
      val destination = queue.poll()
      destinations.add(destination)
      adjacency.entries.forEach { (_, edges) ->
        val edge = edges.filter { it.destination == destination }
        if (edge.isNotEmpty()) {
          queue.add(edge.first().source)
        }
      }
    }
    return destinations.toList()
  }

  private fun isEmpty(): Boolean {
    return adjacency.size == 0
  }

  override fun isNotEmpty(): Boolean {
    return !isEmpty()
  }
}

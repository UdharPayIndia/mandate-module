package com.udharpay.core.syncmanager.domain.enities.graph

interface GraphCreator<T> {
    fun create(list: List<T>): Graph<T>
}

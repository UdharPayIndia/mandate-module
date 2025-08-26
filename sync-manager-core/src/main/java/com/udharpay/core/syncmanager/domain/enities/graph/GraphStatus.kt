package com.udharpay.core.syncmanager.domain.enities.graph

sealed class GraphStatus(val value: String) {
    object Idle: GraphStatus("IDLE")
    object Creation: GraphStatus("CREATION")
    object Execution: GraphStatus("EXECUTION")
    object Executed: GraphStatus("EXECUTED")
}

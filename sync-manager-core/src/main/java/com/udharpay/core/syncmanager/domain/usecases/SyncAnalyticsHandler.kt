package com.udharpay.core.syncmanager.domain.usecases

import com.udharpay.kernel.kernelcommon.analytics.AnalyticsHandler
import com.udharpay.core.syncmanager.domain.enities.SyncEvent
import com.udharpay.core.syncmanager.domain.enities.SyncStatus

class SyncAnalyticsHandler(private val analyticsHandler: AnalyticsHandler?) {

    companion object {
        private const val EVENT_SYNC_INITIATED = "SyncInitiated"
        private const val EVENT_SYNC_DE_INITIATED = "SyncDeInitiated"

        private const val EVENT_SYNC_ENQUEUED = "SyncEnqueued"
        private const val EVENT_SYNC_IN_PROGRESS = "SyncInProgress"
        private const val EVENT_SYNC_FAILED = "SyncFailed"
        private const val EVENT_SYNC_SUCCESS = "SyncSuccess"

        private const val EVENT_SYNC_CONSTRAINT_CHANGE = "SyncConstraintChange"

        private const val PARAM_SYNC_TYPE = "syncType"
    }

    fun sendEvent(event: SyncEvent) {
        when(event) {
            SyncEvent.Initiated -> {
                analyticsHandler?.sendEvent(EVENT_SYNC_INITIATED)
            }
            is SyncEvent.Enqueue -> {
                event.syncTypes.forEach {
                    val param = hashMapOf<String, Any>()
                    param[PARAM_SYNC_TYPE] = it
                    analyticsHandler?.sendEvent(EVENT_SYNC_ENQUEUED, param)
                }
            }
            is SyncEvent.VertexAck -> {
                val param = hashMapOf<String, Any>()
                val eventName = when (event.syncStatus) {
                    SyncStatus.Enqueue -> {
                        EVENT_SYNC_ENQUEUED
                    }
                    SyncStatus.InProgress -> {
                        EVENT_SYNC_IN_PROGRESS
                    }
                    SyncStatus.Success -> {
                        EVENT_SYNC_SUCCESS
                    }
                    SyncStatus.Failed -> {
                        EVENT_SYNC_FAILED
                    }
                }
                param[PARAM_SYNC_TYPE] = event.vertex.data.syncType
                analyticsHandler?.sendEvent(eventName, param)
            }
            SyncEvent.DeInitiated -> {
                analyticsHandler?.sendEvent(EVENT_SYNC_DE_INITIATED)
            }
            is SyncEvent.ConstraintChange -> {
                val param = hashMapOf<String, Any>()
                param[event.constraintType] = event.constraintValue
                analyticsHandler?.sendEvent(EVENT_SYNC_CONSTRAINT_CHANGE, param)
            }
        }
    }
}

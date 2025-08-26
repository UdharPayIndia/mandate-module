package com.rocketpay.mandate.feature.mandate.domain.entities

internal sealed class MandateState(val value: String) {
    object Pending : MandateState("pending")
    object UserAccepted : MandateState("accepted")
    object Active : MandateState("active")
    object Completed : MandateState("completed")
    object Paused : MandateState("paused")
    object Cancelled : MandateState("cancelled")
    object PartiallyCollected : MandateState("partially_collected")
    object Expired : MandateState("expired")
    object Terminated : MandateState("terminated")


    companion object {
        val map by lazy {
            mapOf(
                "pending" to Pending,
                "accepted" to UserAccepted,
                "active" to Active,
                "completed" to Completed,
                "paused" to Paused,
                "terminated" to Terminated,
                "cancelled" to Cancelled,
                "expired" to Expired,
                "partially_collected" to PartiallyCollected
            )
        }

        fun get(value: String): MandateState {
            return map[value] ?: Pending
        }
    }
}

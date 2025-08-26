package com.rocketpay.mandate.feature.permission.feature.domain.entities

internal sealed class ProminentDisclosureStatus(val value: String) {
    object Accepted: ProminentDisclosureStatus("ACCEPTED")
    object Denied: ProminentDisclosureStatus("DENIED")

    companion object {
        val map by lazy {
            mapOf(
                "ACCEPTED" to Accepted,
                "DENIED" to Denied
            )
        }

        fun get(prominentDisclosureStatus: String): ProminentDisclosureStatus {
            return map[prominentDisclosureStatus] ?: Denied
        }
    }
}

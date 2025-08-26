package com.rocketpay.mandate.feature.mandate.presentation.ui.mandatelist.statemachine

internal sealed class MandateFilter(val value: String) {
    object AllMandates : MandateFilter("all_mandates")
    object Pending : MandateFilter("pending")
    object Active : MandateFilter("active")
    object Completed : MandateFilter("completed")
    object Paused : MandateFilter("paused")
    object Others : MandateFilter("others")

    companion object {

        fun get(filterType: String): MandateFilter {
            return map[filterType] ?: AllMandates
        }

        val map by lazy {
            mapOf(
                "all_mandates" to AllMandates,
                "pending" to Pending,
                "active" to Active,
                "completed" to Completed,
                "paused" to Paused,
                "others" to Others,
            )
        }
    }
}

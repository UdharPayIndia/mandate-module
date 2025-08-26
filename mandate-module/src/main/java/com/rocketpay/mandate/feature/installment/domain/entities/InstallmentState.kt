package com.rocketpay.mandate.feature.installment.domain.entities

internal sealed class InstallmentState(val value: String) {
    // global state
    object Created : InstallmentState("created")
    object Expired : InstallmentState("expired")

    // collection state
    object CollectionInitiated : InstallmentState("collection_initiated")
    object CollectionSuccess : InstallmentState("collection_success")
    object CollectionFailed : InstallmentState("collection_failed")

    // settlement state
    object SettlementInitiated : InstallmentState("settlement_initiated")
    object SettlementSuccess : InstallmentState("settlement_success")
    object SettlementFailed : InstallmentState("settlement_failed")

    // settlement state
    object RefundInitiated : InstallmentState("collection_refund_initiated")
    object RefundSuccess : InstallmentState("collection_refund_success")
    object RefundFailed : InstallmentState("collection_refund_failed")
    object Skipped: InstallmentState("skipped")
    object Scheduled: InstallmentState("scheduled")
    object Terminated: InstallmentState("terminated")

    companion object {
        val map by lazy {
            mapOf(
                "created" to Created,
                "expired" to Expired,
                "collection_initiated" to CollectionInitiated,
                "collection_success" to CollectionSuccess,
                "collection_failed" to CollectionFailed,
                "settlement_initiated" to SettlementInitiated,
                "settlement_success" to SettlementSuccess,
                "settlement_failed" to SettlementFailed,
                "collection_refund_initiated" to RefundInitiated,
                "collection_refund_success" to RefundSuccess,
                "collection_refund_failed" to RefundFailed,
                "skipped" to Skipped,
                "scheduled" to Scheduled,
                "terminated" to Terminated
            )
        }

        fun get(state: String?): InstallmentState {
            return map[state] ?: Created
        }
    }
}

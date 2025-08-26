package com.rocketpay.mandate.feature.product.domain.entities

import kotlin.collections.get

internal sealed class ProductOrderStateEnum(val value: String) {
    object Success: ProductOrderStateEnum("SUCCESS")
    object InProgress: ProductOrderStateEnum("IN_PROGRESS")
    object Failed: ProductOrderStateEnum("FAILED")
    object Created: ProductOrderStateEnum("CREATED")
    object SettlementInitiated: ProductOrderStateEnum("SETTLEMENT_INITIATED")
    object SettlementSuccess: ProductOrderStateEnum("SETTLEMENT_SUCCESS")
    object SettlementFailed: ProductOrderStateEnum("SETTLEMENT_FAILED")

    companion object {
        val map by lazy {
            mapOf(
                "SUCCESS" to Success,
                "IN_PROGRESS" to InProgress,
                "FAILED" to Failed,
                "CREATED" to Created,
                "SETTLEMENT_INITIATED" to SettlementInitiated,
                "SETTLEMENT_SUCCESS" to SettlementSuccess,
                "SETTLEMENT_FAILED" to SettlementFailed
            )
        }

        fun get(state: String?): ProductOrderStateEnum {
            return map[state] ?: Created
        }
    }
}
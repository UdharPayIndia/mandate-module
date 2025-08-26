package com.rocketpay.mandate.feature.product.domain.entities

internal sealed class ProductTypeEnum(val value: String) {
    data object Mandate: ProductTypeEnum("MANDATE")
    data object Installment: ProductTypeEnum("INSTALLMENT")
    data object Key: ProductTypeEnum("KEY")
    data object Settlement: ProductTypeEnum("SETTLEMENT")
    object CreditScore : ProductTypeEnum("CS")

    companion object {
        val map by lazy {
            mapOf(
                "KEY" to Key,
                "MANDATE" to Mandate,
                "INSTALLMENT" to Installment,
                "SETTLEMENT" to Settlement,
                "CS" to CreditScore
            )
        }

        fun get(value: String): ProductTypeEnum? {
            return map[value]
        }

        fun getMandateProductTypeEnum(monetizationVariant: String?): ProductTypeEnum {
            return Installment
        }
    }
}
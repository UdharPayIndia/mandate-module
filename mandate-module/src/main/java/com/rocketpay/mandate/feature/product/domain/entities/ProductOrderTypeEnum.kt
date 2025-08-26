package com.rocketpay.mandate.feature.product.domain.entities

internal sealed class ProductOrderTypeEnum {
    data object Redeem: ProductOrderStateEnum("REDEEM")
    data object Purchase: ProductOrderStateEnum("PURCHASE")
    data object PurchaseRefund: ProductOrderStateEnum("PURCHASE_REFUND")
    data object Sale: ProductOrderStateEnum("SALE")
    data object SaleRefund: ProductOrderStateEnum("SALE_REFUND")
    data object Cashback: ProductOrderStateEnum("CASHBACK")
    data object CashBackRefund: ProductOrderStateEnum("CASHBACK_REFUND")
}
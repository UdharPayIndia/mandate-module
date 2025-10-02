package com.rocketpay.mandate.feature.product.domain.entities

internal sealed class ProductOrderTypeEnum(val value: String) {
    data object Redeem: ProductOrderTypeEnum("REDEEM")
    data object RedeemRefund: ProductOrderTypeEnum("REDEEM_REFUND")
    data object Purchase: ProductOrderTypeEnum("PURCHASE")
    data object PurchaseRefund: ProductOrderTypeEnum("PURCHASE_REFUND")
    data object Sale: ProductOrderTypeEnum("SALE")
    data object SaleRefund: ProductOrderTypeEnum("SALE_REFUND")
    data object Cashback: ProductOrderTypeEnum("CASHBACK")
    data object CashBackRefund: ProductOrderTypeEnum("CASHBACK_REFUND")
}
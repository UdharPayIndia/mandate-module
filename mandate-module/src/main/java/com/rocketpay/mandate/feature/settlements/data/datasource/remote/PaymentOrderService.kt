package com.rocketpay.mandate.feature.settlements.data.datasource.remote

import com.rocketpay.mandate.feature.settlements.data.entities.PaymentOrderDto
import com.rocketpay.mandate.feature.settlements.data.entities.PaymentOrderReferenceDto
import com.rocketpay.mandate.feature.settlements.data.entities.SettlementBannerDto
import com.udharpay.core.networkmanager.data.NetworkRequestBuilder
import com.udharpay.core.networkmanager.data.get
import com.udharpay.core.networkmanager.domain.entities.Outcome

internal class PaymentOrderService {

    suspend fun fetchPaymentOrders(serverSequence: Long): Outcome<List<PaymentOrderDto>> {
        return NetworkRequestBuilder()
            .subUrl("/common/payment-orders/v1/sync")
            .queryParameter("server_seq", serverSequence.toString())
            .queryParameter("limit", "100")
            .body(Any())
            .build()
            .get()
    }

    suspend fun fetchSettlementBannerInfo(): Outcome<SettlementBannerDto> {
        return NetworkRequestBuilder()
            .subUrl("/common/party/v1/banner")
            .queryParameter("product_type", "SETTLEMENT")
            .build()
            .get()
    }

    suspend fun fetchPaymentOrderDetails(settlementId: String): Outcome<List<PaymentOrderReferenceDto>> {
        return NetworkRequestBuilder()
            .subUrl("/common/payment-orders/v1/batch-maps")
            .queryParameter("entity_type", "PAYOUT_ORDER")
            .queryParameter("entity_id", settlementId)
            .build()
            .get()
    }
}
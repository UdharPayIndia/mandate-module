package com.rocketpay.mandate.feature.settlements.domain.repositories

import com.rocketpay.mandate.feature.settlements.data.entities.PaymentOrderDto
import com.rocketpay.mandate.feature.settlements.data.entities.PaymentOrderEntity
import com.rocketpay.mandate.feature.settlements.data.entities.PaymentOrderReferenceDto
import com.rocketpay.mandate.feature.settlements.data.entities.SettlementBannerDto
import com.rocketpay.mandate.feature.settlements.domain.entities.PaymentOrder
import com.udharpay.core.networkmanager.domain.entities.Outcome
import kotlinx.coroutines.flow.Flow

internal interface PaymentOrderRepository {
    suspend fun fetchPaymentOrders(serverSequence: Long): Outcome<List<PaymentOrderDto>>
    suspend fun fetchSettlementBannerInfo(): Outcome<SettlementBannerDto>
    suspend fun fetchPaymentOrderDetails(paymentOrderId: String): Outcome<List<PaymentOrderReferenceDto>>
    suspend fun getPaymentOrdersCorrespondsToRefund(paymentOrderType: String, paymentOrderIds: List<String>): List<String>
    suspend fun lastServerSequence(): Long
    fun savePaymentOrders(
        data: List<PaymentOrderDto>,
        onInsert: (PaymentOrderEntity) -> Unit,
        onUpdate: (PaymentOrderEntity, PaymentOrderEntity) -> Unit
    )
    suspend fun getPaginatedPaymentOrders(
        lastFetched: Long,
        orderByDesc: Boolean,  limit: Int
    ): List<PaymentOrder>
    suspend fun getPaymentOrderByIdLive(
        paymentOrderId: String
    ): Flow<PaymentOrder?>
    suspend fun getPaymentOrderById(
        paymentOrderId: String
    ): PaymentOrder?
    suspend fun getSettlementByPayInOrderId(
        payInOrderId: String
    ): String?
}
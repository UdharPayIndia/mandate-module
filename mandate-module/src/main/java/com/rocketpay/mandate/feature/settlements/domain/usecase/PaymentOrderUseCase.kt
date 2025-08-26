package com.rocketpay.mandate.feature.settlements.domain.usecase

import com.rocketpay.mandate.feature.product.presentation.ui.utils.ProductUtils
import com.rocketpay.mandate.feature.settlements.data.entities.PaymentOrderReferenceDto
import com.rocketpay.mandate.feature.settlements.data.entities.SettlementBannerDto
import com.rocketpay.mandate.feature.settlements.domain.entities.PaymentOrder
import com.rocketpay.mandate.feature.settlements.domain.repositories.PaymentOrderRepository
import com.rocketpay.mandate.feature.settlements.presentation.ui.utils.SettlementUtils
import com.rocketpay.mandate.feature.property.data.entities.PropertyType
import com.rocketpay.mandate.feature.property.domain.usecase.PropertyUseCase
import com.udharpay.core.networkmanager.domain.entities.Outcome
import kotlinx.coroutines.flow.Flow

internal class PaymentOrderUseCase internal constructor(
    private val paymentOrderRepository: PaymentOrderRepository,
) {
    internal suspend fun getPaginatedSettlements(
        lastFetched: Long,
        orderByDesc: Boolean,  limit: Int
    ): List<PaymentOrder>{
        return paymentOrderRepository.getPaginatedPaymentOrders(lastFetched, orderByDesc, limit)
    }

    suspend fun fetchPaymentOrderDetails(paymentOrderId: String): Outcome<List<PaymentOrderReferenceDto>>{
        return paymentOrderRepository.fetchPaymentOrderDetails(paymentOrderId)
    }

    suspend fun fetchSettlementBannerInfo(propertyUseCase: PropertyUseCase): Outcome<SettlementBannerDto> {
        val outcome = paymentOrderRepository.fetchSettlementBannerInfo()
        if(outcome is Outcome.Success){
            val bannerMessage = if(outcome.data.showBanner){
                outcome.data.message
            }else{
                ""
            }
            propertyUseCase.setProperty(
                ProductUtils.SETTLEMENT_BANNER_MESSAGE,
                bannerMessage,
                PropertyType.Miscellaneous
            )
        }
        return outcome
    }

    internal suspend fun getPaymentOrderByIdLive(
        paymentOrderId: String
    ): Flow<PaymentOrder?>{
        return paymentOrderRepository.getPaymentOrderByIdLive(paymentOrderId)
    }

    suspend fun getSettlementByPayInOrderId(
        payInOrderId: String
    ): String?{
        return paymentOrderRepository.getSettlementByPayInOrderId(payInOrderId)
    }

    internal suspend fun getPaymentOrderById(
        paymentOrderId: String
    ): PaymentOrder?{
        return paymentOrderRepository.getPaymentOrderById(paymentOrderId)
    }

    suspend fun getPaymentOrdersCorrespondsToRefund(paymentOrderType: String, paymentOrderIds: List<String>): List<String>{
        return paymentOrderRepository.getPaymentOrdersCorrespondsToRefund(paymentOrderType, paymentOrderIds)
    }
}
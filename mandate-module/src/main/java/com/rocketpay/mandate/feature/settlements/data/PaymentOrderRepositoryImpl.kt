package com.rocketpay.mandate.feature.settlements.data

import androidx.sqlite.db.SimpleSQLiteQuery
import com.rocketpay.mandate.feature.settlements.data.datasource.local.PaymentOrderDao
import com.rocketpay.mandate.feature.settlements.data.datasource.local.SettlementDataStore
import com.rocketpay.mandate.feature.settlements.data.datasource.remote.PaymentOrderService
import com.rocketpay.mandate.feature.settlements.data.entities.PaymentOrderDto
import com.rocketpay.mandate.feature.settlements.data.entities.PaymentOrderEntity
import com.rocketpay.mandate.feature.settlements.data.entities.PaymentOrderReferenceDto
import com.rocketpay.mandate.feature.settlements.data.entities.SettlementBannerDto
import com.rocketpay.mandate.feature.settlements.data.mapper.PaymentOrderDtoToEntityMapper
import com.rocketpay.mandate.feature.settlements.data.mapper.PaymentOrderEntToDomMapper
import com.rocketpay.mandate.feature.settlements.domain.entities.PaymentOrder
import com.rocketpay.mandate.feature.settlements.domain.entities.PaymentOrderStateEnum
import com.rocketpay.mandate.feature.settlements.domain.entities.PaymentOrderType
import com.rocketpay.mandate.feature.settlements.domain.repositories.PaymentOrderRepository
import com.udharpay.core.networkmanager.domain.entities.Outcome
import com.rocketpay.mandate.common.jsonconverter.JsonConverter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

internal class PaymentOrderRepositoryImpl(
    private val paymentOrderService: PaymentOrderService,
    private val paymentOrderDao: PaymentOrderDao,
    private val settlementDataStore: SettlementDataStore,
    private val paymentOrderDtoToEntityMapper: PaymentOrderDtoToEntityMapper,
    private val paymentOrderEntToDomMapper: PaymentOrderEntToDomMapper
): PaymentOrderRepository {

    override suspend fun fetchPaymentOrders(serverSequence: Long): Outcome<List<PaymentOrderDto>> {
        return paymentOrderService.fetchPaymentOrders(serverSequence)
    }

    override suspend fun fetchSettlementBannerInfo(): Outcome<SettlementBannerDto>{
        return paymentOrderService.fetchSettlementBannerInfo()
    }

    override suspend fun fetchPaymentOrderDetails(paymentOrderId: String): Outcome<List<PaymentOrderReferenceDto>>{
        val outcome = paymentOrderService.fetchPaymentOrderDetails(paymentOrderId)
        if(outcome is Outcome.Success){
            val referenceString = JsonConverter.getInstance().toJson(outcome.data) ?: ""
            paymentOrderDao.updatePaymentOrderReference(paymentOrderId, referenceString)
        }
        return outcome
    }

    override suspend fun getPaymentOrdersCorrespondsToRefund(paymentOrderType: String, paymentOrderIds: List<String>): List<String>{
        return paymentOrderDao.getPaymentOrdersCorrespondsToRefund(paymentOrderType, paymentOrderIds)
    }
    override suspend fun lastServerSequence(): Long {
        return paymentOrderDao.lastServerSequence()
    }

    override fun savePaymentOrders(
        data: List<PaymentOrderDto>,
        onInsert: (PaymentOrderEntity) -> Unit,
        onUpdate: (PaymentOrderEntity, PaymentOrderEntity) -> Unit
    ) {
        val entities = paymentOrderDtoToEntityMapper.mapList(data)
        paymentOrderDao.upsert(entities, onInsert, onUpdate)
    }

    override suspend fun getPaginatedPaymentOrders(
        lastFetched: Long,
        orderByDesc: Boolean,  limit: Int
    ): List<PaymentOrder> {
        val query = getRawQueryForPaymentOrder(
            PaymentOrderType.Settle.value,
            PaymentOrderStateEnum.Success.value,
            lastFetched, orderByDesc, limit)
        return paymentOrderDao.getPaginatedPaymentOrders(query)
            .map { paymentOrderEntToDomMapper.map(it)
            }
    }

    override suspend fun getPaymentOrderByIdLive(
        paymentOrderId: String
    ): Flow<PaymentOrder?> {
        return paymentOrderDao.getOne(paymentOrderId).transform {
            if(it != null){
                emit(paymentOrderEntToDomMapper.map(it))
            }else{
                emit(null)
            }
        }
    }

    override suspend fun getPaymentOrderById(
        paymentOrderId: String
    ): PaymentOrder? {
        val it = paymentOrderDao.getOneNonLive(paymentOrderId)
        return if(it != null){
            paymentOrderEntToDomMapper.map(it)
        }else{
            null
        }
    }

    override suspend fun getSettlementByPayInOrderId(
        payInOrderId: String
    ): String?{
        val settlements = paymentOrderDao.getAllByStateNonLive(PaymentOrderStateEnum.Success.value).map {
            paymentOrderEntToDomMapper.map(it)
        }
        settlements.forEach {
            val isExists = it.references.find { it.payInOrderId == payInOrderId }
            if(isExists != null){
                return it.id
            }
        }
        return null
    }

    private fun getRawQueryForPaymentOrder(
        type: String,
        state: String,
        lastFetched: Long,
        orderByDesc: Boolean,
        limit: Int,
    ): SimpleSQLiteQuery {
        val select = " SELECT * FROM paymentOrder WHERE is_deleted = 0 AND type = '$type' AND state = '$state' "
        val typeBaseCondition = if(orderByDesc){
                    " AND created_at < $lastFetched "
                }else{
                    " AND created_at > $lastFetched "
                }
        val orderByString = if(orderByDesc){
            " ORDER by created_at DESC LIMIT $limit "
        }else{
            " ORDER by created_at LIMIT $limit "
        }
        return SimpleSQLiteQuery(select + typeBaseCondition + orderByString)
    }
}
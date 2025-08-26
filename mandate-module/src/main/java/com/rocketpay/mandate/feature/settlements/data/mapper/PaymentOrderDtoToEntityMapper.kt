package com.rocketpay.mandate.feature.settlements.data.mapper

import com.rocketpay.mandate.feature.settlements.data.entities.PaymentOrderDto
import com.rocketpay.mandate.feature.settlements.data.entities.PaymentOrderEntity
import com.rocketpay.mandate.common.jsonconverter.JsonConverter
import com.udharpay.kernel.kernelcommon.mapper.ListMapper

internal class PaymentOrderDtoToEntityMapper: ListMapper<PaymentOrderDto, PaymentOrderEntity> {
    override fun map(source: PaymentOrderDto): PaymentOrderEntity {
        return PaymentOrderEntity(
            id = source.id,
            isDeleted = source.isDeleted,
            createdAt = source.createdAt,
            updatedAt = source.updatedAt,
            type = source.type,
            state = source.state,
            serverSequence = source.serverSequence,
            referenceId = source.referenceId,
            payeesString = JsonConverter.getInstance().toJson(source.payees) ?: "",
            metaString = JsonConverter.getInstance().toJson(source.meta) ?: ""
        )
    }
}
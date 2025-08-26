package com.rocketpay.mandate.feature.settlements.data.mapper

import com.google.gson.reflect.TypeToken
import com.rocketpay.mandate.feature.settlements.data.entities.PaymentOrderEntity
import com.rocketpay.mandate.feature.settlements.data.entities.PaymentOrderMetaDto
import com.rocketpay.mandate.feature.settlements.data.entities.PaymentOrderReferenceDto
import com.rocketpay.mandate.feature.settlements.data.entities.PaymentOrderSummaryDto
import com.rocketpay.mandate.feature.settlements.domain.entities.PaymentOrder
import com.rocketpay.mandate.common.jsonconverter.JsonConverter
import com.udharpay.kernel.kernelcommon.mapper.ListMapper

internal class PaymentOrderEntToDomMapper: ListMapper<PaymentOrderEntity, PaymentOrder> {
    override fun map(source: PaymentOrderEntity): PaymentOrder {
        return PaymentOrder(
            id = source.id,
            isDeleted = source.isDeleted,
            createdAt = source.createdAt,
            updatedAt = source.updatedAt,
            type = source.type,
            state = source.state,
            serverSequence = source.serverSequence,
            referenceId = source.referenceId,
            payees = getSummaryDto(source.payeesString),
            meta = getMetaDto(source.metaString),
            references = getReferenceDto(source.referenceString)
        )
    }

    private fun getSummaryDto(source: String): List<PaymentOrderSummaryDto> {
        return if (source.isNullOrEmpty()) {
            emptyList()
        } else {
            val type = object : TypeToken<List<PaymentOrderSummaryDto>>(){}.type
            val config = JsonConverter.getInstance().fromJson<List<PaymentOrderSummaryDto>>(source, type)
            if (config == null) {
                emptyList()
            } else {
                config
            }
        }
    }

    private fun getMetaDto(source: String): PaymentOrderMetaDto? {
        return if (source.isNullOrEmpty()) {
            null
        } else {
            val config = JsonConverter.getInstance().fromJson(source, PaymentOrderMetaDto::class.java)
            if (config == null) {
                null
            } else {
                config
            }
        }
    }

    private fun getReferenceDto(source: String): List<PaymentOrderReferenceDto> {
        return if (source.isNullOrEmpty()) {
            emptyList()
        } else {
            val type = object : TypeToken<List<PaymentOrderReferenceDto>>(){}.type
            val config = JsonConverter.getInstance().fromJson<List<PaymentOrderReferenceDto>>(source, type)
            if (config == null) {
                emptyList()
            } else {
                config
            }
        }
    }
}
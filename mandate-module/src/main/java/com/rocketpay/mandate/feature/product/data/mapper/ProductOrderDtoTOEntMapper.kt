package com.rocketpay.mandate.feature.product.data.mapper

import com.rocketpay.mandate.common.basemodule.common.presentation.ext.long
import com.rocketpay.mandate.common.jsonconverter.JsonConverter
import com.rocketpay.mandate.feature.product.data.entities.ProductOrderDto
import com.rocketpay.mandate.feature.product.data.entities.ProductOrderEntity
import com.udharpay.kernel.kernelcommon.mapper.ListMapper

internal class ProductOrderDtoTOEntMapper : ListMapper<ProductOrderDto, ProductOrderEntity> {
    override fun map(source: ProductOrderDto): ProductOrderEntity {
        return ProductOrderEntity(
            id = source.id,
            isDeleted = source.deleted ?: false,
            createdAt = source.createdAt.long(),
            updatedAt = source.updatedAt.long(),
            serverSequence = source.syncSequence.long(),
            tenantId = source.tenantId.orEmpty(),
            referenceId = source.referenceId.orEmpty(),
            state = source.state.orEmpty(),
            benefitString = JsonConverter.getInstance().toJson(source.benefit) ?: "",
            priceString = JsonConverter.getInstance().toJson(source.price) ?: "",
            paymentOrderId = source.paymentOrderId.orEmpty(),
            orderType = source.orderType.orEmpty(),
            productType = source.productType.orEmpty(),
            metaString = JsonConverter.getInstance().toJson(source.meta) ?: ""
        )
    }
}
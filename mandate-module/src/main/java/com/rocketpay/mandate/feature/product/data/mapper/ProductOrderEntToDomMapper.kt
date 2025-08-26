package com.rocketpay.mandate.feature.product.data.mapper

import com.rocketpay.mandate.common.basemodule.common.presentation.ext.double
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.long
import com.rocketpay.mandate.common.jsonconverter.JsonConverter
import com.rocketpay.mandate.feature.product.data.entities.ProductOrderEntity
import com.rocketpay.mandate.feature.product.domain.entities.ProductMeta
import com.rocketpay.mandate.feature.product.domain.entities.ProductOrder
import com.rocketpay.mandate.feature.product.data.entities.ProductMetaDto
import com.rocketpay.mandate.feature.product.data.entities.UnitDto
import com.udharpay.kernel.kernelcommon.mapper.ListMapper
import com.rocketpay.mandate.feature.product.domain.entities.Unit

internal class ProductOrderEntToDomMapper : ListMapper<ProductOrderEntity, ProductOrder> {
    override fun map(source: ProductOrderEntity): ProductOrder {
        return ProductOrder(
            id = source.id,
            deleted = source.isDeleted ?: false,
            createdAt = source.createdAt.long(),
            updatedAt = source.updatedAt.long(),
            serverSequence = source.serverSequence.long(),
            tenantId = source.tenantId.orEmpty(),
            referenceId = source.referenceId.orEmpty(),
            state = source.state.orEmpty(),
            benefit = getUnitDto(source.benefitString),
            price = getUnitDto(source.priceString),
            paymentOrderId = source.paymentOrderId.orEmpty(),
            orderType = source.orderType.orEmpty(),
            productType = source.productType.orEmpty(),
            meta = getMetaDto(source.metaString)
        )
    }

    companion object {
        fun getUnitDto(source: String): Unit? {
            return if (source.isNullOrEmpty()) {
                null
            } else {
                val config =
                    JsonConverter.getInstance().fromJson<UnitDto>(source, UnitDto::class.java)
                if (config == null) {
                    null
                } else {
                    Unit(
                        mrp = config.mrp.double(),
                        sp = config.sp.double(),
                        currency = config.currency,
                        unit = config.unit
                    )
                }
            }
        }
    }

    private fun getMetaDto(source: String): ProductMeta? {
        return if (source.isNullOrEmpty()) {
            null
        } else {
            val config = JsonConverter.getInstance().fromJson<ProductMetaDto>(source, ProductMetaDto::class.java)
            if (config == null) {
                null
            } else {
                ProductMeta(
                    paymentUrl = config.paymentUrl.orEmpty(),
                    gatewayReferenceId = config.gatewayReferenceId.orEmpty(),
                    utr = ""
                )
            }
        }
    }
}
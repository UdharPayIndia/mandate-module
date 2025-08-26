package com.rocketpay.mandate.feature.product.data.mapper

import com.rocketpay.mandate.common.basemodule.common.presentation.ext.long
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.AmountUtils
import com.rocketpay.mandate.feature.product.data.entities.ProductPlanDto
import com.rocketpay.mandate.feature.product.domain.entities.ProductPlan
import com.rocketpay.mandate.feature.product.domain.entities.Attribute
import com.udharpay.kernel.kernelcommon.mapper.ListMapper
import com.rocketpay.mandate.feature.product.domain.entities.Unit

internal class ProductPlanDtoToDomMapper : ListMapper<ProductPlanDto, ProductPlan> {
    override fun map(source: ProductPlanDto): ProductPlan {
        return ProductPlan(
            id = source.id,
            deleted = source.deleted ?: false,
            createdAt = source.createdAt.long(),
            updatedAt = source.updatedAt.long(),
            tenantId = source.tenantId.orEmpty(),
            templateId = source.templateId.orEmpty(),
            attribute = Attribute(
                title = source.attribute?.title.orEmpty(),
                subTitle = source.attribute?.subTitle.orEmpty(),
                cashbackBenefit = source.attribute?.cashbackBenefit.orEmpty()
            ),
            price = Unit(
                mrp = AmountUtils.stringToDouble(source.price?.mrp.orEmpty()),
                sp = AmountUtils.stringToDouble(source.price?.sp.orEmpty()),
                currency = source.price?.currency.orEmpty(),
                unit = source.price?.unit.orEmpty()
            ),
            benefit = Unit(
                mrp = AmountUtils.stringToDouble(source.benefit?.mrp.orEmpty()),
                sp = AmountUtils.stringToDouble(source.benefit?.sp.orEmpty()),
                currency = source.benefit?.currency.orEmpty(),
                unit = source.benefit?.unit.orEmpty()
            ),
            sellerId = source.sellerId.orEmpty()
        )
    }

}

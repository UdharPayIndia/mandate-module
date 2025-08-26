package com.rocketpay.mandate.feature.product.data.mapper

import com.rocketpay.mandate.common.basemodule.common.presentation.ext.double
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.long
import com.rocketpay.mandate.feature.product.data.entities.ProductWalletDto
import com.rocketpay.mandate.feature.product.data.entities.ProductWalletEntity
import com.udharpay.kernel.kernelcommon.mapper.ListMapper

internal class ProductWalletDtoToEntMapper : ListMapper<ProductWalletDto, ProductWalletEntity> {
    override fun map(source: ProductWalletDto): ProductWalletEntity {
        return ProductWalletEntity(
            id = source.id,
            createdAt = source.createdAt.long(),
            updatedAt = source.updatedAt.long(),
            payin = source.payin.double(),
            outstanding = source.outstanding.double(),
            underPayout = source.underPayout.double(),
            payout = source.payout.double(),
            currency = source.currency.orEmpty(),
            unit = source.unit.orEmpty(),
            tenantId = source.tenantId.orEmpty(),
            accountId = source.accountId.orEmpty(),
            productType = source.productType.orEmpty()
        )
    }

}

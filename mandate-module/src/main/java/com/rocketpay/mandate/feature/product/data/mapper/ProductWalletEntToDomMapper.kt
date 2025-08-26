package com.rocketpay.mandate.feature.product.data.mapper

import com.rocketpay.mandate.common.basemodule.common.presentation.ext.double
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.long
import com.rocketpay.mandate.feature.product.data.entities.ProductWalletEntity
import com.rocketpay.mandate.feature.product.domain.entities.ProductWallet
import com.udharpay.kernel.kernelcommon.mapper.ListMapper

internal class ProductWalletEntToDomMapper : ListMapper<ProductWalletEntity, ProductWallet> {
    override fun map(source: ProductWalletEntity): ProductWallet {
        return ProductWallet(
            id = source.id,
            createdAt = source.createdAt.long(),
            updatedAt = source.updatedAt.long(),
            payin = source.payin.double(),
            outstanding = source.outstanding.double(),
            underPayout = source.underPayout.double(),
            payout = source.payout.double(),
            currency = source.currency,
            unit = source.unit,
            tenantId = source.tenantId.orEmpty(),
            accountId = source.accountId.orEmpty(),
            productType = source.productType.orEmpty()
        )
    }

}

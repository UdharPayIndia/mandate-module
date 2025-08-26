package com.rocketpay.mandate.feature.product.data

import com.rocketpay.mandate.common.basemodule.common.presentation.ext.double
import com.rocketpay.mandate.feature.property.data.entities.PropertyType
import com.rocketpay.mandate.feature.property.domain.usecase.PropertyUseCase
import com.rocketpay.mandate.feature.product.domain.repositories.ProductRepository
import com.rocketpay.mandate.feature.product.presentation.injection.ProductComponent
import com.rocketpay.mandate.feature.product.presentation.ui.utils.ProductUtils
import com.udharpay.core.networkmanager.domain.entities.Outcome
import com.udharpay.core.syncmanager.domain.enities.DefaultSyncConstraint
import com.udharpay.core.syncmanager.domain.enities.ExistingSyncPolicy
import com.udharpay.core.syncmanager.domain.enities.SyncFailurePolicy
import com.udharpay.core.syncmanager.domain.enities.SyncPriority
import com.udharpay.core.syncmanager.domain.enities.SyncStatus
import com.udharpay.core.syncmanager.domain.repositories.Sync
import com.udharpay.core.syncmanager.domain.repositories.Syncer
import javax.inject.Inject

internal class ProductWalletSync: Sync {
    override fun dependencies() = emptyList<String>()
    override fun priority() = SyncPriority.Medium
    override fun existingSyncPolicy() = ExistingSyncPolicy.Keep
    override fun syncFailurePolicy() = SyncFailurePolicy.Cascade
    override fun syncer() = ProductWalletSyncer()
    override fun constraint() = DefaultSyncConstraint.getLoginAndNetworkConstraint(
        isLoginRequire = true,
        isNetworkRequire = true
    )
}

internal class ProductWalletSyncer: Syncer {

    @Inject
    internal lateinit var productRepository: ProductRepository
    @Inject
    lateinit var propertyUseCase: PropertyUseCase

    companion object {
        const val TYPE = "product_wallet"
        const val EVENT_KEY_BALANCE_UPDATED = "ui_key_balance_update"

    }

    init {
        ProductComponent.Initializer.init().inject(this)
    }

    override suspend fun sync(): SyncStatus {
        return getProductWallet()
    }


    private suspend fun getProductWallet(): SyncStatus {
        return when(val outcome = productRepository.getProductWallet()) {
            is Outcome.Error -> {
                SyncStatus.Failed
            }
            is Outcome.Success -> {
                productRepository.saveProductWallet(outcome.data, {}, { old, new -> })
                var newOutstandingSettlementBalance = 0.0
                outcome.data.forEach {
                    if(it.productType == "SETTLEMENT"){
                        newOutstandingSettlementBalance += it.payin.double() + it.payout.double()
                    }
                }
                updateSettlementBalance(newOutstandingSettlementBalance)
                SyncStatus.Success
            }
        }
    }

    private fun updateSettlementBalance(newOutstandingSettlementBalance: Double){
        val oldOutstandingSettlementBalance = propertyUseCase.getProperty(
            ProductUtils.OUTSTANDING_SETTLEMENT_BALANCE
        )?.value.double()
        if(oldOutstandingSettlementBalance != newOutstandingSettlementBalance){
            propertyUseCase.setProperty(
                ProductUtils.IS_OUTSTANDING_SETTLEMENT_UPDATED,
                true.toString(),
                PropertyType.Miscellaneous
            )
        }
        propertyUseCase.setProperty(
            ProductUtils.OUTSTANDING_SETTLEMENT_BALANCE,
            newOutstandingSettlementBalance.toString(),
            PropertyType.Miscellaneous
        )
    }

}

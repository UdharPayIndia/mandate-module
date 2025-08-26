package com.rocketpay.mandate.feature.product.data

import com.rocketpay.mandate.feature.product.data.entities.ProductOrderEntity
import com.rocketpay.mandate.feature.product.domain.repositories.ProductRepository
import com.rocketpay.mandate.feature.product.presentation.injection.ProductComponent
import com.udharpay.core.networkmanager.domain.entities.Outcome
import com.udharpay.core.syncmanager.domain.enities.DefaultSyncConstraint
import com.udharpay.core.syncmanager.domain.enities.ExistingSyncPolicy
import com.udharpay.core.syncmanager.domain.enities.SyncFailurePolicy
import com.udharpay.core.syncmanager.domain.enities.SyncPriority
import com.udharpay.core.syncmanager.domain.enities.SyncStatus
import com.udharpay.core.syncmanager.domain.repositories.Sync
import com.udharpay.core.syncmanager.domain.repositories.Syncer
import javax.inject.Inject

internal class ProductOrderSync: Sync {
    override fun dependencies() = emptyList<String>()
    override fun priority() = SyncPriority.Medium
    override fun existingSyncPolicy() = ExistingSyncPolicy.Keep
    override fun syncFailurePolicy() = SyncFailurePolicy.Cascade
    override fun syncer() = ProductOrderSyncer()
    override fun constraint() = DefaultSyncConstraint.getLoginAndNetworkConstraint(
        isLoginRequire = true,
        isNetworkRequire = true
    )
}

internal class ProductOrderSyncer: Syncer {

    @Inject
    internal lateinit var productRepository: ProductRepository

    companion object {
        const val TYPE = "productOrder"
    }

    init {
        ProductComponent.Initializer.init().inject(this)
    }

    override suspend fun sync(): SyncStatus {
        return if(getProductOrders()){
            SyncStatus.Success
        }else{
            SyncStatus.Failed
        }
    }


    private suspend fun getProductOrders(): Boolean {
        while (true) {
            val lastServerSequence = productRepository.lastServerSequence()
            when(val outcome = productRepository.syncProductOrder(lastServerSequence)) {
                is Outcome.Error -> {
                    return false
                }
                is Outcome.Success -> {
                    if(outcome.data.isNotEmpty()){
                        productRepository.saveProductOrder(outcome.data,
                            {
                            }, {
                                    old, new ->
                                onProductOrderUpdate(old, new)
                            }
                        )
                    }else{
                        return true
                    }
                }
            }
        }
    }

    private fun onProductOrderUpdate(old: ProductOrderEntity, new: ProductOrderEntity) {
        if (old.state != new.state) {
        }
    }

}

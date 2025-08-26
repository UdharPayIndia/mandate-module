package com.rocketpay.mandate.feature.settlements.data

import com.rocketpay.mandate.feature.settlements.data.entities.PaymentOrderEntity
import com.rocketpay.mandate.feature.settlements.domain.entities.PaymentOrderStateEnum
import com.rocketpay.mandate.feature.settlements.domain.entities.PaymentOrderType
import com.rocketpay.mandate.feature.settlements.domain.repositories.PaymentOrderRepository
import com.rocketpay.mandate.feature.settlements.presentation.injection.SettlementComponent
import com.rocketpay.mandate.main.GlobalState
import com.udharpay.core.networkmanager.domain.entities.Outcome
import com.udharpay.core.syncmanager.domain.enities.DefaultSyncConstraint
import com.udharpay.core.syncmanager.domain.enities.ExistingSyncPolicy
import com.udharpay.core.syncmanager.domain.enities.SyncFailurePolicy
import com.udharpay.core.syncmanager.domain.enities.SyncPriority
import com.udharpay.core.syncmanager.domain.enities.SyncStatus
import com.udharpay.core.syncmanager.domain.repositories.Sync
import com.udharpay.core.syncmanager.domain.repositories.Syncer
import javax.inject.Inject

internal class PaymentOrderSync: Sync {
    override fun dependencies() = emptyList<String>()
    override fun priority() = SyncPriority.Medium
    override fun existingSyncPolicy() = ExistingSyncPolicy.Keep
    override fun syncFailurePolicy() = SyncFailurePolicy.Cascade
    override fun syncer() = PaymentOrderSyncer()
    override fun constraint() = DefaultSyncConstraint.getLoginAndNetworkConstraint(
        isLoginRequire = true,
        isNetworkRequire = true
    )
}

internal class PaymentOrderSyncer: Syncer {

    @Inject
    internal lateinit var paymentOrderRepository: PaymentOrderRepository

    companion object {
        const val TYPE = "payment-order"
    }

    init {
        SettlementComponent.Initializer.init().inject(this)
    }

    override suspend fun sync(): SyncStatus {
        val paymentOrderEntityList = arrayListOf<PaymentOrderEntity>()
        val listOutcome = fetchPaymentOrderList(paymentOrderEntityList)
        fetchPaymentOrderDetails(paymentOrderEntityList)
        return if (listOutcome) {
            SyncStatus.Success
        } else {
            SyncStatus.Failed
        }
    }

    private suspend fun fetchPaymentOrderDetails(paymentOrderEntityList: ArrayList<PaymentOrderEntity>) {
        paymentOrderEntityList.forEach {
            if(it.state == PaymentOrderStateEnum.Success.value && it.type == PaymentOrderType.Settle.value){
                paymentOrderRepository.fetchPaymentOrderDetails(it.id)
            }
        }
    }

    private suspend fun fetchPaymentOrderList(paymentOrderEntityList: ArrayList<PaymentOrderEntity>): Boolean{
        GlobalState.isSettlementRefreshing.value = true
        while (true) {
            val lastServerSequence = paymentOrderRepository.lastServerSequence()
            when(val outcome = paymentOrderRepository.fetchPaymentOrders(lastServerSequence)) {
                is Outcome.Error -> {
                    GlobalState.isSettlementRefreshing.value = false
                    return false
                }
                is Outcome.Success -> {
                    if(outcome.data.isNotEmpty()){
                        paymentOrderRepository.savePaymentOrders(outcome.data,
                            {
                                paymentOrderEntityList.add(it)
                            }, {
                               old, new ->
                                onSettlementUpdate(old, new)
                                paymentOrderEntityList.add(new)
                            }
                        )
                    }else{
                        GlobalState.isSettlementRefreshing.value = false
                        return true
                    }
                }
            }
        }
    }

    private fun onSettlementUpdate(old: PaymentOrderEntity, new: PaymentOrderEntity) {
        if (old.state != new.state) {
        }
    }
}

package com.rocketpay.mandate.feature.mandate.data

import com.rocketpay.mandate.feature.installment.data.datasource.local.InstallmentEntity
import com.rocketpay.mandate.feature.installment.domain.entities.InstallmentState
import com.rocketpay.mandate.feature.installment.domain.repositories.InstallmentRepository
import com.rocketpay.mandate.feature.mandate.data.datasource.local.MandateEntity
import com.rocketpay.mandate.feature.mandate.domain.repositories.MandateRepository
import com.rocketpay.mandate.feature.mandate.domain.usecase.MandateUseCase
import com.rocketpay.mandate.feature.mandate.presentation.injection.MandateComponent
import com.rocketpay.mandate.main.GlobalState
import com.rocketpay.mandate.main.init.MandateManager
import com.udharpay.core.networkmanager.domain.entities.Outcome
import com.udharpay.core.syncmanager.domain.enities.DefaultSyncConstraint
import com.udharpay.core.syncmanager.domain.enities.ExistingSyncPolicy
import com.udharpay.core.syncmanager.domain.enities.SyncFailurePolicy
import com.udharpay.core.syncmanager.domain.enities.SyncPriority
import com.udharpay.core.syncmanager.domain.enities.SyncStatus
import com.udharpay.core.syncmanager.domain.repositories.Sync
import com.udharpay.core.syncmanager.domain.repositories.Syncer
import javax.inject.Inject

internal class MandateSync : Sync {
    override fun dependencies() = emptyList<String>()
    override fun priority() = SyncPriority.Medium
    override fun existingSyncPolicy() = ExistingSyncPolicy.Keep
    override fun syncFailurePolicy() = SyncFailurePolicy.Cascade
    override fun syncer() = MandateSyncer()
    override fun constraint() = DefaultSyncConstraint.getLoginAndNetworkConstraint(
        isLoginRequire = true, isNetworkRequire = true
    )
}

internal class MandateSyncer : Syncer {

    @Inject
    internal lateinit var mandateRepository: MandateRepository
    @Inject
    internal lateinit var installmentRepository: InstallmentRepository
    @Inject
    lateinit var mandateUseCase: MandateUseCase

    companion object {
        const val TYPE = "mandate"

        internal fun onMandateUpdate(old: MandateEntity, new: MandateEntity) {
            if (old.state != new.state) {
                if (new.isSelfMandate != null && new.isSelfMandate) {
                }
            }
        }

        internal fun onInstallmentUpdate(
            old: InstallmentEntity,
            new: InstallmentEntity
        ) {
            if (old.state != new.state) {
                val installmentIds = mutableSetOf<String>()
                if (InstallmentState.get(new.state) is InstallmentState.SettlementSuccess) {
                    installmentIds.add(new.id)
                }
            }
        }
    }

    init {
        MandateComponent.Initializer.init().inject(this)
    }

    override suspend fun sync(): SyncStatus {
        GlobalState.isRefreshing.value = true
        val mandateIds = mutableSetOf<String>()
        val mandateSyncStatus = syncMandates(mandateIds)
        val installmentSyncStatus = syncInstallments(mandateIds)
        mandateUseCase.updateMandateCalculation(mandateIds)
        mandateRepository.saveMandatesSubtext(mandateIds)
        GlobalState.isRefreshing.value = false
        return if(mandateSyncStatus && installmentSyncStatus){
            SyncStatus.Success
        }else{
            SyncStatus.Failed
        }
    }

    private suspend fun syncMandates(mandateIds: MutableSet<String>): Boolean{
        while (true) {
            val maxMandateEntity = mandateRepository.getMaxTimeStamp()
            when (val outcome = mandateRepository.syncMandates(maxMandateEntity?.createdAt ?: 0,
                maxMandateEntity?.updatedAt ?: 0)) {
                is Outcome.Error -> {
                    return false
                }

                is Outcome.Success -> {
                    val list = outcome.data.items
                    if (list.isNotEmpty()) {
                        mandateRepository.saveMandates(
                            list,
                            {
                                mandateIds.add(it.id)
                            },
                            { old, new ->
                                onMandateUpdate(old, new)
                                new.isUpdated = old.updatedAt != new.updatedAt
                                mandateIds.add(new.id)
                            }
                        )
                    } else {
                        return true
                    }
                }
            }
        }
    }

    private suspend fun syncInstallments(mandateIds: MutableSet<String>): Boolean{
        while(true){
            val maxInstallmentEntity = installmentRepository.getMaxTimeStamp()
            when(val outcome = installmentRepository.fetchInstallments(
                maxInstallmentEntity?.createdAt ?: 0,
                maxInstallmentEntity?.updatedAt ?: 0)
            ) {
                is Outcome.Error -> {
                    return false
                }
                is Outcome.Success -> {
                    val list = outcome.data.items
                    if(list.isNotEmpty()) {
                        installmentRepository.saveInstallments(list,
                            {
                                mandateIds.add(it.mandateId)
                            },
                            { old, new ->
                                onInstallmentUpdate(old, new)
                                mandateIds.add(new.mandateId)
                            }
                        )
                    }else{
                        return true
                    }
                }
            }
        }
    }

}

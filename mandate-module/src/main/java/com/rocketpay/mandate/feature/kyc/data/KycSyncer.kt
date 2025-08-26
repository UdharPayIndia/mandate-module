package com.rocketpay.mandate.feature.kyc.data

import com.rocketpay.mandate.feature.kyc.domain.repositories.KycRepository
import com.rocketpay.mandate.feature.kyc.presentation.injection.KycComponent
import com.rocketpay.mandate.feature.property.domain.usecase.PropertyUseCase
import com.udharpay.core.networkmanager.domain.entities.Outcome
import com.udharpay.core.syncmanager.domain.enities.DefaultSyncConstraint
import com.udharpay.core.syncmanager.domain.enities.ExistingSyncPolicy
import com.udharpay.core.syncmanager.domain.enities.SyncFailurePolicy
import com.udharpay.core.syncmanager.domain.enities.SyncPriority
import com.udharpay.core.syncmanager.domain.enities.SyncStatus
import com.udharpay.core.syncmanager.domain.repositories.Sync
import com.udharpay.core.syncmanager.domain.repositories.Syncer
import javax.inject.Inject

internal class KycSync: Sync {
    override fun dependencies() = emptyList<String>()
    override fun priority() = SyncPriority.Medium
    override fun existingSyncPolicy() = ExistingSyncPolicy.Keep
    override fun syncFailurePolicy() = SyncFailurePolicy.Cascade
    override fun syncer() = KycSyncer()
    override fun constraint() = DefaultSyncConstraint.getLoginAndNetworkConstraint(
        isLoginRequire = true,
        isNetworkRequire = true
    )
}

internal class KycSyncer: Syncer {

    @Inject
    internal lateinit var kycRepository: KycRepository
    @Inject
    lateinit var propertyUseCase: PropertyUseCase

    companion object {
        const val TYPE = "kyc"
    }

    init {
        KycComponent.Initializer.init().inject(this)
    }

    override suspend fun sync(): SyncStatus {
        return when (val outcome = kycRepository.fetchKyc(propertyUseCase)) {
            is Outcome.Success -> {
                SyncStatus.Success
            }
            is Outcome.Error -> {
                SyncStatus.Failed
            }
        }
    }
}

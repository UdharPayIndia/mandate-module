package com.rocketpay.mandate.feature.business.data

import com.rocketpay.mandate.feature.business.data.datasource.entities.EnterprisePropertyDto
import com.rocketpay.mandate.feature.business.domain.repositories.BusinessPropertyRepository
import com.rocketpay.mandate.feature.business.presentation.injection.BusinessProfileComponent
import com.rocketpay.mandate.feature.property.data.entities.PropertyDto
import com.rocketpay.mandate.feature.property.data.entities.PropertyType
import com.rocketpay.mandate.feature.property.domain.repositories.PropertyRepository
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

internal class EnterprisePropertySync: Sync {
    override fun dependencies() = emptyList<String>()
    override fun priority() = SyncPriority.Medium
    override fun existingSyncPolicy() = ExistingSyncPolicy.Keep
    override fun syncFailurePolicy() = SyncFailurePolicy.Cascade
    override fun syncer() = EnterprisePropertySyncer()
    override fun constraint() = DefaultSyncConstraint.getLoginAndNetworkConstraint(
        isLoginRequire = true,
        isNetworkRequire = true
    )
}

internal class EnterprisePropertySyncer: Syncer {

    @Inject
    lateinit var businessPropertyRepository: BusinessPropertyRepository
    @Inject lateinit var propertyRepository: PropertyRepository

    companion object {
        const val TYPE = "enterprise_property"
    }

    init {
        BusinessProfileComponent.Initializer.init().inject(this)
    }

    override suspend fun sync(): SyncStatus {
        pullEnterpriseProperties()
        return SyncStatus.Success
    }

    private suspend fun pullEnterpriseProperties(): Outcome<List<EnterprisePropertyDto>> {
        return when(val outcome = businessPropertyRepository.pullEnterprisePropertyList(propertyRepository)) {
            is Outcome.Error -> {
                outcome
            }
            is Outcome.Success -> {
                outcome
            }
        }
    }
}
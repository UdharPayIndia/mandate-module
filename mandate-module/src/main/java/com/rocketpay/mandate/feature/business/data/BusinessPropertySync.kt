package com.rocketpay.mandate.feature.business.data

import com.rocketpay.mandate.feature.business.domain.repositories.BusinessPropertyRepository
import com.rocketpay.mandate.feature.business.presentation.injection.BusinessProfileComponent
import com.rocketpay.mandate.feature.property.data.entities.PropertyDto
import com.rocketpay.mandate.feature.property.data.entities.PropertyType
import com.rocketpay.mandate.feature.property.domain.repositories.PropertyRepository
import com.udharpay.core.networkmanager.domain.entities.Outcome
import com.udharpay.core.syncmanager.domain.enities.DefaultSyncConstraint
import com.udharpay.core.syncmanager.domain.enities.ExistingSyncPolicy
import com.udharpay.core.syncmanager.domain.enities.SyncFailurePolicy
import com.udharpay.core.syncmanager.domain.enities.SyncPriority
import com.udharpay.core.syncmanager.domain.enities.SyncStatus
import com.udharpay.core.syncmanager.domain.repositories.Sync
import com.udharpay.core.syncmanager.domain.repositories.Syncer
import javax.inject.Inject

internal class BusinessPropertySync: Sync {
    override fun dependencies() = emptyList<String>()
    override fun priority() = SyncPriority.Medium
    override fun existingSyncPolicy() = ExistingSyncPolicy.Keep
    override fun syncFailurePolicy() = SyncFailurePolicy.Cascade
    override fun syncer() = BusinessPropertySyncer()
    override fun constraint() = DefaultSyncConstraint.getLoginAndNetworkConstraint(
        isLoginRequire = true,
        isNetworkRequire = true
    )
}

internal class BusinessPropertySyncer: Syncer {

    @Inject
    lateinit var businessPropertyRepository: BusinessPropertyRepository
    @Inject lateinit var propertyRepository: PropertyRepository

    companion object {
        const val TYPE = "business_property"
    }

    init {
        BusinessProfileComponent.Initializer.init().inject(this)
    }

    override suspend fun sync(): SyncStatus {
        pushBusinessProperties()
        pullBusinessProperties()
        return SyncStatus.Success
    }

    private suspend fun pushBusinessProperties(): Boolean {
        val businessProperties = propertyRepository.getDirtyProperties(PropertyType.Merchant)
        if (businessProperties.isEmpty()) {
            return true
        }

        val hashMap = hashMapOf<String, String?>()
        businessProperties.map { hashMap[it.id] = it.value }
        return when(businessPropertyRepository.pushBusinessProperties(hashMap)) {
            is Outcome.Error -> {
                true
            }
            is Outcome.Success -> {
                propertyRepository.markPropertiesNonDirty(businessProperties)
                true
            }
        }
    }

    private suspend fun pullBusinessProperties(): Outcome<List<PropertyDto>> {
        return when(val outcome = businessPropertyRepository.pullBusinessProperties()) {
            is Outcome.Error -> {
                outcome
            }
            is Outcome.Success -> {
                outcome.data?.let {
                    propertyRepository.saveProperties(it, PropertyType.Merchant)
                }
                outcome
            }
        }
    }
}
package com.rocketpay.mandate.common.syncmanager.client.data.datasource.mapper

import com.udharpay.kernel.kernelcommon.mapper.ListMapper
import com.udharpay.core.syncmanager.data.entities.SyncRequestEnt
import com.rocketpay.mandate.common.syncmanager.client.data.datasource.SyncRequestEntity

internal class SyncEntToTableMapper:
    ListMapper<SyncRequestEnt, SyncRequestEntity> {
    override fun map(source: SyncRequestEnt): SyncRequestEntity {
        return SyncRequestEntity(
            id = source.id,
            syncType = source.syncType,
            syncStatus = source.syncStatus,
            retryCount = source.retryCount,
            backOffTime = source.backOffTime
        )
    }
}

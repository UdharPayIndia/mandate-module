package com.rocketpay.mandate.common.syncmanager.client.data.datasource.mapper

import com.udharpay.kernel.kernelcommon.mapper.ListMapper
import com.udharpay.core.syncmanager.data.entities.SyncRequestEnt
import com.rocketpay.mandate.common.syncmanager.client.data.datasource.SyncRequestEntity

internal class SyncTableToEntMapper:
    ListMapper<SyncRequestEntity, SyncRequestEnt> {
    override fun map(source: SyncRequestEntity): SyncRequestEnt {
        return SyncRequestEnt(
            id = source.id,
            syncType = source.syncType,
            syncStatus = source.syncStatus,
            retryCount = source.retryCount,
            backOffTime = source.backOffTime
        )
    }
}

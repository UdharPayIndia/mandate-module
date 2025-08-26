package com.udharpay.core.syncmanager.data.mapper

import com.udharpay.core.syncmanager.data.entities.SyncRequestEnt
import com.udharpay.core.syncmanager.domain.enities.SyncRequest
import com.udharpay.kernel.kernelcommon.mapper.ListMapper

class SyncDomToEntMapper:
    ListMapper<SyncRequest, SyncRequestEnt> {
    override fun map(source: SyncRequest): SyncRequestEnt {
        return SyncRequestEnt(
            id = source.id,
            syncType = source.syncType,
            syncStatus = source.syncStatus.value,
            retryCount = source.retryCount,
            backOffTime = source.backOffTime
        )
    }
}

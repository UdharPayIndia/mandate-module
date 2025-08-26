package com.udharpay.core.syncmanager.data.mapper

import com.udharpay.core.syncmanager.data.entities.SyncRequestEnt
import com.udharpay.core.syncmanager.domain.enities.SyncRequest
import com.udharpay.core.syncmanager.domain.enities.SyncStatus
import com.udharpay.kernel.kernelcommon.mapper.ListMapper

class SyncEntToDomMapper :
    ListMapper<SyncRequestEnt, SyncRequest> {
    override fun map(source: SyncRequestEnt): SyncRequest {
        return SyncRequest(
            id = source.id,
            syncType = source.syncType,
            syncStatus = SyncStatus.map[source.syncStatus] ?: SyncStatus.Enqueue,
            retryCount = source.retryCount,
            backOffTime = source.backOffTime
        )
    }
}

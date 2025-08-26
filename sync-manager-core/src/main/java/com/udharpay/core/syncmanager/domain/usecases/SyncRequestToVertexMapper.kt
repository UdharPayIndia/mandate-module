package com.udharpay.core.syncmanager.domain.usecases

import com.udharpay.kernel.kernelcommon.mapper.ListMapper
import com.udharpay.kernel.kernelcommon.register.Register
import com.udharpay.core.syncmanager.domain.enities.SyncRequest
import com.udharpay.core.syncmanager.domain.enities.SyncVertex
import com.udharpay.core.syncmanager.domain.repositories.Sync

class SyncRequestToVertexMapper(
    private val syncRegister: Register<String, Sync>,
): ListMapper<SyncRequest, SyncVertex> {
    override fun map(source: SyncRequest): SyncVertex {
        val sync = syncRegister.get(source.syncType)
        return SyncVertex(
            syncType = source.syncType,
            dependencies = sync.dependencies(),
            priority = sync.priority(),
            constraint = sync.constraint(),
            retryCount = source.retryCount,
            backOffTime = source.backOffTime,
            id = source.id,
        )
    }
}

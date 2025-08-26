package com.udharpay.core.syncmanager.domain.usecases

import com.udharpay.kernel.kernelcommon.register.Register
import com.udharpay.kernel.kernelcommon.taskexecutor.TaskExecutor
import com.udharpay.core.syncmanager.domain.enities.SyncStatus
import com.udharpay.core.syncmanager.domain.repositories.Sync
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SyncImmediateExecutor(
    private val syncRegister: Register<String, Sync>,
    private val taskExecutor: TaskExecutor
) {

    fun execute(syncType: String): Flow<SyncStatus> {
        return flow {
            emit(SyncStatus.InProgress)
            val syncer = syncRegister.get(syncType).syncer()
            val result  = taskExecutor.execute { syncer.sync() }
            emit(result)
        }
    }

    fun execute(syncTypes: List<String>): Flow<HashMap<String, SyncStatus>> {
        return flow {
            val hashMap: HashMap<String,SyncStatus> = hashMapOf()
            syncTypes.forEach { syncType ->
                hashMap[syncType] = SyncStatus.InProgress
                emit(hashMap)
                val syncer = syncRegister.get(syncType).syncer()
                val result  = taskExecutor.execute { syncer.sync() }
                hashMap[syncType] = result
                emit(hashMap)
            }
        }
    }
}

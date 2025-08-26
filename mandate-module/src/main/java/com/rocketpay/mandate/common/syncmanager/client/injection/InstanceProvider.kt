package com.rocketpay.mandate.common.syncmanager.client.injection

import android.content.Context
import com.udharpay.kernel.kernelcommon.analytics.AnalyticsHandler
import com.udharpay.kernel.kernelcommon.eventbus.FlowEventBus
import com.udharpay.kernel.kernelcommon.idgenerator.IdGenerator
import com.udharpay.kernel.kernelcommon.register.Register
import com.udharpay.kernel.kernelcommon.taskexecutor.TaskExecutorImpl
import com.udharpay.core.syncmanager.data.SyncRepositoryImpl
import com.udharpay.core.syncmanager.data.mapper.SyncDomToEntMapper
import com.udharpay.core.syncmanager.data.mapper.SyncEntToDomMapper
import com.udharpay.core.syncmanager.domain.enities.SyncEvent
import com.udharpay.core.syncmanager.domain.repositories.Sync
import com.udharpay.core.syncmanager.domain.usecases.*
import com.udharpay.core.syncmanager.domain.usecases.graph.SyncGraphCreator
import com.udharpay.core.syncmanager.domain.usecases.graph.SyncGraphProcessor
import com.udharpay.core.syncmanager.domain.usecases.graph.vertex.SyncVertexProcessor
import com.rocketpay.mandate.common.syncmanager.client.SyncManagerObserver
import com.rocketpay.mandate.common.syncmanager.client.data.database.SyncDatabase
import com.rocketpay.mandate.common.syncmanager.client.data.datasource.SyncRequestLDSImpl
import com.rocketpay.mandate.common.syncmanager.client.data.datasource.mapper.SyncEntToTableMapper
import com.rocketpay.mandate.common.syncmanager.client.data.datasource.mapper.SyncTableToEntMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

internal class InstanceProvider(
    context: Context,
    coroutineScope: CoroutineScope,
    constraintObserverRegister: Register<String, Flow<Any>>,
    syncRegister: Register<String, Sync>,
    analyticsHandler: AnalyticsHandler?,
    initialSyncEventBusCapacity: Int
) {
    private val taskExecutor = TaskExecutorImpl(coroutineScope)

    private val syncEventBus = FlowEventBus<SyncEvent>(initialSyncEventBusCapacity)

    private val syncRequestToVertex = SyncRequestToVertexMapper(syncRegister)

    private val constraintHandler =
        ConstraintHandler(
            syncEventBus = syncEventBus,
            constraintObserverRegister = constraintObserverRegister,
            taskExecutor = taskExecutor
        )

    private val syncRepository = SyncRepositoryImpl(
        syncRequestLDS = SyncRequestLDSImpl(
            syncEntToTableMapper = SyncEntToTableMapper(),
            syncTableToEntMapper = SyncTableToEntMapper(),
            syncRequestDao = SyncDatabase.getDatabase(context).syncRequestDao()
        ),
        syncDomToEntMapper = SyncDomToEntMapper(),
        syncEntToDomMapper = SyncEntToDomMapper()
    )

    private val existingSyncPolicyHandler =  ExistingSyncPolicyHandler()
    private val syncRequestManager = SyncRequestManager(
        syncRepository = syncRepository,
        syncRegister = syncRegister,
        idGenerator = IdGenerator(),
        existingSyncPolicyHandler = existingSyncPolicyHandler
    )

    private val syncGraphManager = SyncGraphManager(
        graphCreator = SyncGraphCreator(),
        graphProcessor = SyncGraphProcessor(
            SyncVertexProcessor(
                syncRegister = syncRegister,
                syncEventBus = syncEventBus,
                taskExecutor = taskExecutor
            )
        ),
        constraintHandler = constraintHandler
    )

    val syncManagerObserver = SyncManagerObserver(context)

    private val syncAnalyticsHandler = SyncAnalyticsHandler(analyticsHandler)

    private val syncImmediateExecutor = SyncImmediateExecutor(syncRegister, taskExecutor)

    private val failurePolicyHandler = FailurePolicyHandler()

    val syncManagerImpl = SyncManagerImpl(
        taskExecutor = taskExecutor,
        syncEventBus = syncEventBus,
        syncRegister = syncRegister,
        syncRequestToVertex = syncRequestToVertex,
        syncGraphManager = syncGraphManager,
        syncRequestManager = syncRequestManager,
        syncAnalyticsHelper = syncAnalyticsHandler,
        syncImmediateExecutor = syncImmediateExecutor,
        failurePolicyHandler = failurePolicyHandler
    )
}

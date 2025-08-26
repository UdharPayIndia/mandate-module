package com.rocketpay.mandate.common.syncmanager.client

import android.content.Context
import com.udharpay.core.syncmanager.domain.enities.SyncStatus
import com.udharpay.core.syncmanager.domain.enities.graph.GraphStatus
import com.udharpay.core.syncmanager.domain.repositories.Sync
import com.rocketpay.mandate.common.syncmanager.client.injection.InstanceProvider
import com.udharpay.kernel.kernelcommon.analytics.AnalyticsHandler
import com.udharpay.kernel.kernelcommon.register.Register
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

internal class SyncManager private constructor(
    context: Context,
    coroutineScope: CoroutineScope,
    constraintObserverRegister: Register<String, Flow<Any>>,
    syncRegister: Register<String, Sync>,
    analyticsHandler: AnalyticsHandler?
) {
    companion object {

        private const val EVENT_BUS_INIT_CAPACITY: Int = 64

        private var instance: SyncManager? = null
        fun getInstance(): SyncManager {
            return instance ?: throw Exception("SyncManager is not built, Please use Builder to build")
        }
    }

    private var instanceProvider: InstanceProvider = InstanceProvider(
        context,
        coroutineScope,
        constraintObserverRegister,
        syncRegister,
        analyticsHandler,
        EVENT_BUS_INIT_CAPACITY
    )

    class Builder {
        private lateinit var context: Context
        private lateinit var coroutineScope: CoroutineScope
        private lateinit var constraintObserverRegister: Register<String, Flow<Any>>
        private lateinit var syncRegister: Register<String, Sync>
        private var analyticsHandler: AnalyticsHandler? = null

        fun setContext(context: Context) = apply {
            this.context = context
        }

        fun setScope(coroutineScope: CoroutineScope) = apply {
            this.coroutineScope = coroutineScope
        }

        fun setConstraintObserverRegister(constraintObserverRegister: Register<String, Flow<Any>>) = apply {
            this.constraintObserverRegister = constraintObserverRegister
        }

        fun setSyncRegister(syncRegister: Register<String, Sync>) = apply {
            this.syncRegister = syncRegister
        }

        fun setAnalyticsHelper(analyticsHandler: AnalyticsHandler?) = apply {
            this.analyticsHandler = analyticsHandler
        }

        fun build() {
            if (instance != null) {
                throw Exception("SyncManager is already built, you can not Re-built")
            }

            instance = SyncManager(
                context,
                coroutineScope,
                constraintObserverRegister,
                syncRegister,
                analyticsHandler
            )
        }
    }


    internal fun initiateSync() {
        instanceProvider.syncManagerImpl.initiateSync()
    }

    fun getObserver(): SyncManagerObserver {
        return instanceProvider.syncManagerObserver
    }

    //////////////////////////////////////////////////////////
    ////////////////////// Client APIs ///////////////////////
    //////////////////////////////////////////////////////////

    fun enqueue(syncType: String) {
        instanceProvider.syncManagerImpl.enqueue(syncType)
    }

    fun enqueue(syncTypes: List<String>) {
        instanceProvider.syncManagerImpl.enqueue(syncTypes)
    }

    fun getStatus(syncType: String): Flow<SyncStatus?> {
        return instanceProvider.syncManagerImpl.getStatus(syncType)
    }

    fun getStatus(syncTypes: List<String>): Flow<HashMap<String, SyncStatus>> {
        return instanceProvider.syncManagerImpl.getStatus(syncTypes)
    }

    fun immediate(syncType: String): Flow<SyncStatus> {
        return instanceProvider.syncManagerImpl.execute(syncType)
    }

    fun immediate(syncTypes: List<String>): Flow<HashMap<String, SyncStatus>> {
        return instanceProvider.syncManagerImpl.execute(syncTypes)
    }

    fun getGraphStatus(): Flow<GraphStatus> {
        return instanceProvider.syncManagerImpl.getGraphStatus()
    }
}

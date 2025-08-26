package com.rocketpay.mandate.common.syncmanager.client

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.work.*

internal class SyncManagerObserver(private val context: Context): LifecycleObserver {

    companion object {
        const val TYPE = "SYNC_MANAGER"
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun enqueueSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncWorkRequest = OneTimeWorkRequestBuilder<SyncInitiationWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(TYPE, ExistingWorkPolicy.REPLACE,  syncWorkRequest)
    }
}

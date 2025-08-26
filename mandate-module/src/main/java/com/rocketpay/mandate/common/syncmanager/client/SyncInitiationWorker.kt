package com.rocketpay.mandate.common.syncmanager.client

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay

internal class SyncInitiationWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            SyncManager.getInstance().initiateSync()
            delay(10000)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

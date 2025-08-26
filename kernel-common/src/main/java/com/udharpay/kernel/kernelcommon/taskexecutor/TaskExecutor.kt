package com.udharpay.kernel.kernelcommon.taskexecutor

import kotlinx.coroutines.CoroutineScope

interface TaskExecutor {
    fun executeInBackground(block: suspend () -> Unit)
    fun executeInMain(block: () -> Unit)
    fun executeInCurrent(block: suspend () -> Unit)
    suspend fun <T> execute(block: suspend () -> T): T
    fun executeInBackground(scope: CoroutineScope, block: suspend () -> Unit)
}

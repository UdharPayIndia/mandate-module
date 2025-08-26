package com.udharpay.kernel.kernelcommon.taskexecutor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskExecutorImpl(private val scope: CoroutineScope): TaskExecutor {

    override fun executeInBackground(block: suspend () -> Unit) {
        scope.launch(Dispatchers.IO) { block() }
    }

    override fun executeInMain(block: () -> Unit) {
        scope.launch(Dispatchers.Main) { block() }
    }

    override fun executeInCurrent(block: suspend () -> Unit) {
        scope.launch { block() }
    }

    override suspend fun <T> execute(block: suspend () -> T): T {
        return withContext(Dispatchers.IO) {
            block()
        }
    }

    override fun executeInBackground(scope: CoroutineScope, block: suspend () -> Unit) {
        scope.launch(Dispatchers.IO) { block() }
    }
}

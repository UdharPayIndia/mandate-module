package com.rocketpay.mandate.common.mvistatemachine.contract

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal fun <T> Flow<T>.collectIn(scope: CoroutineScope, action: suspend (value: T) -> Unit) {
    scope.launch {
        withContext(Dispatchers.IO) {
            collect {
                action(it)
            }
        }
    }
}

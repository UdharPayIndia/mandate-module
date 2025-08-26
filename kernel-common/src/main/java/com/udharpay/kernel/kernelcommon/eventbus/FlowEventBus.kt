package com.udharpay.kernel.kernelcommon.eventbus

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect

class FlowEventBus<T>(capacity: Int) : EventBus<T> {

    private val flow = MutableSharedFlow<T>(capacity)

    override suspend fun fire(event: T) {
        flow.emit(event)
    }

    override suspend fun listen(observer: suspend (T) -> Unit) {
        flow.collect {
            observer(it)
        }
    }
}

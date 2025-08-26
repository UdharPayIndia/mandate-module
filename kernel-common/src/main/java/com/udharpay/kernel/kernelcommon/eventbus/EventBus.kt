package com.udharpay.kernel.kernelcommon.eventbus

interface EventBus<T> {
    suspend fun fire(event: T)
    suspend fun listen(observer: suspend (T) -> Unit)
}

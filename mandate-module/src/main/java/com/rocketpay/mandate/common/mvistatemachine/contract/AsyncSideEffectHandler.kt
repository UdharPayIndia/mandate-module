package com.rocketpay.mandate.common.mvistatemachine.contract

import kotlinx.coroutines.CoroutineScope

internal interface AsyncSideEffectHandler<E : Event, ASF : AsyncSideEffect> {
    suspend fun handleSideEffect(sideEffect: ASF, dispatchEvent: (E) -> Unit, scope: CoroutineScope)
}

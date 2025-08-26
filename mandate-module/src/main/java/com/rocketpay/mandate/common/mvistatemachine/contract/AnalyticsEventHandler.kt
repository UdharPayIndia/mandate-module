package com.rocketpay.mandate.common.mvistatemachine.contract

internal interface AnalyticsEventHandler<E : Event, S : State> {
    fun sendEvent(event: E, state: S)
}

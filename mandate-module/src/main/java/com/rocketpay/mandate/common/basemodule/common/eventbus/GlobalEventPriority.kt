package com.rocketpay.mandate.common.basemodule.common.eventbus

sealed class GlobalEventPriority(val value: Int) {
    object Critical: GlobalEventPriority(0)
    object High: GlobalEventPriority(1)
    object Medium: GlobalEventPriority(2)
    object Low: GlobalEventPriority(3)
}

package com.rocketpay.mandate.common.basemodule.common.eventbus

data class GlobalEvent(
    val eventId: String,
    val data: Any,
    val eventPriority: GlobalEventPriority = GlobalEventPriority.Medium
)

package com.rocketpay.mandate.common.basemodule.common.eventbus

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.rocketpay.mandate.common.mvistatemachine.liveevent.LiveEvent

object GlobalEventBus {

    private var events = LiveEvent<GlobalEvent>()

    fun fire(event: GlobalEvent) {
        events.postValue(event)
    }

    fun listen(owner: LifecycleOwner, observer: Observer<GlobalEvent>) {
        events.observe(owner, observer)
    }
}
